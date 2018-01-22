package com.test.kovilapauvaday.prototype_connect.users_amies_profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.test.kovilapauvaday.prototype_connect.HomeActivity;
import com.test.kovilapauvaday.prototype_connect.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    // views
    private TextView profilePseudo, profileNumero;
    private Button buttonEnvoyerDemande, buttonAnnulerDemande;
    private EditText editPseudo;

    // firebase
    private DatabaseReference userDatabase;
    private DatabaseReference amieDemandeDatabase;
    private DatabaseReference amiesDatabase;
    private DatabaseReference monDatabase;
    private DatabaseReference globaleDatabase;


    private String monId;
    private String monPseudo = "";
    private String userPseudo = "";
    private String monNumero = "";
    private String userNumero = "";

    private String amieOuNon;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        final String userId = getIntent().getStringExtra("id_envoyeur");
        monId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        globaleDatabase = FirebaseDatabase.getInstance().getReference();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        amieDemandeDatabase = FirebaseDatabase.getInstance().getReference().child("Amie_Demande");
        amiesDatabase = FirebaseDatabase.getInstance().getReference().child("Amies");
        monDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(monId);

        profilePseudo = (TextView) findViewById(R.id.profile_pseudo);
        profileNumero = (TextView) findViewById(R.id.profile_numero);
        buttonEnvoyerDemande = (Button) findViewById(R.id.profile_envoyer_demande);
        buttonAnnulerDemande = (Button) findViewById(R.id.profile_annule_demande);
        editPseudo = (EditText) findViewById(R.id.profile_pseudo_edit);

        amieOuNon = "non";

        buttonAnnulerDemande.setVisibility(View.INVISIBLE);
        buttonAnnulerDemande.setEnabled(false);

        monDatabase.child("pseudo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                monPseudo = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        monDatabase.child("numero").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                monNumero = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        userDatabase.child("pseudo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userPseudo = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        userDatabase.child("numero").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userNumero = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final String pseudo = dataSnapshot.child("pseudo").getValue().toString();
                String numero = dataSnapshot.child("numero").getValue().toString();

                profilePseudo.setText(pseudo);
                profileNumero.setText(numero);

                toolbar = (Toolbar) findViewById(R.id.profile_app_bar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle(userPseudo);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                if(monId.equals(userId)){
                    buttonEnvoyerDemande.setEnabled(true);
                    buttonEnvoyerDemande.setVisibility(View.VISIBLE);

                    if(! amieOuNon.equals("enregistrer")) {
                        buttonEnvoyerDemande.setText("CHANGER PSEUDO");
                    }

                    buttonEnvoyerDemande.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(! amieOuNon.equals("enregistrer")) {
                                profilePseudo.setVisibility(View.GONE);
                                editPseudo.setVisibility(View.VISIBLE);
                                editPseudo.setText(pseudo);
                                amieOuNon = "enregistrer";
                                buttonEnvoyerDemande.setText("ENREGISTRER");
                            } else {
                                monDatabase.child("pseudo").setValue(editPseudo.getText().toString());
                                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);

                                intent.putExtra("id_envoyeur", "vide");
                                intent.putExtra("latitude", "0");
                                intent.putExtra("longtitude", "0");
                                intent.putExtra("user_pseudo", "vide");
                                intent.putExtra("type_class", "vide");
                                startActivity(intent);
                            }
                        }
                    });
                    buttonAnnulerDemande.setEnabled(false);
                    buttonAnnulerDemande.setVisibility(View.INVISIBLE);
                }


                //--------------- FRIENDS LIST / REQUEST FEATURE -----------------------
                amieDemandeDatabase.child(monId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(userId)){
                            String type_envoie = dataSnapshot.child(userId).child("type_envoie").getValue().toString();

                            if(type_envoie.equals("recu")){
                                //amieOuNon = "req_received";
                                amieOuNon = "recu";
                                buttonEnvoyerDemande.setText("ACCEPTER LA DEMANDE");

                                buttonAnnulerDemande.setVisibility(View.VISIBLE);
                                buttonAnnulerDemande.setEnabled(true);
                            } else if(type_envoie.equals("envoye")) {
                                //amieOuNon = "req_sent";
                                amieOuNon = "envoye";
                                buttonEnvoyerDemande.setText("ANNULET LA DEMANDE");

                                buttonAnnulerDemande.setVisibility(View.INVISIBLE);
                                buttonAnnulerDemande.setEnabled(false);
                            }
                        } else {
                            amiesDatabase.child(monId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(userId)){
                                        //amieOuNon = "friends";
                                        amieOuNon = "oui";
                                        buttonEnvoyerDemande.setText("SUPPRIMER DE AMIES");

                                        buttonAnnulerDemande.setVisibility(View.INVISIBLE);
                                        buttonAnnulerDemande.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonEnvoyerDemande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonEnvoyerDemande.setEnabled(false);

                // --------------- SONT PAS AMIES ------------
                //if(amieOuNon.equals("not_friends")){
                if(amieOuNon.equals("non")){
                    DatabaseReference newNotificationref = globaleDatabase.child("Notifications").child(userId).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("id_envoyeur", monId);
                    notificationData.put("type_class", "ProfileActivity");
                    notificationData.put("latitude", "0");
                    notificationData.put("longtitude", "0");
                    notificationData.put("notification_msg", "Souhaite vous ajouter dans la liste de ces amies");

                    Map requestMap = new HashMap();
                    requestMap.put("Amie_Demande/" + monId + "/" + userId + "/type_envoie", "envoye");
                    requestMap.put("Amie_Demande/" + userId + "/" + monId + "/type_envoie", "recu");
                    requestMap.put("Notifications/" + userId + "/" + newNotificationId, notificationData);

                    globaleDatabase.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Toast.makeText(ProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();
                            } else {
                                //amieOuNon = "req_sent";
                                amieOuNon = "envoye";
                                buttonEnvoyerDemande.setText("ANNULET LA DEMANDE");
                            }
                            buttonEnvoyerDemande.setEnabled(true);
                        }
                    });
                }

                // --------------- ANNULER ENVOIE AMIE ------------
                //if(amieOuNon.equals("req_sent")){
                if(amieOuNon.equals("envoye")){
                    amieDemandeDatabase.child(monId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            amieDemandeDatabase.child(userId).child(monId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    buttonEnvoyerDemande.setEnabled(true);
                                    //amieOuNon = "not_friends";
                                    amieOuNon = "non";
                                    buttonEnvoyerDemande.setText("DEMANDE d'AJOUT AU AMIES");

                                    buttonAnnulerDemande.setVisibility(View.INVISIBLE);
                                    buttonAnnulerDemande.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                // ------------ RECUE AMITIE ----------
                //if(amieOuNon.equals("req_received")){
                if(amieOuNon.equals("recu")){

                    Map friendsMap = new HashMap();
                    friendsMap.put("Amies/" + monId + "/" + userId + "/pseudo", userPseudo);
                    friendsMap.put("Amies/" + monId + "/" + userId + "/numero", userNumero);
                    friendsMap.put("Amies/" + userId + "/"  + monId + "/pseudo", monPseudo);
                    friendsMap.put("Amies/" + userId + "/"  + monId + "/numero", monNumero);

                    friendsMap.put("Amie_Demande/" + monId + "/" + userId, null);
                    friendsMap.put("Amie_Demande/" + userId + "/" + monId, null);

                    globaleDatabase.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null){
                                buttonEnvoyerDemande.setEnabled(true);
                                //amieOuNon = "friends";
                                amieOuNon = "oui";
                                buttonEnvoyerDemande.setText("SUPPRIMER DE AMIES");

                                buttonAnnulerDemande.setVisibility(View.INVISIBLE);
                                buttonAnnulerDemande.setEnabled(false);
                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // ------------ ENLEVET DE LISTE AMIES ---------
                //if(amieOuNon.equals("friends")){
                if(amieOuNon.equals("oui")){
                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Amies/" + monId + "/" + userId, null);
                    unfriendMap.put("Amies/" + userId + "/" + monId, null);

                    globaleDatabase.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null){
                                //amieOuNon = "not_friends";
                                amieOuNon = "non";
                                buttonEnvoyerDemande.setText("DEMANDE d'AJOUT AU AMIES");

                                buttonAnnulerDemande.setVisibility(View.INVISIBLE);
                                buttonAnnulerDemande.setEnabled(false);
                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            buttonEnvoyerDemande.setEnabled(true);
                        }
                    });

                }
            }
        });


    }


}
