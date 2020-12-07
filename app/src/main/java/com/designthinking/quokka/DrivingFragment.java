package com.designthinking.quokka;

import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.designthinking.quokka.api.Device;
import com.designthinking.quokka.api.Drive;
import com.designthinking.quokka.event.EventManager;
import com.designthinking.quokka.event.IEventListener;
import com.designthinking.quokka.event.messages.OnDrivingInfoUpdate;
import com.designthinking.quokka.event.messages.OnLocationUpdate;
import com.designthinking.quokka.event.messages.OnSpeedUpdate;
import com.designthinking.quokka.event.messages.OnStartDriving;
import com.designthinking.quokka.event.messages.OnStopDriving;
import com.designthinking.quokka.map.QuokkaMap;
import com.designthinking.quokka.retrofit.RetrofitClient;
import com.designthinking.quokka.util.LocationUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DrivingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrivingFragment extends Fragment implements IEventListener {

    private static final long UPDATE_RATE = 1000; // 실제 서버에 위치가 업데이트되는 주기 (ms)

    private Handler handler;

    private QuokkaMap quokkaMap;

    private RetrofitClient client;

    private ViewGroup root;
    private TextView timeText;
    private TextView distText;
    private TextView batteryText;
    private TextView timeLeftText;
    private TextView chargeText;
    private Button stopBtn;
    private ViewGroup warnBg;
    private ImageView childSign;
    private ImageView speedLimitSign;
    private SeekBar speedBar;
    private TextView speedText;

    private Drive drive;

    private Runnable textUpdateRunnable;
    private Runnable warnUpdateRunnable;
    private int warnTick;

    private long lastLocationUpdateTimestamp;

    private MediaPlayer mp;

    public DrivingFragment() {
        // Required empty public constructor
    }

    public static DrivingFragment newInstance() {
        DrivingFragment fragment = new DrivingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventManager.getInstance().addListener(this);

        handler = new Handler();

        textUpdateRunnable = () -> {
            timeText.setText(String.format("%02d:%02d", drive.getSeconds() / 60, drive.getSeconds() % 60));

            handler.postDelayed(textUpdateRunnable, 1000);
        };

        warnUpdateRunnable = () -> {
            if(drive.shouldWarn()){
                warnBg.setVisibility(warnTick % 2 == 0 ? View.VISIBLE : View.GONE);
                speedLimitSign.setVisibility(View.VISIBLE);
                switch (drive.getWarnType()){
                    case MAX_SPEED:
                        speedLimitSign.setImageResource(R.drawable.speed_limit_25);
                        break;
                    case AREA:
                        speedLimitSign.setImageResource(R.drawable.speed_limit_15);
                        break;
                    default:
                        speedLimitSign.setVisibility(View.GONE);
                }

                if(warnTick % 2 == 0){
                    mp.seekTo(0);
                    mp.start();
                }
            }
            else{
                warnBg.setVisibility(View.GONE);
                speedLimitSign.setVisibility(View.GONE);
            }
            warnTick++;

            handler.postDelayed(warnUpdateRunnable, 750);
        };

        mp = MediaPlayer.create(getContext(), R.raw.alert);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driving, container, false);
        root = view.findViewById(R.id.root);
        timeText = view.findViewById(R.id.time);
        distText = view.findViewById(R.id.dist);
        batteryText = view.findViewById(R.id.battery);
        timeLeftText = view.findViewById(R.id.time_left);
        chargeText = view.findViewById(R.id.charge);
        stopBtn = view.findViewById(R.id.stop);
        warnBg = view.findViewById(R.id.warn_bg);
        childSign = view.findViewById(R.id.child_sign);
        speedLimitSign = view.findViewById(R.id.speed_limit_sign);
        speedBar = view.findViewById(R.id.speed);
        speedText = view.findViewById(R.id.speed_text);

        root.setVisibility(View.GONE);

        stopBtn.setOnClickListener(v -> {
            if(drive == null) return;
            client.getApi().finishDriving(drive.pk).enqueue(new Callback<Drive>() {
                @Override
                public void onResponse(Call<Drive> call, Response<Drive> response) {
                    if(response.code() == 200){
                        if(drive == null) return;
                        drive.set(response.body());
                        EventManager.getInstance().invoke(new OnStopDriving(drive));
                    }
                }

                @Override
                public void onFailure(Call<Drive> call, Throwable t) {

                }
            });
        });

        speedBar.setMax(99);
        speedBar.setProgress(0);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedText.setText(progress + "km/h");
                EventManager.getInstance().invoke(new OnSpeedUpdate(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

    public void setClient(RetrofitClient client){
        this.client = client;
    }

    public void setQuokkaMap(QuokkaMap quokkaMap){
        this.quokkaMap = quokkaMap;
    }

    public void stopDrive(){
        if(drive == null) return;
    }

    public boolean isDriving(){
        return drive != null;
    }

    public Device getDevice(){
        return drive.getDevice();
    }

    public Drive getDrive(){
        return drive;
    }

    public void onStartDriving(OnStartDriving event){
        this.drive = event.drive;

        root.setVisibility(View.VISIBLE);

        handler.post(textUpdateRunnable);
        handler.post(warnUpdateRunnable);
    }

    public void onStopDriving(OnStopDriving event){
        this.drive = null;

        root.setVisibility(View.GONE);

        handler.removeCallbacks(textUpdateRunnable);
        handler.removeCallbacks(warnUpdateRunnable);

        warnBg.setVisibility(View.GONE);
        childSign.setVisibility(View.GONE);
        speedLimitSign.setVisibility(View.GONE);
    }

    public void onLocationUpdate(OnLocationUpdate event){
        if(drive == null || getDevice() == null) return;

        if(lastLocationUpdateTimestamp + UPDATE_RATE > System.currentTimeMillis()) return;

        Location location = event.location;

        client.getApi().updateDevice(getDevice().pk, location.getLatitude(), location.getLongitude()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

        client.getApi().updateDrive(drive.pk, location.getLatitude(), location.getLongitude(), (int)location.getSpeed()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

        client.getApi().getDriveStatus(drive.pk).enqueue(new Callback<Drive>() {
            @Override
            public void onResponse(Call<Drive> call, Response<Drive> response) {
                if(response.code() == 200){
                    if(drive != null && response.body() != null){
                        drive.set(response.body());
                        EventManager.getInstance().invoke(new OnDrivingInfoUpdate(drive));
                    }
                }
            }

            @Override
            public void onFailure(Call<Drive> call, Throwable t) {

            }
        });

        if(quokkaMap.isInSafeZone(LocationUtil.toLatLng(location)))
            childSign.setVisibility(View.VISIBLE);
        else
            childSign.setVisibility(View.GONE);

        lastLocationUpdateTimestamp = System.currentTimeMillis();
    }

    public void onDrivingInfoUpdate(OnDrivingInfoUpdate event){
        chargeText.setText(event.drive.charge + "");
        distText.setText(event.drive.dist + "km");
        int timeLeft = event.drive.getRoute().getRidingTime();
        timeLeftText.setText(timeLeft / 60 + "");
    }

}