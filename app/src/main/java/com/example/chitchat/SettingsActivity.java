package com.example.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private EditText userName, userStatus;
    private String userId;
    private DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        userName = findViewById(R.id.user_name);
        userStatus = findViewById(R.id.user_status);
        rootRef = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        retrieveUserInfo();
        userName.setVisibility(View.INVISIBLE);
    }

    /*Onclick Button Method*/
    public void updateProfile(View view) {


        String username = userName.getText().toString();
        String userstatus = userStatus.getText().toString();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(userstatus)) {
            Toast.makeText(SettingsActivity.this, "Username or Status is invalid", Toast.LENGTH_SHORT).show();
        } else {

            HashMap<String, String> profileDetail = new HashMap<>();
            profileDetail.put("userId", userId);
            profileDetail.put("username", username);
            profileDetail.put("status", userstatus);
            rootRef.child("Users").child(userId).setValue(profileDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SettingsActivity.this, "Username and status set Successfully", Toast.LENGTH_SHORT).show();
                        Intent MainActivityIntent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(MainActivityIntent);

                    } else {
                        Toast.makeText(SettingsActivity.this, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }

    /* user defined Helper Method */
    private void retrieveUserInfo() {
        rootRef.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("username")) && (snapshot.hasChild("image"))) {
                    String retrieveUserName = snapshot.child("username").getValue().toString();
                    String retrieveUserStatus = snapshot.child("status").getValue().toString();
                    String retrieveUserImage = snapshot.child("image").getValue().toString();
                    userName.setText(retrieveUserName);
                    userStatus.setText(retrieveUserStatus);
                } else if ((snapshot.exists()) && (snapshot.hasChild("username"))) {
                    String retrieveUserName = snapshot.child("username").getValue().toString();
                    String retrieveUserStatus = snapshot.child("status").getValue().toString();
                    userName.setText(retrieveUserName);
                    userStatus.setText(retrieveUserStatus);

                } else {
                    userName.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this, "Set or Update Profile", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}