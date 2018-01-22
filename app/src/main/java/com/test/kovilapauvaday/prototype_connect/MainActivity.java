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
    private String name = "pseudo"; //nom de l'utilisateur par defaut

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    Intent intent;

    //views
    private ViewGroup partieAutentification;
    private TextView infoConnexion;
    private TextView detailConnexion;
    private EditText editNumeroTel;
    private EditText editCodeTel;
    private Button buttonEntrer;
    private Button buttonVerifier;
    private Button buttonRenvoyer;

    // firebase
    private DatabaseReference monDatabase;
    private FirebaseAuth firebaseAuth; //mAuth

    // verification de numero
    private boolean progresse = false;
    private String monVerificationId;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    //permission pour la localisation
    PermissionManager permissionManager;

    //authentification avec facebook
    private static final String TAG = "FacebookLogin";
    private CallbackManager callbackManager;
    private LoginResult fbLoginResult;
    private boolean facebook_mode;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this.firebaseAuth = FirebaseAuth.getInstance();
        this.intent = new Intent(this, HomeActivity.class);

        // permission
        permissionManager = new PermissionManager() { };
        permissionManager.checkAndRequestPermissions(this);

        facebook_mode = false;

       /*if(FirebaseAuth.getInstance().getCurrentUser() != null ||Profile.getCurrentProfile() != null ){
           Log.v("facebook - profile", "###currentUser=" + FirebaseAuth.getInstance().getCurrentUser() );
           intent.putExtra(HomeActivity.FBK_MODE, false);

           intent.putExtra("id_envoyeur", "vide");
           intent.putExtra("latitude", "0");
           intent.putExtra("longtitude", "0");
           intent.putExtra("user_pseudo", "vide");
           startActivity(intent);
           finish();
        }*/

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        // initialisations des vues
        partieAutentification = (ViewGroup) findViewById(R.id.partie_autentification);
        infoConnexion = (TextView) findViewById(R.id.info_connexion);
        detailConnexion = (TextView) findViewById(R.id.detail_connexion);
        editNumeroTel = (EditText) findViewById(R.id.edit_numero_tel);
        editCodeTel = (EditText) findViewById(R.id.edit_code_tel);
        buttonEntrer = (Button) findViewById(R.id.button_entrer);
        buttonVerifier = (Button) findViewById(R.id.button_verifier);
        buttonRenvoyer = (Button) findViewById(R.id.button_renvoyer);

        FacebookSdk.sdkInitialize(getApplicationContext());//fbk
        AppEventsLogger.activateApp(this); //fbk

        buttonEntrer.setOnClickListener(this);
        buttonVerifier.setOnClickListener(this);
        buttonRenvoyer.setOnClickListener(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // la vérification s'est bien passée
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                progresse = false;
                updateUI(STATE_VERIFY_SUCCESS, credential);
                signInWithPhoneAuthCredential(credential);
            }

            // la vérification a echoué
            @Override
            public void onVerificationFailed(FirebaseException e) {
                progresse = false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    editNumeroTel.setError("Le numéro est invalide.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(MainActivity.this, "NON quota", Toast.LENGTH_SHORT).show();
                }
                updateUI(STATE_VERIFY_FAILED);
            }

            //l'envoie de code
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                monVerificationId = verificationId;
                forceResendingToken = token;

                updateUI(STATE_CODE_SENT);
            }

        };

        //bouton login de facebook
       com.facebook.login.widget.LoginButton loginButton = findViewById(R.id.login_button);
       loginButton.setReadPermissions("email", "public_profile"/*,"user_friends","read_custom_friendlists"*/);

       setAuthListener();

        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                }
        );

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
    /**
     * Methode utilisée pour authentification facebook
     * A chaque fois que l'utilisateur passe en mode authentifier avec firebase,
     * L'activité HomeActivity est démarrer
     */
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

                        }
                    }
                });
    }

    /**
     * Methode utilisé pour authentification facebook
     * Transforme les token d'authentification de facebook en
     * token d'authentification avec firebase
     * @param token
     */
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication avec facebook échoué",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    //Methode utilisé pour l'authentification avec facebook
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // permission pour la localisation
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

        if (progresse && validatePhoneNumber()) {
            facebook_mode = false;
            startPhoneNumberVerification(editNumeroTel.getText().toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, progresse);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        progresse = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
        progresse = true;
        infoConnexion.setVisibility(View.INVISIBLE);
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
                                editCodeTel.setError("Code est invalide");
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
                enableViews(buttonEntrer, editNumeroTel);
                disableViews(buttonVerifier, buttonRenvoyer, editCodeTel);
                detailConnexion.setText(null);
                break;
            case STATE_CODE_SENT:
                enableViews(buttonVerifier, buttonRenvoyer, editNumeroTel, editCodeTel);
                disableViews(buttonEntrer);
                detailConnexion.setText("Code est envoyé");
                detailConnexion.setTextColor(Color.parseColor("#43a047"));
                break;
            case STATE_VERIFY_FAILED:
                enableViews(buttonEntrer, buttonVerifier, buttonRenvoyer, editNumeroTel,
                 editCodeTel);
                detailConnexion.setText("Vérification échouée");
                detailConnexion.setTextColor(Color.parseColor("#dd2c00"));
                break;
            case STATE_VERIFY_SUCCESS:
                disableViews(buttonEntrer, buttonVerifier, buttonRenvoyer, editNumeroTel, editCodeTel);
                detailConnexion.setText("Vérfié");
                detailConnexion.setTextColor(Color.parseColor("#43a047"));

                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        editCodeTel.setText(cred.getSmsCode());
                    } else {
                        editCodeTel.setText("Invalide");
                        editCodeTel.setTextColor(Color.parseColor("#4bacb8"));
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                detailConnexion.setText("Echoué");
                detailConnexion.setTextColor(Color.parseColor("#dd2c00"));
                break;
            case STATE_SIGNIN_SUCCESS:
                infoConnexion.setText("Connexion");
                break;
        }

        if (user == null) {
            partieAutentification.setVisibility(View.VISIBLE);
            infoConnexion.setText("Deconnecté");
        } else {
            partieAutentification.setVisibility(View.GONE);
            register_user(editNumeroTel.getText().toString());

           /* Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra(HomeActivity.FBK_MODE, false);
            intent.putExtra("id_envoyeur", "vide");
            intent.putExtra("latitude", "0");
            intent.putExtra("longtitude", "0");
            intent.putExtra("user_pseudo", "vide");
            intent.putExtra("type_class", "vide");*/

            //startActivity(intent);
            //finish();

        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = editNumeroTel.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            editNumeroTel.setError("Numéro est invalide !!!");
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
            case R.id.button_entrer:
                if (!validatePhoneNumber()) {
                    return;
                }
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                startPhoneNumberVerification(editNumeroTel.getText().toString());

                break;
            case R.id.button_verifier:
                String code = editCodeTel.getText().toString();

                if (TextUtils.isEmpty(code)) {
                    editCodeTel.setError("Ne peut pas être vide");
                    return;
                }

                verifyPhoneNumberWithCode(monVerificationId, code);
                break;
            case R.id.button_renvoyer:
                resendVerificationCode(editNumeroTel.getText().toString(), forceResendingToken);
                break;
        }
    }

    private void register_user(final String numero) {
        //FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        monDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("numero", numero);
        userMap.put("pseudo", name);
        userMap.put("device_token", deviceToken);

        monDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }

            }
        });
    }

}

