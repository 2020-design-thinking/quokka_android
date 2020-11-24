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
import android.widget.Toast;

import com.designthinking.quokka.retrofit.RetrofitClient;
import com.designthinking.quokka.view.VEditText;

public class RegisterActivity extends AppCompatActivity {

    RetrofitClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        VEditText nameEdit = findViewById(R.id.name);
        VEditText idEdit = findViewById(R.id.id);
        VEditText pwEdit = findViewById(R.id.password);
        VEditText pwConfirmEdit = findViewById(R.id.password_confirm);
        VEditText emailEdit = findViewById(R.id.email);

        client = new RetrofitClient(this);

        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 더 깐깐한 Validation 구현

                boolean pass = true;
                if(nameEdit.length() < 3 || nameEdit.length() > 4){
                    nameEdit.setErrorMessage("본인의 이름을 입력해주세요");
                    pass = false;
                }
                else nameEdit.removeErrorMessage();
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
                if(emailEdit.length() == 0){
                    emailEdit.setErrorMessage("이메일을 입력해주세요");
                    pass = false;
                }
                else emailEdit.removeErrorMessage();

                if(!pass) return;

                String firstName, lastName;
                if(nameEdit.length() == 3){
                    firstName = nameEdit.getText().toString().substring(0, 1);
                    lastName = nameEdit.getText().toString().substring(1, 3);
                }
                else{
                    firstName = nameEdit.getText().toString().substring(0, 2);
                    lastName = nameEdit.getText().toString().substring(2, 4);
                }

                client.getApi().register(
                        idEdit.getText().toString(),
                        emailEdit.getText().toString(),
                        pwEdit.getText().toString(),
                        firstName,
                        lastName,
                        "2020-11-24").enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == 200){
                            Intent intent = new Intent(RegisterActivity.this, SuccessActivity.class);
                            intent.putExtra("title", "가입 성공!");
                            intent.putExtra("desc", "이제 등록한 아이디로 로그인 할 수 있어요");
                            intent.putExtra("activity", "com.designthinking.quokka.LoginActivity");
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "가입에 실패했습니다. 입력한 내용을 다시 한번 검토해주세요.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "서버가 응답하지 않습니다.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}