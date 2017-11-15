package com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions;

/**
 * Created by tails on 15/11/2017.
 */

public class Booking {
    private String id;
    private String ComicName;
    private int Number;

    public Booking(){
        ComicName ="";
        Number = -1;
    }

    public Booking(String id, String comicName, int number){
        this.id = id;
        ComicName = comicName;
        Number = number;
    }

    public String getId(){
        return  id;
    }

    public void setComicName(String comicName){
        ComicName = comicName;
    }

    public String getComicName(){
        return ComicName;
    }

    public int getNumber(){
        return Number;
    }

    @Override
    public String toString() {
        return String.format("%s %d",ComicName, Number);
    }
}
