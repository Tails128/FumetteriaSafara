package com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for the Tournament entity in the database
 */

@IgnoreExtraProperties
public class Tournament implements java.io.Serializable{
    public String id;                                                       //Tournament id
    public String Nome;                                                     //Tournament name
    public long DataTorneo;                                                 //Tournament date
    public Map<String,TournamentPosition> Piazzamenti = new HashMap<>();   //Tournament results

    public Tournament(){
        Piazzamenti = new HashMap<>();
    }
    public Tournament(String nome, long dataTorneo, Map<String, TournamentPosition> piazzamenti){
        Nome = nome;
        DataTorneo = dataTorneo;
        Piazzamenti = piazzamenti;
    }

    @Override
    public String toString(){
        return Nome;
    }
}
