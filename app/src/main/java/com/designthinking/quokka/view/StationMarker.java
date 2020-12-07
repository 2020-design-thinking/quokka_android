package com.designthinking.quokka.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.designthinking.quokka.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StationMarker extends FrameLayout {

    public StationMarker(@NonNull Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.station_marker, this, true);
    }
}
