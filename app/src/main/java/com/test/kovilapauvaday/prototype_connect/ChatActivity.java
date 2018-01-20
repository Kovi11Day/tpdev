package com.test.kovilapauvaday.prototype_connect;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.test.kovilapauvaday.prototype_connect.maps.LocalisationGPS;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private Toolbar mChatToolbar;
    private String userId;
    private String userPseudo;

    private LocalisationGPS localisationGPS;
    private Location mLocation;
    private double latitudeEnvoyeur;
    private double longtitudeEnvoyeur;

    private DatabaseReference mRootRef;

    private ImageButton buttonLocalisation;

    private String monId;
    private String newNotificationId;
    private String latitudeRecepteur;
    private String longtitudeRecepteur;


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    private FirebaseAuth mAuth;

    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;

    private MessageAdapter mAdapter;
    private final List<Messages> messagesList = new ArrayList<>();
    private RecyclerView mMessagesList;

    private SwipeRefreshLayout mRefreshLayout;
    private LinearLayoutManager mLinearLayout;

    private int mCurrentPage = 1;
    private int itemPos = 0;
    private static final int GALLERY_PICK = 1;

    private String mLastKey = "";
    private String mPrevKey = "";
    private static final int TOTAL_ITEMS_TO_LOAD = 10;


    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        // recuperations
        userId = getIntent().getStringExtra("from_user_id");
        userPseudo = getIntent().getStringExtra("user_pseudo");
        latitudeRecepteur = getIntent().getStringExtra("latitude");
        longtitudeRecepteur = getIntent().getStringExtra("longtitude");

        Log.i("#################################################### ChatActivity", "latitude : " + latitudeRecepteur);
        Log.i("#################################################### ChatActivity", "longtitude : " + longtitudeRecepteur);
        Log.i("#################################################### ChatActivity", "user_pseudo : " + userPseudo);
        Log.i("#################################################### ChatActivity", "from_user_id : " + userId);

        // toolbar
        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);
        getSupportActionBar().setTitle(userPseudo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // firebase
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        monId = mCurrentUser.getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        // localisation
       /* localisationGPS = new LocalisationGPS(getApplicationContext());
        mLocation = localisationGPS.getLocation();
        latitudeEnvoyeur = mLocation.getLatitude();
        longtitudeEnvoyeur = mLocation.getLongitude();*/

        Log.i("HomeActivity", "latitude : " + latitudeRecepteur);
        Log.i("HomeActivity", "longtitude : " + longtitudeRecepteur);


        if((! latitudeRecepteur.equals("0")) && (! longtitudeRecepteur.equals("0"))) {

            creatAlertDialog();
        }



        buttonLocalisation = (ImageButton) findViewById(R.id.button_localisation);
        buttonLocalisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    localisationGPS = new LocalisationGPS(getApplicationContext());
                    mLocation = localisationGPS.getLocation();
                    latitudeEnvoyeur = mLocation.getLatitude();
                    longtitudeEnvoyeur = mLocation.getLongitude();
                }catch(Exception e){
                    Toast.makeText(getBaseContext(), "activer votre localisation", Toast.LENGTH_LONG).show();
                }

                DatabaseReference newNotificationref = mRootRef.child("notifications").child(userId).push();
                newNotificationId = newNotificationref.getKey();


                HashMap<String, String> notificationData = new HashMap<>();
                notificationData.put("idEnv", monId);//BD2.getInstance().ID);
                notificationData.put("typeClass", "ChatActivity");
                notificationData.put("latitude", latitudeEnvoyeur +"");
                notificationData.put("longtitude", longtitudeEnvoyeur +"");


                Map requestMapNotifications = new HashMap();
                requestMapNotifications.put("notifications/" + userId + "/" + newNotificationId, notificationData);

                mRootRef.updateChildren(requestMapNotifications, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    }
                });
            }
        });



        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        /*View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);
        mTitleView = (TextView) findViewById(R.id.custom_bar_title);
        mLastSeenView = (TextView) findViewById(R.id.custom_bar_seen);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);*/

        mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessageView = (EditText) findViewById(R.id.chat_message_view);

        mAdapter = new MessageAdapter(messagesList);

        mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);

        //------- IMAGE STORAGE ---------

        mRootRef.child("Chat").child(monId).child(userId).child("seen").setValue(true);

        loadMessages();


        mRootRef.child("Chat").child(monId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(userId)){

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + monId + "/" + userId, chatAddMap);
                    chatUserMap.put("Chat/" + userId + "/" + monId, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
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



        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();

            }
        });


        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                itemPos = 0;

                loadMessages();//loadMoreMessages();


            }
        });



    }




    private void loadMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(monId).child(userId);

        Query messageQuery = messageRef.limitToLast(100);//mCurrentPage * TOTAL_ITEMS_TO_LOAD);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                itemPos++;

                if(itemPos == 1){

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(messagesList.size() - 1);

                mRefreshLayout.setRefreshing(false);

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


        String message = mChatMessageView.getText().toString();

        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + monId + "/" + userId;
            String chat_user_ref = "messages/" + userId + "/" + monId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(monId).child(userId).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", monId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mChatMessageView.setText("");

            mRootRef.child("Chat").child(monId).child(userId).child("seen").setValue(true);
            mRootRef.child("Chat").child(monId).child(userId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.child("Chat").child(userId).child(monId).child("seen").setValue(false);
            mRootRef.child("Chat").child(userId).child(monId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                }
            });

        }

    }






    private void creatAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChatActivity.this);
        alertDialogBuilder.setTitle("Autorise a " + userPseudo + " avoir votre position ?");
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Oui",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        DatabaseReference newNotificationrefDialog = mRootRef.child("notifications").child(userId).push();
                        newNotificationId = newNotificationrefDialog.getKey();

                        HashMap<String, String> notificationData = new HashMap<>();
                        notificationData.put("idEnv", monId);//BD2.getInstance().ID);
                        notificationData.put("typeClass", "HomeActivity");
                        notificationData.put("latitude", latitudeEnvoyeur +"");
                        notificationData.put("longtitude", longtitudeEnvoyeur +"");

                        Map requestMapNotifications = new HashMap();
                        requestMapNotifications.put("notifications/" + userId + "/" + newNotificationId, notificationData);

                        mRootRef.updateChildren(requestMapNotifications, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            }
                        });

                        Intent intentHome = new Intent(ChatActivity.this, HomeActivity.class);
                        intentHome.putExtra("latitude", latitudeRecepteur);
                        intentHome.putExtra("longtitude", longtitudeRecepteur);
                        intentHome.putExtra("user_pseudo", userPseudo);
                        intentHome.putExtra("from_user_id", "vide");
                        intentHome.putExtra("type_class", "vide");
                        startActivity(intentHome);
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

    /*private void loadMoreMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(monId).child(userId);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){

                    messagesList.add(itemPos++, message);

                } else {

                    mPrevKey = mLastKey;

                }


                if(itemPos == 1) {

                    mLastKey = messageKey;

                }


                Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);

                mAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10, 0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }*/



}
