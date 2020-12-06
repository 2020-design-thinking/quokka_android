package com.designthinking.quokka.location;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LocationPermission {

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;

    public static boolean granted(Activity activity){
        return ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void request(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

}
