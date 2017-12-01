package com.test.kovilapauvaday.prototype_connect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import android.content.Intent;
import android.util.Log;
public class MainActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a callbackManager//
        setContentView(R.layout.activity_main);

        // Initialize your instance of callbackManager//
        callbackManager = CallbackManager.Factory.create();

        // Register your callback//
        LoginManager.getInstance().registerCallback(callbackManager,

                // If the login attempt is successful, then call onSuccess and pass the LoginResult//
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // Print the user’s ID and the Auth Token to Android Studio’s Logcat Monitor//
                        Log.d(TAG, "User ID: " +
                                loginResult.getAccessToken().getUserId() + "\n" +
                                "Auth Token: " + loginResult.getAccessToken().getToken());
                    }

                    // If the user cancels the login, then call onCancel//
                    @Override
                    public void onCancel() {
                    }

                    // If an error occurs, then call onError//
                    @Override
                    public void onError(FacebookException exception) {
                    }
                });
    }

    // Override the onActivityResult method and pass its parameters to the callbackManager//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}