package com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Class which manages the user update and checks for eventual name / surname presence
 */

public class UserManager {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();
    public static User CurrentUserData = new User();

    /**
     * a function to attempt reading the user data
     * @param user
     * @param ctx context is required since the function may need to make a Toast
     */
    public static void readUser(FirebaseUser user, final Context ctx){
        String query = "Users/"+user.getUid()+"/";
        reference.child(query).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CurrentUserData = dataSnapshot.getValue(User.class);
                if(CurrentUserData == null )
                    CurrentUserData = new User();
                if(CurrentUserData.Nome.equals("") || CurrentUserData.Cognome.equals(""))
                    Toast.makeText(ctx,"Benvenuto!\nCome prima cosa imposta il tuo nome ed il tuo cognome in impostazioni!\nUsa i tuoi Nome e Cognome reali!" ,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * writes an empty user into database.
     * @param user
     */
    public static void writeUser(FirebaseUser user){
        Map<String,Object> temp = new HashMap<>();
        temp.put(user.getUid(),CurrentUserData);
        reference.child("Users/").updateChildren(temp);
    }

    /**
     * function which writes user data to db, it must be called after the user's logged since
     * only logged users have access the them data
     * @param name
     * @param surname
     * @param user
     */
    public static void writeUser(String name, String surname, FirebaseUser user){
        if(user == null)
            return;
        String id = user.getUid();
        User temporaryUser = new User();
        temporaryUser.Nome = name;
        temporaryUser.Cognome = surname;
        reference.child("Users/" + id).updateChildren(temporaryUser.toMap());
    }
}
