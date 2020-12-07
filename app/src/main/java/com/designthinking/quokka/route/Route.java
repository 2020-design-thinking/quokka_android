package com.designthinking.quokka.route;

import com.designthinking.quokka.api.Device;
import com.designthinking.quokka.core.Policy;
import com.designthinking.quokka.location.LocationProvider;
import com.designthinking.quokka.util.LocationUtil;
import com.google.android.gms.maps.model.LatLng;

public class Route {

    public LocationProvider locationProvider;
    public LatLng target;
    public Device device;

    private LatLng driveStartLatLng;

    /*public Route(LocationProvider locationProvider, LatLng target){
        this.locationProvider = locationProvider;
        this.target = target;
    }*/

    public Route(LocationProvider locationProvider, Device device){
        this.locationProvider = locationProvider;
        this.device = device;
    }

    public Route(LocationProvider locationProvider, Device device, LatLng target){
        this.locationProvider = locationProvider;
        this.device = device;
        this.target = target;
    }

    public LatLng getCurrentLocation(){
        return LocationUtil.toLatLng(locationProvider.getLastLocation());
    }

    public void setDriveStartLatLng(LatLng driveStartLatLng){
        this.driveStartLatLng = driveStartLatLng;
    }

    public LatLng getDriveStartLatLng(){
        return driveStartLatLng;
    }

    // METERS
    public int getWalkingDistance(){
        double res;

        if(device == null)
            res = LocationUtil.calcFastDist(LocationUtil.toLatLng(locationProvider.getLastLocation()), target);
        else
            res = LocationUtil.calcFastDist(LocationUtil.toLatLng(locationProvider.getLastLocation()), device.getLocation());

        return (int)res;
    }

    // METERS
    public int getRidingDistance(){
        if(device == null) return 0;
        if(target == null) return 0;
        double res = LocationUtil.calcFastDist(device.getLocation(), target);
        return (int)res;
    }

    // METERS
    public int getTotalDistance(){
        return getWalkingDistance() + getRidingDistance();
    }

    // SECONDS
    public int getWalkingTime(){
        return (int)(getWalkingDistance() / 1.4f);
    }

    // SECONDS
    public int getRidingTime(){
        return (int)(getRidingDistance() / 5.5f);
    }

    // SECONDS
    public int getTotalTime(){
        return getWalkingTime() + getRidingTime();
    }

    // WON
    public int getCharge(){
        return Policy.getCharge(getRidingDistance(), device.battery);
    }

    public boolean hasDestination(){
        return target != null;
    }
}
