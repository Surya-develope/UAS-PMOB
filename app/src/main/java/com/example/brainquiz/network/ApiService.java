package com.example.brainquiz.network;

import com.example.brainquiz.models.LoginResponse;
import com.example.brainquiz.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("user/register")
    Call<User> register(@Body User user);

    @POST("user/login")
    Call<LoginResponse> login(@Body User user);

    @GET("user/get-user")
    Call<User> getUser();
}