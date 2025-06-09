package com.example.brainquiz.activities;
import com.example.brainquiz.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class EditActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private EditText etNama, etDeskripsi;
    private TextView tvJudul;
    private Button btnSimpanPerubahan;
    private ApiService apiService;
    private String tingkatanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Inisialisasi view
        tvJudul = findViewById(R.id.tvJudul);
        etNama = findViewById(R.id.etNama);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        btnSimpanPerubahan = findViewById(R.id.btnSimpanPerubahan);

        // Inisialisasi Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Ambil data dari Intent
        tingkatanId = getIntent().getStringExtra("tingkatanId");
        String nama = getIntent().getStringExtra("tingkatanNama");
        String deskripsi = getIntent().getStringExtra("tingkatanDeskripsi");

        // Validasi data dari Intent
        if (tingkatanId == null || tingkatanId.isEmpty()) {
            Toast.makeText(this, "ID Tingkatan tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set judul dan isi EditText dengan data awal
        tvJudul.setText("Edit Tingkatan: " + (nama != null ? nama : "Tidak ada nama"));
        etNama.setText(nama != null ? nama : "");
        etDeskripsi.setText(deskripsi != null ? deskripsi : "");

        // Aksi tombol simpan
        btnSimpanPerubahan.setOnClickListener(v -> simpanPerubahan());
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        String token = sp.getString("token", "");
        Log.d("EditActivity", "Token: " + token);
        return token;
    }

    private void simpanPerubahan() {
        String namaBaru = etNama.getText().toString().trim();
        String deskripsiBaru = etDeskripsi.getText().toString().trim();

        // Validasi input
        if (namaBaru.isEmpty()) {
            etNama.setError("Nama tidak boleh kosong");
            etNama.requestFocus();
            return;
        }

        if (deskripsiBaru.isEmpty()) {
            etDeskripsi.setError("Deskripsi tidak boleh kosong");
            etDeskripsi.requestFocus();
            return;
        }

        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan, silakan login ulang", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat objek Tingkatan dengan data baru
        Tingkatan tingkatan = new Tingkatan();
        int id;
        try {
            id = Integer.parseInt(tingkatanId); // Konversi tingkatanId ke integer untuk parameter API
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID Tingkatan tidak valid: " + tingkatanId, Toast.LENGTH_SHORT).show();
            return;
        }
        tingkatan.setId(id); // Set ID sebagai integer
        tingkatan.setNama(namaBaru);
        tingkatan.setDescription(deskripsiBaru);

        // Log request body
        Log.d("EditActivity", "Mengirim request - ID: " + tingkatanId + ", Nama: " + namaBaru + ", Deskripsi: " + deskripsiBaru);
        Log.d("EditActivity", "Request Body: " + new Gson().toJson(tingkatan));

        // Kirim permintaan ke server
        apiService.updateTingkatan("Bearer " + token, id, tingkatan).enqueue(new Callback<TingkatanResponse>() {
            @Override
            public void onResponse(Call<TingkatanResponse> call, Response<TingkatanResponse> response) {
                Log.d("EditActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("EditActivity", "Response Body: " + new Gson().toJson(response.body()));
                    String message = response.body().getMessage() != null ? response.body().getMessage() : "Perubahan disimpan";
                    Toast.makeText(EditActivity.this, message, Toast.LENGTH_SHORT).show();

                    // Kembalikan data yang diedit ke TingkatanActivity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("tingkatanId", tingkatanId);
                    resultIntent.putExtra("namaBaru", namaBaru);
                    resultIntent.putExtra("deskripsiBaru", deskripsiBaru);
                    setResult(RESULT_OK, resultIntent);

                    Log.d("EditActivity", "Mengirim data kembali - ID: " + tingkatanId + ", Nama: " + namaBaru + ", Deskripsi: " + deskripsiBaru);
                    finish(); // Kembali ke TingkatanActivity
                } else {
                    Log.e("EditActivity", "Gagal menyimpan - Error Code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("EditActivity", "Error Body: " + errorBody);
                        } catch (Exception e) {
                            Log.e("EditActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(EditActivity.this, "Gagal menyimpan perubahan: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TingkatanResponse> call, Throwable t) {
                Log.e("EditActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(EditActivity.this, "Error jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


