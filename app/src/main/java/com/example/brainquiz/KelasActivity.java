package com.example.brainquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Kelas;
import com.example.brainquiz.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KelasActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private Button btnTambahKelas;
    private EditText searchBar;
    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelas);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        gridLayout = findViewById(R.id.gridLayout);
        btnTambahKelas = findViewById(R.id.btnTambahKelas);
        searchBar = findViewById(R.id.searchBar);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Set click listener for "Tambah Kelas" button
        btnTambahKelas.setOnClickListener(v -> {
            Intent intent = new Intent(KelasActivity.this, TambahKelasActivity.class);
            startActivity(intent);
        });

        // Fetch initial data
        fetchKelas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from TambahKelasActivity
        fetchKelas();
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private void fetchKelas() {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("KelasActivity", "Token: " + token);
        apiService.getKelas("Bearer " + token).enqueue(new Callback<KelasResponse>() {
            @Override
            public void onResponse(Call<KelasResponse> call, Response<KelasResponse> response) {
                Log.d("KelasActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<Kelas> data = response.body().getData();
                    Toast.makeText(KelasActivity.this, "Dapat " + data.size() + " kelas", Toast.LENGTH_SHORT).show();
                    tampilkanKategori(data);
                } else {
                    Log.e("KelasActivity", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("KelasActivity", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("KelasActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(KelasActivity.this, "Gagal mengambil data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KelasResponse> call, Throwable t) {
                Log.e("KelasActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(KelasActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tampilkanKategori(List<Kelas> listKelas) {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(2);

        final float density = getResources().getDisplayMetrics().density;

        for (Kelas kelas : listKelas) {
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
            icon.setImageResource(R.drawable.ic_kelas);
            icon.setColorFilter(Color.WHITE);
            card.addView(icon);

            // TextView
            TextView tvNama = new TextView(this);
            tvNama.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            tvNama.setText(kelas.getNama());
            tvNama.setTextColor(Color.WHITE);
            tvNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvNama.setTypeface(null, Typeface.BOLD);
            tvNama.setPadding(0, (int) (8 * density), 0, 0);
            card.addView(tvNama);

            // Click listener for card
            card.setOnClickListener(v -> Toast.makeText(this, kelas.getNama() + " diklik", Toast.LENGTH_SHORT).show());

            // Add to Grid
            gridLayout.addView(card);
        }
    }
}