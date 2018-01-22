package com.test.kovilapauvaday.prototype_connect;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.test.kovilapauvaday.prototype_connect.messages.ChatActivity;
import com.test.kovilapauvaday.prototype_connect.messages.MessagesActivity;
import com.test.kovilapauvaday.prototype_connect.model.GlobalDataSingleton;
import com.test.kovilapauvaday.prototype_connect.users_amies_profile.MesAmiesActivity;
import com.test.kovilapauvaday.prototype_connect.users_amies_profile.ProfileActivity;
import com.test.kovilapauvaday.prototype_connect.users_amies_profile.UsersActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    //google-maps et firebase
    private GoogleMap mMap;
    private LocalisationGPS localisationGPS;
    private Location mLocation;
    private double latitude = 0;
    private double longtitude = 0;

    //données reservés pour la notification
    private String user_pseudo = "vide";
    private String id_envoyeur = "vide";
    private String type_class = "vide";
    private String lat = "0";
    private String lon = "0";

    private Intent resultIntent = null;

    //données utiliser pour fonctionnalités facebook
    Intent intent_contacts;
    String id;
    GlobalDataSingleton model = GlobalDataSingleton.getInstance();
    public static final String KEY_ID = "KEY_ID";
    public static final String  FBK_MODE= "FBK_MODE";
    Boolean facebook_mode;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //reçoit les données de notification en mode background
        lat = getIntent().getStringExtra("latitude");
        lon = getIntent().getStringExtra("longtitude");
        user_pseudo = getIntent().getStringExtra("user_pseudo");
        id_envoyeur = getIntent().getStringExtra("id_envoyeur");
        type_class = getIntent().getStringExtra("type_class");

        //get facebook information from intent
        this.intent_contacts = new Intent(this, ContactsActivity.class);
        this.facebook_mode = getIntent().getBooleanExtra(FBK_MODE, false);
        if(this.facebook_mode)
         this.id = getIntent().getStringExtra(KEY_ID);

        // pour savoir si la localisation est alumée ou non
        try{
            localisationGPS = new LocalisationGPS(HomeActivity.this);
            mLocation = localisationGPS.getLocation();
            double lt = mLocation.getLatitude();
            double lo = mLocation.getLongitude();
        } catch (Exception e) {
            Toast.makeText(HomeActivity.this, "Activez la localisation !!!", Toast.LENGTH_LONG).show();
        }

        if (lat == null && lon == null) {
            latitude = 0;
            longtitude = 0;
            latitude = (new Double(DonnesAmie.latitude)).doubleValue();
            longtitude = (new Double(DonnesAmie.longtitude)).doubleValue();
        } else if ((!lat.equals("0")) && (!lon.equals("0"))) {
            latitude = (new Double(lat)).doubleValue();
            longtitude = (new Double(lon)).doubleValue();
            DonnesAmie.latitude = lat;
            DonnesAmie.longtitude = lon;
            DonnesAmie.pseudo = user_pseudo;
        }

        if(user_pseudo == null && id_envoyeur == null && type_class == null) {
           user_pseudo = DonnesAmie.pseudo;
           id_envoyeur = "vide";
           type_class = "vide";
        }

        if((! user_pseudo.equals("vide")) && (! id_envoyeur.equals("vide")) && (! type_class.equals("vide"))) {
            //va à l'activité qui a été envoyé par la notification
            if (type_class.equals("ProfileActivity")) {
                resultIntent = new Intent(this, ProfileActivity.class);

                resultIntent.putExtra("id_envoyeur", id_envoyeur);

                startActivity(resultIntent);
            } else if (type_class.equals("ChatActivity")) {
                resultIntent = new Intent(this, ChatActivity.class);
                resultIntent.putExtra("id_envoyeur", id_envoyeur);
                resultIntent.putExtra("user_pseudo", user_pseudo);
                resultIntent.putExtra("latitude", lat);
                resultIntent.putExtra("longtitude", lon);

                startActivity(resultIntent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //recuperer les amies de facebook
        if( facebook_mode) {
            AccessToken.getCurrentAccessToken();
            String graphPath = "/me/friends";

            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    graphPath,
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            StringBuffer buff = new StringBuffer();
                            JSONArray friends = null;
                            try {
                                friends = response.getJSONObject().getJSONArray("data");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            for (int i = 0; i < friends.length(); i++) {
                                try {
                                    model.addFriend(friends.getJSONObject(i).getString("name"),
                                            friends.getJSONObject(i).getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    }
            ).executeAsync();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.contacts) {
            Intent intent = new Intent(this, UsersActivity.class);
            startActivity(intent);
        }  else if (id == R.id.mes_amies) {
            Intent intent = new Intent(this, MesAmiesActivity.class);
            startActivity(intent);
        } else if (id == R.id.messages) {
            Intent intent = new Intent(this, MessagesActivity.class);
            startActivity(intent);
        }else if (id == R.id.parameters) {
            String iduser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("id_envoyeur", iduser);
            startActivity(intent);
        } else if (id == R.id.sortie) {
            FirebaseAuth.getInstance().signOut();

            if(this.facebook_mode)
                LoginManager.getInstance().logOut();

            Intent sortieIntent = new Intent(this, MainActivity.class);
            startActivity(sortieIntent);
            finish();
        }else if (id == R.id.nav_contacts) {
            startActivity(this.intent_contacts);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                // montrer l'amie
                if(latitude != 0 && longtitude != 0) {
                    LatLng sydney = new LatLng(latitude, longtitude);
                    mMap.addMarker(new MarkerOptions().position(sydney).title(user_pseudo));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                }
            } else {
                // Quand il n'y a pas de permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);
                Toast.makeText(HomeActivity.this,
                        "Allez dans les paramètres et donnez l'autorisation pour la localisation !!!", Toast.LENGTH_LONG).show();

            }
        }
    }

}
