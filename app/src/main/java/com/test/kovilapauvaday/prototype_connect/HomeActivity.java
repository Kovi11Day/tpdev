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
import com.test.kovilapauvaday.prototype_connect.model.GlobalDataSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    //google-maps et firebase
    private GoogleMap mMap;
    private LocalisationGPS localisationGPS;
    private FloatingActionButton fab;
    private Location mLocation;

    private double latitude = 0;
    private double longtitude = 0;
    private String user_pseudo = "vide";

    private String from_user_id = "vide";
    private String type_class = "vide";
    private Intent resultIntent = null;
    private String lat = "0";
    private String lon = "0";

    private static int MY_LOCATION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    //facebook
    Intent intent_contacts;
    String id;
    TextView txtTestProfile;
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

        //get facebook information from intent
        this.intent_contacts = new Intent(this, ContactsActivity.class);
        this.facebook_mode = getIntent().getBooleanExtra(FBK_MODE, false);
        if(this.facebook_mode)
         this.id = getIntent().getStringExtra(KEY_ID);


        lat = getIntent().getStringExtra("latitude");
        lon = getIntent().getStringExtra("longtitude");
        user_pseudo = getIntent().getStringExtra("user_pseudo");

        from_user_id = getIntent().getStringExtra("from_user_id");
        type_class = getIntent().getStringExtra("type_class");

        Log.i("HomeActivity", "latitude : " + lat);
        Log.i("HomeActivity", "longtitude : " + lon);
        Log.i("HomeActivity", "user_pseudo : " + user_pseudo);
        Log.i("HomeActivity", "from_user_id : " + from_user_id);
        Log.i("HomeActivity", "type_class : " + type_class);

        if (lat == null && lon == null) {
            latitude = 0;
            longtitude = 0;
        } else if ((!lat.equals("0")) && (!lon.equals("0"))) {
            latitude = (new Double(lat)).doubleValue();
            longtitude = (new Double(lon)).doubleValue();
        }

        if(user_pseudo == null && from_user_id == null && type_class == null) {
            user_pseudo = "vide";
            from_user_id = "vide";
            type_class = "vide";
        }

        if((! user_pseudo.equals("vide")) && (! from_user_id.equals("vide")) && (! type_class.equals("vide"))) {


            if (type_class.equals("ProfileActivity")) {
                resultIntent = new Intent(this, ProfileActivity.class);

                resultIntent.putExtra("from_user_id", from_user_id);

                startActivity(resultIntent);
            } else if (type_class.equals("ChatActivity")) {
                resultIntent = new Intent(this, ChatActivity.class);

                resultIntent.putExtra("from_user_id", from_user_id);
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

        //google-maps
        //latitude = getIntent().getDoubleExtra("ami_latitude", 0.0);
        //longtitude = getIntent().getDoubleExtra("ami_longtitude", 0.0);
        //pseudo = getIntent().getStringExtra("ami_pseudo");
        //Toast.makeText(HomeActivity.this, pseudo + "\n" + latitude + "\n" + longtitude, Toast.LENGTH_LONG).show();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //recuperer amies facebook
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
        } /*else if (id == R.id.carte) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }*/ else if (id == R.id.mes_amies) {
            Intent intent = new Intent(this, MesAmiesActivity.class);
            startActivity(intent);
        } else if (id == R.id.messages) {

        }else if (id == R.id.parameters) {
            Intent intent = new Intent(this, ParametersActivity.class);
            startActivity(intent);
        } else if (id == R.id.sortie) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            //String iduser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Intent intent = new Intent(this, MainActivity.class);
            //intent.putExtra("user_id", iduser);
            startActivity(intent);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                // Add a marker in Sydney and move the camera
                if(latitude != 0 && longtitude != 0) {
                    LatLng sydney = new LatLng(latitude, longtitude);
                    mMap.addMarker(new MarkerOptions().position(sydney).title(user_pseudo));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                }
            } else {
                // Show rationale and request permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                Toast.makeText(HomeActivity.this, "Aller dans parrametres et donnez autorisation pour localisation !!!", Toast.LENGTH_LONG).show();

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(HomeActivity.this, "Pas de autorisation", Toast.LENGTH_LONG).show();

            }
        }
    }

}
