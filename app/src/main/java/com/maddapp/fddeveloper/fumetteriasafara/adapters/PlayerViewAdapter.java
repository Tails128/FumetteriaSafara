package com.maddapp.fddeveloper.fumetteriasafara.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.PositionRecap;
import com.maddapp.fddeveloper.fumetteriasafara.R;
import com.maddapp.fddeveloper.fumetteriasafara.tournament.FragmentPlayerLadder.OnListFragmentInteractionListener;

import java.util.List;

/**
 * Adapter made to show player scores with a trophy symbol coloured according to them position, and to also show them points
 */
public class PlayerViewAdapter extends RecyclerView.Adapter<PlayerViewAdapter.ViewHolder> {

    private final List<PositionRecap> mValues;
    private final OnListFragmentInteractionListener mListener;

    public PlayerViewAdapter(List<PositionRecap> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_players, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).Nome);
        holder.mPoints.setText(String.valueOf(mValues.get(position).Punteggio));

        Context mContext = holder.mImage.getContext();
        switch (holder.mItem.Posizione){
            case 1:
                holder.mImage.setColorFilter(ContextCompat.getColor(mContext, R.color.gold));
                break;
            case 2:
                holder.mImage.setColorFilter(ContextCompat.getColor(mContext, R.color.silver));
                break;
            case 3:
                holder.mImage.setColorFilter(ContextCompat.getColor(mContext, R.color.bronze));
                break;
            default:
                //TODO: non sta togliendo il trofeo
                holder.mImage.setColorFilter(null);
                break;
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onClassificaGiocatoreInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mValues == null)
            return 0;
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mContentView;
        final ImageView mImage;
        PositionRecap mItem;
        final TextView mPoints;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mImage = view.findViewById(R.id.ImagePosizione);
            mPoints = view.findViewById(R.id.points);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
