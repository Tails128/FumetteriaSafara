package com.maddapp.fddeveloper.fumetteriasafara.main;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.maddapp.fddeveloper.fumetteriasafara.adapters.TransactionRecyclerViewAdapter;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.TransactionView;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.Transaction;
import com.maddapp.fddeveloper.fumetteriasafara.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * An activity with the details of the transactions.
 */

public class TransactionRecapActivity extends AppCompatActivity implements TransactionRecyclerViewAdapter.onTransactionListInteraction {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();
    private List<TransactionView> items = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseUser User;
    RecyclerView mrecyclerView;

    /**
     * on create function, sets up the variables and the FireBase listener
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_recycler);

        //mrecyclerView is assigned for the setList function
        mrecyclerView = findViewById(R.id.list);
        mrecyclerView.addItemDecoration(new DividerItemDecoration(TransactionRecapActivity.this, DividerItemDecoration.VERTICAL));
        String mode = getIntent().getStringExtra("mode");
        //if the mode is not supported the activity finishes
        if(!mode.equals("Tornei") && !mode.equals("Transazioni"))
            finish();
        //items is initialized
        items = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        //user's transactions are read accordingly to the mode which was passed
        String query = String.format("Transazioni/%s/%s", User.getUid(),mode);
        reference.child(query).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot transazione, String s) {
                String temp = transazione.getKey();
                if(! temp.trim().equals("Somma")) {
                    TransactionView t = new TransactionView(transazione.getValue(Transaction.class), transazione.getKey());
                    items.add(t);
                }
                setList();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for(TransactionView t : items){
                    if(t.id.equals(dataSnapshot.getKey())){
                        Transaction transaction = dataSnapshot.getValue(Transaction.class);
                        t.Valore = transaction.Valore;
                        t.Descrizione = transaction.Descrizione;
                        t.Data = transaction.Data;
                        setList();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int i = 0;
                for(TransactionView t : items) {
                    if (t.id.equals(dataSnapshot.getKey())) {
                        break;
                    }
                    i++;
                }
                items.remove(i);
                setList();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * the function which needs to be called every time {@Link items} is updated
     */
    public void setList(){
        mrecyclerView.setAdapter(new TransactionRecyclerViewAdapter(items, this));
    }

    /**
     * On interaction interface function for the custom TransactionRecyclerViewAdapter adapter.
     * On interaction an AlertDialog displaying all the data related to the transaction is shown.
     * @param transaction the transaction which needs to be fully displayed.
     */
    @Override
    public void onInteraction(TransactionView transaction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALIAN);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(transaction.Data);
        String date = df.format(calendar.getTime());
        String message = String.format("Transazione per un valore di: %1$,.2f €\nEffettuata in data: " + date,transaction.Valore);
        builder.setTitle(transaction.Descrizione)
                .setMessage(message)
                .setPositiveButton("Ok",null);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        dialog.show();
    }
}
