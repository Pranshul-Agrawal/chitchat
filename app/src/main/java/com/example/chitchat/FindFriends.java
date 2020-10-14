package com.example.chitchat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FindFriends extends AppCompatActivity {

    private Toolbar mtoolBar;
    private RecyclerView findFriendRecycerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        findFriendRecycerView = findViewById(R.id.find_friends_recyclerview);
        findFriendRecycerView.setLayoutManager(new LinearLayoutManager(this));
        mtoolBar = findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mtoolBar);
        getSupportActionBar().setTitle("Find Friends");
    }
}