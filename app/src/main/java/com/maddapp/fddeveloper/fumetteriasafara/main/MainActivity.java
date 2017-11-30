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
import com.maddapp.fddeveloper.fumetteriasafara.dialogs.FragmentAddBooking;
import com.maddapp.fddeveloper.fumetteriasafara.landing.LandingScreenActivity;
import com.maddapp.fddeveloper.fumetteriasafara.R;
import com.maddapp.fddeveloper.fumetteriasafara.sharedThings.CardsManager;
import com.maddapp.fddeveloper.fumetteriasafara.tournament.ChampionshipActivity;

import java.util.Locale;

/**
 * The main activity of the app: after logging in this activity works as a hub.
 * this activity is a bottom navigation bar activity with 4 areas:
 * - Home, which shows the logo and the bonus point recap
 * - Bookings, which shows all the bookings made by the user and allows the user to book a comic
 * - Games, which shows the TCG supported by the reseller and allows to check the passed championships dapta
 * - Settings, which manages some simple settings
 */
public class MainActivity extends AppCompatActivity implements  GameSelectionFragment.OnFragmentInteractionListener,
                                                                SettingsFragment.OnFragmentInteractionListener,
                                                                ChampionshipSelectionFragment.OnFragmentInteractionListener,
                                                                HomeFragment.OnFragmentInteractionListener,
                                                                BookingFragment.OnListFragmentInteractionListener {
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private final HomeFragment homeFragment = HomeFragment.newInstance();
    private final BookingFragment bookingFragment = BookingFragment.newInstance();
    private final GameSelectionFragment gameSelectionFragment = GameSelectionFragment.newInstance();
    private final SettingsFragment optionFragment = SettingsFragment.newInstance();
    private final FragmentAddBooking fragmentAddBooking = FragmentAddBooking.newInstance();
    private boolean backToGames = false;
    FirebaseUser User;
    LoginManager mLoginManager;
    ChampionshipSelectionFragment championshipFragment;

    /**
     * default bottom navigation OnNavigationItemSelectedListener, but using a cross fade, according
     * to Material design principles.
     * It also sets the boolean backToGames to false. Such boolean is used in the onBackButtonPressed
     * management, so it needs to be reset every time the fragment changes, to prevent the
     * onBackButtonPressed to work incorrectly.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            backToGames = false;
            FragmentTransaction f = fragmentManager.beginTransaction();
            f.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    f.replace(R.id.content, homeFragment).commit();
                    return true;
                case R.id.navigation_abbonamenti:
                    f.replace(R.id.content, bookingFragment).commit();
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

    /**
     * On Create, it assigns data to the variables and checks if the user's logged
     * @param savedInstanceState
     */
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
            FragmentTransaction f = fragmentManager.beginTransaction();
            f.replace(R.id.content, homeFragment).commit();
        }
    }

    /**
     * onStart simply hides the support action bar after doing the default Activity.onStart()
     */
    @Override
    protected void onStart(){
        super.onStart();
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
    }

    //Implementing GameSelectionFragment.OnFragmentInteraction

    /**
     * onFragmentGameInteraction's implementation:
     * sets the boolean which allows onBackPress to move back from the championship fragment to the
     * game fragment, then sets the championship fragment in place of the game fragment.
     * @param destination
     */
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

    /**
     * Implementing the onChampionshipSelection interface function.
     * Once the championship is selected, it is possible to analyze data, so the Activity needs to
     * move on to the {@Link ChampionshipActivity} in the tournament package.
     * @param championshipId
     * @param game
     */
    @Override
    public void onChampionshipSelection(String championshipId, String game){
        Intent intent = new Intent(getApplicationContext(), ChampionshipActivity.class);
        intent.putExtra("championshipId",championshipId);
        intent.putExtra("game", game);
        startActivity(intent);
    }

    /**
     * Implementation of the onFragmentHomeInteraction interface function.
     * Changes the Activity to a TransactionRecapActivity to present the user with the list of
     * the instances in which he gained bonus points. Since Tournament bonus points and Transaction
     * bonus points can never be put together, the a mode parameter is required.
     * @param mode either "Tornei" or "Transazioni"
     */
    public void onFragmentHomeInteraction(String mode){
        Intent intent = new Intent(getApplicationContext(), TransactionRecapActivity.class);
        intent.putExtra("mode",mode);
        startActivity(intent);
    }

    /**
     * The function to logout and dismiss the token.
     * as the user's logged out, it is mandatory to go back to the landing Activity.
     */
    public void logout(){
        //firebase logout
        mAuth.signOut();
        //google token dispose
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
            }
        });
        //facebook token dispose
        disconnectFromFacebook();

        //moving back to landing activity
        Intent intent = new Intent(getApplicationContext(), LandingScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }

    /**
     * a function to dispose of the Facebook auth token
     */
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

    /**
     * implementing the settingActivity's makeLog interface function.
     * This function simply checks if the text passed is null or empty and then sends a log to
     * FireBaseCrash
     * @param text
     */
    public void makeLog(String text){
        if(text == null || text.trim().equals(""))
            return;
        String message = String.format("Message: %s",text);
        FirebaseCrash.report(new Exception(message));
    }

    /**
     * implementation of the onAddBooking interface Function.
     * It displays the fragmentAddBooking dialog.
     */
    @Override
    public void onAddBooking() {
        fragmentAddBooking.show(getSupportFragmentManager(),"addBooking");
    }

    /**
     * Implementation of the showBooking interface function.
     * It shows a modal containing the details of the selected booking through the passed arguments
     * @param title title of comic, it will become the title of the modal
     * @param comic_number comic's starting number for the booking (e.g. booking comicX from number 1)
     * @param confirmed a boolean asserting if the shopkeeper confirmed the booking or if it is still pending.
     */
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

    /**
     * onBackPressed is rewritten to manage the championshipFragment
     */
    @Override
    public void onBackPressed() {
        if(backToGames) {
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            t.replace(R.id.content, gameSelectionFragment);
            t.commit();
            backToGames = !backToGames;
        }
            else
        super.onBackPressed();
    }
}
