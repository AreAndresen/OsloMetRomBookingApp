package com.skole.s304114mappe3.klasser;

import com.google.android.gms.maps.model.LatLng;

public class Reservasjon {
    //-------ATTRIBUTTER-------
    //private long ID;
    private String dato;
    private String tidFra;
    private String tidTil;
    private String romNr;



    //-------KONTRUKSTØR-------
    public Reservasjon(String dato, String tidFra, String tidTil, String romNr) {
        this.dato = dato;
        this.tidFra = tidFra;
        this.tidTil = tidTil;
        this.romNr = romNr;

    }


    //-------GET/SET-------
    public String getDato() {
        return dato;
    }
    public void setDato(String dato) {
        this.dato = dato;
    }

    public String getTidFra() {
        return tidFra;
    }
    public void setTidFra(String tidFra) {
        this.tidFra = tidFra;
    }

    public String getTidTil() {
        return tidTil;
    }
    public void setTidTil(String tidTil) {
        this.tidTil = tidTil;
    }

    public String getRomNr() {
        return romNr;
    }
    public void setRomNr(String romNr) {
        this.romNr = romNr;
    }


    //-------TOSTRING BRUKES I LISTVIEW, DERFOR FÆRRE VERDIER FOR RYDDIGHET-------
    @Override
    public String toString() {
        return getRomNr();
    }
}

