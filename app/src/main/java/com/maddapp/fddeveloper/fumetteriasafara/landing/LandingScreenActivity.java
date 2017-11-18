package com.maddapp.fddeveloper.fumetteriasafara.landing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crash.FirebaseCrash;
import com.maddapp.fddeveloper.fumetteriasafara.main.MainActivity;
import com.maddapp.fddeveloper.fumetteriasafara.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A Landing screen: it contains three types of login:
 *  - Google login, the most usable one, which connects directly to google accounts
 *  - Facebook login, another usable one, which connects via facebook
 *  - Classic login, which lands to another activity for the classic mail and password login
 *
 *  Using firebase, as long as the mail is the same, multiple login methods are valid
 */
public class LandingScreenActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    private View mLoginFormView;
    private View mProgressView;
    private List<Integer> ignoredGoogleApiMessages = new ArrayList<Integer>(){{add(ConnectionResult.CANCELED); add(ConnectionResult.SIGN_IN_FAILED); add(ConnectionResult.SUCCESS);}};

    private static final int GOOGLE_SIGN_IN = 9001;
    private static final int FACEBOOK_SIGN_IN = 0xFACE;     // ... well played, fb.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);

        //data for loading animation

        mLoginFormView = findViewById(R.id.loginMethods);
        mProgressView = findViewById(R.id.login_progress);

        //firebase initialization

        mAuth= FirebaseAuth.getInstance();

        //google login

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        //facebook login

        mCallbackManager=CallbackManager.Factory.create();
    }

    @Override
    public void onStart(){
        super.onStart();

        //no need for action bar

        if(getSupportActionBar()!= null)
            getSupportActionBar().hide();

        //if the user's logged, go to main activity

        FirebaseUser usr = mAuth.getCurrentUser();
        if(usr != null){
            handleLoggedUser();
        }

        //mail login

        findViewById(R.id.mailFab).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        //google login

        mGoogleApiClient.connect();
        findViewById(R.id.googleFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
            }
        });

        //facebook login

        final LoginButton loginButton = findViewById(R.id.facebook_login);
        loginButton.setReadPermissions("email","public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                //cancel's allowed without consequences
            }

            @Override
            public void onError(FacebookException error) {
                //send a report to firebase and notice the user
                FirebaseCrash.report(new Exception("FB ERROR: " + error.getMessage()));
                Toast.makeText(getBaseContext(),"Errore nel login di facebook",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.facebookFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.callOnClick();
            }
        });

        //todo: twitter login ?

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
        //On connection failed, if the error is not in the ignored generic messages send firebase crash and notify
        if(! ignoredGoogleApiMessages.contains(connectionResult.getErrorCode())) {
            FirebaseCrash.report(new Exception("Google api error: " + connectionResult.getErrorMessage()));
            Toast.makeText(getBaseContext(),"Errore nel login di google: " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        //on external login success handle via handlelogin functions
        if(requestCode == GOOGLE_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
        else if(requestCode == FACEBOOK_SIGN_IN){
            mCallbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result){
        //get result if success, pass it to login
        if(result.isSuccess()){
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        //get token, pass it
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        handleLoginWithCredential(credential);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        //get token, pass it
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        handleLoginWithCredential(credential);
    }

    private void handleLoginWithCredential(AuthCredential credential){
        //set login animation, log via token
        LoadingAnimation(true);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, get user, handle the login
                            FirebaseUser user = mAuth.getCurrentUser();
                            handleLoggedUser();
                        } else {
                            // Sign in fail, remove loading animation, notice the user
                            LoadingAnimation(false);
                            Toast.makeText(LandingScreenActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleLoggedUser(){
        //start new root activity and close this one
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }

    private void LoadingAnimation(final boolean show){
        //simple loading animation, eventually will be updated with the graphics update.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
