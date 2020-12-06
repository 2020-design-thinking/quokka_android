package com.designthinking.quokka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.designthinking.quokka.retrofit.IResult;
import com.designthinking.quokka.retrofit.Result;
import com.designthinking.quokka.retrofit.RetrofitClient;
import com.designthinking.quokka.view.VEditText;

public class LoginActivity extends AppCompatActivity {

    private VEditText idText, pwText;
    private TextView loginErrorText;

    private RetrofitClient api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        api = new RetrofitClient(this);

        TextView logoName = findViewById(R.id.logo_name);
        ViewCompat.setTransitionName(logoName, "logo_name");

        TextView gotoRegister = findViewById(R.id.goto_register);
        gotoRegister.setPaintFlags(gotoRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        idText = findViewById(R.id.id);
        pwText = findViewById(R.id.pw);
        loginErrorText = findViewById(R.id.login_error_text);

        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean blank = false;

                if(idText.getText().length() == 0){
                    idText.setErrorMessage("아이디를 입력해주세요");
                    blank = true;
                }
                else{
                    idText.removeErrorMessage();
                }

                if(pwText.getText().length() == 0){
                    pwText.setErrorMessage("비밀번호를 입력해주세요");
                    blank = true;
                }
                else{
                    idText.removeErrorMessage();
                }

                if(blank) return;

                loginErrorText.setVisibility(View.GONE);
                api.login(idText.getText().toString(), pwText.getText().toString(), new IResult() {
                    @Override
                    public void result(Result code) {
                        switch (code){
                            case SUCCESS:
                                Intent intent = new Intent(LoginActivity.this, RequestDestinationActivity.class);
                                startActivity(intent);
                                break;
                            case FAIL:
                                loginErrorText.setText("일치하는 로그인 정보가 존재하지 않습니다");
                                loginErrorText.setVisibility(View.VISIBLE);
                                break;
                            case NO_SERVER:
                                loginErrorText.setText("서버에 연결할 수 없습니다 :(");
                                loginErrorText.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });
            }
        });
    }
}