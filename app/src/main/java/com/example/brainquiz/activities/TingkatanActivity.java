package com.example.brainquiz.activities;
import com.example.brainquiz.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.os.Bundle;
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
import android.app.Dialog;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Tingkatan;
import com.example.brainquiz.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.brainquiz.models.TingkatanResponse;
import com.example.brainquiz.helpers.CardDisplayHelper;

public class TingkatanActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private static final int REQUEST_CODE_EDIT = 100; // Kode untuk startActivityForResult

    private GridLayout gridTingkatan;
    private Button btnTambahTingkatan;
    private EditText etCariTingkatan;
    private ApiService apiService;
    private List<Tingkatan> tingkatanList = new ArrayList<>(); // Simpan daftar tingkatan
    private CardDisplayHelper cardHelper;

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

        // Initialize card helper
        cardHelper = new CardDisplayHelper(this);

        // Set click listener for "Tambah Tingkatan" button
        btnTambahTingkatan.setOnClickListener(v -> {
            Intent intent = new Intent(TingkatanActivity.this, com.example.brainquiz.activities.TambahTingkatanActivity.class);
            startActivity(intent);
        });

        // Setup search listener
        setupSearchListener();

        // Fetch initial data
        fetchTingkatan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from TambahTingkatanActivity
        fetchTingkatan();
    }

    private void setupSearchListener() {
        etCariTingkatan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTingkatan(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private void fetchTingkatan() {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            Log.e("TingkatanActivity", "Token is empty!");
            return;
        }

        Log.d("TingkatanActivity", "Starting fetchTingkatan...");
        Log.d("TingkatanActivity", "Token: " + token);
        Log.d("TingkatanActivity", "Making API call to: tingkatan/get-tingkatan");

        apiService.getTingkatan("Bearer " + token).enqueue(new Callback<TingkatanResponse>() {
            @Override
            public void onResponse(Call<TingkatanResponse> call, Response<TingkatanResponse> response) {
                Log.d("TingkatanActivity", "Response received!");
                Log.d("TingkatanActivity", "Response Code: " + response.code());
                Log.d("TingkatanActivity", "Response successful: " + response.isSuccessful());
                Log.d("TingkatanActivity", "Response body null: " + (response.body() == null));

                if (response.isSuccessful() && response.body() != null) {
                    TingkatanResponse responseBody = response.body();
                    Log.d("TingkatanActivity", "Response success flag: " + responseBody.isSuccess());
                    Log.d("TingkatanActivity", "Response message: " + responseBody.getMessage());

                    List<Tingkatan> data = responseBody.getData();
                    Log.d("TingkatanActivity", "Data list size: " + (data != null ? data.size() : "null"));

                    tingkatanList.clear();
                    if (data != null) {
                        tingkatanList.addAll(data);
                        for (int i = 0; i < data.size(); i++) {
                            Tingkatan t = data.get(i);
                            Log.d("TingkatanActivity", "Tingkatan " + i + ": ID=" + t.getId() + ", Nama=" + t.getNama() + ", Desc=" + t.getDescription());
                        }
                    }

                    if (data == null || data.isEmpty()) {
                        Toast.makeText(TingkatanActivity.this, "Tidak ada tingkatan ditemukan", Toast.LENGTH_LONG).show();
                        Log.w("TingkatanActivity", "No tingkatan data found");
                    } else {
                        Toast.makeText(TingkatanActivity.this, "Berhasil memuat " + data.size() + " tingkatan", Toast.LENGTH_SHORT).show();
                        Log.d("TingkatanActivity", "Successfully loaded " + data.size() + " tingkatan");
                    }
                    tampilantingkatan(data != null ? data : new ArrayList<>());
                } else {
                    Log.e("TingkatanActivity", "API call failed!");
                    Log.e("TingkatanActivity", "Error Code: " + response.code());
                    Log.e("TingkatanActivity", "Error Message: " + response.message());

                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("TingkatanActivity", "Error Body: " + errorBody);
                        } catch (Exception e) {
                            Log.e("TingkatanActivity", "Error reading error body: " + e.getMessage());
                        }
                    }

                    String errorMsg = "Gagal mengambil data tingkatan";
                    if (response.code() == 401) {
                        errorMsg = "Token tidak valid, silakan login ulang";
                    } else if (response.code() == 404) {
                        errorMsg = "Endpoint tidak ditemukan";
                    } else if (response.code() >= 500) {
                        errorMsg = "Server error: " + response.code();
                    }

                    Toast.makeText(TingkatanActivity.this, errorMsg + " (" + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TingkatanResponse> call, Throwable t) {
                Log.e("TingkatanActivity", "Network call failed!");
                Log.e("TingkatanActivity", "onFailure: " + t.getMessage(), t);

                String errorMsg = "Koneksi gagal";
                if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "Tidak dapat terhubung ke server. Periksa koneksi internet.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Koneksi timeout. Coba lagi.";
                } else if (t instanceof java.net.ConnectException) {
                    errorMsg = "Tidak dapat terhubung ke server.";
                }

                Toast.makeText(TingkatanActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void tampilantingkatan(List<Tingkatan> listTingkatan) {
        Log.d("TingkatanActivity", "tampilantingkatan called with " + (listTingkatan != null ? listTingkatan.size() : "null") + " items");

        cardHelper.setupGrid(gridTingkatan);

        if (listTingkatan == null || listTingkatan.isEmpty()) {
            Log.w("TingkatanActivity", "No tingkatan to display");
            TextView noDataText = cardHelper.createNoDataMessage("Belum ada tingkatan.\nKlik 'Tambah Tingkatan' untuk menambah.");
            gridTingkatan.addView(noDataText);
            return;
        }

        Log.d("TingkatanActivity", "Creating cards for " + listTingkatan.size() + " tingkatan");

        for (int index = 0; index < listTingkatan.size(); index++) {
            Tingkatan tingkatan = listTingkatan.get(index);
            Log.d("TingkatanActivity", "Creating card " + index + " for tingkatan: " + tingkatan.getNama());

            // Buat card menggunakan CardDisplayHelper yang konsisten
            LinearLayout card = cardHelper.createCard();
            LinearLayout contentLayout = cardHelper.createContentLayout(R.drawable.ic_tingkatan, tingkatan.getNama());

            // Tambahkan menu icon
            ImageView menuIcon = cardHelper.createMenuIcon(tingkatan, tingkatan.getNama(), new CardDisplayHelper.CardActionListener() {
                @Override
                public void onEditClick(Object item) {
                    Tingkatan t = (Tingkatan) item;
                    if (t.getId() != 0) {
                        Intent intent = new Intent(TingkatanActivity.this, com.example.brainquiz.activities.EditActivity.class);
                        intent.putExtra("tingkatanId", String.valueOf(t.getId()));
                        intent.putExtra("tingkatanNama", t.getNama());
                        intent.putExtra("tingkatanDeskripsi", t.getDescription());
                        startActivityForResult(intent, REQUEST_CODE_EDIT);
                    } else {
                        Toast.makeText(TingkatanActivity.this, "ID tingkatan tidak valid", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onDeleteClick(Object item) {
                    Tingkatan t = (Tingkatan) item;
                    if (t.getId() != 0) {
                        String token = getToken();
                        if (!token.isEmpty()) {
                            apiService.deleteTingkatan("Bearer " + token, t.getId()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(TingkatanActivity.this, "Tingkatan " + (t.getNama() != null ? t.getNama() : "") + " berhasil dihapus", Toast.LENGTH_SHORT).show();
                                        fetchTingkatan();
                                    } else {
                                        Toast.makeText(TingkatanActivity.this, "Gagal menghapus: " + response.code(), Toast.LENGTH_SHORT).show();
                                        Log.e("DeleteTingkatan", "Error Code: " + response.code());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(TingkatanActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("DeleteTingkatan", "onFailure: " + t.getMessage(), t);
                                }
                            });
                        } else {
                            Toast.makeText(TingkatanActivity.this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TingkatanActivity.this, "ID tingkatan tidak valid", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onDeleteSuccess() {
                    fetchTingkatan();
                }
            });

            card.addView(contentLayout);
            card.addView(menuIcon);

            // Tambahkan tag untuk identifikasi card
            card.setTag(String.valueOf(tingkatan.getId()));

            // Add to Grid
            gridTingkatan.addView(card);
            Log.d("TingkatanActivity", "Card " + index + " added to grid for tingkatan: " + tingkatan.getNama());
        }

        Log.d("TingkatanActivity", "Finished creating " + listTingkatan.size() + " cards. Grid now has " + gridTingkatan.getChildCount() + " children");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK && data != null) {
            // Ambil data yang diedit dari EditActivity
            String tingkatanId = data.getStringExtra("tingkatanId");
            String namaBaru = data.getStringExtra("namaBaru");
            String deskripsiBaru = data.getStringExtra("deskripsiBaru");

            // Perbarui data di tingkatanList
            for (Tingkatan tingkatan : tingkatanList) {
                try {
                    int id = Integer.parseInt(tingkatanId); // Konversi tingkatanId ke int
                    if (tingkatan.getId() == id) {
                        tingkatan.setNama(namaBaru);
                        tingkatan.setDescription(deskripsiBaru);
                        break;
                    }
                } catch (NumberFormatException e) {
                    Log.e("TingkatanActivity", "Invalid ID format: " + tingkatanId);
                }
            }

            // Perbarui UI hanya untuk card yang diedit
            for (int i = 0; i < gridTingkatan.getChildCount(); i++) {
                LinearLayout card = (LinearLayout) gridTingkatan.getChildAt(i);
                if (card.getTag() != null && card.getTag().equals(tingkatanId)) {
                    TextView tvNama = card.findViewWithTag("nama_" + tingkatanId);
                    if (tvNama != null) {
                        tvNama.setText(namaBaru != null ? namaBaru : "Tidak ada nama");
                    }
                    break;
                }
            }

            Log.d("TingkatanActivity", "Updated - ID: " + tingkatanId + ", Nama: " + namaBaru);
        }
    }

    private void filterTingkatan(String query) {
        List<Tingkatan> filteredList = new ArrayList<>();
        for (Tingkatan tingkatan : tingkatanList) {
            if (tingkatan == null || tingkatan.getNama() == null) continue;
            if (tingkatan.getNama().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(tingkatan);
            }
        }
        tampilantingkatan(filteredList);
    }
}


