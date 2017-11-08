package com.maddapp.fddeveloper.fumetteriasafara.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.Transaction;
import com.maddapp.fddeveloper.fumetteriasafara.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * List adapter for the transaction entity
 */
public class TransactionAdapter extends ArrayAdapter<Transaction> {

    private Transaction[] Items;

    public TransactionAdapter(@NonNull Context context, Transaction[] Items) {
        super(context, R.layout.tournament_list_item_layout, Items);
        this.Items = Items;
    }

    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent){

        TwoLineListItem tll;
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View customView = inflater.inflate(android.R.layout.simple_list_item_2,parent,false);
            tll = (TwoLineListItem) customView;
        }
        else
            tll = (TwoLineListItem) convertView;

        TextView t1 = tll.getText1();
        TextView t2 = tll.getText2();
        t1.setText(Items[position].Descrizione);
        Date date = new Date(Items[position].Data);
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ITALIAN);
        String string2 = "%1$,.2f € | " + df.format(date);
        string2 = String.format(string2,Items[position].Valore, date);
        t2.setText(string2);



/*        Transaction current = getItem(position);
        String text = current.text;
        TextView element = (TextView) customView.findViewById(R.id.listItemText);

        element.setText(text);

        element.setOnClickListener(current.callBackText);
*/        return tll;

    }
}
