package com.maddapp.fddeveloper.fumetteriasafara.landing;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.maddapp.fddeveloper.fumetteriasafara.main.MainActivity;
import com.maddapp.fddeveloper.fumetteriasafara.R;

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
    private static final int GOOGLE_SIGN_IN = 9001;
    private static final int FACEBOOK_SIGN_IN = 0xFACE;     // ... well played, fb.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);

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

        if(getSupportActionBar()!= null)
            getSupportActionBar().hide();

        FirebaseUser usr = mAuth.getCurrentUser();
        if(usr != null){
            handleLoggedUser();
        }

        mGoogleApiClient.connect();

        TextView tw = findViewById(R.id.button2);
        tw.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        //google login

        SignInButton sib = findViewById(R.id.googleSignIn);
        sib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
            }
        });

        //facebook login

        LoginButton loginButton = findViewById(R.id.facebook_login);
        loginButton.setReadPermissions("email","public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                //todo
            }

            @Override
            public void onError(FacebookException error) {
                //todo
            }
        });

        //todo: twitter login
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
        Log.d("Register","Register Fail" + connectionResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == GOOGLE_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
        else if(requestCode == FACEBOOK_SIGN_IN){
            mCallbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            //todo
                            Log.d("google-login","loggato");
                            handleLoggedUser();
                        }
                        else
                        {
                            //todo
                            Log.d("google-login","NON loggato");
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Facebook-login", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            handleLoggedUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Facebook-login", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LandingScreenActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleLoggedUser(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }

    private void handleFailedLogin(){
        //todo
    }

    private void restoreLoadingScreen(){
        //todo
    }
}
