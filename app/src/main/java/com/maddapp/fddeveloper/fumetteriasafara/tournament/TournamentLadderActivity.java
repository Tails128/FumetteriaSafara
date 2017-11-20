package com.maddapp.fddeveloper.fumetteriasafara.tournament;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.Tournament;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.TournamentPosition;
import com.maddapp.fddeveloper.fumetteriasafara.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A Per tournament ladder. Shown as a list it displays the players, them score and them ranking (1st 2nd 3rd or more)
 */
public class TournamentLadderActivity extends AppCompatActivity {

    private AlertDialog.Builder mAlertDialog;
    private AlertDialog mBuiltDialog;
    Tournament mTournament;

    /**
     * onCreate a list containing a player ladder tournament is set. In this case it is a player ladder
     * which is tournament-based.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //attempt to get a tournament and to create a ladder. If no tournament is passed, the activity ends
        mTournament = (Tournament) getIntent().getSerializableExtra("Tournament");
        if(mTournament == null){
            Toast.makeText(this, R.string.empty_tournament_error_ita,Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //UI update for the list
        setContentView(R.layout.simple_list_activity);
        final ListView mList = findViewById(R.id.simple_list);
        //creating an AlertDialog for the user placement detailed view
        mAlertDialog = new AlertDialog.Builder(TournamentLadderActivity.this, R.style.MyAlertDialogStyle);
        List<TournamentPosition> mCollection = new ArrayList<>(mTournament.Piazzamenti.values());
        Collections.sort(mCollection, new Comparator<TournamentPosition>() {
            @Override
            public int compare(TournamentPosition p1, TournamentPosition p2){
                if(p1.Posizione > p2.Posizione)
                    return 1;
                if(p2.Posizione > p1.Posizione)
                    return -1;
                return 0;
            }
        });
        //getting data from the list.
        TournamentPosition[] mArray = mCollection.toArray(new TournamentPosition[mTournament.Piazzamenti.size()]);
        mList.setAdapter(new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,mArray));
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListView mlist = findViewById(R.id.simple_list);
                TournamentPosition mPos = (TournamentPosition) mlist.getAdapter().getItem(i);
                mAlertDialog.setTitle(mPos.toString())
                        .setMessage(mPos.toDetailString())
                        .setNeutralButton(R.string.Chiudi, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mBuiltDialog.dismiss();
                            }
                        })
                        .setIcon(R.drawable.ic_trophy_black_24dp);
                mBuiltDialog = mAlertDialog.create();
                mBuiltDialog.show();
            }
        });
    }
}
