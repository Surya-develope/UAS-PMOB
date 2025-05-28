package com.example.brainquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Kelas;
import com.example.brainquiz.filter.Tingkatan;
import com.example.brainquiz.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TingkatanActivity extends AppCompatActivity {

    private GridLayout gridTingkatan;
    private Button btnTambahTingkatan;
    private EditText etCariTingkatan;
    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";

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

        // Set click listener for "Tambah Tingkatan" button
        btnTambahTingkatan.setOnClickListener(v -> {
            Intent intent = new Intent(TingkatanActivity.this, TambahTingkatanActivity.class);
            startActivity(intent);
        });

        // Fetch initial data
        fetchTingkatan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from TambahTingkatanActivity
        fetchTingkatan();
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private void fetchTingkatan() {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("TingkatanActivity", "Token: " + token);
        apiService.getTingkatan("Bearer " + token).enqueue(new Callback<TingkatanResponse>() {
            @Override
            public void onResponse(Call<TingkatanResponse> call, Response<TingkatanResponse> response) {
                Log.d("TingkatanActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<Tingkatan> data = response.body().getData();
                    if (data.isEmpty()) {
                        Toast.makeText(TingkatanActivity.this, "Tidak ada tingkatan", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TingkatanActivity.this, "Dapat " + data.size() + " tingkatan", Toast.LENGTH_SHORT).show();
                    }
                    tampilantingkatan(data);
                } else {
                    Log.e("TingkatanActivity", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("TingkatanActivity", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("TingkatanActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(TingkatanActivity.this, "Gagal mengambil data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TingkatanResponse> call, Throwable t) {
                Log.e("TingkatanActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(TingkatanActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tampilantingkatan(List<Tingkatan> listtingkatan) {
        gridTingkatan.removeAllViews();
        gridTingkatan.setColumnCount(2);

        final float density = getResources().getDisplayMetrics().density;

        for (Tingkatan tingkatan : listtingkatan) {
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
            tvNama.setText(tingkatan.getNama() != null ? tingkatan.getNama() : "Tidak ada nama");
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
                Dialog dialog = new Dialog(TingkatanActivity.this);
                dialog.setContentView(R.layout.dialog_menu);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Opsi Edit
                LinearLayout itemEdit = dialog.findViewById(R.id.menu_edit);
                if (itemEdit != null) {
                    itemEdit.setOnClickListener(v -> {
                        if (tingkatan.getId() != null && !tingkatan.getId().isEmpty()) {
                            Intent intent = new Intent(TingkatanActivity.this, EditActivity.class);
                            intent.putExtra("tingkatanId", tingkatan.getId());
                            intent.putExtra("tingkatanNama", tingkatan.getNama());
                            startActivity(intent);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(TingkatanActivity.this, "ID tingkatan tidak valid", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                // Opsi Hapus
                LinearLayout itemHapus = dialog.findViewById(R.id.itemHapus);
                if (itemHapus != null) {
                    itemHapus.setOnClickListener(v -> {
                        if (tingkatan.getId() != null && !tingkatan.getId().isEmpty()) {
                            try {
                                int id = Integer.parseInt(tingkatan.getId()); // Konversi String ke int
                                new android.app.AlertDialog.Builder(TingkatanActivity.this)
                                        .setTitle("Konfirmasi Hapus")
                                        .setMessage("Apakah Anda yakin ingin menghapus " + (tingkatan.getNama() != null ? tingkatan.getNama() : "tingkatan ini") + "?")
                                        .setPositiveButton("Ya", (dialogConfirm, which) -> {
                                            String token = getToken();
                                            if (!token.isEmpty()) {
                                                apiService.deleteTingkatan("Bearer " + token, id).enqueue(new Callback<Void>() {
                                                    @Override
                                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                                        if (response.isSuccessful()) {
                                                            Toast.makeText(TingkatanActivity.this, "Tingkatan " + (tingkatan.getNama() != null ? tingkatan.getNama() : "") + " berhasil dihapus", Toast.LENGTH_SHORT).show();
                                                            fetchTingkatan();
                                                        } else {
                                                            Toast.makeText(TingkatanActivity.this, "Gagal menghapus: " + response.code(), Toast.LENGTH_SHORT).show();
                                                            Log.e("DeleteTingkatan", "Error Code: " + response.code());
                                                            if (response.errorBody() != null) {
                                                                try {
                                                                    Log.e("DeleteTingkatan", "Error Body: " + response.errorBody().string());
                                                                } catch (Exception e) {
                                                                    Log.e("DeleteTingkatan", "Error reading error body: " + e.getMessage());
                                                                }
                                                            }
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
                                            dialog.dismiss();
                                        })
                                        .setNegativeButton("Tidak", (dialogConfirm, which) -> dialogConfirm.dismiss())
                                        .show();
                            } catch (NumberFormatException e) {
                                Toast.makeText(TingkatanActivity.this, "ID tingkatan tidak valid", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(TingkatanActivity.this, "ID tingkatan tidak valid", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    });
                }

                dialog.show();
            });

            // Add to Grid
            gridTingkatan.addView(card);
        }
    }
}