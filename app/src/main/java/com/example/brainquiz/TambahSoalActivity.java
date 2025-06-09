package com.example.brainquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Soal;
import com.example.brainquiz.network.ApiService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TambahSoalActivity extends AppCompatActivity {

    private TextView tvKuisTitle;
    private EditText etQuestion, etOptionA, etOptionB, etOptionC, etOptionD;
    private RadioGroup rgCorrectAnswer;
    private RadioButton rbA, rbB, rbC, rbD;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    
    private int kuisId;
    private String kuisTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_soal);

        initViews();
        initRetrofit();
        getIntentData();
        setupClickListeners();
    }

    private void initViews() {
        tvKuisTitle = findViewById(R.id.tvKuisTitle);
        etQuestion = findViewById(R.id.etQuestion);
        etOptionA = findViewById(R.id.etOptionA);
        etOptionB = findViewById(R.id.etOptionB);
        etOptionC = findViewById(R.id.etOptionC);
        etOptionD = findViewById(R.id.etOptionD);
        rgCorrectAnswer = findViewById(R.id.rgCorrectAnswer);
        rbA = findViewById(R.id.rbA);
        rbB = findViewById(R.id.rbB);
        rbC = findViewById(R.id.rbC);
        rbD = findViewById(R.id.rbD);
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

    private void getIntentData() {
        Intent intent = getIntent();
        kuisId = intent.getIntExtra("kuis_id", 0);
        kuisTitle = intent.getStringExtra("kuis_title");
        
        if (kuisTitle != null) {
            tvKuisTitle.setText("Tambah Soal untuk: " + kuisTitle);
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveSoal());
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

    private void saveSoal() {
        String question = etQuestion.getText().toString().trim();
        String optionA = etOptionA.getText().toString().trim();
        String optionB = etOptionB.getText().toString().trim();
        String optionC = etOptionC.getText().toString().trim();
        String optionD = etOptionD.getText().toString().trim();

        // Validation
        if (question.isEmpty()) {
            etQuestion.setError("Pertanyaan tidak boleh kosong");
            etQuestion.requestFocus();
            return;
        }

        if (optionA.isEmpty()) {
            etOptionA.setError("Opsi A tidak boleh kosong");
            etOptionA.requestFocus();
            return;
        }

        if (optionB.isEmpty()) {
            etOptionB.setError("Opsi B tidak boleh kosong");
            etOptionB.requestFocus();
            return;
        }

        if (optionC.isEmpty()) {
            etOptionC.setError("Opsi C tidak boleh kosong");
            etOptionC.requestFocus();
            return;
        }

        if (optionD.isEmpty()) {
            etOptionD.setError("Opsi D tidak boleh kosong");
            etOptionD.requestFocus();
            return;
        }

        int selectedId = rgCorrectAnswer.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Pilih jawaban yang benar", Toast.LENGTH_SHORT).show();
            return;
        }

        String correctAnswer = "";
        if (selectedId == R.id.rbA) correctAnswer = "A";
        else if (selectedId == R.id.rbB) correctAnswer = "B";
        else if (selectedId == R.id.rbC) correctAnswer = "C";
        else if (selectedId == R.id.rbD) correctAnswer = "D";

        showLoading(true);

        // Create Soal object
        Soal soal = new Soal();
        soal.setQuestion(question);
        soal.setCorrectAnswer(correctAnswer);
        soal.setKuisId(kuisId);

        // Create options map
        Map<String, String> options = new HashMap<>();
        options.put("A", optionA);
        options.put("B", optionB);
        options.put("C", optionC);
        options.put("D", optionD);
        soal.setOptionsJson(options);

        String token = getToken();
        if (token.isEmpty()) {
            showLoading(false);
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("TambahSoal", "Saving soal for kuis ID: " + kuisId);
        Log.d("TambahSoal", "Question: " + question);
        Log.d("TambahSoal", "Correct Answer: " + correctAnswer);

        apiService.addSoal("Bearer " + token, soal).enqueue(new Callback<SoalResponse>() {
            @Override
            public void onResponse(Call<SoalResponse> call, Response<SoalResponse> response) {
                showLoading(false);
                
                Log.d("TambahSoal", "Response Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    SoalResponse soalResponse = response.body();
                    if (soalResponse.isSuccess()) {
                        Toast.makeText(TambahSoalActivity.this, "Soal berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(TambahSoalActivity.this, "Gagal menambahkan soal: " + soalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TambahSoal", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("TambahSoal", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("TambahSoal", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(TambahSoalActivity.this, "Gagal menambahkan soal: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SoalResponse> call, Throwable t) {
                showLoading(false);
                Log.e("TambahSoal", "onFailure: " + t.getMessage(), t);
                Toast.makeText(TambahSoalActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
