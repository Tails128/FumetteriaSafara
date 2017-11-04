package com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for user.
 */

@IgnoreExtraProperties
public class User {
    public String Nome ="";
    public String Cognome ="";

    public User(){}

    public User(String nome, String cognome){
        Nome = nome;
        Cognome = cognome;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("Nome",Nome);
        result.put("Cognome",Cognome);
        return  result;
    }
}
