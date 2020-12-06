package com.designthinking.quokka.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.designthinking.quokka.R;
import com.designthinking.quokka.api.Device;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class QuokkaMarker extends FrameLayout {

    private static final int[] STATE_LOW_BATT = {R.attr.state_low_batt};

    private Device device;

    private ProgressBar batteryProgressBar;

    public QuokkaMarker(@NonNull Context context, Device device) {
        super(context);
        this.device = device;
        inflate();

        batteryProgressBar = findViewById(R.id.progress);
        batteryProgressBar.setProgress((int)(device.battery * 100));

        refreshDrawableState();
    }

    private void inflate(){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.quokka_normal_marker, this, true);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (device.isLowBattery()) {
            mergeDrawableStates(drawableState, STATE_LOW_BATT);
        }
        return drawableState;
    }
}
