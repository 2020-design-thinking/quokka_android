package com.designthinking.quokka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.designthinking.quokka.location.FakeLocationProvider;
import com.designthinking.quokka.location.LocationProvider;
import com.designthinking.quokka.location.LocationPermission;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class SetDestinationActivity extends AppCompatActivity implements OnMapReadyCallback, LocationSource.OnLocationChangedListener {

    private GoogleMap map;
    private LocationProvider locationProvider;

    private Geocoder geocoder;

    private Handler handler;

    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_destination);

        EditText locationNameText = findViewById(R.id.search);
        locationNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO: Search
                if(s.toString().equals("영통역")){
                    MarkerOptions options = new MarkerOptions();
                    options.position(new LatLng(37.251316, 127.071383));
                    map.addMarker(options);

                    LatLngBounds bounds = new LatLngBounds.Builder()
                            .include(new LatLng(lastLocation.getLatitude(),
                                    lastLocation.getLongitude()))
                            .include(new LatLng(37.251316, 127.071383)).build();
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                }
            }
        });

        findViewById(R.id.positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SetDestinationActivity.this, LoadingActivity.class));
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        handler = new Handler();

        //locationProvider = new FakeLocationProvider(37.247630, 127.078420, 0.0005, 0.0005, handler);
        //locationProvider.activate(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        geocoder = new Geocoder(this);

        if(!LocationPermission.granted(this)){
            LocationPermission.request(this);
        }
        else{
            enableMyLocationOnMap();
        }
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocationOnMap(){
        map.setLocationSource(locationProvider);
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(),
                        location.getLongitude()), 16f));
        lastLocation = location;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case LocationPermission.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    LocationPermission.request(this);
                }
                else{
                    enableMyLocationOnMap();
                }
            }
        }
    }
}