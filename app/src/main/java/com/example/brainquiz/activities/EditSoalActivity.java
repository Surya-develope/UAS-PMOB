package com.example.brainquiz.activities;
import com.example.brainquiz.R;

import android.content.Intent;
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
import com.example.brainquiz.models.SoalResponse;
import com.example.brainquiz.utils.GsonHelper;
import com.example.brainquiz.utils.AuthManager;
import com.example.brainquiz.utils.NetworkHelper;
import com.example.brainquiz.utils.ApiConstants;
import com.example.brainquiz.utils.JsonTestHelper;

public class EditSoalActivity extends AppCompatActivity {

    private TextView tvTitle;
    private EditText etQuestion, etOptionA, etOptionB, etOptionC, etOptionD;
    private RadioGroup rgCorrectAnswer;
    private RadioButton rbA, rbB, rbC, rbD;
    private Button btnUpdate, btnCancel;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private AuthManager authManager;

    private int soalId;
    private int kuisId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_soal);

        initViews();
        initRetrofit();
        initAuthManager();

        // Check authentication before proceeding
        if (!authManager.requireAuthentication(this)) {
            return;
        }

        getIntentData();
        setupClickListeners();

        // Test JSON parsing for EditSoal response (for development only)
        // JsonTestHelper.testEditSoalResponse();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
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
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://brainquiz0.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create(GsonHelper.getGson()))
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    private void initAuthManager() {
        authManager = AuthManager.getInstance(this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        soalId = intent.getIntExtra("soal_id", 0);
        kuisId = intent.getIntExtra("kuis_id", 0);
        String question = intent.getStringExtra("question");
        String optionA = intent.getStringExtra("option_a");
        String optionB = intent.getStringExtra("option_b");
        String optionC = intent.getStringExtra("option_c");
        String optionD = intent.getStringExtra("option_d");
        String correctAnswer = intent.getStringExtra("correct_answer");

        // Set data to views
        tvTitle.setText("Edit Soal");
        etQuestion.setText(question);
        etOptionA.setText(optionA);
        etOptionB.setText(optionB);
        etOptionC.setText(optionC);
        etOptionD.setText(optionD);

        // Set correct answer radio button
        if ("A".equals(correctAnswer)) {
            rbA.setChecked(true);
        } else if ("B".equals(correctAnswer)) {
            rbB.setChecked(true);
        } else if ("C".equals(correctAnswer)) {
            rbC.setChecked(true);
        } else if ("D".equals(correctAnswer)) {
            rbD.setChecked(true);
        }
    }

    private void setupClickListeners() {
        btnUpdate.setOnClickListener(v -> updateSoal());
        btnCancel.setOnClickListener(v -> finish());
    }



    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnUpdate.setEnabled(!show);
        btnCancel.setEnabled(!show);
    }

    private void updateSoal() {
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

        if (!authManager.hasValidToken()) {
            showLoading(false);
            Toast.makeText(this, ApiConstants.ERROR_UNAUTHORIZED, Toast.LENGTH_SHORT).show();
            authManager.logoutAndRedirect(this);
            return;
        }

        Log.d("EditSoal", "Updating soal ID: " + soalId);
        Log.d("EditSoal", "Question: " + question);
        Log.d("EditSoal", "Correct Answer: " + correctAnswer);

        apiService.updateSoal(authManager.getAuthorizationHeader(), soalId, soal).enqueue(new Callback<SoalResponse>() {
            @Override
            public void onResponse(Call<SoalResponse> call, Response<SoalResponse> response) {
                showLoading(false);
                
                Log.d("EditSoal", "Response Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    SoalResponse soalResponse = response.body();
                    if (soalResponse.isSuccess()) {
                        Toast.makeText(EditSoalActivity.this, "Soal berhasil diupdate", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(EditSoalActivity.this, "Gagal mengupdate soal: " + soalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("EditSoal", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("EditSoal", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("EditSoal", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(EditSoalActivity.this, "Gagal mengupdate soal: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SoalResponse> call, Throwable t) {
                showLoading(false);
                Log.e("EditSoal", "onFailure: " + t.getMessage(), t);
                Toast.makeText(EditSoalActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


