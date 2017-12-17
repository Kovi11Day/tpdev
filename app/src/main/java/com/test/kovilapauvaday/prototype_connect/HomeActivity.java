package com.test.kovilapauvaday.prototype_connect;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.test.kovilapauvaday.prototype_connect.model.GlobalDataSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
     Intent intent_contacts;
     String id;
    TextView txtTestProfile;
    GlobalDataSingleton model = GlobalDataSingleton.getInstance();
    public static final String KEY_ID = "KEY_ID";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.intent_contacts = new Intent(this, ContactsActivity.class);
        //String id = ((this.intent_contacts.getExtras()).getString(Intent.EXTRA_TEXT));
        this.id = getIntent().getStringExtra(KEY_ID);
         this.txtTestProfile = findViewById(R.id.textview_test_profilename);
        txtTestProfile.setText("init");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        AccessToken.getCurrentAccessToken();

        //get facebook contacts
        //String graphPath = "/{" + "me" + "}/friendlists";
        String graphPath = "/me/friends";

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                graphPath,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                /* handle the result */
                /*try {

                }catch (JSONException e){
                    e.printStackTrace();
                }*/
                        StringBuffer buff  = new StringBuffer();
                        JSONArray friends = null;
                        try {
                            friends = response.getJSONObject().getJSONArray("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for(int i =0;i< friends.length(); i++){
                            try {
                                /*buff.append("friend " + i + ": "
                                        +"name: "
                                        + friends.getJSONObject(i).getString("name")
                                        + "id: "
                                        + friends.getJSONObject(i).getString("id")
                                );*/
                                model.addFriend(friends.getJSONObject(i).getString("name"),
                                        friends.getJSONObject(i).getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //txtTestProfile.setText(buff.toString());
                            txtTestProfile.setText(model.getFriends().toString());

                        }
                        //txtTestProfile.setText(response.toString());

                    }
                }
        ).executeAsync();
        //boolean profileName = Profile.getCurrentProfile() == null;
        //boolean profileName = GlobalDataSingleton.getInstance().getProfile() == null;
        //txtTestProfile.setText(id);
        //Log.v("facebook - profile", id );
        /*if (profileName)
            txtTestProfile.setText(GlobalDataSingleton.getInstance().getStr());
        else
            txtTestProfile.setText(GlobalDataSingleton.getInstance().getStr());*/



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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contacts) {
            startActivity(this.intent_contacts);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
