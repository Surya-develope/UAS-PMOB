package com.example.brainquiz.activities;
import com.example.brainquiz.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.graphics.drawable.ColorDrawable;
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

import com.example.brainquiz.filter.Kategori;
import com.example.brainquiz.network.ApiService;
import com.example.brainquiz.models.KategoriResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KategoriActivity extends AppCompatActivity {

    private GridLayout gridKategori;
    private Button btnTambahKategori;
    private EditText searchBar; // Tambahkan searchBar yang sesuai dengan layout
    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private static final int REQUEST_CODE_EDIT = 100;
    private List<Kategori> kategoriList = new ArrayList<>(); // Simpan daftar kategori

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        gridKategori = findViewById(R.id.gridKategori);
        btnTambahKategori = findViewById(R.id.btnTambahKategori);
        searchBar = findViewById(R.id.searchBar); // Inisialisasi searchBar

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Set click listener for "Tambah Kategori" button
        btnTambahKategori.setOnClickListener(v -> {
            Intent intent = new Intent(KategoriActivity.this, com.example.brainquiz.activities.TambahKategoriActivity.class);
            startActivity(intent);
        });

        // Setup search listener
        setupSearchListener();

        // Fetch initial data
        fetchKategori();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchKategori();
    }

    private void setupSearchListener() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterKategori(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private void fetchKategori() {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("KategoriActivity", "Token: " + token);
        apiService.getKategori("Bearer " + token).enqueue(new Callback<KategoriResponse>() {
            @Override
            public void onResponse(Call<KategoriResponse> call, Response<KategoriResponse> response) {
                Log.d("KategoriActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Kategori> data = response.body().getData();
                    kategoriList.clear();
                    if (data != null) {
                        kategoriList.addAll(data);
                    }
                    if (kategoriList.isEmpty()) {
                        Toast.makeText(KategoriActivity.this, "Tidak ada kategori", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(KategoriActivity.this, "Dapat " + kategoriList.size() + " kategori", Toast.LENGTH_SHORT).show();
                    }
                    tampilkanKategori(kategoriList);
                } else {
                    Log.e("KategoriActivity", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("KategoriActivity", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("KategoriActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(KategoriActivity.this, "Gagal mengambil data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KategoriResponse> call, Throwable t) {
                Log.e("KategoriActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(KategoriActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tampilkanKategori(List<Kategori> listKategori) {
        gridKategori.removeAllViews();
        gridKategori.setColumnCount(2);

        final float density = getResources().getDisplayMetrics().density;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cardWidth = (screenWidth / 2) - (int)(32 * density); // Adjust for better sizing

        for (Kategori kategori : listKategori) {
            // Container Card
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER);

            // Layout Parameters
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cardWidth;
            params.height = (int)(160 * density); // Fixed height that looks good on most devices
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
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
            card.setBackgroundResource(R.drawable.bg_tingkatan_card);

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
            icon.setImageResource(R.drawable.ic_kategori);
            icon.setColorFilter(Color.WHITE);
            contentLayout.addView(icon);

            // TextView nama
            TextView tvNama = new TextView(this);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textParams.gravity = Gravity.CENTER;
            textParams.topMargin = (int) (12 * density);
            tvNama.setLayoutParams(textParams);

            String nama = kategori.getNama() != null ? kategori.getNama() : "Nama tidak tersedia";
            tvNama.setText(nama);
            tvNama.setTextColor(Color.WHITE);
            tvNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tvNama.setTypeface(null, Typeface.BOLD);
            contentLayout.addView(tvNama);

            card.addView(contentLayout);

            // Tambahkan tombol opsi (ikon tiga titik)
            ImageView menuIcon = new ImageView(this);
            menuIcon.setImageResource(R.drawable.ic_more_vert);
            menuIcon.setColorFilter(Color.WHITE);

            LinearLayout.LayoutParams menuParams = new LinearLayout.LayoutParams(
                    (int) (24 * density),
                    (int) (24 * density)
            );
            menuParams.gravity = Gravity.END | Gravity.TOP;
            menuIcon.setLayoutParams(menuParams);
            card.addView(menuIcon);
            // Custom dialog untuk opsi Edit dan Hapus
            menuIcon.setOnClickListener(view -> {
                Dialog dialog = new Dialog(KategoriActivity.this);
                dialog.setContentView(R.layout.dialog_menu);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Opsi Edit
                LinearLayout itemEdit = dialog.findViewById(R.id.menu_edit);
                if (itemEdit != null) {
                    itemEdit.setOnClickListener(v -> {
                        if (kategori.getId() != 0) {
                            Intent intent = new Intent(KategoriActivity.this, com.example.brainquiz.activities.EditKategoriActivity.class);
                            intent.putExtra("kategoriId", String.valueOf(kategori.getId()));
                            intent.putExtra("kategoriNama", kategori.getNama());
                            intent.putExtra("kategoriDeskripsi", kategori.getDescription());
                            Log.d("KategoriActivity", "Launching EditKategoriActivity with kategoriId: " + kategori.getId());
                            startActivityForResult(intent, REQUEST_CODE_EDIT);
                            dialog.dismiss();
                        } else {
                            Log.e("KategoriActivity", "ID kategori tidak valid: " + kategori.getId());
                            Toast.makeText(KategoriActivity.this, "ID kategori tidak valid", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                // Opsi Hapus
                LinearLayout itemHapus = dialog.findViewById(R.id.itemHapus);
                if (itemHapus != null) {
                    itemHapus.setOnClickListener(v -> {
                        if (kategori.getId() != 0) {
                            new AlertDialog.Builder(KategoriActivity.this)
                                    .setTitle("Konfirmasi Hapus")
                                    .setMessage("Apakah Anda yakin ingin menghapus " + (kategori.getNama() != null ? kategori.getNama() : "kategori ini") + "?")
                                    .setPositiveButton("Ya", (dialogConfirm, which) -> {
                                        String token = getToken();
                                        if (!token.isEmpty()) {
                                            apiService.deleteKategori("Bearer " + token, kategori.getId()).enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    if (response.isSuccessful()) {
                                                        Toast.makeText(KategoriActivity.this, "Kategori " + (kategori.getNama() != null ? kategori.getNama() : "") + " berhasil dihapus", Toast.LENGTH_SHORT).show();
                                                        fetchKategori();
                                                    } else {
                                                        Toast.makeText(KategoriActivity.this, "Gagal menghapus: " + response.code(), Toast.LENGTH_SHORT).show();
                                                        Log.e("DeleteKategori", "Error Code: " + response.code());
                                                        if (response.errorBody() != null) {
                                                            try {
                                                                Log.e("DeleteKategori", "Error Body: " + response.errorBody().string());
                                                            } catch (Exception e) {
                                                                Log.e("DeleteKategori", "Error reading error body: " + e.getMessage());
                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Toast.makeText(KategoriActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                    Log.e("DeleteKategori", "onFailure: " + t.getMessage(), t);
                                                }
                                            });
                                        } else {
                                            Toast.makeText(KategoriActivity.this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton("Tidak", (dialogConfirm, which) -> dialogConfirm.dismiss())
                                    .show();
                        } else {
                            Toast.makeText(KategoriActivity.this, "ID kategori tidak valid", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    });
                }

                dialog.show();
            });

            // Tambahkan tag untuk identifikasi card dan TextView
            card.setTag(String.valueOf(kategori.getId()));
            tvNama.setTag("nama_" + kategori.getId());

            // Add to Grid
            gridKategori.addView(card);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK && data != null) {
            String kategoriId = data.getStringExtra("kategoriId");
            String namaBaru = data.getStringExtra("namaBaru");
            String deskripsiBaru = data.getStringExtra("deskripsiBaru");

            // Perbarui UI hanya untuk card yang diedit
            for (int i = 0; i < gridKategori.getChildCount(); i++) {
                LinearLayout card = (LinearLayout) gridKategori.getChildAt(i);
                if (card.getTag() != null && card.getTag().equals(kategoriId)) {
                    TextView tvNama = card.findViewWithTag("nama_" + kategoriId);
                    if (tvNama != null) {
                        tvNama.setText(namaBaru != null ? namaBaru : "Nama tidak tersedia");
                    }
                    break;
                }
            }

            Log.d("KategoriActivity", "Updated - ID: " + kategoriId + ", Nama: " + namaBaru);
        }
    }

    private void filterKategori(String query) {
        List<Kategori> filteredList = new ArrayList<>();
        for (Kategori kategori : kategoriList) {
            if (kategori == null || kategori.getNama() == null) continue;
            if (kategori.getNama().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(kategori);
            }
        }
        tampilkanKategori(filteredList);
    }
}


