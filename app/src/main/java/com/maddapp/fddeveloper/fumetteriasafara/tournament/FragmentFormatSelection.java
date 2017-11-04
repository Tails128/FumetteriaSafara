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

import com.maddapp.fddeveloper.fumetteriasafara.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment for the format selection. Every format is displayed as a list item. On click the
 * onFragmentClassificheFormatiInteraction is called
 */
public class FragmentFormatSelection extends Fragment {

    private List<String> mFormati = new ArrayList<>();
    private ListView mListFormati;

    private OnFragmentInteractionListener mListener;

    public FragmentFormatSelection() {
        // Required empty public constructor
    }

    public static FragmentFormatSelection newInstance() {
        FragmentFormatSelection fragment = new FragmentFormatSelection();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_list, container, false);
        mListFormati = view.findViewById(R.id.list_fragment);
        setList(mFormati);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onFragmentClassificheFormatiInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setList(List<String> formati){
        mFormati = formati;
        if(mFormati.size()!=0 &&getContext()!= null) {
            mListFormati.setAdapter(new ArrayAdapter(getContext() , android.R.layout.simple_list_item_1, mFormati.toArray()));
            mListFormati.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mListener.onFragmentClassificheFormatiInteraction(mListFormati.getAdapter().getItem(i).toString());
                }
            });
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentClassificheFormatiInteraction(String formato);
    }
}
