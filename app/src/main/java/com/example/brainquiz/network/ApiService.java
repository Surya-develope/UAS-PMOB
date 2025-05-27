package com.example.brainquiz.network;

import com.example.brainquiz.LoginRequest;
import com.example.brainquiz.TingkatanResponse;
import com.example.brainquiz.filter.Kategori;
import com.example.brainquiz.filter.Kelas;
import com.example.brainquiz.filter.Pendidikan;
import com.example.brainquiz.filter.Tingkatan;
import com.example.brainquiz.models.User;
import com.example.brainquiz.KategoriResponse;
import com.example.brainquiz.KelasResponse;
import com.example.brainquiz.PendidikanResponse;
import com.example.brainquiz.KuisResponse;

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
    Call<ResponseBody> login(@Body LoginRequest loginRequest);

    @GET("user/get-user")
    Call<User> getUser(@Header("Authorization") String token);

    @GET("tingkatan/get-tingkatan")
    Call<TingkatanResponse> getTingkatan(@Header("Authorization") String token);

    @POST("tingkatan/add-tingkatan")
    Call<TingkatanResponse> addTingkatan(@Header("Authorization") String token, @Body Tingkatan tingkatan);

    @GET("pendidikan/get-pendidikan")
    Call<PendidikanResponse> getPendidikan(@Header("Authorization") String token);

    @POST("pendidikan/add-pendidikan")
    Call<PendidikanResponse> addPendidikan(@Header("Authorization") String token, @Body Pendidikan pendidikan);

    @GET("kategori/get-kategori")
    Call<KategoriResponse> getKategori(@Header("Authorization") String token);

    @POST("kategori/add-kategori")
    Call<KategoriResponse> addKategori(@Header("Authorization") String token, @Body Kategori kategori);

    @GET("kelas/get-kelas")
    Call<KelasResponse> getKelas(@Header("Authorization") String token);

    @POST("kelas/add-kelas")
    Call<KelasResponse> addKelas(@Header("Authorization") String token, @Body Kelas kelas);

    @GET("kuis/get-kuis")
    Call<KuisResponse> getKuis(@Header("Authorization") String token);
}