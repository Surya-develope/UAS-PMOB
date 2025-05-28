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

import com.example.brainquiz.filter.Pendidikan;
import com.example.brainquiz.network.ApiService;
import com.example.brainquiz.PendidikanResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditPendidikanActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private EditText etNama, etDeskripsi;
    private TextView tvJudul;
    private Button btnSimpanPerubahan;
    private ApiService apiService;
    private String pendidikanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kelas); // Gunakan layout yang sama dengan kelas

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
        pendidikanId = getIntent().getStringExtra("pendidikanId");
        String nama = getIntent().getStringExtra("pendidikanNama");
        String deskripsi = getIntent().getStringExtra("pendidikanDeskripsi");

        // Validasi data dari Intent
        if (pendidikanId == null || pendidikanId.isEmpty()) {
            Toast.makeText(this, "ID Pendidikan tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set judul dan isi EditText dengan data awal
        tvJudul.setText("Edit Pendidikan: " + (nama != null ? nama : "Tidak ada nama"));
        etNama.setText(nama != null ? nama : "");
        etDeskripsi.setText(deskripsi != null ? deskripsi : "");

        // Aksi tombol simpan
        btnSimpanPerubahan.setOnClickListener(v -> simpanPerubahan());
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        String token = sp.getString("token", "");
        Log.d("EditPendidikanActivity", "Token: " + token);
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

        Pendidikan pendidikan = new Pendidikan();
        int id;
        try {
            id = Integer.parseInt(pendidikanId);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID Pendidikan tidak valid: " + pendidikanId, Toast.LENGTH_SHORT).show();
            return;
        }
        pendidikan.setId(id);
        pendidikan.setNama(namaBaru);
        pendidikan.setDescription(deskripsiBaru);

        Log.d("EditPendidikanActivity", "Mengirim request - ID: " + pendidikanId + ", Nama: " + namaBaru + ", Deskripsi: " + deskripsiBaru);
        Log.d("EditPendidikanActivity", "Request Body: " + new Gson().toJson(pendidikan));

        apiService.updatePendidikan("Bearer " + token, id, pendidikan).enqueue(new Callback<PendidikanResponse>() {
            @Override
            public void onResponse(Call<PendidikanResponse> call, Response<PendidikanResponse> response) {
                Log.d("EditPendidikanActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("EditPendidikanActivity", "Response Body: " + new Gson().toJson(response.body()));
                    String message = response.body().getMessage() != null ? response.body().getMessage() : "Perubahan disimpan";
                    Toast.makeText(EditPendidikanActivity.this, message, Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("pendidikanId", pendidikanId);
                    resultIntent.putExtra("namaBaru", namaBaru);
                    resultIntent.putExtra("deskripsiBaru", deskripsiBaru);
                    setResult(RESULT_OK, resultIntent);

                    Log.d("EditPendidikanActivity", "Mengirim data kembali - ID: " + pendidikanId + ", Nama: " + namaBaru + ", Deskripsi: " + deskripsiBaru);
                    finish();
                } else {
                    Log.e("EditPendidikanActivity", "Gagal menyimpan - Error Code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("EditPendidikanActivity", "Error Body: " + errorBody);
                        } catch (Exception e) {
                            Log.e("EditPendidikanActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(EditPendidikanActivity.this, "Gagal menyimpan perubahan: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PendidikanResponse> call, Throwable t) {
                Log.e("EditPendidikanActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(EditPendidikanActivity.this, "Error jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}