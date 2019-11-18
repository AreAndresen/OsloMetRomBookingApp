package com.skole.s304114mappe3.klasser;

import com.google.android.gms.maps.model.LatLng;

public class Rom {

    //-------ATTRIBUTTER-------
    private String romNr;
    private String bygg;
    private String antSitteplasser;
    private LatLng koordinater;


    //-------KONTRUKSTØR-------
    public Rom(String romNr, String bygg, String antSitteplasser, LatLng koordinater) {
        this.romNr = romNr;
        this.bygg = bygg;
        this.antSitteplasser = antSitteplasser;
        this.koordinater = koordinater;
    }


    //-------GET/SET-------
    public String getRomNr() {
        return romNr;
    }
    public void setRomNr(String romNr) {
        this.romNr = romNr;
    }

    public String getBygg() {
        return bygg;
    }
    public void setBygg(String bygg) {
        this.bygg = bygg;
    }

    public String getAntSitteplasser() {
        return antSitteplasser;
    }
    public void setAntSitteplasser(String antSitteplasser) {
        this.antSitteplasser = antSitteplasser;
    }

    public LatLng getLatLen() {
        return koordinater;
    }
    public void setKoordinater(LatLng koordinater) {
        this.koordinater = koordinater;
    }


    //-------TOSTRING-------
    @Override
    public String toString() {
        return getRomNr();
    }
}

