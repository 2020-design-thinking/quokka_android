package com.designthinking.quokka.location;

import android.content.Context;
import android.os.Looper;

import com.designthinking.quokka.event.EventManager;
import com.designthinking.quokka.event.messages.OnLocationUpdate;
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
                    setLocation(locationResult.getLastLocation());
                }
            }, Looper.getMainLooper());
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
    }
}
