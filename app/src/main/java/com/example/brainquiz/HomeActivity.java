package com.example.brainquiz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.network.ApiService;
import com.example.brainquiz.filter.Tingkatan;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    LinearLayout menuTingkatan, menuKategori, menuKelas, menuPendidikan;
    LinearLayout menuKuis, menuSoal, menuJawabSoal, menuHasilKuis;
    Button btnLogout;

    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_home);

        // Inisialisasi semua menu
        menuTingkatan = findViewById(R.id.menu_tingkatan);
        menuKategori = findViewById(R.id.menu_kategori);
        menuKelas = findViewById(R.id.menu_kelas);
        menuPendidikan = findViewById(R.id.menu_pendidikan);
        menuKuis = findViewById(R.id.menu_kuis);
        menuSoal = findViewById(R.id.menu_soal);
        menuJawabSoal = findViewById(R.id.menu_jawab_soal);
        menuHasilKuis = findViewById(R.id.menu_hasil_kuis);

        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://brainquiz0.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Event Listener
        menuTingkatan.setOnClickListener(v -> startActivity(new Intent(this, TingkatanActivity.class)));
        menuKategori.setOnClickListener(v -> startActivity(new Intent(this, KategoriActivity.class)));
        menuKelas.setOnClickListener(v -> startActivity(new Intent(this, KelasActivity.class)));
        menuPendidikan.setOnClickListener(v -> startActivity(new Intent(this, PendidikanActivity.class)));
        menuKuis.setOnClickListener(v -> startActivity(new Intent(this, KuisActivity.class)));
        menuSoal.setOnClickListener(v -> showToast("Menu Soal diklik"));
        menuJawabSoal.setOnClickListener(v -> showToast("Menu Jawab Soal diklik"));
        menuHasilKuis.setOnClickListener(v -> showToast("Menu Hasil Kuis diklik"));

        // Fetch initial data
        fetchTingkatan();
    }

    // Ambil token dari SharedPreferences
    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        Log.d("SharedPreferences", "Token diambil: " + token);
        return token;
    }

    // Fetch data tingkatan
    private void fetchTingkatan() {
        String token = getToken();
        if (token.isEmpty()) {
            showToast("Token tidak ditemukan");
            return;
        }

        apiService.getTingkatan("Bearer " + token).enqueue(new Callback<TingkatanResponse>() {
            @Override
            public void onResponse(Call<TingkatanResponse> call, Response<TingkatanResponse> response) {
                Log.d("fetchTingkatan", "Response code: " + response.code());

                if (response.code() == 401) {
                    showToast("Session expired, please login again");
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                    return;
                }

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Tingkatan> list = response.body().getData() != null ? response.body().getData() : new ArrayList<>();
                    showToast("Dapat " + list.size() + " tingkatan");
                } else {
                    String errorBody = "Error body not available";
                    if (response.errorBody() != null) {
                        try {
                            errorBody = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e("fetchTingkatan", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Log.e("fetchTingkatan", "Error body: " + errorBody);
                    showToast("Gagal mengambil data tingkatan: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TingkatanResponse> call, Throwable t) {
                showToast("Error: " + t.getMessage());
                Log.e("fetchTingkatan", "onFailure: " + t.getMessage(), t);
            }
        });
    }

    // Menampilkan Toast
    private void showToast(String pesan) {
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
    }
}