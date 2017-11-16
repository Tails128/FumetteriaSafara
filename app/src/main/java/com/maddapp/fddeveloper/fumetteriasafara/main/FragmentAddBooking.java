package com.maddapp.fddeveloper.fumetteriasafara.main;

import android.content.Context;
import android.nfc.FormatException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.maddapp.fddeveloper.fumetteriasafara.R;
import com.maddapp.fddeveloper.fumetteriasafara.adapters.SimpleSpinnerItem;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.Book;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FragmentAddBooking extends DialogFragment {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();
    private Map<String, Book> books = new HashMap<>();
    private Spinner mSpinner;
    private EditText startingNumber;
    Context ctx;

    public FragmentAddBooking() {
        //the constructor must set child event listeners to populate the spinner.
        //books is the support list which keeps the book list
        reference.child("Books").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                books.put(dataSnapshot.getKey(), dataSnapshot.getValue(Book.class));
                setSpinner();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                books.put(dataSnapshot.getKey(), dataSnapshot.getValue(Book.class));
                setSpinner();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                books.remove(dataSnapshot.getKey());
                setSpinner();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * parameterless initializator, adds a child event listener to keep the booklist updated.
     * @return a new instance of FragmentAddBooking
     */
    public static FragmentAddBooking newInstance() {
        FragmentAddBooking fragment = new FragmentAddBooking();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getActivity();        //context-related error prevention
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_books,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //dismiss button
        view.findViewById(R.id.btnCancella).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        //confirm button
        view.findViewById(R.id.btnConferma).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //First check if there's a selected book
            String id = ((SimpleSpinnerItem)mSpinner.getSelectedItem()).id;
            if(id == null || id.equals("")){
                Toast.makeText(ctx, ctx.getText(R.string.error_comic_spinner), Toast.LENGTH_SHORT).show();
                return;
            }
            //then we check if a comic number is selected (0 is available in case there are "number 0 books"
            int comicNumber;
            try{
                comicNumber =Integer.parseInt(startingNumber.getText().toString());
                if(comicNumber < 0)
                    throw new FormatException("Format exception in fragment add booking");
            }
            catch(Exception e){
                Toast.makeText( ctx, ctx.getText(R.string.error_comic_number), Toast.LENGTH_SHORT).show();
                return;
            }
            //finally we put in the user's temporary bookings the key-value pair corresponding to the booking he selected
            String query = String.format(Locale.ITALIAN,"TempBookings/%s/%s", FirebaseAuth.getInstance().getUid(), id);
            reference.child(query).setValue(comicNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(ctx, ctx.getString(R.string.success_comic_booking), Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            });
            }
        });
        mSpinner = view.findViewById(R.id.spinner);
        startingNumber = view.findViewById(R.id.starting_number);
        setSpinner();       //synchronize booklist with spinner
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * A function used to sync the spinner with the "books" list.
     * It provides a default message if there's no available book
     */
    private void setSpinner(){
        //the process starts only if the spinner has been initialized
        if(mSpinner != null) {
            SimpleSpinnerItem[] items = {new SimpleSpinnerItem("",ctx.getString(R.string.error_comic_no_available_comic))};
            if (books.size() != 0) {            //if there are books the spinner is populated
                items = new SimpleSpinnerItem[books.size()];
                int i = 0;
                for (String key : books.keySet()) {
                    items[i] = new SimpleSpinnerItem(key, books.get(key).getName());
                }
            }
            //spinner's adapter creation and application
            ArrayAdapter<SimpleSpinnerItem> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinner.setAdapter(adapter);
        }
    }
}
