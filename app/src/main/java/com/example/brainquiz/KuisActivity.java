package com.example.brainquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.brainquiz.filter.Kuis;
import com.example.brainquiz.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KuisActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private Button btnTambahKuis;
    private EditText searchBar;
    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private List<Kuis> kuisList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuis);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        gridLayout = findViewById(R.id.gridQuiz);
        btnTambahKuis = findViewById(R.id.btnTambahKuis);
        searchBar = findViewById(R.id.searchBar); // Pastikan ada di layout

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Initialize kuis list
        kuisList = new ArrayList<>();

        // Set click listener for "Tambah Kuis" button
        btnTambahKuis.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(KuisActivity.this, TambahKuisActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("KuisActivity", "Error starting TambahKuisActivity: " + e.getMessage());
                Toast.makeText(this, "Gagal membuka Tambah Kuis", Toast.LENGTH_SHORT).show();
            }
        });

        // Set search bar text change listener
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterKuis(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Fetch initial data
        fetchKuis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from TambahKuisActivity
        fetchKuis();
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private void fetchKuis() {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("KuisActivity", "Token: " + token);
        apiService.getKuis("Bearer " + token).enqueue(new Callback<KuisResponse>() {
            @Override
            public void onResponse(Call<KuisResponse> call, Response<KuisResponse> response) {
                Log.d("KuisActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    kuisList = response.body().getData();
                    if (kuisList == null) {
                        kuisList = new ArrayList<>();
                        Toast.makeText(KuisActivity.this, "Tidak ada data kuis", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(KuisActivity.this, "Dapat " + kuisList.size() + " kuis", Toast.LENGTH_SHORT).show();
                    }
                    displayKuis(kuisList);
                } else {
                    Log.e("KuisActivity", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("KuisActivity", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("KuisActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(KuisActivity.this, "Gagal mengambil data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KuisResponse> call, Throwable t) {
                Log.e("KuisActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(KuisActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayKuis(List<Kuis> kuis) {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(2);

        final float density = getResources().getDisplayMetrics().density;

        for (Kuis kuisItem : kuis) {
            if (kuisItem == null) continue;

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
                    (int) (8 * density),
                    (int) (8 * density),
                    (int) (8 * density),
                    (int) (8 * density)
            );
            card.setLayoutParams(params);

            // Styling
            card.setPadding(
                    (int) (16 * density),
                    (int) (16 * density),
                    (int) (16 * density),
                    (int) (16 * density)
            );
            try {
                card.setBackgroundResource(R.drawable.bg_card);
            } catch (Exception e) {
                Log.e("KuisActivity", "Error setting bg_card: " + e.getMessage());
                card.setBackgroundColor(Color.GRAY);
            }
            card.setElevation(4 * density);

            // ImageView
            ImageView icon = new ImageView(this);
            icon.setLayoutParams(new LinearLayout.LayoutParams(
                    (int) (48 * density),
                    (int) (48 * density)
            ));
            try {
                icon.setImageResource(R.drawable.question);
            } catch (Exception e) {
                Log.e("KuisActivity", "Error setting question drawable: " + e.getMessage());
            }
            icon.setColorFilter(Color.WHITE);
            card.addView(icon);

            // TextView
            TextView tvTitle = new TextView(this);
            tvTitle.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            String title = kuisItem.getTitle();
            tvTitle.setText(title != null ? title : "Kuis Tanpa Judul");
            tvTitle.setTextColor(Color.WHITE);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvTitle.setTypeface(null, Typeface.BOLD);
            tvTitle.setPadding(0, (int) (8 * density), 0, 0);
            card.addView(tvTitle);

            // Click listener for card (Sementara hanya Toast)
            card.setOnClickListener(v -> {
                try {
                    String clickedTitle = kuisItem.getTitle() != null ? kuisItem.getTitle() : "Kuis Tanpa Judul";
                    Toast.makeText(KuisActivity.this, clickedTitle + " diklik", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("KuisActivity", "Error showing toast: " + e.getMessage(), e);
                    Toast.makeText(KuisActivity.this, "Gagal menampilkan kuis", Toast.LENGTH_SHORT).show();
                }
            });

            // Add to Grid
            gridLayout.addView(card);
        }
    }

    private void filterKuis(String query) {
        List<Kuis> filteredList = new ArrayList<>();
        for (Kuis kuis : kuisList) {
            if (kuis == null || kuis.getTitle() == null) continue;
            if (kuis.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(kuis);
            }
        }
        displayKuis(filteredList);
    }
}