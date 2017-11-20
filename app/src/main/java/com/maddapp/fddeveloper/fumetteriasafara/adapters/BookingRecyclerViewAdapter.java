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

/**
 * A RecyclerView adapter for the Booking entity.
 * It displays icons asserting if the booking is pending or confirmed and a text containing the booking's
 * comic book's name and first number.
 */
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

    /**
     * onBind variables are set, the icon swaps according to the mItem.isConfirmed() and an OnClick
     * listener is set according to the showBooking interface.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //swapping accordingly to isConfirmed()
        if(! holder.mItem.isConfirmed())
            holder.mImageView.setImageResource(R.drawable.ic_hourglass_empty_black_24dp);
        else
            holder.mImageView.setImageResource(R.drawable.ic_done_black_24dp);
        holder.mContentView.setText(mValues.get(position).toString());

        //getting data for the onClick
        final String name = holder.mItem.getComicName();
        final int number = holder.mItem.getNumber();
        final boolean confirmed = holder.mItem.isConfirmed();

        //setting onClick
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
        final View mView;
        final TextView mContentView;        // TextView in the view
        final ImageView mImageView;         // ImageView in the view
        Booking mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mImageView = view.findViewById(R.id.image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
