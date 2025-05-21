package com.example.brainquiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.network.ApiService;
import com.example.brainquiz.filter.Kategori;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KategoriActivity extends AppCompatActivity {

    private GridLayout gridKategori;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori);

        // Hide action bar
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Initialize views
        gridKategori = findViewById(R.id.gridKategori);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://brainquiz0.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Fetch categories
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

        apiService.getKategori("Bearer " + token).enqueue(new Callback<KategoriResponse>() {
            @Override
            public void onResponse(Call<KategoriResponse> call, Response<KategoriResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Kategori> kategoriList = response.body().getData();
                    Toast.makeText(KategoriActivity.this, "Dapat " + kategoriList.size() + " kategori", Toast.LENGTH_SHORT).show();
                    tampilkanKategori(kategoriList);
                } else {
                    Log.e("KategoriActivity", "Error " + response.code());
                    Toast.makeText(KategoriActivity.this, "Gagal mengambil data kategori", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KategoriResponse> call, Throwable t) {
                Log.e("KategoriActivity", "onFailure: " + t.getMessage());
                Toast.makeText(KategoriActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tampilkanKategori(List<Kategori> listKategori) {
        gridKategori.removeAllViews();

        for (Kategori kategori : listKategori) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER);
            int paddingPx = (int) (16 * getResources().getDisplayMetrics().density);
            card.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            card.setBackgroundResource(R.drawable.bg_tingkatan_card); // Ensure this drawable exists

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            int marginPx = (int) (8 * getResources().getDisplayMetrics().density);
            params.setMargins(marginPx, marginPx, marginPx, marginPx);
            card.setLayoutParams(params);

            // Icon
            ImageView icon = new ImageView(this);
            int sizePx = (int) (48 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(sizePx, sizePx);
            icon.setLayoutParams(iconParams);
            icon.setImageResource(R.drawable.ic_book); // Ensure this drawable exists
            icon.setColorFilter(getResources().getColor(android.R.color.white));
            card.addView(icon);

            // Category Name
            TextView nama = new TextView(this);
            LinearLayout.LayoutParams namaParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            namaParams.topMargin = (int) (8 * getResources().getDisplayMetrics().density);
            nama.setLayoutParams(namaParams);
            nama.setText(kategori.getNama());
            nama.setTextColor(getResources().getColor(android.R.color.white));
            nama.setTextSize(14);
            card.addView(nama);

            // Add click listener for the card
            card.setOnClickListener(v -> Toast.makeText(this, kategori.getNama() + " diklik", Toast.LENGTH_SHORT).show());

            gridKategori.addView(card);
        }
    }
}