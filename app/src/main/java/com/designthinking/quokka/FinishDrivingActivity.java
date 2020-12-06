package com.designthinking.quokka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class FinishDrivingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_driving);

        TextView distText = findViewById(R.id.dist);
        TextView drivingChargeText = findViewById(R.id.charge_origin);
        TextView discountedChargeText = findViewById(R.id.charge_discount);
        TextView chargeText = findViewById(R.id.charge);
        RatingBar ratingBar = findViewById(R.id.rating);
        Button paymentBtn = findViewById(R.id.payment);

        distText.setText(getIntent().getStringExtra("dist") + "km");
        drivingChargeText.setText(getIntent().getIntExtra("driving_charge", 0) + "원");
        discountedChargeText.setText("-" + getIntent().getIntExtra("discounted_charge", 0) + "원");
        chargeText.setText(getIntent().getIntExtra("charge", 0) + "원");
        ratingBar.setMax(5);
        ratingBar.setRating(getIntent().getFloatExtra("rating", 0));
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinishDrivingActivity.this, SuccessActivity.class);
                intent.putExtra("title", "결제 완료!");
                intent.putExtra("desc", "이용해 주셔서 감사합니다");
                intent.putExtra("activity", "com.designthinking.quokka.MainActivity");
                startActivity(intent);
            }
        });
    }
}