package com.maddapp.fddeveloper.fumetteriasafara.tournament;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
    private List<Tournament> mListTornei;
    private String gioco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListTornei =  (List<Tournament>) getIntent().getSerializableExtra("listaTornei");
        gioco = getIntent().getStringExtra("gioco");
        if(mListTornei == null || gioco == null)
            finish();
        else
        {
            setContentView(R.layout.activity_fragment_placeholder);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction f = fm.beginTransaction();
            f.replace(R.id.tempFrame, mFragment).commit();

            Map<String,PositionRecap> recap = new HashMap<>();
            for(Tournament t : mListTornei){
                for(String key : t.Piazzamenti.keySet()){
                    TournamentPosition pt = t.Piazzamenti.get(key);                //create a championship ladder
                    if(recap.containsKey(key))
                        recap.get(key).addPosizione(pt);
                    else {
                        String Nome = key;
                        if (CardsManager.containsKey(gioco, key))
                            Nome = CardsManager.getTessera(gioco,key).toString();
                        recap.put(key, new PositionRecap(Nome,pt));
                    }
                }
            }
            List<PositionRecap> result = PositionRecap.RatePosizioni(new ArrayList<>(recap.values()));
            mFragment.setList(result);
        }
    }

    @Override
    public void onClassificaGiocatoreInteraction(Object item) {

    }
}
