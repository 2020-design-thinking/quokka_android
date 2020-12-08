package com.designthinking.quokka.core;

import com.designthinking.quokka.api.Device;

public class Policy {

    public static boolean isLowBattery(Device device){
        return device.battery <= 0.3f;
    }

    public static int getBatteryDiscount(float battery){
        if(battery <= 0.3f) return 150;
        else if(battery <= 0.5f) return 90;
        return 0;
    }

    public static int getCharge(int meters, float battery){
        return Math.max((Math.round(meters / 100f) * 90) + 400 - getBatteryDiscount(battery), 0);
    }

}
