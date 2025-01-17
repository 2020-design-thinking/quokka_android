package com.designthinking.quokka;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.designthinking.quokka.api.Device;
import com.designthinking.quokka.api.Drive;
import com.designthinking.quokka.api.SafeZone;
import com.designthinking.quokka.api.Station;
import com.designthinking.quokka.camera.IImageListener;
import com.designthinking.quokka.camera.ImageProvider;
import com.designthinking.quokka.camera.VideoPreview;
import com.designthinking.quokka.event.EventManager;
import com.designthinking.quokka.event.IEventListener;
import com.designthinking.quokka.event.messages.OnLoadingFinish;
import com.designthinking.quokka.event.messages.OnLocationUpdate;
import com.designthinking.quokka.event.messages.OnPreStopDriving;
import com.designthinking.quokka.event.messages.OnSafeZoneUpdate;
import com.designthinking.quokka.event.messages.OnSpeedUpdate;
import com.designthinking.quokka.event.messages.OnStartDriving;
import com.designthinking.quokka.event.messages.OnStationUpdate;
import com.designthinking.quokka.event.messages.OnStopDriving;
import com.designthinking.quokka.location.FakeLocationProvider;
import com.designthinking.quokka.location.LocationProvider;
import com.designthinking.quokka.location.LocationPermission;
import com.designthinking.quokka.map.QuokkaMap;
import com.designthinking.quokka.place.Place;
import com.designthinking.quokka.reserve.Reserve;
import com.designthinking.quokka.retrofit.RetrofitClient;
import com.designthinking.quokka.route.Route;
import com.designthinking.quokka.route.RouteCalculator;
import com.designthinking.quokka.util.LocationUtil;
import com.designthinking.quokka.view.DeviceDetailView;
import com.designthinking.quokka.view.QuokkaMarker;
import com.designthinking.quokka.view.StationMarker;
import com.designthinking.quokka.view.ViewBitmap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.maps.android.SphericalUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        IImageListener, DestinationFragment.RouteTypeChangeListener, DestinationFragment.DestinationChangeListener,
        QuokkaFragment.ReserveListener, IEventListener {

    private static final int REQUEST_START_DRIVING = 101;

    private GoogleMap map;
    private boolean mapInitPos = false;

    private RetrofitClient client;

    private List<Device> devices = new ArrayList<>();

    private Handler handler;

    private ConstraintLayout rootLayout;
    private ViewGroup imageProviderContainer;
    private RelativeLayout defaultLayout;
    private Button qrScan;

    private Location lastLocation;

    private DeviceDetailView deviceDetailView;

    //private DrivingView drivingView;
    //private DrivingManager drivingManager;

    private LocationProvider locationProvider;

    private ImageProvider imageProvider;

    private QuokkaFragment quokkaFragment;
    private DestinationFragment destinationFragment;
    private DrivingFragment drivingFragment;
    private LoadingFragment loadingFragment;

    private Marker destinationMarker;
    private List<Polyline> polylines = new ArrayList<>();
    private List<Circle> circles = new ArrayList<>();
    private List<Marker> stationMarkers = new ArrayList<>();

    private QuokkaMap quokkaMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("MAIN", "Create");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventManager.getInstance().addListener(this);

        rootLayout = findViewById(R.id.root);
        defaultLayout = findViewById(R.id.default_layout);
        imageProviderContainer = findViewById(R.id.image_provider_root);
        quokkaFragment = (QuokkaFragment) getSupportFragmentManager().findFragmentById(R.id.quokka_fragment);
        quokkaFragment.setListener(this);
        destinationFragment = (DestinationFragment) getSupportFragmentManager().findFragmentById(R.id.destination_fragment);
        destinationFragment.setRouteTypeChangeListener(this);
        destinationFragment.setDestinationChangeListener(this);
        drivingFragment = (DrivingFragment) getSupportFragmentManager().findFragmentById(R.id.driving_fragment);
        loadingFragment = (LoadingFragment) getSupportFragmentManager().findFragmentById(R.id.loading_fragment);
        
        client = new RetrofitClient(this);
        quokkaMap = new QuokkaMap(client);

        quokkaFragment.setClient(client);
        drivingFragment.setClient(client);
        drivingFragment.setQuokkaMap(quokkaMap);

        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                client.getApi().deviceList().enqueue(new Callback<List<Device>>() {
                    @Override
                    public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                        if(response.code() == 200){
                            updateDevices(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Device>> call, Throwable t) {

                    }
                });
                handler.postDelayed(this, 2000);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        deviceDetailView = new DeviceDetailView(this, defaultLayout);

        //drivingManager = new DrivingManager(client.getApi());
        //drivingView = new DrivingView(this, rootLayout, drivingManager, null);

        locationProvider = new FakeLocationProvider(LocationUtil.getRandomLatLng(), handler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("MAIN", "Destroy");
        handler.removeCallbacksAndMessages(null);
        EventManager.getInstance().removeAllListeners();
    }

    public void updateDevices(List<Device> devices){
        List<Device> toAdd = new ArrayList<>();
        for(Device newDevice : devices){
            Device oldDevice = getDevice(newDevice.pk);
            if(oldDevice == null){
                MarkerOptions options = new MarkerOptions();
                options.position(newDevice.getLocation());
                options.icon(BitmapDescriptorFactory.fromBitmap((new ViewBitmap(new QuokkaMarker(this, newDevice), this)).getBitmap()));
                options.title(newDevice.getId() + "");
                newDevice.marker = map.addMarker(options);

                toAdd.add(newDevice);
            }
            else{
                oldDevice.set(newDevice);

                oldDevice.marker.setPosition(oldDevice.getLocation());
                oldDevice.marker.setIcon(BitmapDescriptorFactory.fromBitmap((new ViewBitmap(new QuokkaMarker(this, newDevice), this)).getBitmap()));
                oldDevice.marker.setTitle(oldDevice.getId() + "");
            }
        }
        this.devices.addAll(toAdd);

        updateMarkerAlpha();

        // 초기 루트
        if(quokkaFragment.getRoute() == null && lastLocation != null)
            showRecommandedRoute();
        // 디바이스 업데이트에 따라 경로 재설정
        else if(!quokkaFragment.isReserved() && !drivingFragment.isDriving()
                && destinationFragment.getState() != DestinationFragment.State.NONE)
            destinationFragment.setState(destinationFragment.getState());

    }

    public void updateMarkerAlpha(){
        for(Device device : devices){
            if(drivingFragment.isDriving() || !device.isAvailable())
                device.marker.setAlpha(0f);
            else if(quokkaFragment.getRoute() == null
                    || quokkaFragment.getRoute().device == device)
                device.marker.setAlpha(1f);
            else
                device.marker.setAlpha(0.5f);
        }
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

    private void setRoute(Route route){
        if(route == null) return;
        if(drivingFragment.isDriving()) return;
        if(quokkaFragment.getRoute() != null && quokkaFragment.getRoute().equals(route)) return;

        quokkaFragment.setRoute(route);

        drawRoute(route, true);
        updateMarkerAlpha();
    }

    private void drawRoute(Route route, boolean camUpdate) {
        if(route == null) return;

        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

        for(Polyline polyline : polylines)
            polyline.remove();
        polylines.clear();

        polylines.add(map.addPolyline(new PolylineOptions()
                .add(route.device.getLocation(), route.getCurrentLocation())
                .width(20)
                .color(Color.GRAY)
                .pattern(Arrays.asList(new Dot(), new Gap(15f)))
                .geodesic(true)));
        bounds.include(route.getCurrentLocation()).include(route.device.getLocation());
        if(route.target != null){
            polylines.add(map.addPolyline(new PolylineOptions()
                    .add(route.device.getLocation(), route.target)
                    .width(20)
                    .color(Color.parseColor("#4668f2"))
                    .jointType(JointType.ROUND)
                    .startCap(new RoundCap())
                    .endCap(new RoundCap())
                    .geodesic(true)));
            if(!quokkaFragment.isReserved())
                bounds.include(route.target);
        }

        if(destinationMarker != null)
            destinationMarker.remove();

        if(route.target != null){
            MarkerOptions options = new MarkerOptions();
            options.position(route.target);
            destinationMarker = map.addMarker(options);
        }

        if(quokkaFragment.isReserved()){
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(LocationUtil.toLatLng(locationProvider.getLastLocation()))
                    .zoom(17)
                    .tilt(30)
                    .bearing((float)SphericalUtil.computeHeading(
                            LocationUtil.toLatLng(locationProvider.getLastLocation()),
                            quokkaFragment.getReserve().device.getLocation())).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
        }
        else if(drivingFragment.isDriving() && route.target != null){
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(LocationUtil.toLatLng(locationProvider.getLastLocation()))
                    .zoom(17)
                    .tilt(30)
                    .bearing((float)SphericalUtil.computeHeading(
                            LocationUtil.toLatLng(locationProvider.getLastLocation()),
                            route.target)).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
        }

        if(camUpdate) map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 300));
    }

    private void showRecommandedRoute(){
        destinationFragment.setState(DestinationFragment.State.FASTEST);
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocationOnMap(){
        map.setLocationSource(locationProvider);
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(map != null && lastLocation != null){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 16f));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnMarkerClickListener(marker -> {
            /*CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
            map.animateCamera(center);*/
            if(marker.getAlpha() == 0f) return false;
            if(quokkaFragment.isReserved()) return true;

            if(marker.getTitle() == null) return false;
            Device device = getDevice(Integer.parseInt(marker.getTitle()));
            if(device == null) return false;

            destinationFragment.setState(DestinationFragment.State.NONE);

            if(destinationFragment.getDestination() != null)
                setRoute(new Route(locationProvider, device, destinationFragment.getDestination().location));
            else
                setRoute(new Route(locationProvider, device));

            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(device.getLocation(), 16f));

            //deviceDetailView.show(device);

            return true;
        });

        if(!LocationPermission.granted(this)){
            LocationPermission.request(this);
        }
        else{
            enableMyLocationOnMap();
        }

        map.setPadding(dpToPx(10), dpToPx(80), dpToPx(10), dpToPx(200)); // l, t, r, b

        EventManager.getInstance().invoke(new OnLoadingFinish());
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
            /*drivingView.start(device, new IResult() {
                @Override
                public void result(Result code) {
                    //TODO: Exception Handling
                    if(code == Result.SUCCESS){
                        // Start Driving!
                    }
                    else{
                        Intent intent = new Intent(MainActivity.this, ErrorActivity.class);
                        intent.putExtra("title", "이미 사용중!");
                        intent.putExtra("desc", "해당 쿼카는 이미 사용중입니다");
                        intent.putExtra("activity", "com.designthinking.quokka.MainActivity");
                        startActivity(intent);
                    }
                }
            });*/
        }
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

    @Override
    public void updateImageFile(File file) {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
        client.getApi().postImage(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
    public void onChanged(DestinationFragment.State state) {
        if(locationProvider.getLastLocation() == null) return;

        Route route;
        switch (state){
            case FASTEST:
                if(destinationFragment.getDestination() == null)
                    route = RouteCalculator.calculateFastest(locationProvider, devices);
                else
                    route = RouteCalculator.calculateFastest(locationProvider, devices, destinationFragment.getDestination().location);
                setRoute(route);
                break;
            case CHEAPEST:
                if(destinationFragment.getDestination() == null)
                    route = RouteCalculator.calculateCheapest(locationProvider, devices);
                else
                    route = RouteCalculator.calculateCheapest(locationProvider, devices, destinationFragment.getDestination().location);
                setRoute(route);
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof AutoCompleteTextView) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onChanged(Place place) {
        if(quokkaFragment.isReserved() || drivingFragment.isDriving()){
            destinationFragment.setState(DestinationFragment.State.NONE);
            quokkaFragment.getRoute().target = place.location;
        }
        else{
            destinationFragment.setState(DestinationFragment.State.FASTEST);
        }
    }

    @Override
    public void onReserved(Reserve reserve) {
        drawRoute(quokkaFragment.getRoute(), true);
        destinationFragment.setRouteTypeVisibility(reserve == null && !drivingFragment.isDriving());

        if(reserve != null){
            locationProvider.setTargetLatLng(reserve.device.getLocation());
            locationProvider.setSpeed(30);
        }
        else{
            locationProvider.setTargetLatLng(null);
            locationProvider.setSpeed(0);
        }
    }

    public void onStartDriving(OnStartDriving event){
        deviceDetailView.hide();
        defaultLayout.setVisibility(View.GONE);

        imageProvider = new VideoPreview(MainActivity.this, handler);
        imageProvider.setListener(MainActivity.this);

        imageProviderContainer.addView(imageProvider);

        if(quokkaFragment.getRoute().target == null){
            quokkaFragment.getRoute().target = LocationUtil.getRandomLatLng();
        }
        locationProvider.setTargetLatLng(quokkaFragment.getRoute().target);
        locationProvider.setSpeed(0);

        destinationFragment.setRouteTypeVisibility(false);

        updateMarkerAlpha();
    }

    public void onPreStopDriving(OnPreStopDriving event){
        imageProvider.stop();
        imageProviderContainer.removeView(imageProvider);
    }

    public void onStopDriving(OnStopDriving event){
        defaultLayout.setVisibility(View.VISIBLE);
        locationProvider.deactivate();

        Drive drive = event.drive;
        Intent intent = new Intent(this, FinishDrivingActivity.class);
        intent.putExtra("latlngs", new double[]{
                drive.getRoute().getDriveStartLatLng().latitude,
                drive.getRoute().getDriveStartLatLng().longitude,
                drive.getRoute().device.getLocation().latitude,
                drive.getRoute().device.getLocation().longitude
        });
        intent.putExtra("time", drive.getSeconds() / 60);
        intent.putExtra("dist", drive.dist);
        intent.putExtra("rating", drive.safety_rate);
        intent.putExtra("driving_charge", drive.driving_charge);
        intent.putExtra("charge_discount", drive.charge_discount);
        intent.putExtra("safety_discount", drive.safety_discount);
        intent.putExtra("charge", drive.charge);

        startActivity(intent);

        locationProvider.setTargetLatLng(null);
        locationProvider.setSpeed(0);

        destinationFragment.setRouteTypeVisibility(true);
    }

    public void onLocationUpdate(OnLocationUpdate event){
        lastLocation = event.location;
        //lastLocation.setSpeed(drivingView.getSpeed());
        /*map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lastLocation.getLatitude(),
                        lastLocation.getLongitude()), 16f));*/

        if(!mapInitPos){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LocationUtil.toLatLng(lastLocation), 16f));
            mapInitPos = true;
        }

        if(quokkaFragment.getRoute() == null && devices != null)
            showRecommandedRoute();

        //drivingManager.updateLocation(lastLocation);
        //drivingView.update();

        if(drivingFragment.isDriving())
            drivingFragment.getDevice().setLocation(LocationUtil.toLatLng(event.location));

        drawRoute(quokkaFragment.getRoute(), false);
    }

    public void onSpeedUpdate(OnSpeedUpdate event){
        locationProvider.setSpeed(event.speed);
    }

    public void onSafeZoneUpdate(OnSafeZoneUpdate event){
        for(Circle circle : circles)
            circle.remove();

        for(SafeZone safeZone : event.safeZones){
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(safeZone.getLatLng());
            circleOptions.radius(safeZone.getRadius() * 1000);
            circleOptions.strokeColor(ContextCompat.getColor(this, R.color.colorWarn));
            circleOptions.fillColor(ContextCompat.getColor(this, R.color.colorWarnFill));
            circleOptions.strokeWidth(5);
            circles.add(map.addCircle(circleOptions));
        }
    }

    public void onStationUpdate(OnStationUpdate event){
        for(Marker marker : stationMarkers)
            marker.remove();

        for(Station station : event.stations){
            MarkerOptions options = new MarkerOptions();
            options.position(station.getLatLng());
            options.icon(BitmapDescriptorFactory.fromBitmap((new ViewBitmap(new StationMarker(this), this)).getBitmap()));
            stationMarkers.add(map.addMarker(options));
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}