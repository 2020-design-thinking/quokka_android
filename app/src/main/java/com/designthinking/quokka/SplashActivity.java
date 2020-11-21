package com.designthinking.quokka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.view.Window;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        final TextView logoName = findViewById(R.id.logo_name);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                ActivityOptions options = ActivityOptions.
                        makeSceneTransitionAnimation(SplashActivity.this, logoName, "logo_name");
                startActivity(intent, options.toBundle());
            }
        }, 2000);
    }
}