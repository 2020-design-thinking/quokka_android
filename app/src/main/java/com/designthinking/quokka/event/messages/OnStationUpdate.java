package com.designthinking.quokka.event.messages;

import com.designthinking.quokka.api.Station;
import com.designthinking.quokka.event.Event;

import java.util.List;

public class OnStationUpdate extends Event {

    public List<Station> stations;

    public OnStationUpdate(List<Station> stations){
        this.stations = stations;
    }

}
