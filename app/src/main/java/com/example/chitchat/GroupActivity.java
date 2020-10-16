package com.example.chitchat;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import static android.view.View.FOCUS_DOWN;

public class GroupActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText message;
    private ScrollView mScrollView;
    private TextView displayMessage;
    private String groupName,currentUid,currentUname ,cDate,cTime;
    private DatabaseReference userRef,groupNameRef,groupMessageKeyRef;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        message=findViewById(R.id.text_reply_box);
        mScrollView=findViewById(R.id.scroll_view);
        displayMessage=findViewById(R.id.group_chat_textView);
        groupName= Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupName")).toString();
        mToolbar=findViewById(R.id.group_chat_bar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(groupName);
        currentUid= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(groupName);
        getUserInfo();



    }

    protected void onStart()
    {

        super.onStart();
        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                if (snapshot.exists()) {
                    Iterator iterator = snapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        /* Order must not be changed */
                        String date = (String) ((DataSnapshot) iterator.next()).getValue();
                        String message = (String) ((DataSnapshot) iterator.next()).getValue();
                        String name = (String) ((DataSnapshot) iterator.next()).getValue();
                        String time = (String) ((DataSnapshot) iterator.next()).getValue();
                        displayMessage.append(date+" ("+time+") "+"\n"+message+"\n"+name+"\n\n\n");

                        mScrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });

                    }
                }

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

    public void sendMessage(View view)
    {
        String message=this.message.getText().toString();
        String messagekey=groupNameRef.push().getKey();
        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this,"Enter some Text",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate=Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDateFormat=new SimpleDateFormat("MMM dd yyyy");
            cDate=currentDateFormat.format(calForDate.getTime());

            Calendar calForTime=Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
            cTime=currentTimeFormat.format(calForTime.getTime());

            HashMap <String,Object> groupMessageKey=new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey);

            groupMessageKeyRef=groupNameRef.child(messagekey);

            HashMap <String,Object> messageInfoMap=new HashMap<>();
            messageInfoMap.put("name",currentUname);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",cDate);
            messageInfoMap.put("time",cTime);
            groupMessageKeyRef.updateChildren(messageInfoMap);
        }
        this.message.setText("");
        mScrollView.fullScroll(FOCUS_DOWN);
    }
    public void getUserInfo()
    {
        userRef.child(currentUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    currentUname=snapshot.child("username").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
