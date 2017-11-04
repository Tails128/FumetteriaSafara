package com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Class for the Membership card entity in the database
 */

@IgnoreExtraProperties
public class MembershipCard {
    public String Nome;         // Name
    public String Cognome;      // Surname
    public String idUtente;    // Card N°

    @Override
    public String toString(){
        if(Nome == null)
            Nome = "";
        if(Cognome == null)
            Cognome = "";
        return Nome + " " + Cognome;
    }
}
