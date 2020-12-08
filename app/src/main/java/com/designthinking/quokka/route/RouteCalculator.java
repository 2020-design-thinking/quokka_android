package com.designthinking.quokka.route;

import android.location.Location;

import com.designthinking.quokka.api.Device;
import com.designthinking.quokka.location.LocationProvider;
import com.designthinking.quokka.util.LocationUtil;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RouteCalculator {

    public static Route calculateFastest(LocationProvider locationProvider, List<Device> devices){
        Route res = null;
        for(Device device : devices){
            if(!device.isAvailable()) continue;
            Route route = new Route(locationProvider, device);
            if(res == null || res.getTotalTime() > route.getTotalTime())
                res = route;
        }
        return res;
    }

    public static Route calculateFastest(LocationProvider locationProvider, List<Device> devices, LatLng target){
        Route res = null;
        for(Device device : devices){
            if(!device.isAvailable()) continue;
            Route route = new Route(locationProvider, device, target);
            if(res == null || res.getTotalTime() > route.getTotalTime())
                res = route;
        }
        return res;
    }

    public static Route calculateCheapest(LocationProvider locationProvider, List<Device> devices){
        Route res = null;
        for(Device device : devices){
            if(!device.isLowBattery() || !device.isAvailable()) continue;
            Route route = new Route(locationProvider, device);
            if(res == null || res.getTotalTime() > route.getTotalTime())
                res = route;
        }
        return res;
    }

    // Fastest Route 보다 가격은 싸야 한다
    // 그런 경로가 없다면 null
    public static Route calculateCheapest(LocationProvider locationProvider, List<Device> devices, LatLng target){
        int maxCharge = calculateFastest(locationProvider, devices, target).getCharge();
        Route res = null;
        for(Device device : devices){
            if(!device.isLowBattery() || !device.isAvailable()) continue;
            Route route = new Route(locationProvider, device, target);
            if(maxCharge <= route.getCharge()) continue;
            if(res == null || res.getTotalTime() > route.getTotalTime())
                res = route;
        }
        return res;
    }

}
