package com.example.brainquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class HasilKuisActivity extends AppCompatActivity {

    private static final String TAG = "HasilKuisActivity";
    private RecyclerView recyclerViewHasil;
    private TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_hasil_kuis);

        // Initialize views
        recyclerViewHasil = findViewById(R.id.recycler_view_hasil);
        tvNoData = findViewById(R.id.tv_no_data);

        // Set visibility of views
        recyclerViewHasil.setVisibility(View.GONE);
        tvNoData.setVisibility(View.VISIBLE);

        // Setup navigation
        setupNavigation();
    }

    private void setupNavigation() {
        // Initialize bottom navigation
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navKuis = findViewById(R.id.nav_kuis);
        LinearLayout navJawabSoal = findViewById(R.id.nav_jawab_soal);
        LinearLayout navHasil = findViewById(R.id.nav_hasil);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        navKuis.setOnClickListener(v -> {
            startActivity(new Intent(this, KuisActivity.class));
            finish();
        });

        navJawabSoal.setOnClickListener(v -> {
            startActivity(new Intent(this, JawabSoalActivity.class));
            finish();
        });

        navHasil.setOnClickListener(v -> {
            // Already in HasilKuisActivity, do nothing
            showToast("Anda sudah berada di Hasil Kuis");
        });
    }

    private void showToast(String pesan) {
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
    }
}