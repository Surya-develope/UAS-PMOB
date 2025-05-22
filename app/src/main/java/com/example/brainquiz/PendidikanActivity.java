package com.example.brainquiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Pendidikan;
import com.example.brainquiz.network.ApiService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PendidikanActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private ApiService apiService;
    private TextView tvPendidikanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendidikan);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        tvPendidikanList = findViewById(R.id.tv_pendidikan_list);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

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
                Log.d("PendidikanActivity", "Raw Response: " + response.raw().toString());

                if (response.isSuccessful() && response.body() != null) {
                    List<Pendidikan> data = response.body().getData();
                    Log.d("PendidikanActivity", "Data Size: " + data.size());
                    Toast.makeText(PendidikanActivity.this, "Dapat " + data.size() + " pendidikan", Toast.LENGTH_SHORT).show();

                    StringBuilder sb = new StringBuilder();
                    for (Pendidikan p : data) {
                        sb.append(p.getNama()).append("\n");
                    }
                    tvPendidikanList.setText(sb.toString());
                } else {
                    Log.e("PendidikanActivity", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("PendidikanActivity", "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
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
}