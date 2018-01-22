package com.test.kovilapauvaday.prototype_connect.users_amies_profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.test.kovilapauvaday.prototype_connect.R;
import com.test.kovilapauvaday.prototype_connect.messages.MessagesActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.test.kovilapauvaday.prototype_connect.model.GlobalDataSingleton;

public class MesAmiesActivity extends AppCompatActivity {

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

        getSupportActionBar().setTitle("Mes Amies");
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
        FirebaseRecyclerAdapter<User, AmiesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, AmiesViewHolder>(

                User.class,
                R.layout.user_layout,
                AmiesViewHolder.class,
                amiesDatabase

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
                        Intent profileIntent = new Intent(MesAmiesActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("id_envoyeur", userId);
                        startActivity(profileIntent);
                    }
                });
            }

        };


        recyclerView.setAdapter(firebaseRecyclerAdapter);

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
