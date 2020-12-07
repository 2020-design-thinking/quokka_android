package com.designthinking.quokka.event.messages;

import com.designthinking.quokka.event.Event;

public class OnSpeedUpdate extends Event {

    public int speed;

    public OnSpeedUpdate(int speed){
        this.speed = speed;
    }

}
