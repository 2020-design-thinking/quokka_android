package com.designthinking.quokka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView logoName = findViewById(R.id.logo_name);
        ViewCompat.setTransitionName(logoName, "logo_name");

        TextView gotoRegister = findViewById(R.id.goto_register);
        gotoRegister.setPaintFlags(gotoRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}