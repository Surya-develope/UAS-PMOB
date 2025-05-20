package com.example.brainquiz;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Pendidikan;
import com.example.brainquiz.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PendidikanActivity extends AppCompatActivity {

    // Ganti dengan base URL yang sesuai
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";

    private ApiService apiService;
    private String token = "Bearer YOUR_TOKEN_HERE"; // Ganti dengan token yang valid

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendidikan);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        fetchPendidikan();
    }

    private void fetchPendidikan() {
        Call<List<Pendidikan>> call = apiService.getPendidikan(token);
        call.enqueue(new Callback<List<Pendidikan>>() {
            @Override
            public void onResponse(Call<List<Pendidikan>> call, Response<List<Pendidikan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pendidikan> pendidikanList = response.body();
                    // Misalnya tampilkan nama pendidikan di log atau toast
                    StringBuilder sb = new StringBuilder();
                    for (Pendidikan p : pendidikanList) {
                        sb.append(p.getNama()).append("\n");
                    }
                    Toast.makeText(PendidikanActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PendidikanActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pendidikan>> call, Throwable t) {
                Toast.makeText(PendidikanActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("PendidikanActivity", "onFailure: ", t);
            }
        });
    }
}
