package com.designthinking.quokka.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.designthinking.quokka.FinishDrivingActivity;
import com.designthinking.quokka.R;
import com.designthinking.quokka.api.Device;
import com.designthinking.quokka.api.Drive;
import com.designthinking.quokka.drive.DrivingManager;
import com.designthinking.quokka.drive.IFinishDrivingListener;
import com.designthinking.quokka.retrofit.IResult;
import com.designthinking.quokka.retrofit.Result;

import java.util.logging.Handler;

public class DrivingView {

    private View view;
    private DrivingManager manager;

    private TextView chargeText;

    // DEV
    private SeekBar speedBar;
    private TextView speedText;

    public DrivingView(Context context, ViewGroup container, DrivingManager manager, IFinishDrivingListener listener){
        this.manager = manager;

        view = LayoutInflater.from(context).inflate(R.layout.driving_layout, container, false);

        chargeText = view.findViewById(R.id.charge);

        view.findViewById(R.id.stop_driving).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(manager != null){
                    manager.finishDriving(new DrivingManager.IDrivingResult() {
                        @Override
                        public void result(Result result, Drive drive) {
                            if(result == Result.SUCCESS){
                                listener.onFinishDriving(drive);
                                view.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        // DEV
        speedBar = view.findViewById(R.id.speed);
        speedText = view.findViewById(R.id.speed_text);
        speedBar.setMax(25);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedText.setText(progress + "km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        container.addView(view);
        view.setVisibility(View.GONE);
    }

    public void start(Device device, IResult callback){
        manager.startDriving(device, new IResult() {
            @Override
            public void result(Result code) {
                if(code == Result.SUCCESS){
                    view.setVisibility(View.VISIBLE);
                }
                callback.result(code);
            }
        });
    }

    public void update(){
        if(manager != null && manager.isDriving()){
            chargeText.setText(manager.getDrive().charge + "원");
        }
    }

    public int getSpeed(){
        return speedBar.getProgress();
    }

}
