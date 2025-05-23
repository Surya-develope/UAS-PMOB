package com.example.brainquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Kategori;
import com.example.brainquiz.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KategoriActivity extends AppCompatActivity {

    private GridLayout gridKategori;
    private Button btnTambahKategori;
    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        gridKategori = findViewById(R.id.gridKategori);
        btnTambahKategori = findViewById(R.id.btnTambahKategori);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Set click listener for "Tambah Kategori" button
        btnTambahKategori.setOnClickListener(v -> {
            Intent intent = new Intent(KategoriActivity.this, TambahKategoriActivity.class);
            startActivity(intent);
        });

        // Fetch initial data
        fetchKategori();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from TambahKategoriActivity
        fetchKategori();
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private void fetchKategori() {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("KategoriActivity", "Token: " + token);
        apiService.getKategori("Bearer " + token).enqueue(new Callback<KategoriResponse>() {
            @Override
            public void onResponse(Call<KategoriResponse> call, Response<KategoriResponse> response) {
                Log.d("KategoriActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Kategori> kategoriList = response.body().getData();
                    if (kategoriList.isEmpty()) {
                        Toast.makeText(KategoriActivity.this, "Tidak ada kategori", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(KategoriActivity.this, "Dapat " + kategoriList.size() + " kategori", Toast.LENGTH_SHORT).show();
                    }
                    tampilkanKategori(kategoriList);
                } else {
                    Log.e("KategoriActivity", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("KategoriActivity", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("KategoriActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(KategoriActivity.this, "Gagal mengambil data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KategoriResponse> call, Throwable t) {
                Log.e("KategoriActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(KategoriActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tampilkanKategori(List<Kategori> listKategori) {
        gridKategori.removeAllViews();
        gridKategori.setColumnCount(2);

        final float density = getResources().getDisplayMetrics().density;

        for (Kategori kategori : listKategori) {
            // Container Card
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER);

            // Layout Parameters
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f);
            params.setMargins(
                    (int) (16 * density),
                    (int) (16 * density),
                    (int) (16 * density),
                    (int) (16 * density)
            );
            card.setLayoutParams(params);

            // Styling
            card.setPadding(
                    (int) (16 * density),
                    (int) (16 * density),
                    (int) (16 * density),
                    (int) (16 * density)
            );
            card.setBackgroundResource(R.drawable.bg_tingkatan_card);

            // ImageView
            ImageView icon = new ImageView(this);
            icon.setLayoutParams(new LinearLayout.LayoutParams(
                    (int) (48 * density),
                    (int) (48 * density)
            ));
            icon.setImageResource(R.drawable.ic_book);
            icon.setColorFilter(Color.WHITE);
            card.addView(icon);

            // TextView
            TextView tvNama = new TextView(this);
            tvNama.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            tvNama.setText(kategori.getNama());
            tvNama.setTextColor(Color.WHITE);
            tvNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvNama.setPadding(0, (int) (8 * density), 0, 0);
            card.addView(tvNama);

            // Add to Grid
            gridKategori.addView(card);
        }
    }
}