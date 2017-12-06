package com.maddapp.fddeveloper.fumetteriasafara.tournament;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.Tournament;
import com.maddapp.fddeveloper.fumetteriasafara.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A fragment for the selection of the tournament. Each Tournament is displayed as
 * a list item, on click it calls the onFragmentTournamentLadderInteraction interface
 */
public class FragmentTournamentSelection extends Fragment {

    private List<Tournament> mTournaments = new ArrayList<>();
    private ListView mTournamentList;
    Context ctx;

    private OnFragmentInteractionListener mListener;

    /**
     * default public empty constructor. Please avoid using this and use {@Link newInstance} instead
     */
    public FragmentTournamentSelection() {
        // Required empty public constructor
    }

    /**
     * public newInstance, the optimal constructor for this class. Atm it is empty
     * @return a fully operating FragmentTournamentSelection
     */
    public static FragmentTournamentSelection newInstance() {
//        FragmentTournamentSelection fragment = new FragmentTournamentSelection();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return new FragmentTournamentSelection();
    }

    /**
     * onCreate variables are set and the list is synchronized. Also the context is taken in order to avoid
     * multiple times getActivity()
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_simple_list, container, false);
        ctx = getActivity();
        mTournamentList = view.findViewById(R.id.list_fragment);
        setList(mTournaments);
        return view;
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
     * updates the UI accordingly to the passed array
     * @param tournaments
     */
    public void setList(List<Tournament> tournaments){
        mTournaments = tournaments;

        Collections.sort(mTournaments, new Comparator<Tournament>() {
            @Override
            public int compare(Tournament tournament, Tournament t1) {
                if(tournament.DataTorneo < t1.DataTorneo)
                    return -1;
                if(tournament.DataTorneo > t1.DataTorneo)
                    return 1;
                return 0;
            }
        });
        if(tournaments.size()!=0 && ctx != null) {
            mTournamentList.setAdapter(new ArrayAdapter(ctx, android.R.layout.simple_list_item_1, mTournaments.toArray()));
            mTournamentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Tournament t = (Tournament) mTournamentList.getAdapter().getItem(i);
                    mListener.onFragmentTournamentLadderInteraction(t.id);
                }
            });
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentTournamentLadderInteraction(String id);      //interface for the list item onclick interaction
    }
}
