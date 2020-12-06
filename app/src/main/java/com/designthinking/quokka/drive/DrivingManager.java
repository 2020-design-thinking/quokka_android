package com.designthinking.quokka.drive;

import android.content.Context;
import android.location.Location;

import com.designthinking.quokka.api.Device;
import com.designthinking.quokka.api.Drive;
import com.designthinking.quokka.api.IApiServer;
import com.designthinking.quokka.retrofit.IResult;
import com.designthinking.quokka.retrofit.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrivingManager {

    private IApiServer api;

    private Device device;
    private Drive drive;

    public DrivingManager(IApiServer api){
        this.api = api;
    }

    public Drive getDrive(){
        return drive;
    }

    public void startDriving(Device device, IResult callback){
        this.device = device;
        api.startDriving(device.getId()).enqueue(new Callback<Drive>() {
            @Override
            public void onResponse(Call<Drive> call, Response<Drive> response) {
                if(response.code() != 200){
                    callback.result(Result.FAIL);
                    // TODO: 실패 원인
                }
                else{
                    drive = response.body();
                    callback.result(Result.SUCCESS);
                }
            }

            @Override
            public void onFailure(Call<Drive> call, Throwable t) {
                callback.result(Result.NO_SERVER);
            }
        });
    }

    public void updateLocation(Location location){

    }

    public void finishDriving(IDrivingResult callback){
        if(drive == null) return;

        api.finishDriving(drive.pk).enqueue(new Callback<Drive>() {
            @Override
            public void onResponse(Call<Drive> call, Response<Drive> response) {
                if(response.code() != 200){
                    callback.result(Result.FAIL, null);
                }
                else{
                    drive = response.body();
                    callback.result(Result.SUCCESS, response.body());
                    drive = null;
                }
            }

            @Override
            public void onFailure(Call<Drive> call, Throwable t) {
                callback.result(Result.NO_SERVER, null);
            }
        });
    }

    public boolean isDriving(){
        return drive != null;
    }

    public interface IDrivingResult{
        void result(Result result, Drive drive);
    }

}
