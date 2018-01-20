package com.test.kovilapauvaday.prototype_connect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.test.kovilapauvaday.prototype_connect.model.User;


public class MesAmiesActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    private LinearLayoutManager mLayoutManager;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mes_amies_activity);

        mToolbar = (Toolbar) findViewById(R.id.amies_appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Mes amies");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String monId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(monId);



        mLayoutManager = new LinearLayoutManager(this);

        mUsersList = (RecyclerView) findViewById(R.id.amies_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<User, AmiesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, AmiesViewHolder>(

                User.class,
                R.layout.user_layout,
                AmiesViewHolder.class,
                mUsersDatabase

        ) {
            @Override
            protected void populateViewHolder(AmiesViewHolder viewHolder, User model, int position) {
                final String pseudo = model.getPseudo();
                final String numero = model.getNumero();
                viewHolder.setPseudo(pseudo);
                viewHolder.setNumero(numero);

                final String userId = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(MesAmiesActivity.this, ChatActivity.class);
                        profileIntent.putExtra("from_user_id", userId);
                        profileIntent.putExtra("user_pseudo", pseudo);
                        profileIntent.putExtra("latitude", "0");
                        profileIntent.putExtra("longtitude", "0");
                        startActivity(profileIntent);
                    }
                });
            }

        };


        mUsersList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class AmiesViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public AmiesViewHolder(View itemView) {
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