package com.designthinking.quokka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, SuccessActivity.class);
                intent.putExtra("title", "가입 성공!");
                intent.putExtra("desc", "이제 등록한 아이디로 로그인 할 수 있어요");
                intent.putExtra("activity", "com.designthinking.quokka.LoginActivity");
                startActivity(intent);
            }
        });
    }
}