package com.designthinking.quokka.location;

import android.content.Context;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.LocationSource;

public class RealLocationProvider implements ILocationProvider {

    public RealLocationProvider(Context context){
        FusedLocationProviderClient locationProvider = LocationServices.getFusedLocationProviderClient(context);
        LocationRequest locationRequest = new LocationRequest();

        try{
            locationProvider.requestLocationUpdates(locationRequest, new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null || locationResult.getLastLocation() == null) {
                        return;
                    }
                    for(OnLocationChangedListener listener : listeners){
                        listener.onLocationChanged(locationResult.getLastLocation());
                    }
                }
            }, Looper.getMainLooper());
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        registerListener(onLocationChangedListener);
    }

    @Override
    public void registerListener(OnLocationChangedListener onLocationChangedListener){
        listeners.remove(onLocationChangedListener);
        listeners.add(onLocationChangedListener);
    }

    @Override
    public void deactivate() {

    }

    @Override
    public void start() {

    }
}
