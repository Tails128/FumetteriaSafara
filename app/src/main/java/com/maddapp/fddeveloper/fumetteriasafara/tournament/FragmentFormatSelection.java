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
 * onFragmentFormatLadderInteraction is called
 */
public class FragmentFormatSelection extends Fragment {

    private List<String> mFormats = new ArrayList<>();
    private ListView mListFormats;

    private OnFragmentInteractionListener mListener;

    /**
     * default empty constructor. Please avoid using this and use {@Link FragmentFormatSelection.newInstance} instead
     */
    public FragmentFormatSelection() {
        // Required empty public constructor
    }

    /**
     * default empty newInstance constructor. No parameters are needed.
     * @return
     */
    public static FragmentFormatSelection newInstance() {
//        FragmentFormatSelection fragment = new FragmentFormatSelection();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return new FragmentFormatSelection();
    }

    /**
     * onCreate view: sets the variables for {@Link setList} and calls it
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_list, container, false);
        mListFormats = view.findViewById(R.id.list_fragment);
        setList();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onFragmentFormatLadderInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * The function which updates the list in the UI. needs to be called everytime the list is updated.
     */
    public void setList(){
        if(mFormats.size()!=0 &&getContext()!= null) {
            mListFormats.setAdapter(new ArrayAdapter<>(getContext() , android.R.layout.simple_list_item_1, mFormats.toArray()));
            mListFormats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mListener.onFragmentFormatLadderInteraction(mListFormats.getAdapter().getItem(i).toString());
                }
            });
        }
    }

    public void setList(List<String> list){
        mFormats = list;
        setList();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentFormatLadderInteraction(String format);  //interface which implements the format list element onClick action
    }
}
