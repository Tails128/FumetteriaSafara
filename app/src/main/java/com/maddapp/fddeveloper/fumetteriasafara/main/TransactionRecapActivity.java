package com.maddapp.fddeveloper.fumetteriasafara.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maddapp.fddeveloper.fumetteriasafara.adapters.TransactionAdapter;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.Transaction;
import com.maddapp.fddeveloper.fumetteriasafara.R;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity with the details of the transactions
 */

public class TransactionRecapActivity extends AppCompatActivity {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();
    private FirebaseAuth mAuth;
    private FirebaseUser User;
    private List<Transaction> transazioni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_list_activity);

        String mode = getIntent().getStringExtra("mode");
        if(!mode.equals("Tornei") && !mode.equals("Transazioni"))
            finish();
        transazioni = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        String query = String.format("Transazioni/%s/%s", User.getUid(),mode);
        reference.child(query).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot transazione : dataSnapshot.getChildren()){
                    String temp = transazione.getKey();
                    if(! temp.trim().equals("Somma"))
                        transazioni.add(transazione.getValue(Transaction.class));
                }
                setList(transazioni);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO
            }
        });
    }

    public void setList(List<Transaction> list){
        transazioni = list;
        ListView temp = findViewById(R.id.simple_list);
        temp.setAdapter(new TransactionAdapter(getBaseContext(), list.toArray(new Transaction[list.size()])));
    }
}
