package com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions;

import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.Transaction;

/**
 * transaction view which stores the id and the base values
 */

public class TransactionView {
    public long Data;             //Date
    public String Descrizione;      //Description
    public Double Valore;           //Amount
    public String id;

    public TransactionView(Transaction t, String id){
        Data = t.Data;
        Descrizione = t.Descrizione;
        Valore = t.Valore;
        this.id = id;
    }
}
