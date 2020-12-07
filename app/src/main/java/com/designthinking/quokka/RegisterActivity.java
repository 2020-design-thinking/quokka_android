package com.designthinking.quokka;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.designthinking.quokka.api.MessageResponse;
import com.designthinking.quokka.retrofit.RetrofitClient;
import com.designthinking.quokka.view.VEditText;

public class RegisterActivity extends AppCompatActivity {

    RetrofitClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //VEditText nameEdit = findViewById(R.id.name);
        VEditText idEdit = findViewById(R.id.id);
        VEditText pwEdit = findViewById(R.id.password);
        VEditText pwConfirmEdit = findViewById(R.id.password_confirm);
        TextView errorText = findViewById(R.id.register_error_text);
        //VEditText emailEdit = findViewById(R.id.email);

        client = new RetrofitClient(this);

        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 더 깐깐한 Validation 구현

                boolean pass = true;
                /*if(nameEdit.length() < 3 || nameEdit.length() > 4){
                    nameEdit.setErrorMessage("본인의 이름을 입력해주세요");
                    pass = false;
                }
                else nameEdit.removeErrorMessage();*/
                if(idEdit.length() < 4){
                    idEdit.setErrorMessage("아이디는 최소 4글자 이상 입력해주세요");
                    pass = false;
                }
                else idEdit.removeErrorMessage();
                if(pwEdit.length() < 8){
                    pwEdit.setErrorMessage("비밀번호는 최소 8글자 이상 입력해주세요");
                    pass = false;
                }
                else pwEdit.removeErrorMessage();
                if(!pwConfirmEdit.getText().toString().equals(pwEdit.getText().toString())){
                    pwConfirmEdit.setErrorMessage("비밀번호가 일치하지 않습니다");
                    pass = false;
                }
                else pwConfirmEdit.removeErrorMessage();
                /*if(emailEdit.length() == 0){
                    emailEdit.setErrorMessage("이메일을 입력해주세요");
                    pass = false;
                }
                else emailEdit.removeErrorMessage();*/

                errorText.setVisibility(View.GONE);

                if(!pass) return;

                /*String firstName, lastName;
                if(nameEdit.length() == 3){
                    firstName = nameEdit.getText().toString().substring(0, 1);
                    lastName = nameEdit.getText().toString().substring(1, 3);
                }
                else{
                    firstName = nameEdit.getText().toString().substring(0, 2);
                    lastName = nameEdit.getText().toString().substring(2, 4);
                }*/

                client.getApi().register(
                        idEdit.getText().toString(),
                        "example@gmail.com",
                        pwEdit.getText().toString(),
                        "강",
                        "동현",
                        "2001-02-27").enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                        if(response.body() != null && response.code() == 200){
                            if(response.body().message.equals("DUP_ID")){
                                errorText.setText("이미 동일한 아이디가 존재합니다");
                                errorText.setVisibility(View.VISIBLE);
                            }
                            else{
                                Intent intent = new Intent(RegisterActivity.this, SuccessActivity.class);
                                intent.putExtra("title", "가입 성공!");
                                intent.putExtra("desc", "이제 등록한 아이디로 로그인 할 수 있어요");
                                intent.putExtra("activity", "com.designthinking.quokka.LoginActivity");
                                startActivity(intent);
                            }
                        }
                        else{
                            errorText.setText("알 수 없는 오류가 발생했습니다");
                            errorText.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        errorText.setText("서버가 응답하지 않습니다");
                        errorText.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }
}