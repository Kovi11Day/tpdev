package com.test.kovilapauvaday.prototype_connect.messages;

/**
 * Created by ARAM on 13/01/2018.
 */

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.kovilapauvaday.prototype_connect.R;
import com.test.kovilapauvaday.prototype_connect.messages.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int MON_MESSAGE = 0;
    private static final int SON_MESSAGE = 1;

    private List<Message> messagesList;
    private DatabaseReference mUserDatabase;

    final String monId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public MessageAdapter(List<Message> mMessageList) {
        this.messagesList = mMessageList;
    }

    @Override
    public int getItemViewType(int position) {
        if(messagesList.size() > 0){
            if(messagesList.get(position).getFrom().equals(monId)){
                return MON_MESSAGE;
            }else{
                return SON_MESSAGE;
            }
        }
        return super.getItemViewType(position);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType){
            case MON_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mon_message, parent, false);
                return new MonfMessageViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message, parent, false);
                return new UserMessageViewHolder(view);
        }

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {

        final Message message = messagesList.get(i);
        final String from_user = message.getFrom();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(from_user.equals(monId)){
                    ((MonfMessageViewHolder)viewHolder).monMessage.setText(message.getMessage());
                } else {
                    ((UserMessageViewHolder)viewHolder).userMessage.setText(message.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }



    class MonfMessageViewHolder extends RecyclerView.ViewHolder{

        public TextView monMessage;

        public MonfMessageViewHolder(View itemView) {
            super(itemView);

            monMessage = (TextView) itemView.findViewById(R.id.text_mon_message);
        }

    }



    class UserMessageViewHolder extends RecyclerView.ViewHolder{

        public TextView userMessage;

        public UserMessageViewHolder(View itemView) {
            super(itemView);

            userMessage = (TextView) itemView.findViewById(R.id.text_user_message);
        }
    }


}

