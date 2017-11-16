package com.maddapp.fddeveloper.fumetteriasafara.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maddapp.fddeveloper.fumetteriasafara.R;
import com.maddapp.fddeveloper.fumetteriasafara.adapters.BookingRecyclerViewAdapter;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.Booking;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class BookingFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private RecyclerView mrecyclerview;
    private List<Booking> items = new ArrayList<>();
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookingFragment() {
        final List<Booking> result = new ArrayList<>();
        String query = String.format("Bookings/%s",mAuth.getCurrentUser().getUid());
        final boolean regularBooking = true;
        reference.child(query).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                childAdded(dataSnapshot, regularBooking);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                childChanged(dataSnapshot, regularBooking);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                childRemoved(dataSnapshot.getKey(),regularBooking);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        final boolean temporaryBooking = false;
        query = String.format("TempBookings/%s",mAuth.getCurrentUser().getUid());
        reference.child(query).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                childAdded(dataSnapshot, temporaryBooking);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                childChanged(dataSnapshot, temporaryBooking);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                childRemoved(dataSnapshot.getKey(),temporaryBooking);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static BookingFragment newInstance() {
        BookingFragment fragment = new BookingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_list, container, false);

        View v = view.findViewById(R.id.list);
        // Set the adapter
        if (v instanceof RecyclerView) {
            Context context = view.getContext();
            mrecyclerview = (RecyclerView) v;
            mrecyclerview.setLayoutManager(new LinearLayoutManager(context));
            mrecyclerview.setAdapter(new BookingRecyclerViewAdapter(items, null));
            mrecyclerview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        }
        view.findViewById(R.id.FABBooking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAddBooking();
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void childAdded(final DataSnapshot booking, final boolean confirmed){
        DatabaseReference nameFinder = database.getReference("Books/" + booking.getKey());
        nameFinder.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Book b = dataSnapshot.getValue(Book.class);
                String title = "";
                if(b!= null)
                    title = b.getName();
                items.add(new Booking(booking.getKey(), title, booking.getValue(int.class), confirmed));
                setList(items);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void childChanged(DataSnapshot dataSnapshot, boolean confirmed){
        for(Booking b : items){
            if(b.getId().equals(dataSnapshot.getKey()) && b.isConfirmed() == confirmed)
            {
                b.setNumber(dataSnapshot.getValue(int.class));
                setList(items);
                break;
            }
        }
    }

    public void childRemoved(String key, boolean confirmed){
        int i = -1;
        for(Booking b : items){
            i++;
            if(b.getId().equals(key) && b.isConfirmed() == confirmed)
                break;
            items.remove(i);
            setList(items);
        }
    }

    public void setList(List<Booking> items){
        if(mrecyclerview!= null)
            mrecyclerview.setAdapter(new BookingRecyclerViewAdapter(items,null));
        this.items = items;
    }

    public interface OnListFragmentInteractionListener {
        void onAddBooking();
    }
}
