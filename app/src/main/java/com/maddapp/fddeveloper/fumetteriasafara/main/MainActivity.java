package com.maddapp.fddeveloper.fumetteriasafara.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.UserManager;
import com.maddapp.fddeveloper.fumetteriasafara.landing.LandingScreenActivity;
import com.maddapp.fddeveloper.fumetteriasafara.R;
import com.maddapp.fddeveloper.fumetteriasafara.sharedThings.CardsManager;
import com.maddapp.fddeveloper.fumetteriasafara.tournament.ChampionshipSelectionFragment;
import com.maddapp.fddeveloper.fumetteriasafara.tournament.GameSelectionFragment;
import com.maddapp.fddeveloper.fumetteriasafara.tournament.ChampionshipActivity;

import java.util.Locale;

/**
 * The main activity of the app: after logging in this activity works as a hub
 */
public class MainActivity extends AppCompatActivity implements  GameSelectionFragment.OnFragmentInteractionListener,
                                                                SettingsFragment.OnFragmentInteractionListener,
                                                                ChampionshipSelectionFragment.OnFragmentInteractionListener,
                                                                HomeFragment.OnFragmentInteractionListener,
                                                                BookingFragment.OnListFragmentInteractionListener {
    private FirebaseAuth mAuth;
    private FirebaseUser User;
    private GoogleApiClient mGoogleApiClient;
    private LoginManager mLoginManager;
    final FragmentManager fm = getSupportFragmentManager();
    private final HomeFragment homeFragment = HomeFragment.newInstance();
    private final BookingFragment abbonamentiFragment = BookingFragment.newInstance();
    private final GameSelectionFragment gameSelectionFragment = GameSelectionFragment.newInstance();
    private final SettingsFragment optionFragment = SettingsFragment.newInstance();
    private final FragmentAddBooking fragmentAddBooking = FragmentAddBooking.newInstance();
    private ChampionshipSelectionFragment championshipFragment;
    private boolean backToGames = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            backToGames = false;
            FragmentTransaction f = fm.beginTransaction();
            f.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    f.replace(R.id.content, homeFragment).commit();
                    return true;
                case R.id.navigation_abbonamenti:
                    f.replace(R.id.content, abbonamentiFragment).commit();
                    return true;
                case R.id.navigation_tornei:
                    f.replace(R.id.content, gameSelectionFragment).commit();
                    return true;
                case R.id.navigation_opzioni: {
                    f.replace(R.id.content, optionFragment).commit();
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Firebase setting

        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();

        if(User == null){
            logout();
        }

        //google

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mGoogleApiClient.connect();
        //facebook

        mLoginManager = LoginManager.getInstance();

        //Things to do only the first time the activity starts

        if(savedInstanceState == null) {
            UserManager.readUser(mAuth.getCurrentUser(),MainActivity.this);
            FragmentTransaction f = fm.beginTransaction();
            f.replace(R.id.content, homeFragment).commit();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
    }

    //Implementing GameSelectionFragment.OnFragmentInteraction
    @Override
    public void onFragmentGameInteraction(String destination) {
        backToGames = true;
        championshipFragment = ChampionshipSelectionFragment.newInstance(destination);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        t.replace(R.id.content, championshipFragment);
        t.commit();
        CardsManager.setList(destination);
    }

    //Implementing ChampionshipSelectionFragment.OnFragmentInteraction
    @Override
    public void onChampionshipSelection(String id, String gioco){
        Intent intent = new Intent(getApplicationContext(), ChampionshipActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("gioco", gioco);
        startActivity(intent);
    }

    public void onFragmentHomeInteraction(String mode){
        TransactionRecapActivity recap = new TransactionRecapActivity();
        Intent intent = new Intent(getApplicationContext(), TransactionRecapActivity.class);
        intent.putExtra("mode",mode);
        startActivity(intent);
    }
    //Implementing SettingsFragment.OnFragmentInteraction
    public void logout(){
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
            }
        });
        disconnectFromFacebook();

        Intent intent = new Intent(getApplicationContext(), LandingScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }

    private void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }


    public  void makeLog(String text){
        if(text == null || text.trim().equals(""))
            return;
        String message = String.format("Message: %s",text);
        FirebaseCrash.report(new Exception(message));
    }

    @Override
    public void onAddBooking() {
        fragmentAddBooking.show(getSupportFragmentManager(),"addBooking");
    }

    @Override
    public void showBooking(String title, int comic_number, boolean confirmed) {
        String text = String.format(Locale.ITALIAN,"numero:\t%d\nstato:\t%s",comic_number, confirmed? "approvato" : "in attesa di approvazione");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(text)
                .setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(MainActivity.this.getResources().getColor(R.color.colorPrimary));
            }
        });
        dialog.show();

    }

    @Override
    public void onBackPressed() {
        if(backToGames) {
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            t.replace(R.id.content, gameSelectionFragment);
            t.commit();
        }
            else
        super.onBackPressed();
    }
}
