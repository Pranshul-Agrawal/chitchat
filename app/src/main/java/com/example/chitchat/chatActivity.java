package com.example.chitchat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class chatActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private String messgaeReciverId, messageReciverName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mToolbar = findViewById(R.id.group_chat_bar);
        setSupportActionBar(mToolbar);
        messgaeReciverId = getIntent().getExtras().get("reciverid").toString();
        messageReciverName = getIntent().getExtras().get("recivername").toString();
    }
}