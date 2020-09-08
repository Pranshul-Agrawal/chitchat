package com.example.chitchat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.Objects;

public class GroupActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText message;
    private ScrollView mScrollView;
    private TextView displayMessage;
    private String groupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        message=findViewById(R.id.text_reply_box);
        mScrollView=findViewById(R.id.scroll_view);
        displayMessage=findViewById(R.id.group_chat_textView);
        groupName= Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupName")).toString();
        mToolbar=findViewById(R.id.group_chat_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(groupName);

    }
    public void sendMessage(View view)
    {

    }
}
