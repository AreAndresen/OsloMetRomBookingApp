package com.skole.s304114mappe3.klasser;

import com.google.android.gms.maps.model.LatLng;

public class Rom {

    //-------ATTRIBUTTER-------
    //private long ID;
    private String romNr; //brukes til å slette
    private String beskrivelse;
    //private double lat;
    //private double len;
    private LatLng koordinater;


    //-------KONTRUKSTØR-------
    public Rom(String romNr, String beskrivelse, LatLng koordinater) {
        this.romNr = romNr;
        this.beskrivelse = beskrivelse;
        //this.lat = lat;
        //this.len = len;
        this.koordinater = koordinater;
    }


    //-------GET/SET-------
    public String getRomNr() {
        return romNr;
    }
    public void setRomNr(String romNr) {
        this.romNr = romNr;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }
    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    /*public double getLon() {
        return lon;
    }
    public void setLon(Double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(Double lat) {
        this.lat = lat;
    }*/
    //-------SLUTT GET/SET-------

    public LatLng getLatLen() {
        return koordinater;
    }
    public void setKoordinater(LatLng koordinater) {
        this.koordinater = koordinater;
    }


    //-------TOSTRING BRUKES I LISTVIEW, DERFOR FÆRRE VERDIER FOR RYDDIGHET-------
    @Override
    public String toString() {
        return getBeskrivelse();
    }
}

