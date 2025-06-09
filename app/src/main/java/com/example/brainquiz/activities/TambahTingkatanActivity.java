package com.example.brainquiz.activities;
import com.example.brainquiz.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Tingkatan;
import com.example.brainquiz.network.ApiService;
import com.example.brainquiz.models.TingkatanResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TambahTingkatanActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private EditText etNama, etDeskripsi;
    private Button btnTambah;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_tingkatan);

        // Inisialisasi view
        etNama = findViewById(R.id.etNama);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        btnTambah = findViewById(R.id.btnSimpan);

        // Inisialisasi Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Aksi tombol tambah
        btnTambah.setOnClickListener(v -> tambahTingkatan());
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        String token = sp.getString("token", "");
        Log.d("TambahTingkatanActivity", "Token: " + token);
        return token;
    }

    private void tambahTingkatan() {
        String nama = etNama.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();

        // Validasi input
        if (nama.isEmpty()) {
            etNama.setError("Nama tidak boleh kosong");
            etNama.requestFocus();
            return;
        }

        if (deskripsi.isEmpty()) {
            etDeskripsi.setError("Deskripsi tidak boleh kosong");
            etDeskripsi.requestFocus();
            return;
        }

        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan, silakan login ulang", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat objek Tingkatan
        Tingkatan tingkatan = new Tingkatan();
        tingkatan.setNama(nama);
        tingkatan.setDescription(deskripsi);

        // Log request body
        Log.d("TambahTingkatanActivity", "Request Body: " + new Gson().toJson(tingkatan));

        // Kirim permintaan ke server
        apiService.addTingkatan("Bearer " + token, tingkatan).enqueue(new Callback<TingkatanResponse>() {
            @Override
            public void onResponse(Call<TingkatanResponse> call, Response<TingkatanResponse> response) {
                Log.d("TambahTingkatanActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("TambahTingkatanActivity", "Response Body: " + new Gson().toJson(response.body()));
                    Toast.makeText(TambahTingkatanActivity.this, "Tingkatan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish(); // Kembali ke TingkatanActivity
                } else {
                    Log.e("TambahTingkatanActivity", "Gagal menambahkan - Error Code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("TambahTingkatanActivity", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("TambahTingkatanActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(TambahTingkatanActivity.this, "Gagal menambahkan tingkatan: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TingkatanResponse> call, Throwable t) {
                Log.e("TambahTingkatanActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(TambahTingkatanActivity.this, "Error jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


