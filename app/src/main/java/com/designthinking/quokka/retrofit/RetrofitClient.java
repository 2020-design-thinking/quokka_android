package com.designthinking.quokka.retrofit;

import android.content.Context;
import android.util.Log;

import com.designthinking.quokka.api.IApiServer;
import com.designthinking.quokka.api.Token;
import com.designthinking.quokka.api.TokenResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private Context context;

    private IApiServer api;

    private Retrofit instance;
    private Token token;

    public RetrofitClient(Context context){
        this.context = context;
        token = new Token(context);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new TokenInterceptor()).build();

        instance = new Retrofit.Builder()
                .baseUrl("http://112.151.231.68:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        api = instance.create(IApiServer.class);
    }

    public IApiServer getApi(){
        return api;
    }

    public boolean hasToken(){
        return token.hasToken();
    }

    public void login(String username, String password, final IResult callback){
        api.login(username, password).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, retrofit2.Response<TokenResponse> response) {
                if(response.code() == 200){
                    token.setToken(response.body().token);
                    callback.result(Result.SUCCESS);
                }
                else{
                    callback.result(Result.FAIL);
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                callback.result(Result.NO_SERVER);
            }
        });
    }

    public class TokenInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            if(token.hasToken()){
                builder.addHeader("Authorization", "Token " + token.getToken());
            }
            return chain.proceed(builder.build());
        }
    }

}
