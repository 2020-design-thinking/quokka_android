package com.designthinking.quokka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StartDrivingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_driving);

        TextView name = findViewById(R.id.device_name);
        name.setText(getIntent().getStringExtra("device_name"));
        final int pk = getIntent().getIntExtra("device_pk", -1);

        findViewById(R.id.positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("device_pk", pk);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        findViewById(R.id.negative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}