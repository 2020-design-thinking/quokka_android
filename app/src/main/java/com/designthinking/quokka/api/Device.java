package com.designthinking.quokka.api;

import com.google.android.gms.maps.model.LatLng;

public class Device {

    public static final float LOW_BATTERY = 0.3f;

    public int pk;

    public double lat;
    public double lng;
    public LatLng location;
    public float battery;
    public boolean using;

    public LatLng getLocation(){
        return new LatLng(lat, lng);
    }

    public String getName(){
        return "쿼카 " + pk + "번";
    }

    public int getId(){
        return pk;
    }

    public boolean isAvailable(){
        return !using;
    }

}
