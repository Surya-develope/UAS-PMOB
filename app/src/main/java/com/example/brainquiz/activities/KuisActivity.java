package com.example.brainquiz.activities;
import com.example.brainquiz.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
import com.example.brainquiz.helpers.CardDisplayHelper;
import com.example.brainquiz.models.KuisResponse;

public class KuisActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private Button btnTambahKuis;
    private EditText searchBar;
    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private static final int REQUEST_CODE_EDIT = 100;
    private List<Kuis> kuisList;
    private CardDisplayHelper cardHelper;

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

        // Initialize card helper
        cardHelper = new CardDisplayHelper(this);

        // Set click listener for "Tambah Kuis" button
        btnTambahKuis.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(KuisActivity.this, com.example.brainquiz.activities.TambahKuisActivity.class);
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
        cardHelper.setupGrid(gridLayout);

        for (Kuis kuisItem : kuis) {
            if (kuisItem == null) continue;

            // Buat card menggunakan CardDisplayHelper yang konsisten
            LinearLayout card = cardHelper.createCard();
            LinearLayout contentLayout = cardHelper.createContentLayout(R.drawable.question, kuisItem.getTitle());

            // Tambahkan menu icon untuk admin
            ImageView menuIcon = cardHelper.createMenuIcon(kuisItem, kuisItem.getTitle(), new CardDisplayHelper.CardActionListener() {
                @Override
                public void onEditClick(Object item) {
                    // Tidak digunakan untuk quiz, langsung ke menu
                }

                @Override
                public void onDeleteClick(Object item) {
                    // Tidak digunakan untuk quiz, langsung ke menu
                }

                @Override
                public void onDeleteSuccess() {
                    // Tidak digunakan untuk quiz
                }
            });

            // Override menu click untuk menampilkan menu kuis
            menuIcon.setOnClickListener(v -> showKuisMenu(kuisItem));

            card.addView(contentLayout);
            card.addView(menuIcon);

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

    private void showKuisMenu(Kuis kuis) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_kuis_menu);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Edit Kuis
        LinearLayout menuEdit = dialog.findViewById(R.id.menu_edit_kuis);
        if (menuEdit != null) {
            menuEdit.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.example.brainquiz.activities.EditKuisActivity.class);
                intent.putExtra("kuis_id", kuis.getId());
                intent.putExtra("kuis_title", kuis.getTitle());
                intent.putExtra("kuis_description", kuis.getDescription());
                intent.putExtra("kategori_id", kuis.getKategoriId());
                intent.putExtra("tingkatan_id", kuis.getTingkatanId());
                intent.putExtra("kelas_id", kuis.getKelasId());
                intent.putExtra("pendidikan_id", kuis.getPendidikanId());
                startActivityForResult(intent, REQUEST_CODE_EDIT);
                dialog.dismiss();
            });
        }

        // Kelola Soal
        LinearLayout menuKelolaSoal = dialog.findViewById(R.id.menu_kelola_soal);
        if (menuKelolaSoal != null) {
            menuKelolaSoal.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.example.brainquiz.activities.KelolaSoalActivity.class);
                intent.putExtra("kuis_id", kuis.getId());
                intent.putExtra("kuis_title", kuis.getTitle());
                startActivity(intent);
                dialog.dismiss();
            });
        }

        // Delete Kuis
        LinearLayout menuDelete = dialog.findViewById(R.id.menu_delete_kuis);
        if (menuDelete != null) {
            menuDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Apakah Anda yakin ingin menghapus kuis '" + kuis.getTitle() + "'?\n\nSemua soal dalam kuis ini juga akan terhapus.")
                        .setPositiveButton("Ya", (dialogConfirm, which) -> deleteKuis(kuis.getId()))
                        .setNegativeButton("Tidak", null)
                        .show();
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void deleteKuis(int kuisId) {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.deleteKuis("Bearer " + token, kuisId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(KuisActivity.this, "Kuis berhasil dihapus", Toast.LENGTH_SHORT).show();
                    fetchKuis(); // Refresh list
                } else {
                    Toast.makeText(KuisActivity.this, "Gagal menghapus kuis: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("KuisActivity", "Delete failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(KuisActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("KuisActivity", "Delete error: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            fetchKuis(); // Refresh list when returning from edit
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


