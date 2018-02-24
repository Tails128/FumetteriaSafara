package com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions;

import android.support.annotation.NonNull;

import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.TournamentPosition;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * an easy to read Recap of the user stats. Unlike the Tournament position, this is used for overall stats
 */
public class PositionRecap implements Comparable<PositionRecap>{
    private String id;      //Id
    public String Nome;     //Name
    public int Punteggio;   //Points
    public int Posizione;   //Position

    public PositionRecap(String id, String nome, int punteggio, int posizione){
        this.id = id;
        Nome = nome;
        Punteggio = punteggio;
        Posizione = posizione;
    }

    public PositionRecap(String Nome, TournamentPosition pt){
        this.id = pt.Nome;
        this.Nome = Nome;
        Punteggio = pt.PuntiSvizzera;
        if(pt.PuntiTop >= 0)
        Punteggio += pt.PuntiTop;
    }

    @Override
    public int compareTo(@NonNull PositionRecap p){

        if(Posizione < p.Posizione)
            return 1;
        else if(Posizione > p.Posizione)
            return -1;
        else
            return 0;
    }

    @Override
    public String toString(){
        return id;
    }

    public void addPosizione(TournamentPosition pt){
        Punteggio += pt.PuntiSvizzera;
        Punteggio += pt.PuntiTop;
    }

    public static List<PositionRecap> RatePosizioni(List<PositionRecap> arrayToOrder){
        Collections.sort(arrayToOrder,new Comparator<PositionRecap>() {
            @Override
            public int compare(PositionRecap posizioneRecap, PositionRecap t1) {
                if(posizioneRecap.Punteggio < t1.Punteggio)
                    return 1;
                else if(posizioneRecap.Punteggio > t1.Punteggio)
                    return -1;
                else
                    return 0;
            }
        });
        int prevPoints = 0;
        int accumulator = 0;
        int posizione = 0;
        for (int i = 0; i< arrayToOrder.size(); i++){
            if(prevPoints != arrayToOrder.get(i).Punteggio) {
                posizione += accumulator + 1;
                accumulator = 0;
                prevPoints = arrayToOrder.get(i).Punteggio;
            }
            else{
                accumulator ++;
            }
            arrayToOrder.get(i).Posizione = posizione;
        }
        return arrayToOrder;
    }
}
