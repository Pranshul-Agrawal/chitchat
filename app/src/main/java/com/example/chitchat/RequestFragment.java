package com.example.chitchat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestFragment extends Fragment {
    private View requestFragmentView;
    private RecyclerView mrequestList;
    private DatabaseReference chatRequestRef, userRef, contactRef;
    private String currentUserId;

    public RequestFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requestFragmentView = inflater.inflate(R.layout.fragment_request, container, false);
        mrequestList = requestFragmentView.findViewById(R.id.chat_request_view);
        mrequestList.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("chat_request");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactRef = FirebaseDatabase.getInstance().getReference().child("contacts");
        return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(chatRequestRef.child(currentUserId), Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, ChatRequestViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ChatRequestViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final ChatRequestViewHolder holder, int position, @NonNull Contacts model) {
                holder.itemView.findViewById(R.id.accept_request).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.decline_request).setVisibility(View.VISIBLE);
                final String listUserId = getRef(position).getKey();
                DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String type = snapshot.getValue().toString();
                            if (type.equals("recived")) {
                                userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("profile")) {
                                            String pImage = snapshot.child("profile").getValue().toString();

                                            Picasso.get().load(pImage).placeholder(R.drawable.profile_image).into(holder.imageView);
                                        }
                                        String status = snapshot.child("status").getValue().toString();
                                        final String name = snapshot.child("username").getValue().toString();
                                        holder.name.setText(name);
                                        holder.status.setText(status);
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                CharSequence options[] = {"Accept", "Decline"};
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(name + " Chat Request").setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        if (i == 0) {
                                                            contactRef.child(currentUserId).child(listUserId).child("contacts").setValue("saved")
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                contactRef.child(listUserId).child(currentUserId).child("contacts").setValue("saved")
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                chatRequestRef.child(currentUserId).child(listUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            chatRequestRef.child(listUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                    if (task.isSuccessful()) {
                                                                                                                        Toast.makeText(getContext(), "New Contact Saved", Toast.LENGTH_SHORT).show();
                                                                                                                    }

                                                                                                                }
                                                                                                            });
                                                                                                        }

                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });
                                                        } else if (i == 1) {
                                                            chatRequestRef.child(currentUserId).child(listUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        chatRequestRef.child(listUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(getContext(), "Request deleted", Toast.LENGTH_SHORT).show();
                                                                                }

                                                                            }
                                                                        });
                                                                    }

                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                                builder.show();

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                ChatRequestViewHolder viewHolder = new ChatRequestViewHolder(view);
                return viewHolder;
            }

        };
        mrequestList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatRequestViewHolder extends RecyclerView.ViewHolder {
        TextView name, status;
        CircleImageView imageView;
        Button accept, cancel;

        public ChatRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            status = itemView.findViewById(R.id.user_status);
            imageView = itemView.findViewById(R.id.imageview);
            accept = itemView.findViewById(R.id.accept_request);
            cancel = itemView.findViewById(R.id.decline_request);

        }
    }
}
