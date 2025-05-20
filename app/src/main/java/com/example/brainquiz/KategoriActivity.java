package com.example.brainquiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.network.ApiService;
import com.example.brainquiz.filter.Kategori;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KategoriActivity extends AppCompatActivity {

    private GridLayout gridKategori;
    private ApiService apiService;
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori);

        gridKategori = findViewById(R.id.gridKategori);

        // Ambil token dari SharedPreferences
        SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        token = preferences.getString("token", "");

        // Inisialisasi Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://brainquiz0.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);


        fetchKategori();
    }

    private void fetchKategori() {
        apiService.getKategori("Bearer " + token).enqueue(new Callback<KategoriResponse>() {
            @Override
            public void onResponse(Call<KategoriResponse> call, Response<KategoriResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Kategori> kategoriList = response.body().getData();
                    tampilkanKategori(kategoriList);
                } else {
                    Log.e("API_ERROR", "Response error: " + response.code());
                    Toast.makeText(KategoriActivity.this, "Gagal ambil data kategori", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KategoriResponse> call, Throwable t) {
                Log.e("API_ERROR", "onFailure: " + t.getMessage());
                Toast.makeText(KategoriActivity.this, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show();
            }
        });
    }

        private void tampilkanKategori(List<Kategori> listKategori) {
        gridKategori.removeAllViews();

        for (Kategori kategori : listKategori) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER);
            int paddingPx = (int) (16 * getResources().getDisplayMetrics().density);
            card.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            card.setBackgroundResource(R.drawable.bg_tingkatan_card); // sesuaikan dengan drawable-mu

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
            icon.setImageResource(R.drawable.ic_book); // ganti dengan icon kamu
            icon.setColorFilter(getResources().getColor(android.R.color.white));
            card.addView(icon);

            // Nama Kategori
            TextView nama = new TextView(this);
            LinearLayout.LayoutParams namaParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            namaParams.topMargin = (int) (8 * getResources().getDisplayMetrics().density);
            nama.setLayoutParams(namaParams);
            nama.setText(kategori.getNama()); // pastikan getter sesuai dengan class Kategori
            nama.setTextColor(getResources().getColor(android.R.color.white));
            nama.setTextSize(14);
            card.addView(nama);

            gridKategori.addView(card);
        }
    }
}
