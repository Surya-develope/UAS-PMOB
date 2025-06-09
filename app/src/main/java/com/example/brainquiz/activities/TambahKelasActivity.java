package com.example.brainquiz.activities;
import com.example.brainquiz.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.brainquiz.filter.Kelas;
import com.example.brainquiz.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.brainquiz.models.KelasResponse;

public class TambahKelasActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private ApiService apiService;
    private EditText etNama, etDeskripsi;
    private AppCompatButton btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kelas);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        etNama = findViewById(R.id.etNama);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        btnSimpan = findViewById(R.id.btnSimpan);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Set click listener for "Simpan" button
        btnSimpan.setOnClickListener(v -> saveKelas());
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private void saveKelas() {
        String nama = etNama.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();

        // Validate inputs
        if (nama.isEmpty() || deskripsi.isEmpty()) {
            Toast.makeText(this, "Nama dan deskripsi harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Kelas object
        Kelas kelas = new Kelas();
        kelas.setNama(nama);
        kelas.setDescription(deskripsi);

        // Make API call
        apiService.addKelas("Bearer " + token, kelas).enqueue(new Callback<KelasResponse>() {
            @Override
            public void onResponse(Call<KelasResponse> call, Response<KelasResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(TambahKelasActivity.this, "Kelas berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish(); // Return to KelasActivity
                } else {
                    Log.e("TambahKelasActivity", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("TambahKelasActivity", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("TambahKelasActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(TambahKelasActivity.this, "Gagal menambahkan kelas: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KelasResponse> call, Throwable t) {
                Log.e("TambahKelasActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(TambahKelasActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


