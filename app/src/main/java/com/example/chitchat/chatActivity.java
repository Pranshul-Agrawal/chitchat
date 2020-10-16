package com.example.chitchat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class chatActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private String messgaeReciverId, messageReciverName, senderId;
    private EditText messageText;
    private DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mToolbar = findViewById(R.id.group_chat_bar);
        setSupportActionBar(mToolbar);
        messgaeReciverId = getIntent().getExtras().get("reciverid").toString();
        messageReciverName = getIntent().getExtras().get("recivername").toString();
        messageText = findViewById(R.id.text_reply_box);
        senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

    }

    public void sendMessage(View view) {
        String messageText = this.messageText.getText().toString();
        if (!TextUtils.isEmpty(messageText)) {
            String messageSenderRef = "Messages/" + senderId + "/" + messgaeReciverId;
            String messageReciverRef = "Messages/" + messgaeReciverId + "/" + senderId;

            DatabaseReference userMessageKeyref = rootRef.child("Messages").child(senderId).child(messgaeReciverId).push();
            String messagepushId = userMessageKeyref.getKey();
            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", senderId);

            Map details = new HashMap();
            details.put(messageSenderRef + "/" + messagepushId, messageTextBody);
            details.put(messageReciverRef + "/" + messagepushId, messageTextBody);
            rootRef.updateChildren(details);
            this.messageText.setText("");


        }
    }
}