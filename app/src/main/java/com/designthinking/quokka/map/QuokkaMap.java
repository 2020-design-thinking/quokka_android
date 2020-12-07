package com.designthinking.quokka.map;

import com.designthinking.quokka.api.SafeZone;
import com.designthinking.quokka.api.Station;
import com.designthinking.quokka.event.EventManager;
import com.designthinking.quokka.event.messages.OnSafeZoneUpdate;
import com.designthinking.quokka.event.messages.OnStationUpdate;
import com.designthinking.quokka.retrofit.RetrofitClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuokkaMap {

    private RetrofitClient client;

    private List<SafeZone> safeZoneList = new ArrayList<>();
    private List<Station> stationList = new ArrayList<>();

    public QuokkaMap(RetrofitClient client){
        this.client = client;

        client.getApi().safeZoneList().enqueue(new Callback<List<SafeZone>>() {
            @Override
            public void onResponse(Call<List<SafeZone>> call, Response<List<SafeZone>> response) {
                if(response.code() == 200){
                    setSafeZones(response.body());
                    EventManager.getInstance().invoke(new OnSafeZoneUpdate(getSafeZones()));
                }
            }

            @Override
            public void onFailure(Call<List<SafeZone>> call, Throwable t) {

            }
        });

        client.getApi().stationList().enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
                if(response.code() == 200){
                    setStations(response.body());
                    EventManager.getInstance().invoke(new OnStationUpdate(getStaions()));
                }
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {

            }
        });
    }

    public void setSafeZones(List<SafeZone> safeZones){
        this.safeZoneList = safeZones;
    }

    public List<SafeZone> getSafeZones(){
        return safeZoneList;
    }

    public void setStations(List<Station> stations){
        this.stationList = stations;
    }

    public List<Station> getStaions(){
        return stationList;
    }

    public boolean isInSafeZone(LatLng latLng){
        for(SafeZone zone : safeZoneList){
            if(zone.test(latLng)) return true;
        }
        return false;
    }

}
