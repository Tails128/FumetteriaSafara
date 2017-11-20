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
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     * Please avoid using this and use the {@Link newInstance} function instead.
     */
    public BookingFragment() {
        String query = String.format("Bookings/%s",mAuth.getCurrentUser().getUid());
        //adding child listener for confirmed bookings
        final boolean confirmedBooking = true;
        reference.child(query).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                childAdded(dataSnapshot, confirmedBooking);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                childChanged(dataSnapshot, confirmedBooking);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                childRemoved(dataSnapshot.getKey(),confirmedBooking);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //adding child listener for NOT confirmed bookings
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

    /**
     * simple new instance constructor. Atm it is not doing additional actions
     * @return a new working and correctly initialized BookingFragment
     */
    public static BookingFragment newInstance() {
//        BookingFragment fragment = new BookingFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return new BookingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * onCreateView method.
     * This method simply assigns the {@Link mrecyclerview} variable or re-uses it in order to
     * display the bookings. It also sets the adapter containing the bookings.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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
            mrecyclerview.setAdapter(new BookingRecyclerViewAdapter(items, mListener));
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

    /**
     * Decentralized function to handle the childAdded firing from the firebase listeners.
     * This function was decentralized since the two different paths to retrieve the booking items
     * return the same item structure. This is done in order to set the firebase rules in such a way
     * that the user cannot write on the confirmed items.
     * @param booking
     * @param confirmed
     */
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
                setList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Decentralized function to handle the childChanged firing from the firebase listeners.
     * This function was decentralized since the two different paths to retrieve the booking items
     * return the same item structure. This is done in order to set the firebase rules in such a way
     * that the user cannot write on the confirmed items.
     * @param booking
     * @param confirmed
     */
    public void childChanged(DataSnapshot booking, boolean confirmed){
        for(Booking b : items){
            if(b.getId().equals(booking.getKey()) && b.isConfirmed() == confirmed)
            {
                b.setNumber(booking.getValue(int.class));
                setList();
                break;
            }
        }
    }

    /**
     * Decentralized function to handle the childRemoved firing from the firebase listeners.
     * This function was decentralized since the two different paths to retrieve the booking items
     * return the same item structure. This is done in order to set the firebase rules in such a way
     * that the user cannot write on the confirmed items.
     * @param key
     * @param confirmed
     */
    public void childRemoved(String key, boolean confirmed){
        int i = -1;
        for(Booking b : items){
            i++;
            if(b.getId().equals(key) && b.isConfirmed() == confirmed)
                break;
        }
        items.remove(i);
        setList();
    }

    /**
     * This function needs to be called each time {@Link items} is called.
     * The function which updates the UI accordingly to the items in the {@Link items} variable
     */
    public void setList(){
        if(mrecyclerview!= null)
            mrecyclerview.setAdapter(new BookingRecyclerViewAdapter(items,null));
    }

    public interface OnListFragmentInteractionListener {
        void onAddBooking();    //interface to handle the request to add a booking
        void showBooking(String title, int comic_number, boolean confirmed);    //interface to handle the request to show the booking in detail
    }
}
