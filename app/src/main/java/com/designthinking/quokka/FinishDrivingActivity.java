package com.designthinking.quokka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.Arrays;

public class FinishDrivingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;

    private LatLng driveStartLatLng, driveFinishLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_driving);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        double[] arr = getIntent().getDoubleArrayExtra("latlngs");
        driveStartLatLng = new LatLng(arr[0], arr[1]);
        driveFinishLatLng = new LatLng(arr[2], arr[3]);

        TextView timeText = findViewById(R.id.time);
        TextView distText = findViewById(R.id.dist);

        timeText.setText(getIntent().getLongExtra("time", 0) + "분");
        distText.setText(getIntent().getStringExtra("dist") + "km");

        TextView ratingText = findViewById(R.id.rating_text);
        RatingBar ratingBar = findViewById(R.id.rating);

        ratingText.setText(String.format("%.1f", getIntent().getFloatExtra("rating", 0)));
        ratingBar.setMax(5);
        ratingBar.setRating(getIntent().getFloatExtra("rating", 0));

        TextView drivingChargeText = findViewById(R.id.charge_origin); // 원 요금
        TextView chargeDiscountText = findViewById(R.id.charge_discount); // 충전에 의한 감면
        TextView safetyDiscountText = findViewById(R.id.safety_discount); // 안전 평점에 의한 감면
        TextView chargeText = findViewById(R.id.charge); // 최종 요금

        drivingChargeText.setText(getIntent().getIntExtra("driving_charge", 0) + "원");
        chargeDiscountText.setText("-" + getIntent().getIntExtra("charge_discount", 0) + "원");
        safetyDiscountText.setText("-" + getIntent().getIntExtra("safety_discount", 0) + "%");
        chargeText.setText(getIntent().getIntExtra("charge", 0) + "원");

        Button paymentBtn = findViewById(R.id.payment);
        paymentBtn.setOnClickListener(v -> {
            Intent intent = new Intent(FinishDrivingActivity.this, SuccessActivity.class);
            intent.putExtra("title", "결제 완료!");
            intent.putExtra("desc", "이용해 주셔서 감사합니다");
            intent.putExtra("activity", "com.designthinking.quokka.MainActivity");
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        MarkerOptions options = new MarkerOptions();
        options.position(driveFinishLatLng);
        map.addMarker(options);

        map.addPolyline(new PolylineOptions()
                .add(driveStartLatLng, driveFinishLatLng)
                .width(20)
                .color(Color.parseColor("#4668f2"))
                .jointType(JointType.ROUND)
                .startCap(new RoundCap())
                .endCap(new RoundCap())
                .geodesic(true));

        /*map.moveCamera(CameraUpdateFactory.newLatLngBounds(
                new LatLngBounds.Builder()
                        .include(driveStartLatLng)
                        .include(driveFinishLatLng).build(), 30));*/
    }
}