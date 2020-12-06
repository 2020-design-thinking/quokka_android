package com.designthinking.quokka.event.messages;

import com.designthinking.quokka.api.Device;
import com.designthinking.quokka.api.Drive;
import com.designthinking.quokka.event.Event;

public class OnStartDriving extends Event {

    public Drive drive;

    public OnStartDriving(Drive drive){
        this.drive = drive;
    }

}
