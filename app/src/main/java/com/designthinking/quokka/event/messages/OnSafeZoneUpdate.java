package com.designthinking.quokka.event.messages;

import com.designthinking.quokka.api.SafeZone;
import com.designthinking.quokka.event.Event;

import java.util.List;

public class OnSafeZoneUpdate extends Event {

    public List<SafeZone> safeZones;

    public OnSafeZoneUpdate(List<SafeZone> safeZones){
        this.safeZones = safeZones;
    }

}
