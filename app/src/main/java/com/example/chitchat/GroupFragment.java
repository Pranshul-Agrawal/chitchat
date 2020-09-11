package com.example.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GroupFragment extends Fragment {
    private View groupFragmentView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listOfGroups = new ArrayList<>();
    private DatabaseReference groupRefrence;


    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        groupFragmentView = inflater.inflate(R.layout.fragment_group, container, false);
        listView = groupFragmentView.findViewById(R.id.group_list);
        arrayAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, listOfGroups);
        listView.setAdapter(arrayAdapter);
        groupRefrence = FirebaseDatabase.getInstance().getReference().child("Groups");
        groupRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> group = new HashSet<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    group.add(dataSnapshot.getKey());
                }
                listOfGroups.clear();
                listOfGroups.addAll(group);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
           {
               String currentGroupName=adapterView.getItemAtPosition(position).toString();
               Intent groupChatIntent=new Intent(getContext(),GroupActivity.class);
               groupChatIntent.putExtra("groupName",currentGroupName);
               startActivity(groupChatIntent);

           }
       });
        return groupFragmentView;

    }
}