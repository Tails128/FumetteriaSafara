package com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Locale;

/**
 * Class for the Tournament position entity in the database
 */

@IgnoreExtraProperties
public class TournamentPosition implements java.io.Serializable{
    public String Nome;             // Name
    public int PuntiSvizzera;       // Brawl points
    public int PuntiTop;            // 1 on 1 points
    public int Posizione;           // effective position

    public TournamentPosition(){
        Nome ="";
    }

    public TournamentPosition(int puntiSvizzera, int puntiTop, int posizione){
        Nome ="";
        PuntiSvizzera = puntiSvizzera;
        PuntiTop = puntiTop;
        Posizione = posizione;
    }
    @Override
    public String toString(){
        return String.format("%s° - %s",Posizione, Nome);
    }

    public String toDetailString(){
        if(PuntiTop == -1)
            return String.format(Locale.ITALIAN, "Posizione nel torneo: %d°\nPunti di Svizzera: %d",Posizione,PuntiSvizzera);
        return String.format(Locale.ITALIAN,"Posizione nel torneo: %d°\nPunti di Svizzera: %d\nPunti Posizionali: %d",Posizione,PuntiSvizzera,PuntiTop);
    }
}
