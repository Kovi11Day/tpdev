package com.test.kovilapauvaday.prototype_connect.messages;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.test.kovilapauvaday.prototype_connect.R;
import com.test.kovilapauvaday.prototype_connect.users_amies_profile.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MessagesActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView recyclerView;

    private DatabaseReference amiesDatabase;

    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amies_activity);

        toolbar = (Toolbar) findViewById(R.id.messages_appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String monId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        amiesDatabase = FirebaseDatabase.getInstance().getReference().child("Amies").child(monId);

        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.amies_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<User, MessagesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, MessagesViewHolder>(

                User.class,
                R.layout.user_layout,
                MessagesViewHolder.class,
                amiesDatabase
        ) {
            @Override
            protected void populateViewHolder(MessagesViewHolder viewHolder, User model, int position) {
                final String pseudo = model.getPseudo();
                final String numero = model.getNumero();
                viewHolder.setPseudo(pseudo);
                viewHolder.setNumero(numero);

                final String userId = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(MessagesActivity.this, ChatActivity.class);
                        profileIntent.putExtra("id_envoyeur", userId);
                        profileIntent.putExtra("user_pseudo", pseudo);
                        profileIntent.putExtra("latitude", "0");
                        profileIntent.putExtra("longtitude", "0");
                        startActivity(profileIntent);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    public static class MessagesViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MessagesViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setPseudo(String pseudo){
            TextView userPseudo = (TextView) mView.findViewById(R.id.user_pseudo);
            userPseudo.setText(pseudo);
        }

        public void setNumero(String numero){
            TextView userNumero = (TextView) mView.findViewById(R.id.user_numero);
            userNumero.setText(numero);
        }
    }
}