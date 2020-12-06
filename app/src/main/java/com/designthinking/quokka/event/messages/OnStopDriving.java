package com.designthinking.quokka.event.messages;

import com.designthinking.quokka.api.Drive;
import com.designthinking.quokka.event.Event;

public class OnStopDriving extends Event {

    public Drive drive;

    public OnStopDriving(Drive drive){
        this.drive = drive;
    }

}
