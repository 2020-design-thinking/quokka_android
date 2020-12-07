package com.designthinking.quokka.api;

import android.util.Log;

import com.designthinking.quokka.drive.Warn;
import com.designthinking.quokka.route.Route;

import java.time.Instant;

public class Drive {

    private static final long WARN_PERIOD = 5;

    public int pk;

    public int driving_charge = 400;
    public int charge_discount = 0;
    public int safety_discount = 0;
    public int charge = 400;
    public float safety_rate = 0;
    public String dist = "0";

    public long last_warn_timestamp;
    public int last_warn_type;

    private long timestamp;
    private Route route;

    public void start(){
        timestamp = System.currentTimeMillis();
    }

    public long getSeconds(){
        return (System.currentTimeMillis() - timestamp) / 1000L;
    }

    public void setRoute(Route route){
        this.route = route;
    }

    public Route getRoute(){
        return route;
    }

    public Device getDevice(){
        return route.device;
    }

    public void set(Drive drive){
        this.driving_charge = drive.driving_charge;
        this.charge_discount = drive.charge_discount;
        this.safety_discount = drive.safety_discount;
        this.charge = drive.charge;
        this.safety_rate = drive.safety_rate;
        this.dist = drive.dist;

        this.last_warn_timestamp = drive.last_warn_timestamp;
        this.last_warn_type = drive.last_warn_type;
    }

    public boolean shouldWarn(){
        return last_warn_type != -1 && (System.currentTimeMillis() / 1000L - last_warn_timestamp) <= WARN_PERIOD;
    }

    public Warn getWarnType(){
        return Warn.values()[last_warn_type];
    }

}
