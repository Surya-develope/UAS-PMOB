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
import com.example.brainquiz.helpers.CardDisplayHelper;

public class PilihKuisActivity extends AppCompatActivity {

    private EditText etSearch;
    private GridLayout gridKuis;

    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";

    private List<Kuis> kuisList = new ArrayList<>();
    private CardDisplayHelper cardHelper;

    // Tidak lagi menggunakan array warna, akan menggunakan background drawable yang konsisten

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
        cardHelper = new CardDisplayHelper(this);
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
        cardHelper.setupGrid(gridKuis);

        if (kuisListToShow.isEmpty()) {
            TextView emptyText = cardHelper.createNoDataMessage("Tidak ada kuis ditemukan.\nCoba kata kunci lain.");
            gridKuis.addView(emptyText);
            return;
        }

        for (Kuis kuisItem : kuisListToShow) {
            if (kuisItem == null) continue;

            // Buat card menggunakan CardDisplayHelper yang konsisten
            LinearLayout card = cardHelper.createCard();
            LinearLayout contentLayout = cardHelper.createContentLayout(R.drawable.question, kuisItem.getTitle());
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


