package com.test.kovilapauvaday.prototype_connect;

import android.app.Instrumentation;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.test.kovilapauvaday.prototype_connect.model.GlobalDataSingleton;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    //public static final String TAG = "MainActivity";
    EditText editText;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_main);
        this.editText = findViewById(R.id.edit_text);
        com.facebook.login.widget.LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("read_custom_friendlists");
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }
        );
        this.intent = new Intent(this, HomeActivity.class);
        //intent.setAction()
        // Create a callbackManager//s
           // Initialize your instance of callbackManager//
        callbackManager = CallbackManager.Factory.create();

        //TODO: delete
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER); //log out

                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);//log out confirm

                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);//fb
                    Thread.sleep(10000);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    //inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    inst.sendStringSync("mqkfomgmed_1513539371@tfbnw.net");
                    Thread.sleep(2000);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    inst.sendStringSync("tpdev2017UPMC");
                    Thread.sleep(2000);

                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
                    Thread.sleep(4000);

                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);

                    Thread.sleep(2000);
                    }
                catch(InterruptedException e){
                }
            }
        }).start();*/

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    private ProfileTracker mProfileTracker;

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if(Profile.getCurrentProfile() == null) {
                            mProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                    Profile.setCurrentProfile(currentProfile);
                                    //GlobalDataSingleton.getInstance().setProfile(currentProfile);
                                    //GlobalDataSingleton.getInstance().setStr("if");
                                    Log.v("facebook - profile", currentProfile.getId() );
                                    //intent.setAction(Intent.ACTION_SEND);
                                    intent.putExtra(HomeActivity.KEY_ID, currentProfile.getId());
                                    startActivity(intent);

                                    mProfileTracker.stopTracking();
                                }
                            };

                        }else{
                            Profile profile = Profile.getCurrentProfile();
                           // GlobalDataSingleton.getInstance().setStr("else");
                            intent.putExtra(HomeActivity.KEY_ID, profile.getId());
                            startActivity(intent);

                            Log.v("facebook - profile", profile.getFirstName() );
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



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}

