package com.example.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {

    private View privateChatView;
    ;
    private RecyclerView listView;
    private DatabaseReference privatechatRef, userRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        privateChatView = inflater.inflate(R.layout.fragment_chat, container, false);
        listView = privateChatView.findViewById(R.id.chat_list);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        privatechatRef = FirebaseDatabase.getInstance().getReference().child("contacts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        return privateChatView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(privatechatRef, Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, PrivateChatViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, PrivateChatViewHolder>(options) {
            @NonNull
            @Override
            public PrivateChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                return new PrivateChatViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final PrivateChatViewHolder holder, int position, @NonNull Contacts model) {
                final String userId = getRef(position).getKey();
                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.hasChild("profile")) {
                                String pImage = snapshot.child("profile").getValue().toString();
                                Picasso.get().load(pImage).placeholder(R.drawable.profile_image).into(holder.profileimage);
                            }
                            String status = snapshot.child("status").getValue().toString();
                            String name = snapshot.child("username").getValue().toString();
                            holder.username.setText(name);
                            holder.userstatus.setText("Last seen: " + "\n" + "date " + " time");
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent chatIntent = new Intent(getContext(), chatActivity.class);
                                    chatIntent.putExtra("reciverid", userId);
                                    chatIntent.putExtra("recivername", holder.username.getText().toString());
                                    startActivity(chatIntent);
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        };
        listView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PrivateChatViewHolder extends RecyclerView.ViewHolder {
        TextView username, userstatus;
        CircleImageView profileimage;

        public PrivateChatViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
            userstatus = itemView.findViewById(R.id.user_status);
            profileimage = itemView.findViewById(R.id.imageview);
        }
    }
}