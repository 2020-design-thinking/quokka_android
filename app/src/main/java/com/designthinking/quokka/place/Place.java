package com.designthinking.quokka.place;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Place {

    // For Test
    private static List<Place> db = new ArrayList<>();

    public String name;
    public LatLng location;

    static {
        db.add(new Place("경희대학교 국제캠퍼스", new LatLng(37.24298512691812, 127.08010517577277)));
        db.add(new Place("영통역", new LatLng(37.251717399661395, 127.07139364229327)));
    }

    public Place(String name, LatLng location){
        this.name = name;
        this.location = location;
    }

    public static List<Place> search(String query){
        List<Place> res = new ArrayList<>();
        for(Place place : db){
            if(place.name.contains(query))
                res.add(place);
        }
        return res;
    }

    public static String[] getNames(){
        String[] res = new String[db.size()];
        for(int i = 0; i < db.size(); i++)
            res[i] = db.get(i).name;
        return res;
    }

    public static Place get(int index){
        return db.get(index);
    }
}
