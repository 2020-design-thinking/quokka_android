package com.designthinking.quokka.location;

import android.location.Location;
import android.os.Handler;

import com.designthinking.quokka.event.EventManager;
import com.designthinking.quokka.event.messages.OnLocationUpdate;
import com.designthinking.quokka.util.LocationUtil;
import com.google.android.gms.maps.model.LatLng;

public class FakeLocationProvider extends LocationProvider {

    private static final int UPDATE_PERIOD = 1000;

    private LatLng current;

    private Handler handler;
    private Runnable locationUpdate;

    private LatLng targetLatLng;
    private double speed; // m/s

    public FakeLocationProvider(LatLng init, Handler handler){
        current = init;

        this.handler = handler;

        locationUpdate = () -> {
            Location location = new Location("");
            location.setLatitude(current.latitude);
            location.setLongitude(current.longitude);

            setLocation(location);

            move();

            handler.postDelayed(locationUpdate, UPDATE_PERIOD);
        };

        handler.post(locationUpdate);
    }

    private void move(){
        if(targetLatLng == null) return;

        // Binary search to calculate next position with given speed
        double step = speed * UPDATE_PERIOD / 1000.0;
        double lo = 0, hi = 1;
        for(int i = 0; i < 30; i++){
            double mid = (lo + hi) / 2;
            double dist = LocationUtil.calcFastDist(current,
                    LocationUtil.lerp(current, targetLatLng, mid));
            if(dist <= step)
                lo = mid;
            else
                hi = mid;
        }

        current = LocationUtil.lerp(current, targetLatLng, lo);
    }

    @Override
    public void setTargetLatLng(LatLng latLng){
        targetLatLng = latLng;
    }

    @Override
    public void setSpeed(double speed){
        this.speed = speed;
    }
}
