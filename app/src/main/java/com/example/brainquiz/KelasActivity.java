package com.example.brainquiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

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
    private ApiService apiService;
    private String token;

    EditText searchBar;
    Button btnTambahTingkatan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelas);

        searchBar = findViewById(R.id.searchBar);
        btnTambahTingkatan = findViewById(R.id.btnTambahTingkatan);
        gridKelas = findViewById(R.id.gridLayout); // pastikan ID di XML = gridLayout

        // Ambil token dari SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        token = preferences.getString("token", null);

        // Inisialisasi Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://brainquiz0.up.railway.app/") // ganti sesuai base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Panggil API
        fetchKelas();
    }

    private void fetchKelas() {
        apiService.getKelas("Bearer " + token).enqueue(new Callback<List<Kelas>>() {
            @Override
            public void onResponse(Call<List<Kelas>> call, Response<List<Kelas>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tampilkanKelas(response.body());
                } else {
                    Toast.makeText(KelasActivity.this, "Gagal mengambil data kelas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Kelas>> call, Throwable t) {
                Toast.makeText(KelasActivity.this, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show();
                Log.e("KelasActivity", t.getMessage(), t);
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

            ImageView icon = new ImageView(this);
            int sizePx = (int) (48 * getResources().getDisplayMetrics().density);
            icon.setLayoutParams(new LinearLayout.LayoutParams(sizePx, sizePx));
            icon.setImageResource(R.drawable.ic_kelas); // pastikan drawable ini tersedia
            icon.setColorFilter(getResources().getColor(android.R.color.white));
            card.addView(icon);

            TextView nama = new TextView(this);
            nama.setText(kelas.getNama());
            nama.setTextColor(getResources().getColor(android.R.color.white));
            nama.setTextSize(14);
            nama.setGravity(Gravity.CENTER);
            nama.setPadding(0, paddingPx / 2, 0, 0);
            card.addView(nama);

            gridKelas.addView(card);
        }
    }
}
