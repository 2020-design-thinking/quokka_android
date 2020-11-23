package com.designthinking.quokka.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

public class ViewBitmap {

    private View view;
    private Bitmap bitmap;

    public ViewBitmap(View view, Activity activity){
        this.view = view;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //view.setLayoutParams(new ViewGroup.LayoutParams(256, 256));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        //view.buildDrawingCache();

        bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

}
