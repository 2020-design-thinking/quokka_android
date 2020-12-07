package com.designthinking.quokka.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Random;

public class LocationUtil {

    public static LatLng toLatLng(Location location){
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    // Approximation
    public static double calcFastDist(LatLng la, LatLng lb){
        double R = 6371; // Radius of the earth in km
        double deg2Rad = Math.PI / 180;
        double dLat = (lb.latitude - la.latitude) * deg2Rad;
        double dLon = (lb.longitude - la.longitude) * deg2Rad;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(la.latitude * deg2Rad) * Math.cos(lb.latitude * deg2Rad) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c * 1000.0; // meter
    }

    public static LatLng lerp(LatLng la, LatLng lb, double t){
        t = Math.max(Math.min(t, 1), 0);
        return new LatLng(la.latitude + (lb.latitude - la.latitude) * t,
                la.longitude + (lb.longitude - la.longitude) * t);
    }

    public static LatLng getRandomLatLng(){
        Random random = new Random();
        // 37.254782, 127.063214
        // 37.237027, 127.087035
        double lat = random.nextDouble() * (37.254782 - 37.237027) + 37.237027;
        double lng = random.nextDouble() * (127.087035 - 127.063214) + 127.063214;
        return new LatLng(lat, lng);
    }
}
