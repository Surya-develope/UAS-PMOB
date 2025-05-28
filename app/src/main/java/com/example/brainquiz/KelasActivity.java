package com.example.brainquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.example.brainquiz.filter.Kelas;
import com.example.brainquiz.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KelasActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private Button btnTambahKelas;
    private EditText searchBar;
    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private static final int REQUEST_CODE_EDIT = 100; // Tambahkan konstanta untuk startActivityForResult

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelas);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        gridLayout = findViewById(R.id.gridLayout);
        btnTambahKelas = findViewById(R.id.btnTambahKelas);
        searchBar = findViewById(R.id.searchBar);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Set click listener for "Tambah Kelas" button
        btnTambahKelas.setOnClickListener(v -> {
            Intent intent = new Intent(KelasActivity.this, TambahKelasActivity.class);
            startActivity(intent);
        });

        // Fetch initial data
        fetchKelas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from TambahKelasActivity
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

        Log.d("KelasActivity", "Token: " + token);
        apiService.getKelas("Bearer " + token).enqueue(new Callback<KelasResponse>() {
            @Override
            public void onResponse(Call<KelasResponse> call, Response<KelasResponse> response) {
                Log.d("KelasActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<Kelas> data = response.body().getData();
                    Toast.makeText(KelasActivity.this, "Dapat " + data.size() + " kelas", Toast.LENGTH_SHORT).show();
                    tampilankelas(data); // Perbaiki nama metode
                } else {
                    Log.e("KelasActivity", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("KelasActivity", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("KelasActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(KelasActivity.this, "Gagal mengambil data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KelasResponse> call, Throwable t) {
                Log.e("KelasActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(KelasActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tampilankelas(List<Kelas> listKelas) {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(2);

        final float density = getResources().getDisplayMetrics().density;

        for (Kelas kelas : listKelas) {
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
                    (int) (16 * density),
                    (int) (16 * density),
                    (int) (16 * density),
                    (int) (16 * density)
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

            // ImageView
            ImageView icon = new ImageView(this);
            icon.setLayoutParams(new LinearLayout.LayoutParams(
                    (int) (48 * density),
                    (int) (48 * density)
            ));
            icon.setImageResource(R.drawable.ic_tingkatan);
            icon.setColorFilter(Color.WHITE);
            card.addView(icon);

            // TextView nama
            TextView tvNama = new TextView(this);
            tvNama.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            String nama = kelas.getNama() != null ? kelas.getNama() : "Nama tidak tersedia";
            tvNama.setText(nama);
            tvNama.setTextColor(Color.WHITE);
            tvNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvNama.setPadding(0, (int) (8 * density), 0, 0);
            card.addView(tvNama);

            // Tambahkan tombol opsi (ikon tiga titik)
            ImageView menuIcon = new ImageView(this);
            menuIcon.setImageResource(R.drawable.ic_more_vert);
            menuIcon.setColorFilter(Color.WHITE);

            LinearLayout.LayoutParams menuParams = new LinearLayout.LayoutParams(
                    (int) (24 * density),
                    (int) (24 * density)
            );
            menuParams.gravity = Gravity.END;
            menuParams.topMargin = (int) (8 * density);
            menuIcon.setLayoutParams(menuParams);
            card.addView(menuIcon);

            // Custom dialog untuk opsi Edit dan Hapus
            menuIcon.setOnClickListener(view -> {
                final Dialog dialog = new Dialog(KelasActivity.this);
                dialog.setContentView(R.layout.dialog_menu);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Opsi Edit
                LinearLayout itemEdit = dialog.findViewById(R.id.menu_edit);
                if (itemEdit != null) {
                    itemEdit.setOnClickListener(v -> {
                        if (kelas.getId() != 0) {
                            Intent intent = new Intent(KelasActivity.this, EditKelasActivity.class);
                            intent.putExtra("kelasId", String.valueOf(kelas.getId()));
                            intent.putExtra("kelasNama", kelas.getNama());
                            intent.putExtra("kelasDeskripsi", kelas.getDescription());
                            startActivityForResult(intent, REQUEST_CODE_EDIT);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(KelasActivity.this, "ID kelas tidak valid", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                // Opsi Hapus
                LinearLayout itemHapus = dialog.findViewById(R.id.itemHapus);
                if (itemHapus != null) {
                    itemHapus.setOnClickListener(v -> {
                        if (kelas.getId() != 0) {
                            new AlertDialog.Builder(KelasActivity.this)
                                    .setTitle("Konfirmasi Hapus")
                                    .setMessage("Apakah Anda yakin ingin menghapus " + (kelas.getNama() != null ? kelas.getNama() : "kelas ini") + "?")
                                    .setPositiveButton("Ya", (dialogConfirm, which) -> {
                                        String token = getToken();
                                        if (!token.isEmpty()) {
                                            apiService.deleteKelas("Bearer " + token, kelas.getId()).enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    if (response.isSuccessful()) {
                                                        Toast.makeText(KelasActivity.this, "Kelas " + (kelas.getNama() != null ? kelas.getNama() : "") + " berhasil dihapus", Toast.LENGTH_SHORT).show();
                                                        fetchKelas();
                                                    } else {
                                                        Toast.makeText(KelasActivity.this, "Gagal menghapus: " + response.code(), Toast.LENGTH_SHORT).show();
                                                        Log.e("DeleteKelas", "Error Code: " + response.code());
                                                        if (response.errorBody() != null) {
                                                            try {
                                                                Log.e("DeleteKelas", "Error Body: " + response.errorBody().string());
                                                            } catch (Exception e) {
                                                                Log.e("DeleteKelas", "Error reading error body: " + e.getMessage());
                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Toast.makeText(KelasActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                    Log.e("DeleteKelas", "onFailure: " + t.getMessage(), t);
                                                }
                                            });
                                        } else {
                                            Toast.makeText(KelasActivity.this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton("Tidak", (dialogConfirm, which) -> dialogConfirm.dismiss())
                                    .show();
                        } else {
                            Toast.makeText(KelasActivity.this, "ID kelas tidak valid", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    });
                }

                dialog.show();
            });

            // Tambahkan tag untuk identifikasi card dan TextView
            card.setTag(String.valueOf(kelas.getId()));
            tvNama.setTag("nama_" + kelas.getId());

            // Add to Grid
            gridLayout.addView(card);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK && data != null) {
            // Ambil data yang diedit dari EditKelasActivity
            String kelasId = data.getStringExtra("kelasId");
            String namaBaru = data.getStringExtra("kelasNama");
            String deskripsiBaru = data.getStringExtra("kelasDeskripsi");

            // Perbarui UI hanya untuk card yang diedit
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                LinearLayout card = (LinearLayout) gridLayout.getChildAt(i);
                if (card.getTag() != null && card.getTag().equals(kelasId)) {
                    TextView tvNama = card.findViewWithTag("nama_" + kelasId);
                    if (tvNama != null) {
                        tvNama.setText(namaBaru != null ? namaBaru : "Nama tidak tersedia");
                    }
                    break;
                }
            }

            Log.d("KelasActivity", "Updated - ID: " + kelasId + ", Nama: " + namaBaru);
        }
    }
}