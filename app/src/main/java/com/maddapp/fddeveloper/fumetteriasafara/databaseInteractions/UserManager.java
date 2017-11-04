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

    public static void readUser(FirebaseUser u, final Context cont){
        String query = "Users/"+u.getUid()+"/";
        reference.child(query).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CurrentUserData = dataSnapshot.getValue(User.class);
                if(CurrentUserData == null )
                    CurrentUserData = new User();
                if(CurrentUserData.Nome.equals("") || CurrentUserData.Cognome.equals(""))
                    Toast.makeText(cont,"Benvenuto!\nCome prima cosa imposta il tuo nome ed il tuo cognome in impostazioni!\nUsa i tuoi Nome e Cognome reali!" ,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO
            }
        });
    }

    public static void writeUser(FirebaseUser u){
        Map<String,Object> temp = new HashMap<>();
        temp.put(u.getUid(),CurrentUserData);
        reference.child("Users/").updateChildren(temp);
    }

    public static void writeUser(String nome, String cognome, FirebaseUser u){
        String id = u.getUid();
        User tempuser = new User();
        tempuser.Nome = nome;
        tempuser.Cognome = cognome;
        reference.child("Users/" + id).updateChildren(tempuser.toMap());
    }
}
