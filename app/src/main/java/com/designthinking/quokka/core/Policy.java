package com.designthinking.quokka.core;

import com.designthinking.quokka.api.Device;

public class Policy {

    public static boolean isLowBattery(Device device){
        return device.battery <= 0.3f;
    }

    public static int getBatteryDiscount(float battery){
        if(battery > 0.3f) return 0;
        // 500원 -> 3000원
        return (int)((1 - battery / 0.3f) * 2500) + 500;
    }

    public static int getCharge(int meters, float battery){
        return Math.max((int)(meters / 100f * 80) + 400 - getBatteryDiscount(battery), 0);
    }

}
