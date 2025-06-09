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

import com.example.brainquiz.filter.Kategori;
import com.example.brainquiz.network.ApiService;
import com.example.brainquiz.models.KategoriResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditKategoriActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private EditText etNama, etDeskripsi;
    private TextView tvJudul;
    private Button btnSimpanPerubahan;
    private ApiService apiService;
    private String kategoriId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kelas);

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
        kategoriId = getIntent().getStringExtra("kategoriId");
        String nama = getIntent().getStringExtra("kategoriNama");
        String deskripsi = getIntent().getStringExtra("kategoriDeskripsi");

        // Validasi data dari Intent
        if (kategoriId == null || kategoriId.isEmpty()) {
            Toast.makeText(this, "ID Kategori tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set judul dan isi EditText dengan data awal
        tvJudul.setText("Edit Kategori: " + (nama != null ? nama : "Tidak ada nama"));
        etNama.setText(nama != null ? nama : "");
        etDeskripsi.setText(deskripsi != null ? deskripsi : "");

        // Aksi tombol simpan
        btnSimpanPerubahan.setOnClickListener(v -> simpanPerubahan());
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        String token = sp.getString("token", "");
        Log.d("EditKategoriActivity", "Token: " + token);
        return token;
    }

    private void simpanPerubahan() {
        String namaBaru = etNama.getText().toString().trim();
        String deskripsiBaru = etDeskripsi.getText().toString().trim();

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

        Kategori kategori = new Kategori();
        int id;
        try {
            id = Integer.parseInt(kategoriId);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID Kategori tidak valid: " + kategoriId, Toast.LENGTH_SHORT).show();
            return;
        }
        kategori.setId(id);
        kategori.setNama(namaBaru);
        kategori.setDescription(deskripsiBaru);

        Log.d("EditKategoriActivity", "Mengirim request - ID: " + kategoriId + ", Nama: " + namaBaru + ", Deskripsi: " + deskripsiBaru);
        Log.d("EditKategoriActivity", "Request Body: " + new Gson().toJson(kategori));

        apiService.updateKategori("Bearer " + token, id, kategori).enqueue(new Callback<KategoriResponse>() {
            @Override
            public void onResponse(Call<KategoriResponse> call, Response<KategoriResponse> response) {
                Log.d("EditKategoriActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("EditKategoriActivity", "Response Body: " + new Gson().toJson(response.body()));
                    String message = response.body().getMessage() != null ? response.body().getMessage() : "Perubahan disimpan";
                    Toast.makeText(EditKategoriActivity.this, message, Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("kategoriId", kategoriId);
                    resultIntent.putExtra("namaBaru", namaBaru);
                    resultIntent.putExtra("deskripsiBaru", deskripsiBaru);
                    setResult(RESULT_OK, resultIntent);

                    Log.d("EditKategoriActivity", "Mengirim data kembali - ID: " + kategoriId + ", Nama: " + namaBaru + ", Deskripsi: " + deskripsiBaru);
                    finish();
                } else {
                    Log.e("EditKategoriActivity", "Gagal menyimpan - Error Code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("EditKategoriActivity", "Error Body: " + errorBody);
                        } catch (Exception e) {
                            Log.e("EditKategoriActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(EditKategoriActivity.this, "Gagal menyimpan perubahan: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KategoriResponse> call, Throwable t) {
                Log.e("EditKategoriActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(EditKategoriActivity.this, "Error jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


