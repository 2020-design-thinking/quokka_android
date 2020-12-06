package com.designthinking.quokka.api;

import com.designthinking.quokka.route.Route;

public class Drive {

    public int pk;

    public int driving_charge = 400;
    public int discounted_charge = 0;
    public int charge = 400;
    public float safety_rate = 0;
    public String dist = "0";

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
        this.discounted_charge = drive.discounted_charge;
        this.charge = drive.charge;
        this.safety_rate = drive.safety_rate;
        this.dist = drive.dist;
    }

}
