package com.example.brainquiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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

    private GridLayout gridKelas;
    private EditText searchBar;
    private Button btnTambahTingkatan;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelas);

        // Hide action bar
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Initialize views
        gridKelas = findViewById(R.id.gridLayout);
        searchBar = findViewById(R.id.searchBar);
        btnTambahTingkatan = findViewById(R.id.btnTambahTingkatan);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://brainquiz0.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Set up button click listener
        btnTambahTingkatan.setOnClickListener(v -> Toast.makeText(this, "Tambah Kelas diklik", Toast.LENGTH_SHORT).show());

        // Set up search bar listener (placeholder)
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            Toast.makeText(this, "Pencarian: " + searchBar.getText().toString(), Toast.LENGTH_SHORT).show();
            return true;
        });

        // Fetch classes
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

        apiService.getKelas("Bearer " + token).enqueue(new Callback<List<Kelas>>() {
            @Override
            public void onResponse(Call<List<Kelas>> call, Response<List<Kelas>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Kelas> kelasList = response.body();
                    Toast.makeText(KelasActivity.this, "Dapat " + kelasList.size() + " kelas", Toast.LENGTH_SHORT).show();
                    tampilkanKelas(kelasList);
                } else {
                    Log.e("KelasActivity", "Error " + response.code());
                    Toast.makeText(KelasActivity.this, "Gagal mengambil data kelas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Kelas>> call, Throwable t) {
                Log.e("KelasActivity", "onFailure: " + t.getMessage());
                Toast.makeText(KelasActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tampilkanKelas(List<Kelas> kelasList) {
        gridKelas.removeAllViews();

        for (Kelas kelas : kelasList) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER);
            int paddingPx = (int) (16 * getResources().getDisplayMetrics().density);
            card.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            card.setBackgroundResource(R.drawable.bg_tingkatan_card);

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
            icon.setImageResource(R.drawable.ic_kelas); // Ensure this drawable exists
            icon.setColorFilter(getResources().getColor(android.R.color.white));
            card.addView(icon);

            // Class Name
            TextView nama = new TextView(this);
            LinearLayout.LayoutParams namaParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            namaParams.topMargin = (int) (8 * getResources().getDisplayMetrics().density);
            nama.setLayoutParams(namaParams);
            nama.setText(kelas.getNama());
            nama.setTextColor(getResources().getColor(android.R.color.white));
            nama.setTextSize(14);
            card.addView(nama);

            // Add click listener for the card
            card.setOnClickListener(v -> Toast.makeText(this, kelas.getNama() + " diklik", Toast.LENGTH_SHORT).show());

            gridKelas.addView(card);
        }
    }
}