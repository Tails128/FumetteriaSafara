package com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions;

import java.util.Locale;

/**
 * Booking class,
 * made to keep track of the bookings for books's series.
 * it includes the comic name, the comic id, the current number for the user and a the state of the confirmation.
 * Users can request bookings and the shopkeeper can accept or delete them.
 */
public class Booking {
    private String id;
    private String ComicName;
    private int Number;
    private boolean isConfirmed;

    public Booking(){
        ComicName ="";
        Number = -1;
        id = "";
        isConfirmed = false;
    }

    public Booking(String id, String ComicName, int Number, boolean isConfirmed){
        this.id = id;
        this.ComicName = ComicName;
        this.Number = Number;
        this.isConfirmed = isConfirmed;
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
        return String.format(Locale.ITALIAN,"%s %d",ComicName, Number);
    }
}
