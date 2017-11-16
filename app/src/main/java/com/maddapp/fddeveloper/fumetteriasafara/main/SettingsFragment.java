package com.maddapp.fddeveloper.fumetteriasafara.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.UserManager;
import com.maddapp.fddeveloper.fumetteriasafara.R;


public class SettingsFragment extends Fragment {
    private EditText name;
    private EditText surname;
    private FirebaseAuth mAuth;
    private EditText bugText;
    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
                // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_option, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        name = view.findViewById(R.id.editName);
        name.setText(UserManager.CurrentUserData.Nome);
        surname = view.findViewById(R.id.editSurname);
        surname.setText(UserManager.CurrentUserData.Cognome);
        view.findViewById(R.id.buttonSetName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().trim().equals(""))
                    Toast.makeText(getActivity(),"Inserisci un nome!",Toast.LENGTH_SHORT).show();
                else if(name.getText().toString().trim().equals(""))
                    Toast.makeText(getActivity(),"Inserisci un cognome!",Toast.LENGTH_SHORT).show();
                else{
                    UserManager.CurrentUserData.Nome = name.getText().toString().trim();
                    UserManager.CurrentUserData.Cognome = surname.getText().toString().trim();
                    UserManager.writeUser(mAuth.getCurrentUser());
                }
            }
        });
        view.findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.logout();
            }
        });
        bugText= view.findViewById(R.id.bugText);
        view.findViewById(R.id.bugButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.makeLog(bugText.getText().toString());
                bugText.setText("");
                Toast.makeText(getContext(),"Errore inviato. Grazie!", Toast.LENGTH_SHORT).show();
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
        void logout();
        void makeLog(String text);
    }

}
