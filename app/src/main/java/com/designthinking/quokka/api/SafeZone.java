package com.designthinking.quokka.api;

import com.designthinking.quokka.util.LocationUtil;
import com.google.android.gms.maps.model.LatLng;

public class SafeZone {

    public int pk;
    public double lat;
    public double lng;
    public double radius;

    public LatLng getLatLng(){
        return new LatLng(lat, lng);
    }

    public double getRadius(){
        return radius;
    }

    public boolean test(LatLng latLng){
        return LocationUtil.calcFastDist(getLatLng(), latLng) <= radius * 1000;
    }

}
