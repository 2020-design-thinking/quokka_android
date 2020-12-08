package com.designthinking.quokka;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.designthinking.quokka.api.Device;
import com.designthinking.quokka.api.Drive;
import com.designthinking.quokka.api.MessageResponse;
import com.designthinking.quokka.event.EventManager;
import com.designthinking.quokka.event.IEventListener;
import com.designthinking.quokka.event.messages.OnLocationUpdate;
import com.designthinking.quokka.event.messages.OnStartDriving;
import com.designthinking.quokka.event.messages.OnStopDriving;
import com.designthinking.quokka.reserve.Reserve;
import com.designthinking.quokka.retrofit.RetrofitClient;
import com.designthinking.quokka.route.Route;
import com.designthinking.quokka.util.LocationUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuokkaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuokkaFragment extends Fragment implements IEventListener {

    private Handler handler;

    private ConstraintLayout rootContainer;

    private ImageView imageImage;
    private TextView nameText;
    private TextView batteryText;
    private TextView timeLeftText;

    private LinearLayout estimationContainer;
    private TextView timeText;
    private TextView chargeText;
    private Button cancelBtn;
    private Button reserveBtn;
    private Button startBtn;
    private ImageView qrScanBtn;

    private Route route;

    private RetrofitClient client;

    private Reserve reserve;

    private Runnable reserveTimeUpdate;

    private ReserveListener reserveListener;

    public QuokkaFragment() {
        // Required empty public constructor
    }

    public static QuokkaFragment newInstance() {
        QuokkaFragment fragment = new QuokkaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventManager.getInstance().addListener(this);

        handler = new Handler();

        reserveTimeUpdate = () -> {
            if(!reserve.isExpired()){
                startBtn.setText("탑승 시작 " + String.format("%02d:%02d", reserve.getSecondsLeft() / 60, reserve.getSecondsLeft() % 60));
                handler.postDelayed(reserveTimeUpdate, 1000);
            }
            else{
                cancelReserve();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_quokka, container, false);

        rootContainer = parent.findViewById(R.id.root);

        imageImage = parent.findViewById(R.id.image);
        nameText = parent.findViewById(R.id.name);
        batteryText = parent.findViewById(R.id.battery);
        timeLeftText = parent.findViewById(R.id.time_left);

        estimationContainer = parent.findViewById(R.id.estimation_container);
        timeText = parent.findViewById(R.id.time);
        chargeText = parent.findViewById(R.id.charge);
        cancelBtn = parent.findViewById(R.id.cancel);
        reserveBtn = parent.findViewById(R.id.reserve);
        startBtn = parent.findViewById(R.id.start);
        qrScanBtn = parent.findViewById(R.id.qr_scan);

        rootContainer.setVisibility(View.GONE);

        cancelBtn.setOnClickListener(v -> {
            cancelReserve();
        });

        reserveBtn.setOnClickListener(v -> {
            if(route == null || route.device == null) return;
            final Device device = route.device;
            client.getApi().reserve(route.device.pk).enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if(response.code() == 200){
                        setReserve(new Reserve(device));
                    }
                    else if(response.body() != null && response.body().message != null){
                        Toast.makeText(getContext(), response.body().message, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {

                }
            });
        });

        startBtn.setOnClickListener(v -> {
            if(!isReserved()) return;
            requestDrive(reserve.device);
        });

        /*qrScanBtn.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.setCaptureActivity(QrReaderActivity.class);

            integrator.setOrientationLocked(true);
            integrator.setBeepEnabled(false);
            integrator.setPrompt("");

            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

            integrator.initiateScan();
        });*/

        return parent;
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

                Log.i("QR", "Result = " + pk);

                /*device = getDevice(pk);

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
                }*/
            }
        }
    }

    public void setRoute(Route route){
        this.route = route;

        rootContainer.setVisibility(View.VISIBLE);
        nameText.setText(route.device.getName());
        batteryText.setText("배터리 " + route.device.getIntBattery() + "%");
        timeLeftText.setVisibility(View.GONE);

        if(route.hasDestination()){
            estimationContainer.setVisibility(View.VISIBLE);

            timeText.setText((route.getTotalTime() / 60) + "");
            chargeText.setText(route.getCharge() + "");
        }
        else{
            estimationContainer.setVisibility(View.GONE);
        }

        if(isReserved())
            cancelReserve();
    }

    public Route getRoute(){
        return route;
    }

    public void setClient(RetrofitClient client){
        this.client = client;
    }

    private void setReserve(Reserve reserve){
        this.reserve = reserve;

        if(reserve != null){
            cancelBtn.setVisibility(View.VISIBLE);
            reserveBtn.setVisibility(View.GONE);
            startBtn.setVisibility(View.VISIBLE);

            handler.post(reserveTimeUpdate);
        }
        else{
            cancelBtn.setVisibility(View.GONE);
            reserveBtn.setVisibility(View.VISIBLE);
            startBtn.setVisibility(View.GONE);

            handler.removeCallbacks(reserveTimeUpdate);

            client.getApi().cancelReserve().enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {

                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {

                }
            });
        }

        reserveListener.onReserved(reserve);
    }

    private void cancelReserve(){
        setReserve(null);
    }

    public boolean isReserved(){
        return reserve != null;
    }

    public Reserve getReserve(){
        return reserve;
    }

    public void requestDrive(Device device){
        client.getApi().startDriving(device.pk).enqueue(new Callback<Drive>() {
            @Override
            public void onResponse(Call<Drive> call, Response<Drive> response) {
                if(response.code() == 200){
                    Drive drive = response.body();
                    if(drive == null) return;

                    route.setDriveStartLatLng(device.getLocation());

                    drive.setRoute(route);
                    drive.start();

                    EventManager.getInstance().invoke(new OnStartDriving(drive));
                }
            }

            @Override
            public void onFailure(Call<Drive> call, Throwable t) {

            }
        });
    }

    public void setListener(ReserveListener listener){
        this.reserveListener = listener;
    }

    public interface ReserveListener {
        void onReserved(Reserve reserve);
    }

    public void onStartDriving(OnStartDriving event){
        rootContainer.setVisibility(View.GONE);
        qrScanBtn.setVisibility(View.GONE);
        if(isReserved())
            cancelReserve();
    }

    public void onStopDriving(OnStopDriving event){
        rootContainer.setVisibility(View.VISIBLE);
        qrScanBtn.setVisibility(View.VISIBLE);
    }

    public void onLocationUpdate(OnLocationUpdate event){
        if(route != null && route.hasDestination()){
            // 예상 시간 업데이트
            timeText.setText((route.getTotalTime() / 60) + "");
        }
    }
}