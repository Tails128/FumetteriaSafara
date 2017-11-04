package com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Class for the championship entity in the database
 */

@IgnoreExtraProperties
public class Championship {
    public String Nome;         //Name
    public String id;           //Id
    public long DataInizio;     //Start date
    public long DataFine;       //End date

    public Championship(){
        Nome = "";
        id = "";
        DataInizio = 0;
        DataFine = 0;
    }

    public Championship(String n, String i, long di, long df){
        Nome = n;
        id = i;
        DataInizio = di;
        DataFine = df;
    }

    @Override
    public String toString(){
        return Nome;
    }

}
