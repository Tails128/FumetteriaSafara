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

    /**
     * empty public constructor. Please avoid using this and use {@Link newInstance} instead
     */
    public SettingsFragment() {
                // Required empty public constructor
    }

    /**
     * simple new instance constructor. Atm it is not doing additional actions
     * @return a new working and correctly initialized SettingsFragment
     */
    public static SettingsFragment newInstance() {
//        SettingsFragment fragment = new SettingsFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_option, container, false);
    }

    /**
     * onViewCreated simply sets the onClick listeners for the name update, the request to update
     * name and surname and for the logout button
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        //setting variables
        name = view.findViewById(R.id.editName);
        name.setText(UserManager.CurrentUserData.Nome);
        surname = view.findViewById(R.id.editSurname);
        surname.setText(UserManager.CurrentUserData.Cognome);
        bugText= view.findViewById(R.id.bugText);
        //setting onclick for name update
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
        //setting onclick for the logout button
        view.findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.logout();
            }
        });
        //setting onclick for the logout button
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
        void logout();              //an iterface for the logout is required
        void makeLog(String text);  //an interface to log errors which the users want to signal
    }

}
