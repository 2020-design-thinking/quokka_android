package com.designthinking.quokka.event.messages;

import com.designthinking.quokka.api.Drive;
import com.designthinking.quokka.event.Event;

public class OnDrivingInfoUpdate extends Event {

    public Drive drive;

    public OnDrivingInfoUpdate(Drive drive){
        this.drive = drive;
    }

}
