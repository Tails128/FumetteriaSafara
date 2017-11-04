package com.maddapp.fddeveloper.fumetteriasafara.tournament;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.Tournament;
import com.maddapp.fddeveloper.fumetteriasafara.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment for the selection of the tournament. Each Tournament is displayed as
 * a list item, on click it calls the onFragmentClassificheTorneiInteraction interface
 */
public class FragmentTournamentSelection extends Fragment {

    private List<Tournament> mTornei = new ArrayList<>();
    private ListView mListTornei;

    private OnFragmentInteractionListener mListener;

    public FragmentTournamentSelection() {
        // Required empty public constructor
    }

    public static FragmentTournamentSelection newInstance() {
        FragmentTournamentSelection fragment = new FragmentTournamentSelection();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_simple_list, container, false);
        mListTornei = view.findViewById(R.id.list_fragment);
        setList(mTornei);
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

    public void setList(List<Tournament> tornei){
        mTornei = tornei;
        if(tornei.size()!=0 && getContext()!=null) {
            mListTornei.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, mTornei.toArray()));
            mListTornei.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Tournament t = (Tournament) mListTornei.getAdapter().getItem(i);
                    mListener.onFragmentClassificheTorneiInteraction(t.id);
                }
            });
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentClassificheTorneiInteraction(String id);
    }
}
