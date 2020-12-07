package com.designthinking.quokka.api;

import com.google.android.gms.maps.model.LatLng;

public class Station {

    public int pk;
    public double lat;
    public double lng;

    public LatLng getLatLng(){
        return new LatLng(lat, lng);
    }

}
