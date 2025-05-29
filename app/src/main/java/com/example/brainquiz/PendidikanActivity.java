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
import com.example.brainquiz.filter.Pendidikan;
import com.example.brainquiz.network.ApiService;
import com.example.brainquiz.PendidikanResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PendidikanActivity extends AppCompatActivity {

    private GridLayout gridPendidikan;
    private Button btnTambahPendidikan;
    private EditText searchBar;
    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private static final int REQUEST_CODE_EDIT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendidikan);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        gridPendidikan = findViewById(R.id.gridPendidikan);
        btnTambahPendidikan = findViewById(R.id.btnTambahPendidikan);
        searchBar = findViewById(R.id.searchBar);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Set click listener for "Tambah Pendidikan" button
        btnTambahPendidikan.setOnClickListener(v -> {
            Intent intent = new Intent(PendidikanActivity.this, TambahPendidikanActivity.class);
            startActivity(intent);
        });

        // Fetch initial data
        fetchPendidikan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPendidikan();
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private void fetchPendidikan() {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("PendidikanActivity", "Token: " + token);
        apiService.getPendidikan("Bearer " + token).enqueue(new Callback<PendidikanResponse>() {
            @Override
            public void onResponse(Call<PendidikanResponse> call, Response<PendidikanResponse> response) {
                Log.d("PendidikanActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Pendidikan> pendidikanList = response.body().getData();
                    if (pendidikanList.isEmpty()) {
                        Toast.makeText(PendidikanActivity.this, "Tidak ada pendidikan", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PendidikanActivity.this, "Dapat " + pendidikanList.size() + " pendidikan", Toast.LENGTH_SHORT).show();
                    }
                    tampilkanPendidikan(pendidikanList);
                } else {
                    Log.e("PendidikanActivity", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("PendidikanActivity", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("PendidikanActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(PendidikanActivity.this, "Gagal mengambil data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PendidikanResponse> call, Throwable t) {
                Log.e("PendidikanActivity", "onFailure: " + t.getMessage(), t);
                Toast.makeText(PendidikanActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tampilkanPendidikan(List<Pendidikan> listKategori) {
        gridPendidikan.removeAllViews();
        gridPendidikan.setColumnCount(2);

        final float density = getResources().getDisplayMetrics().density;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cardWidth = (screenWidth / 2) - (int)(32 * density); // Adjust for better sizing

        for (Pendidikan pendidikan : listKategori) {
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
            icon.setImageResource(R.drawable.ic_pendidikan);
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

            String nama = pendidikan.getNama() != null ? pendidikan.getNama() : "Nama tidak tersedia";
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
                Dialog dialog = new Dialog(PendidikanActivity.this);
                dialog.setContentView(R.layout.dialog_menu);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Opsi Edit
                LinearLayout itemEdit = dialog.findViewById(R.id.menu_edit);
                if (itemEdit != null) {
                    itemEdit.setOnClickListener(v -> {
                        if (pendidikan.getId() != 0) {
                            Intent intent = new Intent(PendidikanActivity.this, EditPendidikanActivity.class);
                            intent.putExtra("pendidikanId", String.valueOf(pendidikan.getId()));
                            intent.putExtra("pendidikanNama", pendidikan.getNama());
                            intent.putExtra("pendidikanDeskripsi", pendidikan.getDescription());
                            Log.d("PendidikanActivity", "Launching EditPendidikanActivity with pendidikanId: " + pendidikan.getId());
                            startActivityForResult(intent, REQUEST_CODE_EDIT);
                            dialog.dismiss();
                        } else {
                            Log.e("PendidikanActivity", "ID pendidikan tidak valid: " + pendidikan.getId());
                            Toast.makeText(PendidikanActivity.this, "ID pendidikan tidak valid", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                // Opsi Hapus
                LinearLayout itemHapus = dialog.findViewById(R.id.itemHapus);
                if (itemHapus != null) {
                    itemHapus.setOnClickListener(v -> {
                        if (pendidikan.getId() != 0) {
                            new AlertDialog.Builder(PendidikanActivity.this)
                                    .setTitle("Konfirmasi Hapus")
                                    .setMessage("Apakah Anda yakin ingin menghapus " + (pendidikan.getNama() != null ? pendidikan.getNama() : "pendidikan ini") + "?")
                                    .setPositiveButton("Ya", (dialogConfirm, which) -> {
                                        String token = getToken();
                                        if (!token.isEmpty()) {
                                            apiService.deletePendidikan("Bearer " + token, pendidikan.getId()).enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    if (response.isSuccessful()) {
                                                        Toast.makeText(PendidikanActivity.this, "Pendidikan " + (pendidikan.getNama() != null ? pendidikan.getNama() : "") + " berhasil dihapus", Toast.LENGTH_SHORT).show();
                                                        fetchPendidikan();
                                                    } else {
                                                        Toast.makeText(PendidikanActivity.this, "Gagal menghapus: " + response.code(), Toast.LENGTH_SHORT).show();
                                                        Log.e("DeletePendidikan", "Error Code: " + response.code());
                                                        if (response.errorBody() != null) {
                                                            try {
                                                                Log.e("DeletePendidikan", "Error Body: " + response.errorBody().string());
                                                            } catch (Exception e) {
                                                                Log.e("DeletePendidikan", "Error reading error body: " + e.getMessage());
                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Toast.makeText(PendidikanActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                    Log.e("DeletePendidikan", "onFailure: " + t.getMessage(), t);
                                                }
                                            });
                                        } else {
                                            Toast.makeText(PendidikanActivity.this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton("Tidak", (dialogConfirm, which) -> dialogConfirm.dismiss())
                                    .show();
                        } else {
                            Toast.makeText(PendidikanActivity.this, "ID pendidikan tidak valid", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    });
                }

                dialog.show();
            });

            // Tambahkan tag untuk identifikasi card dan TextView
            card.setTag(String.valueOf(pendidikan.getId()));
            tvNama.setTag("nama_" + pendidikan.getId());

            // Add to Grid
            gridPendidikan.addView(card);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK && data != null) {
            String pendidikanId = data.getStringExtra("pendidikanId");
            String namaBaru = data.getStringExtra("namaBaru");
            String deskripsiBaru = data.getStringExtra("deskripsiBaru");

            // Perbarui UI hanya untuk card yang diedit
            for (int i = 0; i < gridPendidikan.getChildCount(); i++) {
                LinearLayout card = (LinearLayout) gridPendidikan.getChildAt(i);
                if (card.getTag() != null && card.getTag().equals(pendidikanId)) {
                    TextView tvNama = card.findViewWithTag("nama_" + pendidikanId);
                    if (tvNama != null) {
                        tvNama.setText(namaBaru != null ? namaBaru : "Nama tidak tersedia");
                    }
                    break;
                }
            }

            Log.d("PendidikanActivity", "Updated - ID: " + pendidikanId + ", Nama: " + namaBaru);
        }
    }
}