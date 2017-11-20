package com.maddapp.fddeveloper.fumetteriasafara.tournament;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.PositionRecap;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.Tournament;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.TournamentPosition;
import com.maddapp.fddeveloper.fumetteriasafara.R;
import com.maddapp.fddeveloper.fumetteriasafara.sharedThings.CardsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A per-format recap of the championships. Shown as a list it displays the players, them score and them ranking (1st 2nd 3rd or more)
 */
public class FormatLadderActivity extends AppCompatActivity implements FragmentPlayerLadder.OnListFragmentInteractionListener{

    private FragmentPlayerLadder mFragment = new FragmentPlayerLadder();
    List<Tournament> mListTournaments;
    String game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListTournaments =  (List<Tournament>) getIntent().getSerializableExtra("listaTornei");
        game = getIntent().getStringExtra("game");
        //the activity proceeds only if the id is passed
        if(mListTournaments == null || game == null) {
            Toast.makeText(this, R.string.error_no_format_ita, Toast.LENGTH_SHORT);
            finish();
        }
        else
        {
            setContentView(R.layout.activity_fragment_placeholder);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction f = fm.beginTransaction();
            f.replace(R.id.tempFrame, mFragment).commit();
            //here we filter the placements by format and sum them up
            Map<String,PositionRecap> recap = new HashMap<>();
            for(Tournament t : mListTournaments){
                for(String key : t.Piazzamenti.keySet()){
                    TournamentPosition pt = t.Piazzamenti.get(key);                //create a championship ladder
                    if(recap.containsKey(key))
                        recap.get(key).addPosizione(pt);
                    else {
                        String Nome = key;
                        if (CardsManager.containsKey(game, key))
                            Nome = CardsManager.getMembershipCard(game,key).toString();
                        recap.put(key, new PositionRecap(Nome,pt));
                    }
                }
            }
            List<PositionRecap> result = PositionRecap.RatePosizioni(new ArrayList<>(recap.values()));
            mFragment.setList(result);
        }
    }

    /**
     * Empty implementation of the onPlayerLadderInteraction interface function.
     * The implementation is required, but no function is needed in this case.
     * @param item
     */
    @Override
    public void onPlayerLadderInteraction(Object item) {
    }
}
