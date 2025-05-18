package com.example.brainquiz.network;

import com.example.brainquiz.models.LoginResponse;
import com.example.brainquiz.models.User;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("user/register")
    Call<User> register(@Body User user);

    @POST("/user/login")
    Call<ResponseBody> login(String email, String password);
    // Get user info with the Authorization header
    @GET("user/get-user")
    Call<User> getUser(@Header("Authorization") String token);

}
