package com.example.brainquiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tingkatan);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Initialize GridLayout
        gridTingkatan = findViewById(R.id.grid_tingkatan);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://brainquiz0.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Fetch data
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

        apiService.getTingkatan("Bearer " + token).enqueue(new Callback<TingkatanResponse>() {
            @Override
            public void onResponse(Call<TingkatanResponse> call, Response<TingkatanResponse> res) {
                if (res.isSuccessful() && res.body() != null) {
                    List<Tingkatan> data = res.body().getData();
                    Toast.makeText(TingkatanActivity.this, "Dapat " + data.size() + " tingkatan", Toast.LENGTH_SHORT).show();
                    bindDataToCards(data);
                } else {
                    Log.e("TingkatanActivity", "Error " + res.code());
                    Toast.makeText(TingkatanActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TingkatanResponse> call, Throwable t) {
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
            card.setGravity(android.view.Gravity.CENTER);
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