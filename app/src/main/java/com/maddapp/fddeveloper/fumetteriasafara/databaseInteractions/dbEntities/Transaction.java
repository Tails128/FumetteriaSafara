package com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Class for the Transaction entity in the database
 */

@IgnoreExtraProperties
public class Transaction {
    public long Data;             //Date
    public String Descrizione;      //Description
    public Double Valore;           //Amount
}
