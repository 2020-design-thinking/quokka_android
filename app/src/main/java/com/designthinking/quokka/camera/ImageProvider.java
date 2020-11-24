package com.designthinking.quokka.camera;

import android.content.Context;
import android.view.SurfaceView;

import com.designthinking.quokka.location.ILocationListener;

public class ImageProvider extends SurfaceView {

    private IImageListener listener;

    public ImageProvider(Context context) {
        super(context);
    }

    public void setListener(IImageListener listener){
        this.listener = listener;
    }

    public void request(){

    }

    public void stop(){

    }

    protected IImageListener getListener(){
        return listener;
    }

}
