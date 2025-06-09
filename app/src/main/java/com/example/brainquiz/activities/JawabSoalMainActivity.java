package com.example.brainquiz.activities;
import com.example.brainquiz.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class JawabSoalMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jawabsoal);

        setupViews();
        setupNavigation();
    }

    private void setupViews() {
        Button btnPilihKuis = findViewById(R.id.btnPilihKuis);
        btnPilihKuis.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.brainquiz.activities.PilihKuisActivity.class);
            startActivity(intent);
        });
    }

    private void setupNavigation() {
        // Initialize bottom navigation
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navKuis = findViewById(R.id.nav_kuis);
        LinearLayout navJawabSoal = findViewById(R.id.nav_jawab_soal);
        LinearLayout navHasil = findViewById(R.id.nav_hasil);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.brainquiz.activities.HomeActivity.class));
            finish();
        });

        navKuis.setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.brainquiz.activities.KuisActivity.class));
            finish();
        });

        navJawabSoal.setOnClickListener(v -> {
            // Already in JawabSoalMainActivity, do nothing
            showToast("Anda sudah berada di Jawab Soal");
        });

        navHasil.setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.brainquiz.activities.HasilKuisActivity.class));
            finish();
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}


