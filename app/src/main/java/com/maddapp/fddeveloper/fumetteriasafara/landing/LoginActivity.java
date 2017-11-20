package com.maddapp.fddeveloper.fumetteriasafara.landing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maddapp.fddeveloper.fumetteriasafara.R;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.UserManager;

/**
 * An activity which manages via mail login and register.
 */
public class LoginActivity extends AppCompatActivity implements FragmentMailLogin.OnFragmentInteractionListener, FragmentMailRegister.OnFragmentInteractionListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private View mProgressView;
    private View mContentView;

    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    private boolean inRegisterMode = false;
    private FragmentMailLogin fml = FragmentMailLogin.newInstance();
    private FragmentMailRegister sf = FragmentMailRegister.newInstance();

    /**
     * On create function. It simply stores the data and sets the login fragment
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //firebase initialization
        mAuth= FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        //if the user's already logged, there's no need to login, so just finish()
        if(mUser != null)
            finish();

        //setting the login fragment and setting the values for the loading animation
        mProgressView = findViewById(R.id.login_progress);
        mContentView = findViewById(R.id.content);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fml)
                .commit();

        //no need for action bar in this activity
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

    }

    /**
     * Shows the progress UI and hides the login form or vice versa.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
        mContentView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    /**
     * Attempts a login via firebase with mail and password
     * @param email a valid email
     * @param password a valid password
     */
    @Override
    public void forwardLogin(String email, String password) {
        // enables the loading animation
        showProgress(true);
        //attempts the login
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //in case of success, the activity ends
                            finish();
                        }
                        else{
                            showProgress(false);
                            Toast.makeText(LoginActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showProgress(false);
                        Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                })
        ;
    }

    /**
     * If the mail register is requested, the activity starts a transaction to the register fragment
     * (from the login fragment)
     */
    @Override
    public void requestMailRegister() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.content, sf)
                .commit();
        inRegisterMode = true;
    }

    /**
     * In case the fragment is set to Register, the backbutton sets a fragment transaction to go back
     * to the login fragment.
     */
    @Override
    public void onBackPressed() {
        if(inRegisterMode){
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.content, fml)
                    .commit();
            inRegisterMode = false;
        }
        else
            super.onBackPressed();
    }

    /**
     * Forwards the register with valid data, attempting a FireBase registration
     * @param mail a valid mail
     * @param password a valid password
     * @param Name a not empty name
     * @param Surname a not empty password
     */
    @Override
    public void onForwardRegister(String mail, String password, final String Name, final String Surname) {
        showProgress(true);
        //creates the new user
        mAuth.createUserWithEmailAndPassword(mail,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //on success finishes the activity
                            FirebaseUser val = mAuth.getCurrentUser();
                            UserManager.writeUser(Name,Surname, mAuth.getCurrentUser());
                            finish();
                        } else
                        {
                            showProgress(false);
                            Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showProgress(false);
                        Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                })
        ;

    }
}

