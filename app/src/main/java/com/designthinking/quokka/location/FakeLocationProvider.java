package com.designthinking.quokka.location;

import android.location.Location;
import android.os.Handler;

public class FakeLocationProvider extends LocationProvider {

    public double[] values;
    private boolean stop;

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
                getListener().updateLocation(location);
                move();

                if(!stop) handler.postDelayed(this, 1000);
            }
        };

        this.handler = handler;
    }

    public void pause(){
        stop = true;
    }

    public void start(){
        handler.postDelayed(runnable, 1000);
    }

    private void move(){
        values[0] += values[2];
        values[1] += values[3];
    }
}
