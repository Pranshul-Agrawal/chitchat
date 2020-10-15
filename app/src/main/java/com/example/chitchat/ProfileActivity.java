package com.example.chitchat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private String reciveUserId, currentState, senderUserId;
    private CircleImageView circleImageView;
    private TextView name;
    private TextView status;
    private Button sendMessageButton;
    private DatabaseReference userRef, chatRequestRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        reciveUserId = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("visitUserid")).toString();
        circleImageView = findViewById(R.id.visit_profile_image);
        name = findViewById(R.id.visit_user_Name);
        status = findViewById(R.id.visit_user_status);
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("chat_request");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentState = "new";
        senderUserId = mAuth.getCurrentUser().getUid();
        sendMessageButton = findViewById(R.id.send_message);
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
                    manageChatRequest();

                } else if ((snapshot.exists()) && (snapshot.hasChild("username"))) {
                    String retrieveUserName = snapshot.child("username").getValue().toString();
                    String retrieveUserStatus = snapshot.child("status").getValue().toString();
                    name.setText(retrieveUserName);
                    status.setText(retrieveUserStatus);
                    manageChatRequest();

                } else {
                    name.setVisibility(View.VISIBLE);
                    Toast.makeText(ProfileActivity.this, "Set or Update Profile", Toast.LENGTH_SHORT).show();
                    manageChatRequest();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void manageChatRequest() {
        chatRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(reciveUserId)) {


                    String requestType = snapshot.child(reciveUserId).child("request_type").getValue().toString();
                    if (requestType.equals("recived")) {
                        currentState = "request_sent";
                        sendMessageButton.setText("Cancel Request");
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (!senderUserId.equals(reciveUserId)) {
            sendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessageButton.setEnabled(false);
                    if (currentState.equals("new")) {
                        chatRequestRef.child(senderUserId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    chatRequestRef.child(reciveUserId).child("request_type").setValue("recived").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendMessageButton.setEnabled(true);
                                                sendMessageButton.setText("Cancel Request");
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });

        } else {
            sendMessageButton.setVisibility(View.INVISIBLE);
        }

    }


}