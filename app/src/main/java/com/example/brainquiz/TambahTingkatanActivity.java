package com.example.brainquiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.brainquiz.filter.Tingkatan;
import com.example.brainquiz.network.ApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TambahTingkatanActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private ApiService apiService;
    private EditText etNama, etDeskripsi;
    private AppCompatButton btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_tingkatan);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        etNama = findViewById(R.id.etNama);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        btnSimpan = findViewById(R.id.btnSimpan);

        // Initialize Retrofit with custom Gson
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation() // Ensure fields without @Expose are excluded
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)) // Use the custom Gson
                .build();
        apiService = retrofit.create(ApiService.class);

        // Set click listener for "Simpan" button
        btnSimpan.setOnClickListener(v -> saveTingkatan());
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private void saveTingkatan() {
        String nama = etNama.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();

        Log.d("TambahTingkatanActivity", "Input Values: nama=" + nama + ", deskripsi=" + deskripsi);

        if (nama.isEmpty() || deskripsi.isEmpty()) {
            Toast.makeText(this, "Nama dan deskripsi harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Tingkatan tingkatan = new Tingkatan();
        tingkatan.setNama(nama);
        tingkatan.setDescription(deskripsi);

        // Log the JSON being sent
        Gson gson = new GsonBuilder().create(); // Use a new Gson instance for logging to see raw output
        String jsonBody = gson.toJson(tingkatan);
        Log.e("TambahTingkatanActivity", "Request Body: " + jsonBody);
        Log.d("TambahTingkatanActivity", "Token: " + token);

        // Make API call
        apiService.addTingkatan("Bearer " + token, tingkatan).enqueue(new Callback<TingkatanResponse>() {
            @Override
            public void onResponse(Call<TingkatanResponse> call, Response<TingkatanResponse> response) {
                Log.d("TambahTingkatanActivity", "Response Code: " + response.code());
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("TambahTingkatanActivity", "Response Body: " + new Gson().toJson(response.body()));
                        String message = response.body().getMessage() != null ? response.body().getMessage() : "Tingkatan berhasil ditambahkan";
                        Toast.makeText(TambahTingkatanActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.e("TambahTingkatanActivity", "Response body is null");
                        Toast.makeText(TambahTingkatanActivity.this, "Gagal menambahkan tingkatan: Respon kosong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TambahTingkatanActivity", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("TambahTingkatanActivity", "Error Body: " + errorBody);
                            Toast.makeText(TambahTingkatanActivity.this, "Gagal menambahkan tingkatan: " + errorBody, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("TambahTingkatanActivity", "Error reading error body: " + e.getMessage());
                            Toast.makeText(TambahTingkatanActivity.this, "Gagal menambahkan tingkatan: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TambahTingkatanActivity.this, "Gagal menambahkan tingkatan: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TingkatanResponse> call, Throwable t) {
                Log.e("TambahTingkatanActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(TambahTingkatanActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}