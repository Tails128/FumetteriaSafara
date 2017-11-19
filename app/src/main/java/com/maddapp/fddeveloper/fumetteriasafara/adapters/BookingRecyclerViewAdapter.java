package com.maddapp.fddeveloper.fumetteriasafara.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maddapp.fddeveloper.fumetteriasafara.R;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.Booking;
import com.maddapp.fddeveloper.fumetteriasafara.main.BookingFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

public class BookingRecyclerViewAdapter extends RecyclerView.Adapter<BookingRecyclerViewAdapter.ViewHolder> {

    private final List<Booking> mValues;
    private final OnListFragmentInteractionListener mListener;

    public BookingRecyclerViewAdapter(List<Booking> items, OnListFragmentInteractionListener listener) {
        mValues = items == null? new ArrayList<Booking>() : items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if(! holder.mItem.isConfirmed())
            holder.mImageview.setImageResource(R.drawable.ic_hourglass_empty_black_24dp);
        else
            holder.mImageview.setImageResource(R.drawable.ic_done_black_24dp);
        holder.mContentView.setText(mValues.get(position).toString());

        final String name = holder.mItem.getComicName();
        final int number = holder.mItem.getNumber();
        final boolean confirmed = holder.mItem.isConfirmed();

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.showBooking(name, number, confirmed);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final ImageView mImageview;
        public Booking mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mImageview = view.findViewById(R.id.image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
