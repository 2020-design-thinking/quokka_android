package com.designthinking.quokka.api;

import android.content.Context;
import android.content.SharedPreferences;

public class Token {

    private Context context;
    private SharedPreferences preferences;

    private String token;

    public Token(Context context){
        this.context = context;

        preferences = context.getSharedPreferences("token", Context.MODE_PRIVATE);

        token = preferences.getString("token", null);
    }

    public boolean hasToken(){
        return token != null;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token = token;
        preferences.edit().putString("token", token).commit();
    }

}
