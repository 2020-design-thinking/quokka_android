package com.designthinking.quokka.location;

import android.location.Location;

import com.designthinking.quokka.event.EventManager;
import com.designthinking.quokka.event.messages.OnLocationUpdate;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class LocationProvider implements LocationSource {

    private List<OnLocationChangedListener> listeners = new ArrayList<>();

    private Location location;

    public Location getLastLocation(){
        return location;
    }

    protected void setLocation(Location location){
        this.location = location;
        for(OnLocationChangedListener listener : listeners){
            listener.onLocationChanged(location);
        }
        EventManager.getInstance().invoke(new OnLocationUpdate(location));
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        listeners.remove(onLocationChangedListener);
        listeners.add(onLocationChangedListener);
    }

    @Override
    public void deactivate() {

    }

    // Below methods are only for fake provider
    public void setLatLngForce(LatLng latLng){
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        setLocation(location); // for Update
    }

    public void setTargetLatLng(LatLng latLng){

    }

    // m/s
    public void setSpeed(double speed){

    }
}
