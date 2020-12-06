package com.designthinking.quokka.api;

import com.designthinking.quokka.core.Policy;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Device {

    public static final float LOW_BATTERY = 0.3f;

    public int pk;

    public double lat;
    public double lng;
    public float battery;
    public boolean using;
    public boolean reserved;

    public Marker marker;

    public LatLng getLocation(){
        return new LatLng(lat, lng);
    }

    public void setLocation(LatLng latLng){
        lat = latLng.latitude;
        lng = latLng.longitude;
    }

    public String getName(){
        return "쿼카 " + pk + "번";
    }

    public int getId(){
        return pk;
    }

    public boolean isAvailable(){
        return !using && !reserved;
    }

    public boolean isLowBattery(){
        return Policy.isLowBattery(this);
    }

    public int getIntBattery(){
        return (int)(battery * 100);
    }

    public void set(Device device){
        pk = device.pk;
        lat = device.lat;
        lng = device.lng;
        battery = device.battery;
        using = device.using;
        reserved = device.reserved;
        // Marker is not updated
    }

}
