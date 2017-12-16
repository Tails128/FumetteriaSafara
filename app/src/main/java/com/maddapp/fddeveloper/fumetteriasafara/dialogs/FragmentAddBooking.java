package com.maddapp.fddeveloper.fumetteriasafara.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.TemporaryBooking;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class FragmentAddBooking extends DialogFragment {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();
    private Map<String, Book> books = new HashMap<>();
    private Spinner mSpinner;
    private EditText startingNumber;
    private EditText altBookName;
    private CheckBox newBookBox;
    Context ctx;

    /**
     * public constructor. It sets the FireBase listener for the comics to read.
     * Please avoid using this and use the {@Link newInstance} function instead.
     */
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
//        FragmentAddBooking fragment = new FragmentAddBooking();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return new FragmentAddBooking();
    }

    /**
     * simple onCreate function which stores the context in order to avoid calling getActivity multiple times
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getActivity();        //context-related error prevention
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
                    i++;
                }
            }
            //spinner's adapter creation and application
            ArrayAdapter<SimpleSpinnerItem> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinner.setAdapter(adapter);
        }
    }

    /**
     * This is the function which returns a Dialog with a spinner and an input text field to create
     * the booking to submit
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //creating a view for the 'content' chunk of the Dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_books, null);
        //populating the spinner inside the view and saving references for the onClick
        mSpinner = view.findViewById(R.id.spinner);
        setSpinner();
        startingNumber = view.findViewById(R.id.starting_number);
        altBookName = view.findViewById(R.id.newBookName);
        newBookBox = view.findViewById(R.id.notInListBox);
        newBookBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    altBookName.setVisibility(View.VISIBLE);
                    mSpinner.setVisibility(View.GONE);
                }
                else
                {
                    altBookName.setVisibility(View.GONE);
                    mSpinner.setVisibility(View.VISIBLE);
                }
            }
        });
        builder.setTitle(R.string.book_modal_title);                    //Dialog title
        builder.setView(view);                                          //Dialog content
        //Dialog actions
        builder.setNegativeButton(R.string.cancel_comic_ita, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        //the onclick for the positive button is set in a second moment since it needs to prevent the dismiss()
        builder.setPositiveButton(R.string.confirm_comic_ita, null);
        final AlertDialog dialog = builder.create();            //dialog saved for dismiss
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (newBookBox.isChecked()) {
                            //if the book is not in list (therefore new) we must add a new one

                            //getting book name
                            String bookName = altBookName.getText().toString().trim();
                            if (bookName.equals("")) {
                                Toast.makeText(ctx, ctx.getText(R.string.error_comic_name_empty), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //setting the first letter as uppercase
                            bookName = bookName.substring(0, 1).toUpperCase() + bookName.substring(1);

                            //checking if the name appears in the list
                            for (Book b : books.values()) {
                                if (b.getName().toLowerCase().equals(bookName.toLowerCase())) {
                                    Toast.makeText(ctx, ctx.getText(R.string.error_new_book_already_exists), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            //getting book id
                            int comicNumber = getComicNumber();
                            if(comicNumber == -1){
                                Toast.makeText(ctx, ctx.getText(R.string.error_comic_number), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // inserting temporary booking
                            String id = "TEMPBOOKING" + UUID.randomUUID().toString();
                            String query = String.format(Locale.ITALIAN, "TempBookings/%s/%s", FirebaseAuth.getInstance().getUid(), id);
                            TemporaryBooking newTemporaryBooking = new TemporaryBooking();
                            newTemporaryBooking.Nome = bookName;
                            newTemporaryBooking.Numero = comicNumber;
                            reference.child(query).setValue(newTemporaryBooking).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ctx, ctx.getString(R.string.success_comic_booking), Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            });


                        } else {
                            //getting book id
                            String id = ((SimpleSpinnerItem) mSpinner.getSelectedItem()).id;
                            if (id == null || id.equals("")) {
                                Toast.makeText(ctx, ctx.getText(R.string.error_comic_spinner), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //getting book N°"
                            int comicNumber = getComicNumber();
                            if(comicNumber == -1){
                                Toast.makeText(ctx, ctx.getText(R.string.error_comic_number), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //putting in the user's temporary bookings the key-value pair corresponding to the booking he selected
                            String query = String.format(Locale.ITALIAN, "TempBookings/%s/%s", FirebaseAuth.getInstance().getUid(), id);
                            reference.child(query).setValue(comicNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ctx, ctx.getString(R.string.success_comic_booking), Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            });
                        }
                    }
                });
                //finally we color the flat buttons according to the theme
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
            }
        });
        return dialog;
    }

    /**
     * attempts to get the comic book number
     * @return -1 if the comic book number is invalid, 0+ if the comic book is valid
     */
    private int getComicNumber(){
        int comicNumber = -1;
        try {
            comicNumber = Integer.parseInt(startingNumber.getText().toString());
            if (comicNumber < 0) {
                comicNumber = -1;
            }
        } catch (Exception e) {
        }
        return comicNumber;
    }
}
