package com.example.chitchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> userMessageList;
    private DatabaseReference userRef;

    public MessageAdapter(List<Message> userMessageList) {
        this.userMessageList = userMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout, parent, false);
        MessageViewHolder viewHolder = new MessageViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        final String messagesenderid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Message message = userMessageList.get(position);
        final String fromuid = message.getFrom();
        final String toUserid = message.getTo();
        final String fromType = message.getType();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromuid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("profile")) {
                    String pImage = snapshot.child("profile").getValue().toString();
                    Picasso.get().load(pImage).placeholder(R.drawable.profile_image).into(holder.imageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (fromType.equals("text")) {

            holder.imageView.setVisibility(View.INVISIBLE);
            if (fromuid.equals(messagesenderid)) {
                holder.reciverMessageText.setVisibility(View.INVISIBLE);
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_layout);
                holder.senderMessageText.setText(message.getMessage());
            } else {

                holder.senderMessageText.setVisibility(View.INVISIBLE);
                holder.imageView.setVisibility(View.VISIBLE);
                holder.reciverMessageText.setVisibility(View.VISIBLE);
                holder.reciverMessageText.setBackgroundResource(R.drawable.reciver_message_layout);
                holder.reciverMessageText.setText(message.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessageText, reciverMessageText;
        CircleImageView imageView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            reciverMessageText = itemView.findViewById(R.id.reciver_message_text);
            imageView = itemView.findViewById(R.id.message_profile_image);

        }
    }
}
