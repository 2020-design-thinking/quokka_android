package com.designthinking.quokka.api;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IApiServer {

    @FormUrlEncoded
    @POST("users/login")
    Call<TokenResponse> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("users/register")
    Call<MessageResponse> register(@Field("username") String username, @Field("email") String email,
                        @Field("password") String password, @Field("first_name") String first_name,
                        @Field("last_name") String last_name, @Field("birth") String birth);

    @GET("devices/")
    Call<List<Device>> deviceList();

    @POST("devices/{id}/reserve")
    Call<MessageResponse> reserve(@Path(value = "id", encoded = true) int id);

    @POST("users/cancel_reserve")
    Call<MessageResponse> cancelReserve();

    @POST("devices/{id}/drive")
    Call<Drive> startDriving(@Path(value = "id", encoded = true) int id);

    @DELETE("/drive/{id}")
    Call<Drive> finishDriving(@Path(value = "id", encoded = true) int id);

    @FormUrlEncoded
    @PUT("/devices/{id}")
    Call<Void> updateDevice(@Path(value = "id", encoded = true) int id, @Field("lat") double lat, @Field("lng") double lng);

    @FormUrlEncoded
    @PUT("/drive/{id}")
    Call<Void> updateDrive(@Path(value = "id", encoded = true) int id, @Field("lat") double lat, @Field("lng") double lng, @Field("speed") int speed);

    @GET("/drive/{id}")
    Call<Drive> getDriveStatus(@Path(value = "id", encoded = true) int id);

    @Multipart
    @POST("/judge/image")
    Call<Void> postImage(@Part MultipartBody.Part image);

    @GET("map/safe_zone")
    Call<List<SafeZone>> safeZoneList();

    @GET("map/station")
    Call<List<Station>> stationList();

}
