package com.example.chitchat;

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

public class ContactFragment extends Fragment {
    private View contactView;
    private RecyclerView myContactList;
    private DatabaseReference contactRef, userRef;
    private String currentUserId;


    public ContactFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contactView = inflater.inflate(R.layout.fragment_contact, container, false);
        myContactList = contactView.findViewById(R.id.contact_list);
        myContactList.setLayoutManager(new LinearLayoutManager(getContext()));
        contactRef = FirebaseDatabase.getInstance().getReference().child("contacts");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        return contactView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(contactRef, Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {
                String UserId = getRef(position).getKey();
                userRef.child(UserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("profile")) {
                            String pImage = snapshot.child("profile").getValue().toString();
                            String status = snapshot.child("status").getValue().toString();
                            String name = snapshot.child("username").getValue().toString();
                            holder.username.setText(name);
                            holder.userstatus.setText(status);
                            Picasso.get().load(pImage).placeholder(R.drawable.profile_image).into(holder.profileimage);
                        } else {
                            String status = snapshot.child("status").getValue().toString();
                            String name = snapshot.child("username").getValue().toString();
                            holder.username.setText(name);
                            holder.userstatus.setText(status);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                return new ContactsViewHolder(view);
            }
        };
        myContactList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView username, userstatus;
        CircleImageView profileimage;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
            userstatus = itemView.findViewById(R.id.user_status);
            profileimage = itemView.findViewById(R.id.imageview);
        }
    }
}