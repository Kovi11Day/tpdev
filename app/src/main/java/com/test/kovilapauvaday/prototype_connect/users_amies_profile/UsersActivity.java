package com.test.kovilapauvaday.prototype_connect.users_amies_profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.test.kovilapauvaday.prototype_connect.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.test.kovilapauvaday.prototype_connect.model.GlobalDataSingleton;


public class UsersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference usersDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_activity);

        toolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tous les utilisateurs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.users_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                User.class,
                R.layout.user_layout,
                UsersViewHolder.class,
                usersDatabase

        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, User model, int position) {
                viewHolder.setPseudo(model.getPseudo());
                viewHolder.setNumero(model.getNumero());
                final String userId = getRef(position).getKey();

                model.setFirebaseId(userId);
                GlobalDataSingleton.getInstance().addFireBaseUser(model);

                Log.v("id:", userId);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("id_envoyeur", userId);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
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
