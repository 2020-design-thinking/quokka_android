package com.designthinking.quokka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.designthinking.quokka.api.Device;
import com.designthinking.quokka.view.QuokkaMarker;
import com.designthinking.quokka.view.ViewBitmap;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
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
        mapFragment.getView().setClickable(false);

        double[] arr = getIntent().getDoubleArrayExtra("latlngs");
        driveStartLatLng = new LatLng(arr[0], arr[1]);
        driveFinishLatLng = new LatLng(arr[2], arr[3]);

        TextView timeText = findViewById(R.id.time);
        TextView distText = findViewById(R.id.dist);

        timeText.setText(getIntent().getLongExtra("time", 0) + "분");
        distText.setText(getIntent().getStringExtra("dist") + "km");

        TextView ratingText = findViewById(R.id.rating_text);
        RatingBar ratingBar = findViewById(R.id.rating);
        RadarChart radarChart = findViewById(R.id.safety_chart);
        radarChart.getLegend().setEnabled(false);
        radarChart.getDescription().setEnabled(false);
        radarChart.setTouchEnabled(false);
        radarChart.setWebLineWidth(2f);
        radarChart.setWebColor(Color.LTGRAY);
        radarChart.setWebLineWidthInner(2f);
        radarChart.setWebColorInner(Color.LTGRAY);
        radarChart.setWebAlpha(150);
        YAxis yAxis = radarChart.getYAxis();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(1f);
        yAxis.setTypeface(ResourcesCompat.getFont(this, R.font.nanum_barun_gothic));
        yAxis.setTextSize(18f);
        yAxis.setDrawLabels(false);
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(
                new String[]{"보행자 보호", "안전 구역 서행", "속도 조절", "방향 전환", "면허 및 보험"}
        ));
        xAxis.setTypeface(ResourcesCompat.getFont(this, R.font.nanum_barun_gothic));
        xAxis.setTextSize(14f);

        /*PieChart pieChart = findViewById(R.id.safety_chart);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("위험 요소");
        pieChart.setCenterTextSize(22f);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setCenterTextTypeface(ResourcesCompat.getFont(this, R.font.nanum_barun_gothic_b));
        pieChart.getLegend().setEnabled(false);
        pieChart.setExtraOffsets(10, 0, 10, 0);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(14f);
        pieChart.setEntryLabelTypeface(ResourcesCompat.getFont(this, R.font.nanum_barun_gothic_b));
        pieChart.setTouchEnabled(false);*/

        ratingText.setText(String.format("%.1f", getIntent().getFloatExtra("rating", 0)));
        ratingBar.setMax(5);
        ratingBar.setStepSize(0.1f);
        ratingBar.setRating(getIntent().getFloatExtra("rating", 0));
        radarChart.setData(getRadarData());

        TextView drivingChargeText = findViewById(R.id.charge_origin); // 원 요금
        ViewGroup chargeDiscountContainer = findViewById(R.id.charge_discount_container);
        TextView chargeDiscountText = findViewById(R.id.charge_discount); // 충전에 의한 감면
        ViewGroup safetyDiscountContainer = findViewById(R.id.safety_discount_container);
        TextView safetyDiscountText = findViewById(R.id.safety_discount); // 안전 평점에 의한 감면
        TextView chargeText = findViewById(R.id.charge); // 최종 요금

        drivingChargeText.setText(getIntent().getIntExtra("driving_charge", 0) + "원");
        if(getIntent().getIntExtra("charge_discount", 0) == 0) chargeDiscountContainer.setVisibility(View.GONE);
        chargeDiscountText.setText("-" + getIntent().getIntExtra("charge_discount", 0) + "원");
        if(getIntent().getIntExtra("safety_discount", 0) == 0) safetyDiscountContainer.setVisibility(View.GONE);
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

    public RadarData getRadarData(){
        ArrayList<RadarEntry> entries1 = new ArrayList<>();
        entries1.add(new RadarEntry((float)(Math.random())));
        entries1.add(new RadarEntry((float)(Math.random())));
        entries1.add(new RadarEntry((float)(Math.random())));
        entries1.add(new RadarEntry((float)(Math.random())));
        entries1.add(new RadarEntry((float)(Math.random())));

        RadarDataSet ds1 = new RadarDataSet(entries1, "");
        ds1.setColor(ContextCompat.getColor(this, R.color.colorAccent));
        ds1.setDrawFilled(true);
        ds1.setLineWidth(4f);
        //ds1.setDrawHighlightCircleEnabled(true);
        ds1.setDrawHighlightIndicators(true);
        /*ds1.setColors(Arrays.asList(
                Color.parseColor("#4777c0"),
                Color.parseColor("#a374c6"),
                Color.parseColor("#4fb3e8"),
                Color.parseColor("#99cf43"),
                Color.parseColor("#fdc135"),
                Color.parseColor("#fd9a47"),
                Color.parseColor("#eb6e7a"),
                Color.parseColor("#6785c2"))
        );*/
        //ds1.setValueTextColors(ds1.getColors());
        //ds1.setSliceSpace(2f);
        //ds1.setValueTextSize(12f);
        //ds1.setDrawValues(false);
        //ds1.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        /*ds1.setValueLinePart1OffsetPercentage(115f);
        ds1.setValueLinePart1Length(0.6f);
        ds1.setValueLinePart2Length(0.3f);
        ds1.setValueLineWidth(2f);
        //ds1.setValueTextColor(Color.BLACK);
        ds1.setUsingSliceColorAsValueLineColor(true);
        ds1.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //mPie.setEntryLabelColor(Color.BLUE);*/

        RadarData d = new RadarData(ds1);
        d.setValueTypeface(ResourcesCompat.getFont(this, R.font.nanum_barun_gothic));
        d.setDrawValues(false);

        return d;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        MarkerOptions options = new MarkerOptions();
        options.position(driveFinishLatLng);
        map.addMarker(options);

        options = new MarkerOptions();
        options.position(driveStartLatLng);
        Device dummy = new Device();
        dummy.battery = 1f;
        options.icon(BitmapDescriptorFactory.fromBitmap((new ViewBitmap(new QuokkaMarker(this, dummy), this)).getBitmap()));
        map.addMarker(options);

        map.addPolyline(new PolylineOptions()
                .add(driveStartLatLng, driveFinishLatLng)
                .width(20)
                .color(Color.parseColor("#4668f2"))
                .jointType(JointType.ROUND)
                .startCap(new RoundCap())
                .endCap(new RoundCap())
                .geodesic(true));

        // WTF?
        new Handler().postDelayed(() -> {
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(
                    new LatLngBounds.Builder()
                            .include(driveStartLatLng)
                            .include(driveFinishLatLng).build(), 150));
        }, 500);
    }
}