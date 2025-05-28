package com.example.brainquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Kelas;
import com.example.brainquiz.network.ApiService;
import com.example.brainquiz.KelasResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditKelasActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private EditText etNama, etDeskripsi;
    private TextView tvJudul;
    private Button btnSimpanPerubahan;
    private ApiService apiService;
    private String kelasId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kelas); // Buat layout baru untuk EditKelasActivity

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
        kelasId = getIntent().getStringExtra("kelasId");
        String nama = getIntent().getStringExtra("kelasNama");
        String deskripsi = getIntent().getStringExtra("kelasDeskripsi");

        // Validasi data dari Intent
        if (kelasId == null || kelasId.isEmpty()) {
            Toast.makeText(this, "ID Kelas tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set judul dan isi EditText dengan data awal
        tvJudul.setText("Edit Kelas: " + (nama != null ? nama : "Tidak ada nama"));
        etNama.setText(nama != null ? nama : "");
        etDeskripsi.setText(deskripsi != null ? deskripsi : "");

        // Aksi tombol simpan
        btnSimpanPerubahan.setOnClickListener(v -> simpanPerubahan());
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        String token = sp.getString("token", "");
        Log.d("EditKelasActivity", "Token: " + token);
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

        // Buat objek Kelas dengan data baru
        Kelas kelas = new Kelas();
        int id;
        try {
            id = Integer.parseInt(kelasId); // Konversi kelasId ke integer
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID Kelas tidak valid: " + kelasId, Toast.LENGTH_SHORT).show();
            return;
        }
        kelas.setId(id);
        kelas.setNama(namaBaru);
        kelas.setDescription(deskripsiBaru);

        // Log request body
        Log.d("EditKelasActivity", "Mengirim request - ID: " + kelasId + ", Nama: " + namaBaru + ", Deskripsi: " + deskripsiBaru);
        Log.d("EditKelasActivity", "Request Body: " + new Gson().toJson(kelas));

        // Kirim permintaan ke server untuk update kelas
        apiService.updateKelas("Bearer " + token, id, kelas).enqueue(new Callback<KelasResponse>() {
            @Override
            public void onResponse(Call<KelasResponse> call, Response<KelasResponse> response) {
                Log.d("EditKelasActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("EditKelasActivity", "Response Body: " + new Gson().toJson(response.body()));
                    String message = response.body().getMessage() != null ? response.body().getMessage() : "Perubahan disimpan";
                    Toast.makeText(EditKelasActivity.this, message, Toast.LENGTH_SHORT).show();

                    // Kembalikan data yang diedit ke KelasActivity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("kelasId", kelasId);
                    resultIntent.putExtra("namaBaru", namaBaru);
                    resultIntent.putExtra("deskripsiBaru", deskripsiBaru);
                    setResult(RESULT_OK, resultIntent);

                    Log.d("EditKelasActivity", "Mengirim data kembali - ID: " + kelasId + ", Nama: " + namaBaru + ", Deskripsi: " + deskripsiBaru);
                    finish(); // Kembali ke KelasActivity
                } else {
                    Log.e("EditKelasActivity", "Gagal menyimpan - Error Code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("EditKelasActivity", "Error Body: " + errorBody);
                        } catch (Exception e) {
                            Log.e("EditKelasActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(EditKelasActivity.this, "Gagal menyimpan perubahan: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KelasResponse> call, Throwable t) {
                Log.e("EditKelasActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(EditKelasActivity.this, "Error jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}