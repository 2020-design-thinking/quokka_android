package com.designthinking.quokka.location;

import com.google.android.gms.maps.LocationSource;

import java.util.ArrayList;
import java.util.List;

public interface ILocationProvider extends LocationSource {

    List<OnLocationChangedListener> listeners = new ArrayList<>();

    void start();

    void registerListener(OnLocationChangedListener listener);

}
