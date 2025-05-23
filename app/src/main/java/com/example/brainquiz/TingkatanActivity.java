package com.example.brainquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Tingkatan;
import com.example.brainquiz.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TingkatanActivity extends AppCompatActivity {

    private GridLayout gridTingkatan;
    private Button btnTambahTingkatan;
    private EditText etCariTingkatan;
    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tingkatan);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        gridTingkatan = findViewById(R.id.grid_tingkatan);
        btnTambahTingkatan = findViewById(R.id.btn_tambah_tingkatan);
        etCariTingkatan = findViewById(R.id.et_cari_tingkatan);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Set click listener for "Tambah Tingkatan" button
        btnTambahTingkatan.setOnClickListener(v -> {
            Intent intent = new Intent(TingkatanActivity.this, TambahTingkatanActivity.class);
            startActivity(intent);
        });

        // Fetch initial data
        fetchTingkatan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from TambahTingkatanActivity
        fetchTingkatan();
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private void fetchTingkatan() {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("TingkatanActivity", "Token: " + token);
        apiService.getTingkatan("Bearer " + token).enqueue(new Callback<TingkatanResponse>() {
            @Override
            public void onResponse(Call<TingkatanResponse> call, Response<TingkatanResponse> response) {
                Log.d("TingkatanActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<Tingkatan> data = response.body().getData();
                    if (data.isEmpty()) {
                        Toast.makeText(TingkatanActivity.this, "Tidak ada tingkatan", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TingkatanActivity.this, "Dapat " + data.size() + " tingkatan", Toast.LENGTH_SHORT).show();
                    }
                    bindDataToCards(data);
                } else {
                    Log.e("TingkatanActivity", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("TingkatanActivity", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("TingkatanActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(TingkatanActivity.this, "Gagal mengambil data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TingkatanResponse> call, Throwable t) {
                Log.e("TingkatanActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(TingkatanActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindDataToCards(List<Tingkatan> list) {
        gridTingkatan.removeAllViews(); // Clear existing views

        for (Tingkatan tingkatan : list) {
            // Create LinearLayout for the card
            LinearLayout card = new LinearLayout(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Equal column weight
            params.setMargins(8, 8, 8, 8); // Match useDefaultMargins
            card.setLayoutParams(params);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER);
            card.setPadding(16, 16, 16, 16);
            card.setBackgroundResource(R.drawable.bg_tingkatan_card);

            // Create ImageView
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
            imageView.setImageResource(R.drawable.ic_chart_bar);
            imageView.setColorFilter(getResources().getColor(android.R.color.white));

            // Create TextView
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textParams.setMargins(0, 8, 0, 0);
            textView.setLayoutParams(textParams);
            textView.setText(tingkatan.getNama());
            textView.setTextColor(getResources().getColor(android.R.color.white));
            textView.setTextSize(14);

            // Add views to card
            card.addView(imageView);
            card.addView(textView);

            // Set click listener
            card.setOnClickListener(v -> Toast.makeText(this, tingkatan.getNama() + " diklik", Toast.LENGTH_SHORT).show());

            // Add card to GridLayout
            gridTingkatan.addView(card);
        }
    }
}