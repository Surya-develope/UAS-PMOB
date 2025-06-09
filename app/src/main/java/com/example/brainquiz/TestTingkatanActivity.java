package com.example.brainquiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Tingkatan;
import com.example.brainquiz.network.ApiService;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestTingkatanActivity extends AppCompatActivity {

    private TextView tvResult;
    private Button btnTestGet, btnTestAdd;
    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_tingkatan);

        tvResult = findViewById(R.id.tvResult);
        btnTestGet = findViewById(R.id.btnTestGet);
        btnTestAdd = findViewById(R.id.btnTestAdd);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        btnTestGet.setOnClickListener(v -> testGetTingkatan());
        btnTestAdd.setOnClickListener(v -> testAddTingkatan());
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        String token = sp.getString("token", "");
        Log.d("TestTingkatan", "Token: " + token);
        return token;
    }

    private void testGetTingkatan() {
        String token = getToken();
        if (token.isEmpty()) {
            tvResult.setText("❌ ERROR: Token tidak ditemukan!\nSilakan login terlebih dahulu.");
            return;
        }

        tvResult.setText("🔄 Testing GET tingkatan...\nToken: " + token.substring(0, Math.min(20, token.length())) + "...");
        btnTestGet.setEnabled(false);

        Log.d("TestTingkatan", "Making GET request to: " + BASE_URL + "tingkatan/get-tingkatan");
        Log.d("TestTingkatan", "Authorization: Bearer " + token);

        apiService.getTingkatan("Bearer " + token).enqueue(new Callback<TingkatanResponse>() {
            @Override
            public void onResponse(Call<TingkatanResponse> call, Response<TingkatanResponse> response) {
                btnTestGet.setEnabled(true);
                
                StringBuilder result = new StringBuilder();
                result.append("📡 GET TINGKATAN RESPONSE:\n\n");
                result.append("Status Code: ").append(response.code()).append("\n");
                result.append("Is Successful: ").append(response.isSuccessful()).append("\n");
                result.append("Response Message: ").append(response.message()).append("\n\n");

                if (response.isSuccessful() && response.body() != null) {
                    TingkatanResponse responseBody = response.body();
                    result.append("✅ SUCCESS!\n\n");
                    result.append("Response Success Flag: ").append(responseBody.isSuccess()).append("\n");
                    result.append("Response Message: ").append(responseBody.getMessage()).append("\n");
                    
                    List<Tingkatan> data = responseBody.getData();
                    result.append("Data Count: ").append(data != null ? data.size() : "null").append("\n\n");
                    
                    if (data != null && !data.isEmpty()) {
                        result.append("📋 TINGKATAN DATA:\n");
                        for (int i = 0; i < data.size(); i++) {
                            Tingkatan t = data.get(i);
                            result.append("  ").append(i + 1).append(". ID: ").append(t.getId())
                                  .append(", Nama: ").append(t.getNama())
                                  .append(", Desc: ").append(t.getDescription()).append("\n");
                        }
                    } else {
                        result.append("⚠️ No tingkatan data found\n");
                    }
                } else {
                    result.append("❌ FAILED!\n\n");
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            result.append("Error Body: ").append(errorBody).append("\n");
                        } catch (Exception e) {
                            result.append("Error reading error body: ").append(e.getMessage()).append("\n");
                        }
                    }
                }

                tvResult.setText(result.toString());
                Log.d("TestTingkatan", result.toString());
            }

            @Override
            public void onFailure(Call<TingkatanResponse> call, Throwable t) {
                btnTestGet.setEnabled(true);
                String result = "❌ NETWORK FAILURE!\n\n" +
                               "Error: " + t.getMessage() + "\n" +
                               "Type: " + t.getClass().getSimpleName();
                tvResult.setText(result);
                Log.e("TestTingkatan", "Network failure", t);
            }
        });
    }

    private void testAddTingkatan() {
        String token = getToken();
        if (token.isEmpty()) {
            tvResult.setText("❌ ERROR: Token tidak ditemukan!\nSilakan login terlebih dahulu.");
            return;
        }

        tvResult.setText("🔄 Testing ADD tingkatan...");
        btnTestAdd.setEnabled(false);

        // Create test tingkatan
        Tingkatan testTingkatan = new Tingkatan();
        testTingkatan.setNama("Test Tingkatan " + System.currentTimeMillis());
        testTingkatan.setDescription("Test description for debugging");

        Log.d("TestTingkatan", "Adding tingkatan: " + new Gson().toJson(testTingkatan));

        apiService.addTingkatan("Bearer " + token, testTingkatan).enqueue(new Callback<TingkatanResponse>() {
            @Override
            public void onResponse(Call<TingkatanResponse> call, Response<TingkatanResponse> response) {
                btnTestAdd.setEnabled(true);
                
                StringBuilder result = new StringBuilder();
                result.append("📡 ADD TINGKATAN RESPONSE:\n\n");
                result.append("Status Code: ").append(response.code()).append("\n");
                result.append("Is Successful: ").append(response.isSuccessful()).append("\n\n");

                if (response.isSuccessful()) {
                    result.append("✅ SUCCESS! Tingkatan added successfully!\n");
                    Toast.makeText(TestTingkatanActivity.this, "Tingkatan berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                } else {
                    result.append("❌ FAILED!\n");
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            result.append("Error Body: ").append(errorBody).append("\n");
                        } catch (Exception e) {
                            result.append("Error reading error body: ").append(e.getMessage()).append("\n");
                        }
                    }
                }

                tvResult.setText(result.toString());
                Log.d("TestTingkatan", result.toString());
            }

            @Override
            public void onFailure(Call<TingkatanResponse> call, Throwable t) {
                btnTestAdd.setEnabled(true);
                String result = "❌ NETWORK FAILURE!\n\n" +
                               "Error: " + t.getMessage() + "\n" +
                               "Type: " + t.getClass().getSimpleName();
                tvResult.setText(result);
                Log.e("TestTingkatan", "Network failure", t);
            }
        });
    }
}
