package com.maddapp.fddeveloper.fumetteriasafara.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maddapp.fddeveloper.fumetteriasafara.R;


/**
 * A fragment to manage the home section: it recaps the transactions and allows you to see
 * the transactions in detail
 */
public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Button storeButton;
    private Button tournamentButton;

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();
    FirebaseAuth mAuth;
    FirebaseUser User;
    private Context ctx;

    /**
     * Empty constructor.
     * Please use {@Link newInstance} instead
     */
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * simple new instance constructor. Atm it is not doing additional actions
     * @return a new working and correctly initialized HomeFragment
     */
    public static HomeFragment newInstance() {
//        HomeFragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return new HomeFragment();
    }

    /**
     * onCreate, adds FireBase listeners
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //context is saved to avoid multiple getActivity() calls
        ctx = getActivity();

        //Firebase is requested to return the transactions recap
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        //recap for Tournament points
        String query = String.format("Transazioni/%s/Tornei/Somma", User.getUid());
        reference.child(query).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double val = dataSnapshot.getValue(Double.class);
                if(val == null)
                    val = 0d;
                String toFormat = ctx.getString(R.string.punti_torneo) + " %1$,.2f €";
                String text = String.format(toFormat,val);
                tournamentButton.setText(text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //recap for Shop points
        query = String.format("Transazioni/%s/Transazioni/Somma", User.getUid());
        reference.child(query).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double val = dataSnapshot.getValue(Double.class);
                if(val == null)
                    val = 0d;
                String toFormat = ctx.getString(R.string.punti_madda) + " %1$,.2f €";
                String text = String.format(toFormat,val);
                storeButton.setText(text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * default onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * onViewCreated, sets the listener for the two buttons and sets the variables for the other
     * functions
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        storeButton = view.findViewById(R.id.buttonPuntiMadda);
        tournamentButton = view.findViewById(R.id.buttonPuntiTorneo);
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFragmentHomeInteraction("Transazioni");
            }
        });
        tournamentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFragmentHomeInteraction("Tornei");
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                              + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * the interface requests a function tu handle the onClick for the buttons
     */
    public interface OnFragmentInteractionListener {
        void onFragmentHomeInteraction(String value);
    }
}
