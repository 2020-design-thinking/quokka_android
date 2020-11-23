package com.designthinking.quokka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ErrorActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        TextView title = findViewById(R.id.title);
        TextView desc = findViewById(R.id.desc);
        Button goBack = findViewById(R.id.goto_back);

        Bundle bundle = getIntent().getExtras();

        title.setText(bundle.getString("title"));
        desc.setText(bundle.getString("desc"));

        try {
            Class<?> clazz = Class.forName(bundle.getString("activity"));
            intent = new Intent(this, clazz);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }
}