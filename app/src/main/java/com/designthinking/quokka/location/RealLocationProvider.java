package com.designthinking.quokka.location;

import android.content.Context;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class RealLocationProvider extends LocationProvider {

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
                    getListener().updateLocation(locationResult.getLastLocation());
                }
            }, Looper.getMainLooper());
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void start(){

    }
}
