package com.example.brainquiz;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Pendidikan;
import com.example.brainquiz.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PendidikanActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private ApiService apiService;
    private GridLayout gridPendidikan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendidikan);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        gridPendidikan = findViewById(R.id.grid_pendidikan);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        fetchPendidikan();
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private void fetchPendidikan() {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("PendidikanActivity", "Token: " + token);
        apiService.getPendidikan("Bearer " + token).enqueue(new Callback<PendidikanResponse>() {
            @Override
            public void onResponse(Call<PendidikanResponse> call, Response<PendidikanResponse> response) {
                Log.d("PendidikanActivity", "Response Code: " + response.code());
                Log.d("PendidikanActivity", "Raw Response: " + response.raw().toString());

                if (response.isSuccessful() && response.body() != null) {
                    List<Pendidikan> data = response.body().getData();
                    Log.d("PendidikanActivity", "Data Size: " + data.size());
                    Toast.makeText(PendidikanActivity.this, "Dapat " + data.size() + " pendidikan", Toast.LENGTH_SHORT).show();
                    bindDataToCards(data);
                } else {
                    Log.e("PendidikanActivity", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("PendidikanActivity", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("PendidikanActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(PendidikanActivity.this, "Gagal mengambil data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PendidikanResponse> call, Throwable t) {
                Log.e("PendidikanActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(PendidikanActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindDataToCards(List<Pendidikan> list) {
        gridPendidikan.removeAllViews(); // Clear existing views

        for (Pendidikan pendidikan : list) {
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
            textView.setText(pendidikan.getNama());
            textView.setTextColor(getResources().getColor(android.R.color.white));
            textView.setTextSize(14);

            // Add views to card
            card.addView(imageView);
            card.addView(textView);

            // Set click listener
            card.setOnClickListener(v -> Toast.makeText(this, pendidikan.getNama() + " diklik", Toast.LENGTH_SHORT).show());

            // Add card to GridLayout
            gridPendidikan.addView(card);
        }
    }
}