package com.example.chitchat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private String reciveUserId;
    private CircleImageView circleImageView;
    private TextView name;
    private TextView status;
    private Button sendMessageButton;
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        reciveUserId = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("visitUserid")).toString();
        circleImageView = findViewById(R.id.visit_profile_image);
        name = findViewById(R.id.visit_user_Name);
        status = findViewById(R.id.visit_user_status);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        retrieveUserInfo();


    }

    private void retrieveUserInfo() {
        userRef.child(reciveUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("username")) && (snapshot.hasChild("profile"))) {
                    String retrieveUserName = snapshot.child("username").getValue().toString();
                    String retrieveUserStatus = snapshot.child("status").getValue().toString();
                    String retrieveUserImage = snapshot.child("profile").getValue().toString();
                    name.setText(retrieveUserName);
                    status.setText(retrieveUserStatus);
                    Picasso.get().load(retrieveUserImage).placeholder(R.drawable.profile_image).into(circleImageView);
                    // manageChatRequest();

                } else if ((snapshot.exists()) && (snapshot.hasChild("username"))) {
                    String retrieveUserName = snapshot.child("username").getValue().toString();
                    String retrieveUserStatus = snapshot.child("status").getValue().toString();
                    name.setText(retrieveUserName);
                    status.setText(retrieveUserStatus);
                    // manageChatRequest();

                } else {
                    name.setVisibility(View.VISIBLE);
                    Toast.makeText(ProfileActivity.this, "Set or Update Profile", Toast.LENGTH_SHORT).show();
                    //  manageChatRequest();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}