package com.example.brainquiz.network;

import com.example.brainquiz.LoginRequest;
import com.example.brainquiz.TingkatanResponse;
import com.example.brainquiz.filter.Kategori;
import com.example.brainquiz.filter.Kelas;
import com.example.brainquiz.filter.Pendidikan;
import com.example.brainquiz.filter.Tingkatan;
import com.example.brainquiz.models.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("user/register")
    Call<User> register(@Body User user);

    @POST("user/login")
    Call<ResponseBody> login(@Body LoginRequest loginRequest);  // buat class LoginRequest untuk email+password

    @GET("user/get-user")
    Call<User> getUser(@Header("Authorization") String token);

    @GET("tingkatan/get-tingkatan")
    Call<TingkatanResponse> getTingkatan(@Header("Authorization") String token);

    @GET("pendidikan/get-pendidikan")
    Call<List<Pendidikan>> getPendidikan();

    @GET("kategori/get-kategori")
    Call<List<Kategori>> getKategori();

    @GET("kelas/get-kelas")
    Call<List<Kelas>> getKelas();
}
