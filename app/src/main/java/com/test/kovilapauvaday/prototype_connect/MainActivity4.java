package com.test.kovilapauvaday.prototype_connect;

/**
 * Created by kovilapauvaday on 20/01/2018.
 */

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import android.content.Intent;
        import android.hardware.camera2.params.Face;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.facebook.AccessToken;
        import com.facebook.CallbackManager;
        import com.facebook.FacebookCallback;
        import com.facebook.FacebookException;
        import com.facebook.FacebookSdk;
        import com.facebook.Profile;
        import com.facebook.ProfileTracker;
        import com.facebook.login.LoginManager;
        import com.facebook.login.LoginResult;
        import com.facebook.login.widget.LoginButton;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthCredential;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FacebookAuthProvider;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.iid.FirebaseInstanceId;

        import java.util.HashMap;

        import static com.facebook.FacebookSdk.getApplicationContext;

public class MainActivity4 extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "FacebookLogin";
    //private FirebaseAuth mAuth;
    private CallbackManager callbackManager; //facebook callback manager
    private DatabaseReference mDatabase;
    private LoginResult fbLoginResult;
    private String name = "pseudo"; //nom d'utilisateur par defaut
    Intent intent; //pour prochain acitvite qui va etre HomeActivity

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        ///this.mAuth = FirebaseAuth.getInstance();
        this.intent = new Intent(this, HomeActivity.class);

        //pour ne pas devoir s'authentifier a chaque fois
        /*if(FirebaseAuth.getInstance().getCurrentUser()!= null){

            if(Profile.getCurrentProfile() != null){
                intent.putExtra(HomeActivity.FBK_MODE, true);
            }else{
                intent.putExtra(HomeActivity.FBK_MODE, false);
            }

            intent.putExtra("from_user_id", "vide");
            intent.putExtra("latitude", "0");
            intent.putExtra("longtitude", "0");
            intent.putExtra("user_pseudo", "vide");
            intent.putExtra("type_class", "vide");
            startActivity(intent);
            finish();
        }*/

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        // Initialize Facebook Login button
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile"/*,"user_friends","read_custom_friendlists"*/);
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                }
        );
        setAuthListener();
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    private ProfileTracker mProfileTracker;
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if(Profile.getCurrentProfile() == null) {
                            fbLoginResult = loginResult;
                            //deja authentifier avec facebook, maintenant authentifier avec firebase
                            //handleFacebookAccessToken(loginResult.getAccessToken());

                            mProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                    Profile.setCurrentProfile(currentProfile);
                                    name = Profile.getCurrentProfile().getName();

                                    //ajouter utilisateur dans la base de donnee
                                    //si utilisateur existait deja on ne l'ajoute pas
                                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                                        handleFacebookAccessToken(fbLoginResult.getAccessToken());
                                    }
                                    //register_user(currentProfile.getId().toString());
                                    //startActivity(intent);
                                    finish();
                                    mProfileTracker.stopTracking();

                                }

                            };

                        }else{
                            //startActivity(intent);

                        }
                    }
                    @Override
                    public void onCancel() {
                        Log.v("facebook - onCancel", "cancelled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.v("facebook - onError", exception.getMessage());
                    }
                });


        /*loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                FirebaseUser user = mAuth.getCurrentUser();
                Log.d(TAG, "facebook:onSuccess:" + "###mAuth.currentUser="+user);
                String fbname = Profile.getCurrentProfile().getName();
                Log.d(TAG, "facebook:onSuccess:" + "###Profile.currentProfile.name="+fbname);
                register_user("123456789");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });*/
    }

    public void setAuthListener(){
        FirebaseAuth.getInstance().addAuthStateListener(
                new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            Log.v("facebook - onCancel", "###user signed in");
                            //Start HomeActivity
                            startActivity(intent);
                            finish();
                        }else if (user == null){
                            Log.v("facebook - onCancel", "###user not signed in");
                            if(Profile.getCurrentProfile() != null && fbLoginResult!= null)
                                handleFacebookAccessToken(fbLoginResult.getAccessToken());

                            //initialise MainActivity
                        }
                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
        //pour ne pas devoir s'authentifier a chaque fois
        /*if(FirebaseAuth.getInstance().getCurrentUser()!= null){

            if(Profile.getCurrentProfile() != null){
                intent.putExtra(HomeActivity.FBK_MODE, true);
            }else{
                intent.putExtra(HomeActivity.FBK_MODE, false);
            }

            intent.putExtra("from_user_id", "vide");
            intent.putExtra("latitude", "0");
            intent.putExtra("longtitude", "0");
            intent.putExtra("user_pseudo", "vide");
            intent.putExtra("type_class", "vide");
            startActivity(intent);
            finish();
        }*/
        /*LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    private ProfileTracker mProfileTracker;
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if(Profile.getCurrentProfile() == null) {

                            //deja authentifier avec facebook, maintenant authentifier avec firebase
                            handleFacebookAccessToken(loginResult.getAccessToken());

                            mProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                    Profile.setCurrentProfile(currentProfile);
                                    name = Profile.getCurrentProfile().getName();

                                    //ajouter utilisateur dans la base de donnee
                                    //si utilisateur existait deja on ne l'ajoute pas
                                   // if(mAuth.getCurrentUser()!=null) {
                                        register_user(currentProfile.getId().toString());
                                        startActivity(intent);
                                    //}
                                    mProfileTracker.stopTracking();
                                }
                            };

                        }else{
                            //startActivity(intent);

                        }
                    }
                    @Override
                    public void onCancel() {
                        Log.v("facebook - onCancel", "cancelled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.v("facebook - onError", exception.getMessage());
                    }
                });
                */
    }


    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    // [END on_activity_result]

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity4.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                       // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_facebook]

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();

        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        //hideProgressDialog();
        if (user != null) {
            //mStatusTextView.setText(getString(R.string.facebook_status_fmt, user.getDisplayName()));
           // mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
           // findViewById(R.id.button_facebook_signout).setVisibility(View.VISIBLE);
        } else {
            //mStatusTextView.setText(R.string.signed_out);
           // mDetailTextView.setText(null);

            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.button_facebook_signout).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        /*if (i == R.id.button_facebook_signout) {
            signOut();
        }*/
    }


    private void register_user(final String numero) {
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id);

        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("numero", numero);
        userMap.put("pseudo", name);//BD.PSEUDO);
        userMap.put("device_token", deviceToken);


        mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Log.v("facebook - profile", "test3" );

                }

            }
        });
    }
}
