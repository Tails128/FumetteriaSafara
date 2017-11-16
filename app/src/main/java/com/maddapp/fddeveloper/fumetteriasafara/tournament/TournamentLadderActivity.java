package com.maddapp.fddeveloper.fumetteriasafara.tournament;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    private Tournament mTorneo;
    private AlertDialog.Builder malertdialog;
    private AlertDialog mBuiltDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTorneo = (Tournament) getIntent().getSerializableExtra("Tournament");
        if(mTorneo == null){
            finish();
            return;
        }
        setContentView(R.layout.simple_list_activity);
        final ListView mlist = findViewById(R.id.simple_list);
        malertdialog = new AlertDialog.Builder(TournamentLadderActivity.this, R.style.MyAlertDialogStyle);
        List<TournamentPosition> mCollection = new ArrayList<>(mTorneo.Piazzamenti.values());
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
        TournamentPosition[] mArray = mCollection.toArray(new TournamentPosition[mTorneo.Piazzamenti.size()]);
        mlist.setAdapter(new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,mArray));
        mlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListView mlist = findViewById(R.id.simple_list);
                TournamentPosition mPos = (TournamentPosition) mlist.getAdapter().getItem(i);
                malertdialog.setTitle(mPos.toString())
                        .setMessage(mPos.toDetailString())
                        .setNeutralButton(R.string.Chiudi, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mBuiltDialog.dismiss();
                            }
                        })
                        .setIcon(R.drawable.ic_trophy_black_24dp);
                mBuiltDialog = malertdialog.create();
                mBuiltDialog.show();
            }
        });
    }
}
