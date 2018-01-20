package com.test.kovilapauvaday.prototype_connect;
import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karan.churi.PermissionManager.PermissionManager;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {

    //maps and firebase auth
    private String name = "pseudo"; //nom dutilisateur par defaut
    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    //private FirebaseAuth mAuth;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ViewGroup mPhoneNumberViews;
    private ViewGroup mSignedInViews;

    private TextView mStatusText;
    private TextView mDetailText;

    private EditText mPhoneNumberField;
    private EditText mVerificationField;

    private Button mStartButton;
    private Button mVerifyButton;
    private Button mResendButton;
    private Button mSignOutButton;

    // firebase
    ProgressBar progressBar;
    /////////////////////////////////////////////////////////////////////////////////////
    private DatabaseReference mDatabase;
    private DatabaseReference mUserDatabase;
    // permission
    PermissionManager permissionManager;
    //facebook
    private CallbackManager callbackManager;
    Intent intent;
    private LoginResult fbLoginResult;
    private boolean facebook_mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this.mAuth = FirebaseAuth.getInstance();
        this.intent = new Intent(this, HomeActivity.class);

        // permission
        permissionManager = new PermissionManager() { };
        permissionManager.checkAndRequestPermissions(this);
        facebook_mode = false;

        Log.v("facebook - profile", "###checkpoint 1" );

       /*if(FirebaseAuth.getInstance().getCurrentUser() != null ||Profile.getCurrentProfile() != null ){
           Log.v("facebook - profile", "###currentUser=" + FirebaseAuth.getInstance().getCurrentUser() );
           intent.putExtra(HomeActivity.FBK_MODE, false);
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

        mPhoneNumberViews = (ViewGroup) findViewById(R.id.phone_auth_fields);
        mSignedInViews = (ViewGroup) findViewById(R.id.signed_in_buttons);

        mStatusText = (TextView) findViewById(R.id.status);
        mDetailText = (TextView) findViewById(R.id.detail);

        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);

        mStartButton = (Button) findViewById(R.id.button_start_verification);
        mVerifyButton = (Button) findViewById(R.id.button_verify_phone);
        mResendButton = (Button) findViewById(R.id.button_resend);
        mSignOutButton = (Button) findViewById(R.id.sign_out_button);

        // Assign click listeners
        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);

        FacebookSdk.sdkInitialize(getApplicationContext());//fbk
        AppEventsLogger.activateApp(this); //fbk

        Log.v("facebook - profile", "###checkpoint 2" );
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                mVerificationInProgress = false;
                updateUI(STATE_VERIFY_SUCCESS, credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mVerificationInProgress = false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mPhoneNumberField.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(MainActivity.this, "NON quota", Toast.LENGTH_SHORT).show();
                }
                updateUI(STATE_VERIFY_FAILED);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;

                updateUI(STATE_CODE_SENT);
            }

        };
        Log.v("facebook - profile", "###checkpoint 3" );

        //facebook-start
       com.facebook.login.widget.LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile"/*,"user_friends","read_custom_friendlists"*/);
        //loginButton.setReadPermissions("user_friends");
        //loginButton.setReadPermissions("read_custom_friendlists");

        setAuthListener();
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                }
        );
        Log.v("facebook - profile", "###checkpoint 4" );

        // Create a callbackManager//s
        // Initialize your instance of callbackManager//
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    private ProfileTracker mProfileTracker;

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        facebook_mode = true;
                        if(Profile.getCurrentProfile() == null) {
                            fbLoginResult = loginResult;

                            mProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                    Profile.setCurrentProfile(currentProfile);
                                    name = currentProfile.getName();
                                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                                        handleFacebookAccessToken(fbLoginResult.getAccessToken());
                                    }

                                    finish();
                                    mProfileTracker.stopTracking();

                                    /*String mcurrent = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    register_user(currentProfile.getId().toString());

                                    intent.putExtra(HomeActivity.KEY_ID, currentProfile.getId());
                                    intent.putExtra(HomeActivity.FBK_MODE, true);
                                    intent.putExtra("from_user_id", "vide");
                                    intent.putExtra("latitude", "0");
                                    intent.putExtra("longtitude", "0");
                                    intent.putExtra("user_pseudo", "vide");
                                    intent.putExtra("type_class", "vide");
                                    startActivity(intent);
                                    mProfileTracker.stopTracking();*/
                                }
                            };

                        }else{
                            Profile profile = Profile.getCurrentProfile();
                            name = profile.getName();
                            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                                handleFacebookAccessToken(fbLoginResult.getAccessToken());
                            }
                            register_user(Profile.getCurrentProfile().getId().toString());
                            intent.putExtra(HomeActivity.FBK_MODE, true);

                            finish();
                            mProfileTracker.stopTracking();
                            /*Profile profile = Profile.getCurrentProfile();
                            intent.putExtra(HomeActivity.FBK_MODE, true);
                            intent.putExtra(HomeActivity.KEY_ID, profile.getId());
                            startActivity(intent);

                            Log.v("facebook - profile", profile.getFirstName() );*/
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

            //facebook-end

    }
    public void setAuthListener(){
        FirebaseAuth.getInstance().addAuthStateListener(
                new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            if(facebook_mode){
                                register_user(Profile.getCurrentProfile().getId().toString());
                                intent.putExtra(HomeActivity.FBK_MODE, true);
                            }else{
                                intent.putExtra(HomeActivity.FBK_MODE, false);
                            }
                            Log.v("facebook - onCancel", "###user signed in");
                            //intent.putExtra(HomeActivity.FBK_MODE, false);
                            intent.putExtra("from_user_id", "vide");
                            intent.putExtra("latitude", "0");
                            intent.putExtra("longtitude", "0");
                            intent.putExtra("user_pseudo", "vide");
                            intent.putExtra("type_class", "vide");
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
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }


    //facebook-authentification
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    ///////////////////////firebase-and-google-maps////////////////////////////

    // permision
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults);

        ArrayList<String> granted = permissionManager.getStatus().get(0).granted;
        ArrayList<String> denied = permissionManager.getStatus().get(0).denied;

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(currentUser);
        Log.v("facebook - profile", "###checkpoint 7 - onStart" );

        if (mVerificationInProgress && validatePhoneNumber()) {
            facebook_mode = false;
            startPhoneNumberVerification(mPhoneNumberField.getText().toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
        mVerificationInProgress = true;
        mStatusText.setVisibility(View.INVISIBLE);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            updateUI(STATE_SIGNIN_SUCCESS, user);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                mVerificationField.setError("Invalid code.");
                            }
                            updateUI(STATE_SIGNIN_FAILED);
                        }
                    }
                });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        updateUI(STATE_INITIALIZED);
    }

    private void updateUI(int uiState) {
        updateUI(uiState, FirebaseAuth.getInstance().getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                enableViews(mStartButton, mPhoneNumberField);
                disableViews(mVerifyButton, mResendButton, mVerificationField);
                mDetailText.setText(null);
                break;
            case STATE_CODE_SENT:
                enableViews(mVerifyButton, mResendButton, mPhoneNumberField, mVerificationField);
                disableViews(mStartButton);
                mDetailText.setText("Code est envoye");
                mDetailText.setTextColor(Color.parseColor("#43a047"));
                break;
            case STATE_VERIFY_FAILED:
                enableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField);
                mDetailText.setText("Verification echoue");
                mDetailText.setTextColor(Color.parseColor("#dd2c00"));
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case STATE_VERIFY_SUCCESS:
                disableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField);
                mDetailText.setText("Verfie");
                mDetailText.setTextColor(Color.parseColor("#43a047"));

                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        mVerificationField.setText(cred.getSmsCode());
                    } else {
                        mVerificationField.setText(R.string.instant_validation);
                        mVerificationField.setTextColor(Color.parseColor("#4bacb8"));
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                mDetailText.setText("Echoue");
                mDetailText.setTextColor(Color.parseColor("#dd2c00"));
                break;
            case STATE_SIGNIN_SUCCESS:
                mStatusText.setText("Connexion");
                break;
        }

        if (user == null) {
            mPhoneNumberViews.setVisibility(View.VISIBLE);
            mSignedInViews.setVisibility(View.GONE);

            mStatusText.setText("Deconnecte");;
        } else {
            mPhoneNumberViews.setVisibility(View.GONE);
            register_user(mPhoneNumberField.getText().toString());

           /* Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra(HomeActivity.FBK_MODE, false);
            intent.putExtra("from_user_id", "vide");
            intent.putExtra("latitude", "0");
            intent.putExtra("longtitude", "0");
            intent.putExtra("user_pseudo", "vide");
            intent.putExtra("type_class", "vide");*/

            //startActivity(intent);
            //finish();

        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Numero est invalide");
            return false;
        }

        return true;
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                if (!validatePhoneNumber()) {
                    return;
                }
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                startPhoneNumberVerification(mPhoneNumberField.getText().toString());

                break;
            case R.id.button_verify_phone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                resendVerificationCode(mPhoneNumberField.getText().toString(), mResendToken);
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
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

