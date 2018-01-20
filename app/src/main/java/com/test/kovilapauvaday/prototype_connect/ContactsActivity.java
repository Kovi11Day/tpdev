package com.test.kovilapauvaday.prototype_connect;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Profile;
import com.test.kovilapauvaday.prototype_connect.contactslists.ContactAdapter;
import com.test.kovilapauvaday.prototype_connect.model.GlobalDataSingleton;
import com.test.kovilapauvaday.prototype_connect.model.User;

import javax.microedition.khronos.opengles.GL;

public class ContactsActivity extends AppCompatActivity {

    GlobalDataSingleton model = GlobalDataSingleton.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        GlobalDataSingleton.getInstance().unselectAll();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        //TODO: delete after
        for(int i = 0 ; i < 50; i++){
            GlobalDataSingleton.getInstance().getFriends().add(
                    new User("TestUser" + i, "fakeid" + i));
            adapter.notifyDataSetChanged();
        }
    }

}
