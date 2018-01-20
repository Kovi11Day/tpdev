package com.test.kovilapauvaday.prototype_connect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ParametersActivity extends AppCompatActivity {

    private TextView monNumero;
    private Button buttonEnregistrer;
    private EditText monPseudo;

    //Firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    //Progress PEUT ETRE AJOUTER APRES POUR UN PETITE DIALOGE
    //private ProgressDialog mProgress;

    // porc
    /*private EditText editTextPorc;
    private Button savePorc;
    private Button loadPorc;*/

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameters_activity);

        mToolbar = (Toolbar) findViewById(R.id.parameters_appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Parameters");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = new Intent(this, HomeActivity.class);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = mCurrentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id);

        monPseudo = (EditText) findViewById(R.id.mon_pseudo);
        monNumero = (TextView) findViewById(R.id.mon_numero);
        buttonEnregistrer = (Button) findViewById(R.id.buttonEnregistrerParams);

        //monPseudo.setText(BD.PSEUDO);
        //monNumero.setText(BD.NUMERO);

        mStatusDatabase.child("pseudo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                monPseudo.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        mStatusDatabase.child("numero").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                monNumero.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });




        buttonEnregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Progress
                /*mProgress = new ProgressDialog(ParametersActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we save the changes");
                mProgress.show();*/

                //BD.PSEUDO = monPseudo.getText().toString();
                //BD2.getInstance().PSEUDO = monPseudo.getText().toString();

                mStatusDatabase.child("pseudo").setValue(monPseudo.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                //mStatusDatabase.child("pseudo").setValue(monPseudo.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            //mProgress.dismiss();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "ERREUR", Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });

        /*
        editTextPorc = (EditText) findViewById(R.id.textPorc);
        savePorc = (Button) findViewById(R.id.enregistrePorc);
        loadPorc = (Button) findViewById(R.id.getPorc);

        savePorc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileOutputStream fos;
                try {
                    fos = openFileOutput("mes_donnes.txt", Context.MODE_PRIVATE);
                    fos.write(editTextPorc.getText().toString().getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getBaseContext(), editTextPorc.getText().toString(), Toast.LENGTH_LONG).show();
            }
        });
        loadPorc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileInputStream fis = null;
                String text = "";
                StringBuilder sb = null;

                try {
                    fis = openFileInput("mes_donnes.txt");
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);
                    sb = new StringBuilder();

                    while ((text = br.readLine()) != null) {
                        sb.append(text).append("\n");
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Toast.makeText(getBaseContext(), sb.toString(), Toast.LENGTH_LONG).show();
            }
        });*/
    }
}
