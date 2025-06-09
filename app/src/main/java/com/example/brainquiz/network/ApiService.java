package com.example.brainquiz.network;

import com.example.brainquiz.LoginRequest;
import com.example.brainquiz.TingkatanResponse;
import com.example.brainquiz.filter.Kategori;
import com.example.brainquiz.filter.Kelas;
import com.example.brainquiz.filter.Pendidikan;
import com.example.brainquiz.filter.Tingkatan;
import com.example.brainquiz.filter.Kuis;
import com.example.brainquiz.filter.Soal;
import com.example.brainquiz.filter.Jawaban;
import com.example.brainquiz.models.User;
import com.example.brainquiz.KategoriResponse;
import com.example.brainquiz.KelasResponse;
import com.example.brainquiz.PendidikanResponse;
import com.example.brainquiz.KuisResponse;
import com.example.brainquiz.SoalResponse;
import com.example.brainquiz.JawabanResponse;
import com.example.brainquiz.HasilKuisResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

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

    @PATCH("tingkatan/update-tingkatan/{id}")
    Call<TingkatanResponse> updateTingkatan(@Header("Authorization") String token, @Path("id") int id, @Body Tingkatan tingkatan);

    @DELETE("tingkatan/delete-tingkatan/{id}")
    Call<Void> deleteTingkatan(@Header("Authorization") String token, @Path("id") int id);

    @GET("pendidikan/get-pendidikan")
    Call<PendidikanResponse> getPendidikan(@Header("Authorization") String token);

    @POST("pendidikan/add-pendidikan")
    Call<PendidikanResponse> addPendidikan(@Header("Authorization") String token, @Body Pendidikan pendidikan);

    @DELETE("pendidikan/delete-pendidikan/{id}")
    Call<Void> deletePendidikan(@Header("Authorization") String token, @Path("id") int id);

    @PATCH("pendidikan/update-pendidikan/{id}")
    Call<PendidikanResponse> updatePendidikan(@Header("Authorization") String token, @Path("id") int id, @Body Pendidikan pendidikan);

    @GET("kategori/get-kategori")
    Call<KategoriResponse> getKategori(@Header("Authorization") String token);

    @POST("kategori/add-kategori")
    Call<KategoriResponse> addKategori(@Header("Authorization") String token, @Body Kategori kategori);

    @PATCH("kategori/update-kategori/{id}")
    Call<KategoriResponse> updateKategori(@Header("Authorization") String token, @Path("id") int id, @Body Kategori kategori);

    @DELETE("kategori/delete-kategori/{id}")
    Call<Void> deleteKategori(@Header("Authorization") String token, @Path("id") int id);

    @GET("kelas/get-kelas")
    Call<KelasResponse> getKelas(@Header("Authorization") String token);

    @POST("kelas/add-kelas")
    Call<KelasResponse> addKelas(@Header("Authorization") String token, @Body Kelas kelas);

    @DELETE("kelas/delete-kelas/{id}")
    Call<Void> deleteKelas(@Header("Authorization") String token, @Path("id") int id);

    @PATCH("kelas/update-kelas/{id}")
    Call<KelasResponse> updateKelas(@Header("Authorization") String token, @Path("id") int id, @Body Kelas kelas);

    // Kuis endpoints
    @GET("kuis/get-kuis")
    Call<KuisResponse> getKuis(@Header("Authorization") String token);

    @POST("kuis/add-kuis")
    Call<KuisResponse> addKuis(@Header("Authorization") String token, @Body Kuis kuis);

    @PATCH("kuis/update-kuis/{id}")
    Call<KuisResponse> updateKuis(@Header("Authorization") String token, @Path("id") int id, @Body Kuis kuis);

    @DELETE("kuis/delete-kuis/{id}")
    Call<Void> deleteKuis(@Header("Authorization") String token, @Path("id") int id);

    @GET("kuis/filter-kuis")
    Call<KuisResponse> filterKuis(@Header("Authorization") String token,
                                  @Query("kategori_id") Integer kategoriId,
                                  @Query("tingkatan_id") Integer tingkatanId);

    // Soal endpoints
    @GET("soal/get-soal")
    Call<SoalResponse> getAllSoal(@Header("Authorization") String token);

    @GET("soal/get-soal/{kuis_id}")
    Call<SoalResponse> getSoalByKuisId(@Header("Authorization") String token, @Path("kuis_id") int kuisId);

    @POST("soal/add-soal")
    Call<SoalResponse> addSoal(@Header("Authorization") String token, @Body Soal soal);

    @PATCH("soal/update-soal/{id}")
    Call<SoalResponse> updateSoal(@Header("Authorization") String token, @Path("id") int id, @Body Soal soal);

    @DELETE("soal/delete-soal/{id}")
    Call<Void> deleteSoal(@Header("Authorization") String token, @Path("id") int id);

    // Jawaban endpoints
    @POST("hasil-kuis/submit-jawaban")
    Call<JawabanResponse> submitJawaban(@Header("Authorization") String token, @Body List<Jawaban> jawabanList);

    // Hasil Kuis endpoints
    @GET("hasil-kuis/{user_id}/{kuis_id}")
    Call<HasilKuisResponse> getHasilKuis(@Header("Authorization") String token,
                                         @Path("user_id") int userId,
                                         @Path("kuis_id") int kuisId);
}