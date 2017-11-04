package com.maddapp.fddeveloper.fumetteriasafara.tournament;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maddapp.fddeveloper.fumetteriasafara.adapters.PlayerViewAdapter;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.PositionRecap;
import com.maddapp.fddeveloper.fumetteriasafara.R;

import java.util.List;

/**
 * An overall ladder for the championship. Shown as a list it displays the players, them score and them ranking (1st 2nd 3rd or more)
 */
public class FragmentPlayerLadder extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private RecyclerView mrecyclerview;
    private List<PositionRecap> items;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentPlayerLadder() {
    }

    public static FragmentPlayerLadder newInstance() {
        FragmentPlayerLadder fragment = new FragmentPlayerLadder();
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
        View view = inflater.inflate(R.layout.fragment_giocatore_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mrecyclerview = (RecyclerView) view;
            mrecyclerview.setLayoutManager(new LinearLayoutManager(context));
            mrecyclerview.setAdapter(new PlayerViewAdapter(items,null));
        }
        return view;
    }

    public void setList(List<PositionRecap> items){
        items = PositionRecap.RatePosizioni(items);
        if(mrecyclerview!= null)
            mrecyclerview.setAdapter(new PlayerViewAdapter(items,null));
        this.items = items;
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

    public interface OnListFragmentInteractionListener {
        void onClassificaGiocatoreInteraction(Object item);
    }
}
