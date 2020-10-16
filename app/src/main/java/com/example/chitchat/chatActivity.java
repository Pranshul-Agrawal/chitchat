package com.example.chitchat;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class chatActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private String messgaeReciverId, messageReciverName, senderId;
    private EditText messageText;
    private DatabaseReference rootRef;
    private List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessageList;


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
        messageAdapter = new MessageAdapter(messageList);
        userMessageList = findViewById(R.id.list_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        messageList.clear();
        rootRef.child("Messages").child(senderId).child(messgaeReciverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                messageList.add(message);
                int ctr = 0;
                Log.d("ctr", ctr++ + " ");
                messageAdapter.notifyDataSetChanged();
                userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
            messageTextBody.put("to", messgaeReciverId);

            Map details = new HashMap();
            details.put(messageSenderRef + "/" + messagepushId, messageTextBody);
            details.put(messageReciverRef + "/" + messagepushId, messageTextBody);
            rootRef.updateChildren(details);
            this.messageText.setText("");


        }
    }
}