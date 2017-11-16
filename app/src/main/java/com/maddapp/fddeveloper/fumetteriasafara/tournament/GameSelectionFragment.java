package com.maddapp.fddeveloper.fumetteriasafara.tournament;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.maddapp.fddeveloper.fumetteriasafara.R;


/**
 * An activity displaying the store's TCG games to chose from
 */
public class GameSelectionFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Context context;

    public GameSelectionFragment() {
        // Required empty public constructor
    }

    public static GameSelectionFragment newInstance() {
        GameSelectionFragment fragment = new GameSelectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_selection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ImageButton magicButton = view.findViewById(R.id.magicButton);
        magicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFragmentGameInteraction(context.getString(R.string.Magic));
            }
        });

        ImageButton fowButton = view.findViewById(R.id.fowButton);
        fowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFragmentGameInteraction(context.getString(R.string.FOW));
            }
        });

        ImageButton yugiButton = view.findViewById(R.id.yugiohButton);
        yugiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFragmentGameInteraction(context.getString(R.string.Yugi));
            }
        });

        ImageButton pokeButton = view.findViewById(R.id.pokemonButton);
        pokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFragmentGameInteraction(context.getString(R.string.Pokemon));
            }
        });

        super.onViewCreated(view, savedInstanceState);
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
        void onFragmentGameInteraction(String destination);
    }
}
