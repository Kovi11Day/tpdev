package com.test.kovilapauvaday.prototype_connect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.kovilapauvaday.prototype_connect.R;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView profilePseudo, profileNumero;
    private Button buttonEnvoyerDemande, buttonAnnulerDemande;

    private DatabaseReference mUsersDatabase;

    //private ProgressDialog mProgressDialog;

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;

    private DatabaseReference mRootRef;

    private String userCourantId;

    private String amieOuNon;


    //////////////////////////////////////////////////////////////////////////////:
    private DatabaseReference monDatabase;
    private DatabaseReference userDatabase;
    private String monPseudo = "";
    private String userPseudo = "";
    private String monNumero = "";
    private String userNumero = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        for(String key : getIntent().getExtras().keySet()){
            Log.i("Profile", "key: " + key);
        }

        final String user_id = getIntent().getStringExtra("from_user_id");

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        userCourantId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        profilePseudo = (TextView) findViewById(R.id.profile_pseudo);
        profileNumero = (TextView) findViewById(R.id.profile_numero);
        buttonEnvoyerDemande = (Button) findViewById(R.id.profile_envoyer_demande);
        buttonAnnulerDemande = (Button) findViewById(R.id.profile_annule_demande);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //final String click_action = getIntent().getStringExtra("click_action");
        //final String from_user_id = getIntent().getStringExtra("from_user_id");
        //Toast.makeText(ProfileActivity.this, user_id + "\n" + click_action, Toast.LENGTH_LONG).show();

        amieOuNon = "not_friends";

        buttonAnnulerDemande.setVisibility(View.INVISIBLE);
        buttonAnnulerDemande.setEnabled(false);


        /*mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();*/

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        monDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userCourantId);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

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


        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String pseudo = dataSnapshot.child("pseudo").getValue().toString();
                String numero = dataSnapshot.child("numero").getValue().toString();

                profilePseudo.setText(pseudo);
                profileNumero.setText(numero);

                /*if(mCurrent_user.getUid().equals(user_id)){

                    mDeclineBtn.setEnabled(false);
                    mDeclineBtn.setVisibility(View.INVISIBLE);

                    mProfileSendReqBtn.setEnabled(false);
                    mProfileSendReqBtn.setVisibility(View.INVISIBLE);

                }*/


                //--------------- FRIENDS LIST / REQUEST FEATURE -----

                mFriendReqDatabase.child(userCourantId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received")){

                                amieOuNon = "req_received";
                                buttonEnvoyerDemande.setText("ACCEPTER LA DEMANDE");

                                buttonAnnulerDemande.setVisibility(View.VISIBLE);
                                buttonAnnulerDemande.setEnabled(true);


                            } else if(req_type.equals("sent")) {

                                amieOuNon = "req_sent";
                                buttonEnvoyerDemande.setText("ANNULET LA DEMANDE");

                                buttonAnnulerDemande.setVisibility(View.INVISIBLE);
                                buttonAnnulerDemande.setEnabled(false);

                            }

                            //mProgressDialog.dismiss();


                        } else {


                            mFriendDatabase.child(userCourantId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){

                                        amieOuNon = "friends";
                                        buttonEnvoyerDemande.setText("SUPPRIMER DE AMIES");

                                        buttonAnnulerDemande.setVisibility(View.INVISIBLE);
                                        buttonAnnulerDemande.setEnabled(false);

                                    }

                                    //mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    //mProgressDialog.dismiss();

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

                if(amieOuNon.equals("not_friends")){

                    /*HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("idEnv", id);//BD2.getInstance().ID);
                    notificationData.put("cordonnes", id);*/

                    DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("idEnv", userCourantId);
                    notificationData.put("typeClass", "ProfileActivity");
                    notificationData.put("latitude", "0");
                    notificationData.put("longtitude", "0");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + userCourantId + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friend_req/" + user_id + "/" + userCourantId + "/request_type", "received");
                    requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){

                                Toast.makeText(ProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                            } else {

                                amieOuNon = "req_sent";
                                buttonEnvoyerDemande.setText("ANNULET LA DEMANDE");

                            }

                            buttonEnvoyerDemande.setEnabled(true);


                        }
                    });

                }


                // - -------------- ANNULER ENVOIE AMIE ------------
                if(amieOuNon.equals("req_sent")){

                    mFriendReqDatabase.child(userCourantId).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqDatabase.child(user_id).child(userCourantId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    buttonEnvoyerDemande.setEnabled(true);
                                    amieOuNon = "not_friends";
                                    buttonEnvoyerDemande.setText("DEMANDE d'AJOUT AU AMIES");

                                    buttonAnnulerDemande.setVisibility(View.INVISIBLE);
                                    buttonAnnulerDemande.setEnabled(false);


                                }
                            });

                        }
                    });

                }


                // ------------ RECUE AMITIE ----------

                if(amieOuNon.equals("req_received")){

                    //final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + userCourantId + "/" + user_id + "/pseudo", userPseudo);
                    friendsMap.put("Friends/" + userCourantId + "/" + user_id + "/numero", userNumero);
                    friendsMap.put("Friends/" + user_id + "/"  + userCourantId + "/pseudo", monPseudo);
                    friendsMap.put("Friends/" + user_id + "/"  + userCourantId + "/numero", monNumero);

                    friendsMap.put("Friend_req/" + userCourantId + "/" + user_id, null);
                    friendsMap.put("Friend_req/" + user_id + "/" + userCourantId, null);


                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){

                                buttonEnvoyerDemande.setEnabled(true);
                                amieOuNon = "friends";
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

                if(amieOuNon.equals("friends")){

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + userCourantId + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + userCourantId, null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){

                                amieOuNon = "not_friends";
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
