package com.test.kovilapauvaday.prototype_connect;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.test.kovilapauvaday.prototype_connect.model.User;


public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    private LinearLayoutManager mLayoutManager;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private DatabaseReference mRootRef;
    private LocalisationGPS localisationGPS;
    private Location mLocation;
    private double latitude;
    private double longtitude;

    //private ImageButton ibEnvoieLocalisation;
    //private ImageButton ibEnvoieAmitie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_activity);

        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Tous les utilisateurs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mRootRef = FirebaseDatabase.getInstance().getReference();//////////////////////////////////////

        mLayoutManager = new LinearLayoutManager(this);

        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //////////////////////////////////////////////////////////////////////////////////////////////////
        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(

                User.class,
                R.layout.user_layout,
                UsersViewHolder.class,
                mUsersDatabase

        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, User model, int position) {
                viewHolder.setPseudo(model.getPseudo());
                viewHolder.setNumero(model.getNumero());

                final String userId = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("from_user_id", userId);
                        startActivity(profileIntent);

                    }
                });
            }

        };


        mUsersList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
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
