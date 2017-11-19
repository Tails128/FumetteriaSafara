package com.maddapp.fddeveloper.fumetteriasafara.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maddapp.fddeveloper.fumetteriasafara.R;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.TransactionView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionRecyclerViewAdapter extends RecyclerView.Adapter<TransactionRecyclerViewAdapter.ViewHolder> {

    private final List<TransactionView> mValues;
    private final onTransactionListInteraction mListener;

    public TransactionRecyclerViewAdapter(List<TransactionView> items, onTransactionListInteraction listener) {
        mValues = items == null? new ArrayList<TransactionView>() : items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_transaction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if (holder.mItem.Valore >= 0){
            holder.mImageview.setImageResource(R.drawable.ic_add_circle_black_24dp);
            holder.mImageview.setColorFilter(ContextCompat.getColor(holder.mImageview.getContext(), android.R.color.holo_green_light));
        }
        else{
            holder.mImageview.setImageResource(R.drawable.ic_remove_circle_black_24dp);
            holder.mImageview.setColorFilter(ContextCompat.getColor(holder.mImageview.getContext(), android.R.color.holo_red_light));
        }

        String text = String.format(Locale.ITALIAN,"%1$,.2f € ", holder.mItem.Valore);
        holder.mContentView.setText(text);
        holder.mSubContentView.setText(holder.mItem.Descrizione);

        if(mListener != null)
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onInteraction(holder.mItem);
                }
            }
        );
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final TextView mSubContentView;
        public final ImageView mImageview;
        public TransactionView mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mSubContentView = view.findViewById(R.id.subcontent);
            mImageview = view.findViewById(R.id.image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public interface onTransactionListInteraction{
        void onInteraction(TransactionView t);
    }
}
