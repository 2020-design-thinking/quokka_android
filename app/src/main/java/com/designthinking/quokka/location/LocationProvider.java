package com.designthinking.quokka.location;

public abstract class LocationProvider {

    private ILocationListener listener;

    public void setListener(ILocationListener listener){
        this.listener = listener;
    }

    public abstract void pause();

    public abstract void start();

    protected ILocationListener getListener(){
        return listener;
    }

}
