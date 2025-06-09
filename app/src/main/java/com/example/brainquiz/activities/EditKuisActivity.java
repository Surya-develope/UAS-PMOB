package com.example.brainquiz.activities;
import com.example.brainquiz.R;

import android.content.Intent;
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

public class EditKuisActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Spinner spinnerKategori, spinnerTingkatan, spinnerKelas, spinnerPendidikan;
    private Button btnUpdate, btnCancel;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    
    private List<Kategori> kategoriList = new ArrayList<>();
    private List<Tingkatan> tingkatanList = new ArrayList<>();
    private List<Kelas> kelasList = new ArrayList<>();
    private List<Pendidikan> pendidikanList = new ArrayList<>();
    
    private int kuisId;
    private int selectedKategoriId = 0;
    private int selectedTingkatanId = 0;
    private int selectedKelasId = 0;
    private int selectedPendidikanId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kuis);

        initViews();
        initRetrofit();
        getIntentData();
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
        btnUpdate = findViewById(R.id.btnUpdate);
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

    private void getIntentData() {
        Intent intent = getIntent();
        kuisId = intent.getIntExtra("kuis_id", 0);
        String title = intent.getStringExtra("kuis_title");
        String description = intent.getStringExtra("kuis_description");
        selectedKategoriId = intent.getIntExtra("kategori_id", 0);
        selectedTingkatanId = intent.getIntExtra("tingkatan_id", 0);
        selectedKelasId = intent.getIntExtra("kelas_id", 0);
        selectedPendidikanId = intent.getIntExtra("pendidikan_id", 0);

        Log.d("EditKuis", "Intent data received:");
        Log.d("EditKuis", "Kuis ID: " + kuisId);
        Log.d("EditKuis", "Title: " + title);
        Log.d("EditKuis", "Description: " + description);
        Log.d("EditKuis", "Kategori ID: " + selectedKategoriId);
        Log.d("EditKuis", "Tingkatan ID: " + selectedTingkatanId);
        Log.d("EditKuis", "Kelas ID: " + selectedKelasId);
        Log.d("EditKuis", "Pendidikan ID: " + selectedPendidikanId);

        etTitle.setText(title);
        etDescription.setText(description);
    }

    private void setupClickListeners() {
        btnUpdate.setOnClickListener(v -> updateKuis());
        btnCancel.setOnClickListener(v -> finish());
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnUpdate.setEnabled(!show);
        btnCancel.setEnabled(!show);
    }

    private void loadSpinnerData() {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("EditKuis", "Loading spinner data...");
        loadKategori(token);
        loadTingkatan(token);
        loadKelas(token);
        loadPendidikan(token);
    }

    private void loadKategori(String token) {
        Log.d("EditKuis", "Loading kategori data...");
        apiService.getKategori("Bearer " + token).enqueue(new Callback<KategoriResponse>() {
            @Override
            public void onResponse(Call<KategoriResponse> call, Response<KategoriResponse> response) {
                Log.d("EditKuis", "Kategori response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    kategoriList = response.body().getData();
                    Log.d("EditKuis", "Loaded " + kategoriList.size() + " kategori items");
                    setupKategoriSpinner();
                } else {
                    Log.e("EditKuis", "Failed to load kategori: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("EditKuis", "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("EditKuis", "Error reading error body: " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<KategoriResponse> call, Throwable t) {
                Log.e("EditKuis", "Failed to load kategori: " + t.getMessage(), t);
                Toast.makeText(EditKuisActivity.this, "Gagal memuat data kategori", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTingkatan(String token) {
        Log.d("EditKuis", "Loading tingkatan data...");
        apiService.getTingkatan("Bearer " + token).enqueue(new Callback<TingkatanResponse>() {
            @Override
            public void onResponse(Call<TingkatanResponse> call, Response<TingkatanResponse> response) {
                Log.d("EditKuis", "Tingkatan response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    tingkatanList = response.body().getData();
                    Log.d("EditKuis", "Loaded " + tingkatanList.size() + " tingkatan items");
                    setupTingkatanSpinner();
                } else {
                    Log.e("EditKuis", "Failed to load tingkatan: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("EditKuis", "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("EditKuis", "Error reading error body: " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TingkatanResponse> call, Throwable t) {
                Log.e("EditKuis", "Failed to load tingkatan: " + t.getMessage(), t);
                Toast.makeText(EditKuisActivity.this, "Gagal memuat data tingkatan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadKelas(String token) {
        Log.d("EditKuis", "Loading kelas data...");
        apiService.getKelas("Bearer " + token).enqueue(new Callback<KelasResponse>() {
            @Override
            public void onResponse(Call<KelasResponse> call, Response<KelasResponse> response) {
                Log.d("EditKuis", "Kelas response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    kelasList = response.body().getData();
                    Log.d("EditKuis", "Loaded " + kelasList.size() + " kelas items");
                    setupKelasSpinner();
                } else {
                    Log.e("EditKuis", "Failed to load kelas: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("EditKuis", "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("EditKuis", "Error reading error body: " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<KelasResponse> call, Throwable t) {
                Log.e("EditKuis", "Failed to load kelas: " + t.getMessage(), t);
                Toast.makeText(EditKuisActivity.this, "Gagal memuat data kelas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPendidikan(String token) {
        Log.d("EditKuis", "Loading pendidikan data...");
        apiService.getPendidikan("Bearer " + token).enqueue(new Callback<PendidikanResponse>() {
            @Override
            public void onResponse(Call<PendidikanResponse> call, Response<PendidikanResponse> response) {
                Log.d("EditKuis", "Pendidikan response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    pendidikanList = response.body().getData();
                    Log.d("EditKuis", "Loaded " + pendidikanList.size() + " pendidikan items");
                    setupPendidikanSpinner();
                } else {
                    Log.e("EditKuis", "Failed to load pendidikan: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("EditKuis", "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("EditKuis", "Error reading error body: " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PendidikanResponse> call, Throwable t) {
                Log.e("EditKuis", "Failed to load pendidikan: " + t.getMessage(), t);
                Toast.makeText(EditKuisActivity.this, "Gagal memuat data pendidikan", Toast.LENGTH_SHORT).show();
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

        // Set selected item
        for (int i = 0; i < kategoriList.size(); i++) {
            if (kategoriList.get(i).getId() == selectedKategoriId) {
                spinnerKategori.setSelection(i + 1);
                break;
            }
        }

        spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedKategoriId = kategoriList.get(position - 1).getId();
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

        // Set selected item
        for (int i = 0; i < tingkatanList.size(); i++) {
            if (tingkatanList.get(i).getId() == selectedTingkatanId) {
                spinnerTingkatan.setSelection(i + 1);
                break;
            }
        }

        spinnerTingkatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedTingkatanId = tingkatanList.get(position - 1).getId();
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

        // Set selected item
        for (int i = 0; i < kelasList.size(); i++) {
            if (kelasList.get(i).getId() == selectedKelasId) {
                spinnerKelas.setSelection(i + 1);
                break;
            }
        }

        spinnerKelas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedKelasId = kelasList.get(position - 1).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupPendidikanSpinner() {
        Log.d("EditKuis", "Setting up pendidikan spinner with " + pendidikanList.size() + " items");

        List<String> pendidikanNames = new ArrayList<>();
        pendidikanNames.add("Pilih Pendidikan");
        for (Pendidikan pendidikan : pendidikanList) {
            pendidikanNames.add(pendidikan.getNama());
            Log.d("EditKuis", "Added pendidikan: " + pendidikan.getNama() + " (ID: " + pendidikan.getId() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pendidikanNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPendidikan.setAdapter(adapter);

        // Set selected item
        Log.d("EditKuis", "Looking for selected pendidikan ID: " + selectedPendidikanId);
        for (int i = 0; i < pendidikanList.size(); i++) {
            if (pendidikanList.get(i).getId() == selectedPendidikanId) {
                spinnerPendidikan.setSelection(i + 1);
                Log.d("EditKuis", "Set pendidikan selection to position: " + (i + 1));
                break;
            }
        }

        spinnerPendidikan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedPendidikanId = pendidikanList.get(position - 1).getId();
                    Log.d("EditKuis", "Selected pendidikan ID: " + selectedPendidikanId);
                } else {
                    selectedPendidikanId = 0;
                    Log.d("EditKuis", "No pendidikan selected");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("EditKuis", "Nothing selected in pendidikan spinner");
            }
        });
    }

    private void updateKuis() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Judul tidak boleh kosong");
            return;
        }

        if (description.isEmpty()) {
            etDescription.setError("Deskripsi tidak boleh kosong");
            return;
        }

        if (selectedKategoriId == 0 || selectedTingkatanId == 0 ||
            selectedKelasId == 0 || selectedPendidikanId == 0) {
            Toast.makeText(this, "Semua field harus dipilih", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        Kuis kuis = new Kuis();
        kuis.setTitle(title);
        kuis.setDescription(description);
        kuis.setKategoriId(selectedKategoriId);
        kuis.setTingkatanId(selectedTingkatanId);
        kuis.setKelasId(selectedKelasId);
        kuis.setPendidikanId(selectedPendidikanId);

        String token = getToken();
        apiService.updateKuis("Bearer " + token, kuisId, kuis).enqueue(new Callback<KuisResponse>() {
            @Override
            public void onResponse(Call<KuisResponse> call, Response<KuisResponse> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(EditKuisActivity.this, "Kuis berhasil diupdate", Toast.LENGTH_SHORT).show();

                    // Return updated data
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updated_title", title);
                    resultIntent.putExtra("updated_description", description);
                    resultIntent.putExtra("kuis_id", kuisId);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(EditKuisActivity.this, "Gagal mengupdate kuis: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("EditKuis", "Update failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<KuisResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(EditKuisActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("EditKuis", "Update error: " + t.getMessage());
            }
        });
    }
}


