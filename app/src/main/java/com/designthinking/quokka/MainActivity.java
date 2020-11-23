package com.designthinking.quokka;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.designthinking.quokka.api.Device;
import com.designthinking.quokka.drive.DrivingManager;
import com.designthinking.quokka.drive.IFinishDrivingListener;
import com.designthinking.quokka.location.FakeLocationProvider;
import com.designthinking.quokka.location.ILocationListener;
import com.designthinking.quokka.location.LocationProvider;
import com.designthinking.quokka.retrofit.IResult;
import com.designthinking.quokka.retrofit.Result;
import com.designthinking.quokka.retrofit.RetrofitClient;
import com.designthinking.quokka.view.DeviceDetailView;
import com.designthinking.quokka.view.DrivingView;
import com.designthinking.quokka.view.ViewBitmap;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, ILocationListener, IFinishDrivingListener {

    private static final int REQUEST_START_DRIVING = 101;

    private GoogleMap map;

    private RetrofitClient client;

    private List<Device> devices;

    private Handler handler;

    private ConstraintLayout rootLayout;
    private RelativeLayout defaultLayout;
    private Button qrScan;
    private ViewBitmap normalDevice, lowDevice;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;
    private boolean locationPermissionGranted = false;

    private Location lastLocation;

    private DeviceDetailView deviceDetailView;

    private DrivingView drivingView;
    private DrivingManager drivingManager;

    private LocationProvider locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootLayout = findViewById(R.id.root);
        defaultLayout = findViewById(R.id.default_layout);

        normalDevice = new ViewBitmap(LayoutInflater.from(this).inflate(R.layout.quokka_normal_marker, rootLayout, false), this);
        lowDevice = new ViewBitmap(LayoutInflater.from(this).inflate(R.layout.quokka_low_marker, rootLayout, false), this);

        client = new RetrofitClient(this);

        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                client.getApi().deviceList().enqueue(new Callback<List<Device>>() {
                    @Override
                    public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                        if(response.code() == 200){
                            devices = response.body();
                            updateMarkers();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Device>> call, Throwable t) {

                    }
                });

                handler.postDelayed(this, 10000);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        qrScan = findViewById(R.id.qr_scan);
        qrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setCaptureActivity(QrReaderActivity.class);

                integrator.setOrientationLocked(true);
                integrator.setBeepEnabled(false);
                integrator.setPrompt("");

                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

                integrator.initiateScan();
            }
        });

        deviceDetailView = new DeviceDetailView(this, defaultLayout);

        drivingManager = new DrivingManager(client.getApi());
        drivingView = new DrivingView(this, rootLayout, drivingManager, this);

        requestLocationPermission();

        locationProvider = new FakeLocationProvider(37.249893, 127.139157, 0.0005, 0.0005, handler);
        locationProvider.setListener(this);
    }

    public void updateMarkers(){
        map.clear();
        for(Device device : devices){
            if(!device.isAvailable()) continue;
            MarkerOptions options = new MarkerOptions();
            options.position(device.getLocation());
            options.icon(BitmapDescriptorFactory.fromBitmap(device.battery <= Device.LOW_BATTERY ? lowDevice.getBitmap() : normalDevice.getBitmap()));
            options.title(device.getId() + "");
            map.addMarker(options);
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else{
            updateLocation();
        }
    }

    private void updateLocation(){

    }

    private Device getDevice(int pk){
        for(Device device : devices){
            if(device.getId() == pk) return device;
        }
        return null;
    }

    private Device getDevice(String pk){
        for(Device device : devices){
            if(("" + device.getId()).equals(pk)) return device;
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                /*CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
                map.animateCamera(center);*/
                Device device = getDevice(Integer.parseInt(marker.getTitle()));
                if(device == null) return false;

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(device.getLocation(), 16f));

                deviceDetailView.show(device);

                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IntentIntegrator.REQUEST_CODE){
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                String pk = "";
                Device device = null;
                if (result.getContents() != null) {
                    pk = result.getContents();
                } else if(data != null && data.hasExtra("code")){
                    pk = data.getStringExtra("code");
                }
                else{
                    return;
                }
                device = getDevice(pk);

                if(device != null){
                    Intent intent = new Intent(this, StartDrivingActivity.class);
                    intent.putExtra("device_name", device.getName());
                    intent.putExtra("device_pk", device.getId());
                    startActivityForResult(intent, REQUEST_START_DRIVING);
                }
                else{
                    Intent intent = new Intent(this, ErrorActivity.class);
                    intent.putExtra("title", pk);
                    intent.putExtra("desc", "입력하신 번호의 쿼카가 존재하지 않습니다");
                    intent.putExtra("activity", "com.designthinking.quokka.MainActivity");
                    startActivity(intent);
                }
            }
        }
        else if(requestCode == REQUEST_START_DRIVING && resultCode == RESULT_OK){
            Device device = getDevice(data.getIntExtra("device_pk", -1));
            Toast.makeText(this, "Start Driving : " + device.getId(), Toast.LENGTH_LONG).show();
            drivingView.start(device, new IResult() {
                @Override
                public void result(Result code) {
                    //TODO: Exception Handling
                    if(code == Result.SUCCESS){
                        // Start Driving!
                        deviceDetailView.hide();
                        defaultLayout.setVisibility(View.GONE);

                        locationProvider.start();
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    requestLocationPermission();
                }
                else{
                    locationPermissionGranted = true;
                    updateLocation();
                }
            }
        }
    }

    @Override
    public void updateLocation(Location location) {
        lastLocation = location;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lastLocation.getLatitude(),
                        lastLocation.getLongitude()), 16f));

        drivingManager.updateLocation(lastLocation);

        drivingView.update();

        Log.i("MAP", "Location: " + lastLocation.getLatitude() + ", " + lastLocation.getLongitude());
    }

    @Override
    public void onFinishDriving() {
        defaultLayout.setVisibility(View.VISIBLE);
        locationProvider.pause();
    }
}