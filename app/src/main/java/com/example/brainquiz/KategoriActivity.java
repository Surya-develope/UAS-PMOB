package com.example.brainquiz;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
                    if(kategoriList.isEmpty()) {
                        Toast.makeText(KategoriActivity.this, "Tidak ada kategori", Toast.LENGTH_SHORT).show();
                    }
                    tampilkanKategori(kategoriList);
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
        gridKategori.setColumnCount(2);

        final float density = getResources().getDisplayMetrics().density;

        for(Kategori kategori : listKategori) {
            // 1. Container Card
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER);

            // 2. Layout Parameters (Match XML)
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(
                    GridLayout.UNDEFINED,
                    GridLayout.FILL,
                    1f
            );
            params.setMargins(
                    (int)(16 * density),
                    (int)(16 * density),
                    (int)(16 * density),
                    (int)(16 * density)
            );
            card.setLayoutParams(params);

            // 3. Styling
            card.setPadding(
                    (int)(16 * density),
                    (int)(16 * density),
                    (int)(16 * density),
                    (int)(16 * density)
            );
            card.setBackgroundResource(R.drawable.bg_tingkatan_card);

            // 4. ImageView
            ImageView icon = new ImageView(this);
            icon.setLayoutParams(new LinearLayout.LayoutParams(
                    (int)(48 * density),
                    (int)(48 * density)
            ));
            icon.setImageResource(R.drawable.ic_book);
            icon.setColorFilter(Color.WHITE);
            card.addView(icon);

            // 5. TextView
            TextView tvNama = new TextView(this);
            tvNama.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            tvNama.setText(kategori.getNama());
            tvNama.setTextColor(Color.WHITE);
            tvNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvNama.setPadding(0, (int)(8 * density), 0, 0);
            card.addView(tvNama);

            // 6. Add to Grid
            gridKategori.addView(card);
        }
    }
}