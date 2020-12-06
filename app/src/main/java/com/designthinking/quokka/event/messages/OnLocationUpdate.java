package com.designthinking.quokka.event.messages;

import android.location.Location;

import com.designthinking.quokka.event.Event;

public class OnLocationUpdate extends Event {

    public Location location;

    public OnLocationUpdate(Location location){
        this.location = location;
    }

}
