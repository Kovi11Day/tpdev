package com.test.kovilapauvaday.prototype_connect;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.facebook.Profile;
import com.test.kovilapauvaday.prototype_connect.model.GlobalDataSingleton;
import com.test.kovilapauvaday.prototype_connect.model.User;

import javax.microedition.khronos.opengles.GL;

public class ContactsActivity extends AppCompatActivity {

    GlobalDataSingleton model = GlobalDataSingleton.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listViewContacts = (ListView)findViewById(R.id.listview_contacts);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        for(User user: model.getFriends()){
           // listViewContacts
        }
    }

}
