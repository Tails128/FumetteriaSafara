package com.maddapp.fddeveloper.fumetteriasafara.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.Championship;
import com.maddapp.fddeveloper.fumetteriasafara.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment for the selection of championships: a list of championships is displayed
 * and after one's clicked the onChampionshipSelection interaction's called.
 */
public class ChampionshipSelectionFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String id;

    private OnFragmentInteractionListener mListener;

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();

    private List<Championship> championships = new ArrayList<>();
    private ListView mListView;

    /**
     * default empty public constructor, please use newInstance instead
     */
    public ChampionshipSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Function which initializes a new instance of ChampionshipFragment. opposed to the empty
     * constructor, it sets the passed championshipId
     * @param championshipId
     * @return
     */
    public static ChampionshipSelectionFragment newInstance(String championshipId) {
        ChampionshipSelectionFragment fragment = new ChampionshipSelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, championshipId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * onCreate function. It gets the tournament id from the bundle and appends a listener to FireBase
     * to gather the championships of the last three years. The year restriction is set in order to avoid
     * downloading too much data.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //getting id
            id = getArguments().getString(ARG_PARAM1);
            String query = String.format("%s/Campionati",id);
            //appending listener and populating list
            reference.child(query).limitToLast(3).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    championships.clear();
                    for(DataSnapshot yearSnapshot : dataSnapshot.getChildren()){
                        for(DataSnapshot snap : yearSnapshot.getChildren()){
                            Championship c = snap.getValue(Championship.class);
                            if(c!= null) {
                                c.id = snap.getKey();
                                championships.add(c);
                            }
                        }
                    }
                    ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1 , championships.toArray());
                    mListView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        else
            throw new UnsupportedOperationException(getActivity().toString() + "No game was selected");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.simple_list_activity, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = view.findViewById(R.id.simple_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Championship c = (Championship) adapterView.getItemAtPosition(i);
                mListener.onChampionshipSelection(c.id, id);
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

    public interface OnFragmentInteractionListener {
        void onChampionshipSelection(String id, String game);   //interface required to forward the selection flow. It must implement the action to take
                                                                //after the tournament is selected
    }
}
