package com.designthinking.quokka.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.designthinking.quokka.R;
import com.designthinking.quokka.api.Device;

public class DeviceDetailView {

    private View view;

    private TextView name;

    private boolean visible = false;

    public DeviceDetailView(Context context, ViewGroup container){
        view = LayoutInflater.from(context).inflate(R.layout.device_detail_layout, container, false);

        container.addView(view);
        view.setVisibility(View.GONE);

        name = view.findViewById(R.id.device_name);
    }

    public void show(Device device){
        name.setText(device.getName());
        view.setVisibility(View.VISIBLE);
        visible = true;
    }

    public void hide(){
        view.setVisibility(View.GONE);
        visible = false;
    }

}
