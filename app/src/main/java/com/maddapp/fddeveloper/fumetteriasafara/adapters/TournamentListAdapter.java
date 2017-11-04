package com.maddapp.fddeveloper.fumetteriasafara.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maddapp.fddeveloper.fumetteriasafara.R;

/**
 * List adapter for the tournamente entity.
 */

public class TournamentListAdapter extends ArrayAdapter<TournamentListViewItem> {


    public TournamentListAdapter(@NonNull Context context, TournamentListViewItem[] Items) {
        super(context, R.layout.tournament_list_item_layout, Items);
    }

    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent){
        //TODO: recycler view ?
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.tournament_list_item_layout, parent, false);

        TournamentListViewItem current = getItem(position);
        if(current != null)
        {
            String text = "";
            if (current.text != null)
                text = current.text;
            TextView element = customView.findViewById(R.id.listItemText);
            ImageView imgBtn = customView.findViewById(R.id.imageButton2);

            element.setText(text);

            imgBtn.setOnClickListener(current.callBackButton);
            element.setOnClickListener(current.callBackText);
        }
        return customView;

    }
}
