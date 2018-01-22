package com.test.kovilapauvaday.prototype_connect.messages;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.test.kovilapauvaday.prototype_connect.DonnesAmie;
import com.test.kovilapauvaday.prototype_connect.HomeActivity;
import com.test.kovilapauvaday.prototype_connect.R;
import com.test.kovilapauvaday.prototype_connect.LocalisationGPS;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final int NB_MESSAGES_A_MONTRE = 20;

    // veiws
    private ImageButton buttonLocalisation;
    private ImageButton buttonEnvoieMessage;
    private EditText editMessage;

    // localisation
    private LocalisationGPS localisationGPS;
    private Location location;
    private double latitudeEnvoyeur;
    private double longtitudeEnvoyeur;

    // firebase
    private DatabaseReference globaleDatabase;

    // messages adapter
    private MessageAdapter messageAdapter;
    private final List<Message> messagesList = new ArrayList<>();
    private RecyclerView recyclerViewMessages;

    // rafraichir messages
    private SwipeRefreshLayout rafraichirLayoutMessages;
    private LinearLayoutManager linearLayoutMessages;
    private int numeroPageMessage = 1;
    private int itemPos = 0;

    // les donnes de database
    private String userId;
    private String userPseudo;
    private String monId;
    private String newNotificationId;
    private String latitudeRecepteur;
    private String longtitudeRecepteur;

    private String dernierKey = "";
    private String precedentKey = "";

    private Toolbar toolbar;


    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        // recuperations
        userId = getIntent().getStringExtra("id_envoyeur");
        userPseudo = getIntent().getStringExtra("user_pseudo");
        latitudeRecepteur = getIntent().getStringExtra("latitude");
        longtitudeRecepteur = getIntent().getStringExtra("longtitude");

        // toolbar
        toolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(userPseudo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        monId = currentUser.getUid();
        globaleDatabase = FirebaseDatabase.getInstance().getReference();

        if((! latitudeRecepteur.equals("0")) && (! longtitudeRecepteur.equals("0"))) {
            creatAlertDialog();
        }

        buttonLocalisation = (ImageButton) findViewById(R.id.button_localisation);
        buttonLocalisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    // pour etre sur que gps est active
                    localisationGPS = new LocalisationGPS(ChatActivity.this);
                    location = localisationGPS.getLocation();
                    latitudeEnvoyeur = location.getLatitude();
                    longtitudeEnvoyeur = location.getLongitude();

                DatabaseReference newNotificationref = globaleDatabase.child("Notifications").child(userId).push();
                newNotificationId = newNotificationref.getKey();

                HashMap<String, String> notificationData = new HashMap<>();
                notificationData.put("id_envoyeur", monId);//BD2.getInstance().ID);
                notificationData.put("type_class", "ChatActivity");
                notificationData.put("latitude", latitudeEnvoyeur +"");
                notificationData.put("longtitude", longtitudeEnvoyeur +"");
                notificationData.put("notification_msg", "Veux savoir votre place");


                Map requestMapNotifications = new HashMap();
                requestMapNotifications.put("Notifications/" + userId + "/" + newNotificationId, notificationData);
                globaleDatabase.updateChildren(requestMapNotifications, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    }
                });
                Toast.makeText(ChatActivity.this, "Vous avez envoyer la demande.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(ChatActivity.this, "Activer le localisation !!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        buttonEnvoieMessage = (ImageButton) findViewById(R.id.button_envoie_message);
        editMessage = (EditText) findViewById(R.id.edit_message);

        messageAdapter = new MessageAdapter(messagesList);

        // parametrage d'dapter
        recyclerViewMessages = (RecyclerView) findViewById(R.id.messages_list);
        rafraichirLayoutMessages = (SwipeRefreshLayout) findViewById(R.id.rafraichir_messages);
        linearLayoutMessages = new LinearLayoutManager(this);
        recyclerViewMessages.setHasFixedSize(true);
        recyclerViewMessages.setLayoutManager(linearLayoutMessages);
        recyclerViewMessages.setAdapter(messageAdapter);

        globaleDatabase.child("Chat").child(monId).child(userId).child("vu").setValue(true);

        // charge les dernier NB_MESSAGES_A_MONTRE messages
        loadDernierMessages();

        globaleDatabase.child("Chat").child(monId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(userId)){
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("vu", false);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + monId + "/" + userId, chatAddMap);
                    chatUserMap.put("Chat/" + userId + "/" + monId, chatAddMap);

                    globaleDatabase.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonEnvoieMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        rafraichirLayoutMessages.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                numeroPageMessage++;
                itemPos = 0;
                loadAncienMessages();
            }
        });
    }


    private void loadAncienMessages() {
        DatabaseReference messageRef = globaleDatabase.child("Messages").child(monId).child(userId);
        Query messageQuery = messageRef.orderByKey().endAt(dernierKey).limitToLast(NB_MESSAGES_A_MONTRE);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                String messageKey = dataSnapshot.getKey();

                if(!precedentKey.equals(messageKey)){
                    messagesList.add(itemPos++, message);
                } else {
                    precedentKey = dernierKey;
                }

                if(itemPos == 1) {
                    dernierKey = messageKey;
                }

                messageAdapter.notifyDataSetChanged();
                rafraichirLayoutMessages.setRefreshing(false);
                linearLayoutMessages.scrollToPositionWithOffset(NB_MESSAGES_A_MONTRE, 0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {            }
            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });
    }


    private void loadDernierMessages() {
        DatabaseReference messageRef = globaleDatabase.child("Messages").child(monId).child(userId);
        Query messageQuery = messageRef.limitToLast(numeroPageMessage * NB_MESSAGES_A_MONTRE);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                itemPos++;

                if(itemPos == 1){
                    String messageKey = dataSnapshot.getKey();
                    dernierKey = messageKey;
                    precedentKey = messageKey;
                }

                messagesList.add(message);
                messageAdapter.notifyDataSetChanged();

                recyclerViewMessages.scrollToPosition(messagesList.size() - 1);
                rafraichirLayoutMessages.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {        }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {            }
            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });
    }

    private void sendMessage() {
        String message = editMessage.getText().toString();

        if(! TextUtils.isEmpty(message)){
            String mesMessagesRef = "Messages/" + monId + "/" + userId;
            String userMessagesRef = "Messages/" + userId + "/" + monId;

            DatabaseReference messageChemin = globaleDatabase.child("Messages")
                    .child(monId).child(userId).push();

            String messageCheminKey = messageChemin.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("from", monId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(mesMessagesRef + "/" + messageCheminKey, messageMap);
            messageUserMap.put(userMessagesRef + "/" + messageCheminKey, messageMap);

            editMessage.setText("");

            globaleDatabase.child("Chat").child(monId).child(userId).child("vu").setValue(true);
            globaleDatabase.child("Chat").child(userId).child(monId).child("vu").setValue(false);

            globaleDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                }
            });
        }
    }


    private void creatAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChatActivity.this);
        alertDialogBuilder.setTitle("Autorisé a " + userPseudo + " avoir votre position ?");
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Oui",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        try {
                            // pour etre sur que gps est active
                            localisationGPS = new LocalisationGPS(ChatActivity.this);
                            location = localisationGPS.getLocation();
                            latitudeEnvoyeur = location.getLatitude();
                            longtitudeEnvoyeur = location.getLongitude();

                            DatabaseReference newNotificationrefDialog = globaleDatabase.child("Notifications").child(userId).push();
                            newNotificationId = newNotificationrefDialog.getKey();

                            HashMap<String, String> notificationData = new HashMap<>();
                            notificationData.put("id_envoyeur", monId);
                            notificationData.put("type_class", "HomeActivity");
                            notificationData.put("latitude", latitudeEnvoyeur + "");
                            notificationData.put("longtitude", longtitudeEnvoyeur + "");
                            notificationData.put("notification_msg", "Accepte votre demande de localisation");

                            Map requestMapNotifications = new HashMap();
                            requestMapNotifications.put("Notifications/" + userId + "/" + newNotificationId, notificationData);

                            globaleDatabase.updateChildren(requestMapNotifications, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                }
                            });

                            Intent intentHome = new Intent(ChatActivity.this, HomeActivity.class);
                            intentHome.putExtra("latitude", latitudeRecepteur);
                            intentHome.putExtra("longtitude", longtitudeRecepteur);
                            intentHome.putExtra("user_pseudo", userPseudo);
                            intentHome.putExtra("id_envoyeur", "vide");
                            intentHome.putExtra("type_class", "vide");
                            startActivity(intentHome);

                            DonnesAmie.latitude = latitudeRecepteur;
                            DonnesAmie.longtitude = longtitudeRecepteur;

                        }  catch (Exception e) {
                            Toast.makeText(ChatActivity.this, "Activer le localisation !!!", Toast.LENGTH_LONG).show();
                        }
                    }

                })
                .setNegativeButton("Non",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
