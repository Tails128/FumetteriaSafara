package com.maddapp.fddeveloper.fumetteriasafara.tournament;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.PositionRecap;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.Tournament;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.TournamentPosition;
import com.maddapp.fddeveloper.fumetteriasafara.R;
import com.maddapp.fddeveloper.fumetteriasafara.sharedThings.CardsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *  After a championship is selected this activity pops up, letting you choose between three
 *  different possibilities: per-tournament recap, per-format recap and per-championship recap
 */
public class ChampionshipActivity extends AppCompatActivity implements FragmentFormatSelection.OnFragmentInteractionListener, FragmentTournamentSelection.OnFragmentInteractionListener, FragmentPlayerLadder.OnListFragmentInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();
    private FragmentTournamentSelection mfragmentTornei;
    private FragmentFormatSelection mfragmentFormati;
    private FragmentPlayerLadder mfragmentGiocatori;
    private String id;
    private String gioco;
    private Map<String, Map<String,Tournament>> formati = new HashMap<>();
    private Map<String,PositionRecap> posizioni = new HashMap();
    private Map<String, Tournament> tornei = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournaments);

        if(getIntent().getExtras() == null){
            Toast.makeText(getBaseContext(),"Non sono stati passati dati relativi al gioco o al campionato",Toast.LENGTH_SHORT).show();
            return;
        }
        if(getIntent().getExtras().getString("id") == null)
        {
            Toast.makeText(getBaseContext(),"Errore nella selezione del campionato",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        else if(getIntent().getExtras().getString("gioco") == null)
        {
            Toast.makeText(getBaseContext(),"Errore nella selezione del gioco",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        id = getIntent().getExtras().getString("id");
        gioco = getIntent().getExtras().getString("gioco");


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mfragmentTornei = FragmentTournamentSelection.newInstance();
        mfragmentFormati = FragmentFormatSelection.newInstance();
        mfragmentGiocatori = FragmentPlayerLadder.newInstance();

        String query = String.format("%s/Tornei/%s",gioco,id);
        reference.child(query).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot formato : dataSnapshot.getChildren()){
                    if(!formati.containsKey(formato.getKey()))                          //add key to formati
                        formati.put(formato.getKey(),new HashMap<String, Tournament>());
                    for(DataSnapshot torneo : formato.getChildren()){
                        Tournament t = torneo.getValue(Tournament.class);                       //add tornei to map formati and map tornei
                        t.id = torneo.getKey();
                        formati.get(formato.getKey()).put(torneo.getKey(),t);
                        tornei.put(torneo.getKey(), t);
                        for(String key : t.Piazzamenti.keySet()){
                            TournamentPosition pt = t.Piazzamenti.get(key);                //create a championship ladder
                            if(posizioni.containsKey(key))
                                posizioni.get(key).addPosizione(pt);
                            else {
                                String Nome = key;
                                if (CardsManager.containsKey(gioco,key))
                                    Nome = CardsManager.getTessera(gioco,key).toString();
                                posizioni.put(key, new PositionRecap(Nome,pt));
                                tornei.get(torneo.getKey()).Piazzamenti.get(key).Nome = Nome;
                            }
                        }
                    }
                }
                mfragmentGiocatori.setList(new ArrayList<>(posizioni.values()));  //pass the ladder to ladder fragment
                mfragmentFormati.setList(new ArrayList<>(formati.keySet()));
                mfragmentTornei.setList(new ArrayList<>(tornei.values()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tornei, menu);
        return false;   //(true per visibile)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return mfragmentTornei;
                case 1:
                    return mfragmentFormati;
                case 2:
                    return mfragmentGiocatori;
                default:
                    throw new ArrayIndexOutOfBoundsException(getBaseContext().toString() + "La tab selezionata non esiste");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


    public void onFragmentClassificheFormatiInteraction(String id){
        Intent intent = new Intent(getApplicationContext(),FormatLadderActivity.class);
        ArrayList torneiList = new ArrayList(formati.get(id).values());
        intent.putExtra("listaTornei",torneiList);
        intent.putExtra("gioco",gioco);
        startActivity(intent);

    }
    public void onFragmentClassificheTorneiInteraction(String id){
        Intent intent = new Intent(getApplicationContext(),TournamentLadderActivity.class);
        intent.putExtra("Tournament", tornei.get(id));
        startActivity(intent);
    }
    public void onClassificaGiocatoreInteraction(Object item){
    }

}
