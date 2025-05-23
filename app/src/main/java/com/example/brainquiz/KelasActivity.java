package com.example.brainquiz;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Kelas;
import com.example.brainquiz.KelasResponse;
import com.example.brainquiz.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KelasActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private Button btnTambahTingkatan;
    private EditText searchBar;

    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelas);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        gridLayout = findViewById(R.id.gridLayout);
        btnTambahTingkatan = findViewById(R.id.btnTambahTingkatan);
        searchBar = findViewById(R.id.searchBar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        fetchKelas();

        btnTambahTingkatan.setOnClickListener(v -> Toast.makeText(this, "Tambah Tingkatan diklik", Toast.LENGTH_SHORT).show());
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

        apiService.getKelas("Bearer " + token).enqueue(new Callback<KelasResponse>() {
            @Override
            public void onResponse(Call<KelasResponse> call, Response<KelasResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Kelas> data = response.body().getData();
                    Toast.makeText(KelasActivity.this, "Dapat " + data.size() + " kelas", Toast.LENGTH_SHORT).show();
                    bindDataToCards(data);
                } else {
                    Log.e("KelasActivity", "Error " + response.code());
                    Toast.makeText(KelasActivity.this, "Gagal mengambil data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KelasResponse> call, Throwable t) {
                Toast.makeText(KelasActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("KelasActivity", "onFailure: ", t);
            }
        });
    }

    private void bindDataToCards(List<Kelas> list) {
        gridLayout.removeAllViews();

        for (Kelas kelas : list) {
            LinearLayout card = new LinearLayout(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(16, 16, 16, 16);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            card.setLayoutParams(params);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER_HORIZONTAL);
            card.setPadding(32, 32, 32, 32);
            card.setBackgroundResource(R.drawable.bg_card);
            card.setElevation(8f);

            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(96, 96));
            imageView.setImageResource(R.drawable.kelas);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            card.addView(imageView);

            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textView.setText(kelas.getName());
            textView.setTextColor(getResources().getColor(android.R.color.white));
            textView.setTextSize(16);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setPadding(0, 12, 0, 0);
            card.addView(textView);

            gridLayout.addView(card);
        }
    }
}
