package com.test.kovilapauvaday.prototype_connect;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.FirebaseDatabase;
import com.test.kovilapauvaday.prototype_connect.contactslists.ContactAdapter;
import com.test.kovilapauvaday.prototype_connect.model.GlobalDataSingleton;
import com.test.kovilapauvaday.prototype_connect.users_amies_profile.User;

import javax.microedition.khronos.opengles.GL;

public class ContactsActivity extends AppCompatActivity {

    GlobalDataSingleton model = GlobalDataSingleton.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        GlobalDataSingleton.getInstance().unselectAll();
        Toolbar toolbar = (Toolbar) findViewById(R.id.contacts_appBar);
        toolbar.setTitle("Amies Facebook");
        setSupportActionBar(toolbar);

        ListView listViewContacts = (ListView)findViewById(R.id.listview_contacts);
        TextView editText = (TextView) findViewById(R.id.textview_search_contact);

        final ContactAdapter adapter =
                new ContactAdapter(this,
                        R.layout.listitem_contact,
                        GlobalDataSingleton.getInstance().getFriends());

        listViewContacts.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //adding some ficticious friends to test list
        for(int i = 0 ; i < 50; i++){
            GlobalDataSingleton.getInstance().getFriends().add(
                    new User("TestUser" + i, "fakeid" + i));
            adapter.notifyDataSetChanged();
        }

        }



}
