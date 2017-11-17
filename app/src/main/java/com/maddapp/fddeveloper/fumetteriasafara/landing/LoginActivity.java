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
import com.maddapp.fddeveloper.fumetteriasafara.main.SettingsFragment;

/**
 * A login screen that offers login via email/password and also let the user access to a
 * register activity.
 */
public class LoginActivity extends AppCompatActivity implements FragmentMailLogin.OnFragmentInteractionListener, FragmentMailRegister.OnFragmentInteractionListener {
//TODO: autocomplete

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private View mProgressView;
    private View mContentView;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private boolean inRegisterMode = false;
    private FragmentMailLogin fml = FragmentMailLogin.newInstance();
    private FragmentMailRegister sf = FragmentMailRegister.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth= FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser != null)
            finish();

        mProgressView = findViewById(R.id.login_progress);
        mContentView = findViewById(R.id.content);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fml)
                .commit();


        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

    }

    /**
     * Shows the progress UI and hides the login form.
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

    private void fragmentFadeSwap(final View holder, Fragment in){
        final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


        holder.animate().setDuration(shortAnimTime).alpha(0);
//        getSupportFragmentManager().beginTransaction().replace(holder,in);
    }

    @Override
    public void forwardLogin(String email, String password) {
        showProgress(true);

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
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

    @Override
    public void requestMailRegister() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.content, sf)
                .commit();
        inRegisterMode = true;
    }

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

    @Override
    public void onForwardRegister(String mail, String password, final String Name, final String Surname) {
        showProgress(true);

        mAuth.createUserWithEmailAndPassword(mail,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
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

