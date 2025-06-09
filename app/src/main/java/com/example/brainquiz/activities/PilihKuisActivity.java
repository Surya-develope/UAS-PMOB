package com.example.brainquiz.activities;
import com.example.brainquiz.R;

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
import com.example.brainquiz.models.KuisResponse;

public class PilihKuisActivity extends AppCompatActivity {

    private EditText etSearch;
    private GridLayout gridKuis;
    
    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    
    private List<Kuis> kuisList = new ArrayList<>();

    // Array warna untuk card
    private String[] cardColors = {
        "#2196F3", // Blue
        "#4CAF50", // Green
        "#FF9800", // Orange
        "#9C27B0", // Purple
        "#F44336", // Red
        "#00BCD4", // Cyan
        "#795548", // Brown
        "#607D8B"  // Blue Grey
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_kuis);

        initViews();
        initRetrofit();
        setupSearchListener();
        fetchKuis();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        gridKuis = findViewById(R.id.gridKuis);
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterKuis(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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

        Log.d("PilihKuis", "Fetching kuis list...");
        
        apiService.getKuis("Bearer " + token).enqueue(new Callback<KuisResponse>() {
            @Override
            public void onResponse(Call<KuisResponse> call, Response<KuisResponse> response) {
                Log.d("PilihKuis", "Response Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    KuisResponse kuisResponse = response.body();
                    if (kuisResponse.isSuccess()) {
                        kuisList = kuisResponse.getData();
                        Log.d("PilihKuis", "Loaded " + kuisList.size() + " kuis");
                        displayKuis(kuisList);
                        
                        if (kuisList.isEmpty()) {
                            Toast.makeText(PilihKuisActivity.this, "Belum ada kuis tersedia", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PilihKuisActivity.this, "Gagal memuat kuis: " + kuisResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("PilihKuis", "Error " + response.code());
                    Toast.makeText(PilihKuisActivity.this, "Gagal mengambil data kuis: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KuisResponse> call, Throwable t) {
                Log.e("PilihKuis", "onFailure: " + t.getMessage(), t);
                Toast.makeText(PilihKuisActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayKuis(List<Kuis> kuisListToShow) {
        gridKuis.removeAllViews();
        gridKuis.setColumnCount(2);

        final float density = getResources().getDisplayMetrics().density;

        if (kuisListToShow.isEmpty()) {
            // Show empty state
            TextView emptyText = new TextView(this);
            emptyText.setText("Tidak ada kuis ditemukan.\nCoba kata kunci lain.");
            emptyText.setTextSize(16);
            emptyText.setTextColor(Color.GRAY);
            emptyText.setGravity(Gravity.CENTER);
            emptyText.setPadding(32, 64, 32, 64);
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(0, 2); // Span 2 columns
            params.width = GridLayout.LayoutParams.MATCH_PARENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            emptyText.setLayoutParams(params);
            
            gridKuis.addView(emptyText);
            return;
        }

        int cardIndex = 0;
        for (Kuis kuisItem : kuisListToShow) {
            if (kuisItem == null) continue;

            // Container Card
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER);
            card.setPadding(
                    (int) (16 * density),
                    (int) (16 * density),
                    (int) (16 * density),
                    (int) (16 * density)
            );

            // Set background and styling - use different colors for variety
            String cardColor = cardColors[cardIndex % cardColors.length];
            card.setBackgroundColor(Color.parseColor(cardColor));

            // Add corner radius and elevation
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                card.setElevation(8 * density);
                card.setClipToOutline(true);
            }

            GridLayout.LayoutParams cardParams = new GridLayout.LayoutParams();
            cardParams.width = 0;
            cardParams.height = (int) (180 * density);
            cardParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            cardParams.setMargins(
                    (int) (8 * density),
                    (int) (8 * density),
                    (int) (8 * density),
                    (int) (8 * density)
            );
            card.setLayoutParams(cardParams);

            // Create a layout for the icon and text
            LinearLayout contentLayout = new LinearLayout(this);
            contentLayout.setOrientation(LinearLayout.VERTICAL);
            contentLayout.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            contentLayout.setLayoutParams(contentParams);

            // ImageView
            ImageView icon = new ImageView(this);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                    (int) (64 * density),
                    (int) (64 * density)
            );
            iconParams.gravity = Gravity.CENTER;
            icon.setLayoutParams(iconParams);
            try {
                icon.setImageResource(R.drawable.question);
            } catch (Exception e) {
                Log.e("PilihKuis", "Error setting question drawable: " + e.getMessage());
            }
            icon.setColorFilter(Color.WHITE);
            contentLayout.addView(icon);

            // TextView for title
            TextView tvTitle = new TextView(this);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textParams.gravity = Gravity.CENTER;
            textParams.topMargin = (int) (12 * density);
            tvTitle.setLayoutParams(textParams);

            String title = kuisItem.getTitle();
            tvTitle.setText(title != null ? title : "Kuis Tanpa Judul");
            tvTitle.setTextColor(Color.WHITE);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tvTitle.setTypeface(null, Typeface.BOLD);
            contentLayout.addView(tvTitle);

            // Description
            if (kuisItem.getDescription() != null && !kuisItem.getDescription().isEmpty()) {
                TextView tvDescription = new TextView(this);
                LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                descParams.gravity = Gravity.CENTER;
                descParams.topMargin = (int) (4 * density);
                tvDescription.setLayoutParams(descParams);
                tvDescription.setText(kuisItem.getDescription());
                tvDescription.setTextColor(Color.parseColor("#E0E0E0"));
                tvDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                tvDescription.setMaxLines(2);
                tvDescription.setEllipsize(android.text.TextUtils.TruncateAt.END);
                contentLayout.addView(tvDescription);
            }

            card.addView(contentLayout);

            // Set click listener to start quiz
            card.setOnClickListener(v -> {
                Intent intent = new Intent(PilihKuisActivity.this, com.example.brainquiz.activities.JawabSoalActivity.class);
                intent.putExtra("kuis_id", kuisItem.getId());
                intent.putExtra("kuis_title", kuisItem.getTitle());
                intent.putExtra("kuis_description", kuisItem.getDescription());
                startActivity(intent);
            });

            gridKuis.addView(card);
            cardIndex++; // Increment for next card color
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


