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
    private Button sendMessageButton, declinemessagebutton;
    private DatabaseReference userRef, chatRequestRef, contactref;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        contactref = FirebaseDatabase.getInstance().getReference().child("contacts");
        reciveUserId = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("visitUserid")).toString();
        circleImageView = findViewById(R.id.visit_profile_image);
        name = findViewById(R.id.visit_user_Name);
        status = findViewById(R.id.visit_user_status);
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("chat_request");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentState = "new";
        senderUserId = mAuth.getCurrentUser().getUid();
        sendMessageButton = findViewById(R.id.send_message);
        declinemessagebutton = findViewById(R.id.decline_message);
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
        chatRequestRef.child(senderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(reciveUserId)) {
                    String request_type = snapshot.child(reciveUserId).child("request_type").getValue().toString();
                    if (request_type.equals("sent")) {
                        request_type = "request_sent";
                        sendMessageButton.setText("cancel Chat Request");
                    } else if (request_type.equals("recived")) {
                        currentState = "request_recived";
                        sendMessageButton.setText("Accept Chat Request");
                        declinemessagebutton.setVisibility(View.VISIBLE);
                        declinemessagebutton.setEnabled(true);
                        declinemessagebutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                chatRequestRef.child(senderUserId).child(reciveUserId).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    chatRequestRef.child(reciveUserId).child(senderUserId).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    sendMessageButton.setEnabled(true);
                                                                    currentState = "new";
                                                                    sendMessageButton.setText("Send Message");
                                                                    declinemessagebutton.setVisibility(View.INVISIBLE);
                                                                    declinemessagebutton.setEnabled(false);
                                                                }
                                                            });
                                                }


                                            }
                                        });


                            }
                        });
                    }
                } else {
                    contactref.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(reciveUserId)) {
                                currentState = "friend";
                                sendMessageButton.setText("Remove Friend");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
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
                        // send chat request
                        chatRequestRef.child(senderUserId).child(reciveUserId).child("request_type").setValue("sent")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            chatRequestRef.child(reciveUserId).child(senderUserId).child("request_type").setValue("recived")
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                sendMessageButton.setEnabled(true);
                                                                currentState = "request_sent";
                                                                sendMessageButton.setText("Cancel Chat Request");

                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                    if (currentState.equals("request_sent")) {
                        chatRequestRef.child(senderUserId).child(reciveUserId).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            chatRequestRef.child(reciveUserId).child(senderUserId).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            sendMessageButton.setEnabled(true);
                                                            currentState = "new";
                                                            sendMessageButton.setText("Send Message");
                                                        }
                                                    });
                                        }

                                    }
                                });
                    }
                    if (currentState.equals("request_recived")) {
                        contactref.child(senderUserId).child(reciveUserId).child("contacts").setValue("saved")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            contactref.child(reciveUserId).child(senderUserId).child("contacts").setValue("saved")
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                chatRequestRef.child(senderUserId).child(reciveUserId).removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                chatRequestRef.child(reciveUserId).child(senderUserId).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                sendMessageButton.setEnabled(true);
                                                                                                sendMessageButton.setText("Remove Friend");
                                                                                                currentState = "friend";
                                                                                                declinemessagebutton.setVisibility(View.INVISIBLE);
                                                                                                declinemessagebutton.setEnabled(false);

                                                                                            }
                                                                                        });

                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });

                                        }
                                    }
                                });
                    }
                    if (currentState.equals("friend")) {
                        contactref.child(senderUserId).child(reciveUserId).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            contactref.child(reciveUserId).child(senderUserId).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            sendMessageButton.setEnabled(true);
                                                            currentState = "new";
                                                            sendMessageButton.setText("Send Message");
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