package com.designthinking.quokka.reserve;

import com.designthinking.quokka.api.Device;

public class Reserve {

    public static final long RESERVE_TIME = 30;

    public Device device;
    public long timestamp;

    public Reserve(Device device){
        this.device = device;
        this.timestamp = System.currentTimeMillis();
    }

    public long getSecondsLeft(){
        return Math.max(RESERVE_TIME - (System.currentTimeMillis() - timestamp) / 1000L, 0L);
    }

    public boolean isExpired(){
        return getSecondsLeft() == 0L;
    }

}
