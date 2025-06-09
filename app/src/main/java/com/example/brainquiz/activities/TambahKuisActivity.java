package com.example.brainquiz.activities;
import com.example.brainquiz.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Kategori;
import com.example.brainquiz.filter.Kelas;
import com.example.brainquiz.filter.Kuis;
import com.example.brainquiz.filter.Pendidikan;
import com.example.brainquiz.filter.Tingkatan;
import com.example.brainquiz.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.brainquiz.models.TingkatanResponse;
import com.example.brainquiz.models.KategoriResponse;
import com.example.brainquiz.models.KelasResponse;
import com.example.brainquiz.models.PendidikanResponse;
import com.example.brainquiz.models.KuisResponse;

public class TambahKuisActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Spinner spinnerKategori, spinnerTingkatan, spinnerKelas, spinnerPendidikan;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;

    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";

    private List<Kategori> kategoriList = new ArrayList<>();
    private List<Tingkatan> tingkatanList = new ArrayList<>();
    private List<Kelas> kelasList = new ArrayList<>();
    private List<Pendidikan> pendidikanList = new ArrayList<>();

    private int selectedKategoriId = 0;
    private int selectedTingkatanId = 0;
    private int selectedKelasId = 0;
    private int selectedPendidikanId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kuis);

        initViews();
        initRetrofit();
        setupClickListeners();
        loadSpinnerData();
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        spinnerKategori = findViewById(R.id.spinnerKategori);
        spinnerTingkatan = findViewById(R.id.spinnerTingkatan);
        spinnerKelas = findViewById(R.id.spinnerKelas);
        spinnerPendidikan = findViewById(R.id.spinnerPendidikan);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveKuis());
        btnCancel.setOnClickListener(v -> finish());
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
        btnCancel.setEnabled(!show);
    }

    private void loadSpinnerData() {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("TambahKuis", "Loading spinner data...");
        loadKategori(token);
        loadTingkatan(token);
        loadKelas(token);
        loadPendidikan(token);
    }

    private void loadKategori(String token) {
        apiService.getKategori("Bearer " + token).enqueue(new Callback<KategoriResponse>() {
            @Override
            public void onResponse(Call<KategoriResponse> call, Response<KategoriResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    kategoriList = response.body().getData();
                    setupKategoriSpinner();
                }
            }

            @Override
            public void onFailure(Call<KategoriResponse> call, Throwable t) {
                Log.e("TambahKuis", "Failed to load kategori: " + t.getMessage());
            }
        });
    }

    private void loadTingkatan(String token) {
        apiService.getTingkatan("Bearer " + token).enqueue(new Callback<TingkatanResponse>() {
            @Override
            public void onResponse(Call<TingkatanResponse> call, Response<TingkatanResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tingkatanList = response.body().getData();
                    setupTingkatanSpinner();
                }
            }

            @Override
            public void onFailure(Call<TingkatanResponse> call, Throwable t) {
                Log.e("TambahKuis", "Failed to load tingkatan: " + t.getMessage());
            }
        });
    }

    private void loadKelas(String token) {
        apiService.getKelas("Bearer " + token).enqueue(new Callback<KelasResponse>() {
            @Override
            public void onResponse(Call<KelasResponse> call, Response<KelasResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    kelasList = response.body().getData();
                    setupKelasSpinner();
                }
            }

            @Override
            public void onFailure(Call<KelasResponse> call, Throwable t) {
                Log.e("TambahKuis", "Failed to load kelas: " + t.getMessage());
            }
        });
    }

    private void loadPendidikan(String token) {
        apiService.getPendidikan("Bearer " + token).enqueue(new Callback<PendidikanResponse>() {
            @Override
            public void onResponse(Call<PendidikanResponse> call, Response<PendidikanResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pendidikanList = response.body().getData();
                    setupPendidikanSpinner();
                }
            }

            @Override
            public void onFailure(Call<PendidikanResponse> call, Throwable t) {
                Log.e("TambahKuis", "Failed to load pendidikan: " + t.getMessage());
            }
        });
    }

    private void setupKategoriSpinner() {
        List<String> kategoriNames = new ArrayList<>();
        kategoriNames.add("Pilih Kategori");
        for (Kategori kategori : kategoriList) {
            kategoriNames.add(kategori.getNama());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategoriNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKategori.setAdapter(adapter);

        spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedKategoriId = kategoriList.get(position - 1).getId();
                    Log.d("TambahKuis", "Selected kategori ID: " + selectedKategoriId);
                } else {
                    selectedKategoriId = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupTingkatanSpinner() {
        List<String> tingkatanNames = new ArrayList<>();
        tingkatanNames.add("Pilih Tingkatan");
        for (Tingkatan tingkatan : tingkatanList) {
            tingkatanNames.add(tingkatan.getNama());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tingkatanNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTingkatan.setAdapter(adapter);

        spinnerTingkatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedTingkatanId = tingkatanList.get(position - 1).getId();
                    Log.d("TambahKuis", "Selected tingkatan ID: " + selectedTingkatanId);
                } else {
                    selectedTingkatanId = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupKelasSpinner() {
        List<String> kelasNames = new ArrayList<>();
        kelasNames.add("Pilih Kelas");
        for (Kelas kelas : kelasList) {
            kelasNames.add(kelas.getNama());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kelasNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKelas.setAdapter(adapter);

        spinnerKelas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedKelasId = kelasList.get(position - 1).getId();
                    Log.d("TambahKuis", "Selected kelas ID: " + selectedKelasId);
                } else {
                    selectedKelasId = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupPendidikanSpinner() {
        List<String> pendidikanNames = new ArrayList<>();
        pendidikanNames.add("Pilih Pendidikan");
        for (Pendidikan pendidikan : pendidikanList) {
            pendidikanNames.add(pendidikan.getNama());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pendidikanNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPendidikan.setAdapter(adapter);

        spinnerPendidikan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedPendidikanId = pendidikanList.get(position - 1).getId();
                    Log.d("TambahKuis", "Selected pendidikan ID: " + selectedPendidikanId);
                } else {
                    selectedPendidikanId = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void saveKuis() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            etTitle.setError("Judul tidak boleh kosong");
            etTitle.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            etDescription.setError("Deskripsi tidak boleh kosong");
            etDescription.requestFocus();
            return;
        }

        if (selectedKategoriId == 0) {
            Toast.makeText(this, "Pilih kategori terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTingkatanId == 0) {
            Toast.makeText(this, "Pilih tingkatan terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedKelasId == 0) {
            Toast.makeText(this, "Pilih kelas terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedPendidikanId == 0) {
            Toast.makeText(this, "Pilih pendidikan terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        // Create Kuis object sesuai format API
        Kuis kuis = new Kuis();
        kuis.setTitle(title);
        kuis.setDescription(description);
        kuis.setKategoriId(selectedKategoriId);
        kuis.setTingkatanId(selectedTingkatanId);
        kuis.setKelasId(selectedKelasId);
        kuis.setPendidikanId(selectedPendidikanId);

        String token = getToken();
        if (token.isEmpty()) {
            showLoading(false);
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("TambahKuis", "Saving kuis...");
        Log.d("TambahKuis", "Title: " + title);
        Log.d("TambahKuis", "Description: " + description);
        Log.d("TambahKuis", "Kategori ID: " + selectedKategoriId);
        Log.d("TambahKuis", "Tingkatan ID: " + selectedTingkatanId);
        Log.d("TambahKuis", "Kelas ID: " + selectedKelasId);
        Log.d("TambahKuis", "Pendidikan ID: " + selectedPendidikanId);

        apiService.addKuis("Bearer " + token, kuis).enqueue(new Callback<KuisResponse>() {
            @Override
            public void onResponse(Call<KuisResponse> call, Response<KuisResponse> response) {
                showLoading(false);

                Log.d("TambahKuis", "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    KuisResponse kuisResponse = response.body();
                    if (kuisResponse.isSuccess()) {
                        Toast.makeText(TambahKuisActivity.this, "Kuis berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(TambahKuisActivity.this, "Gagal menambahkan kuis: " + kuisResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TambahKuis", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("TambahKuis", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("TambahKuis", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(TambahKuisActivity.this, "Gagal menambahkan kuis: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KuisResponse> call, Throwable t) {
                showLoading(false);
                Log.e("TambahKuis", "onFailure: " + t.getMessage(), t);
                Toast.makeText(TambahKuisActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


