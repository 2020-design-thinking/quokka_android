package com.designthinking.quokka.location;

import android.location.Location;
import android.os.Handler;

import com.google.android.gms.maps.LocationSource;

public class FakeLocationProvider implements ILocationProvider {

    public double[] values;
    private boolean running;

    private Handler handler;
    private Runnable runnable;

    public FakeLocationProvider(double lat, double lng, double dlat, double dlng, Handler handler){
        values = new double[]{lat, lng, dlat, dlng};

        runnable = new Runnable() {
            @Override
            public void run() {
                Location location = new Location("");
                location.setLatitude(values[0]);
                location.setLongitude(values[1]);
                for(OnLocationChangedListener listener : listeners){
                    listener.onLocationChanged(location);
                }
                move();
                if(running) handler.postDelayed(this, 1000);
            }
        };

        this.handler = handler;
    }

    private void move(){
        values[0] += values[2];
        values[1] += values[3];
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        registerListener(onLocationChangedListener);
        // Init Start Position
        handler.post(runnable);
    }

    @Override
    public void registerListener(OnLocationChangedListener onLocationChangedListener){
        listeners.remove(onLocationChangedListener);
        listeners.add(onLocationChangedListener);
    }

    @Override
    public void deactivate() {
        running = false;
    }

    @Override
    public void start() {
        running = true;
        handler.post(runnable);
    }
}
