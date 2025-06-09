package com.example.brainquiz.activities;
import com.example.brainquiz.R;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Jawaban;
import com.example.brainquiz.filter.Soal;
import com.example.brainquiz.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.brainquiz.utils.AuthManager;
import com.example.brainquiz.utils.NetworkHelper;
import com.example.brainquiz.utils.ApiConstants;
import com.example.brainquiz.utils.GsonHelper;
import com.example.brainquiz.models.SoalResponse;
import com.example.brainquiz.models.JawabanResponse;

public class JawabSoalActivity extends AppCompatActivity {

    private TextView tvKuisTitle, tvSoalNumber, tvQuestion, tvProgress;
    private RadioGroup rgOptions;
    private RadioButton rbA, rbB, rbC, rbD;
    private Button btnPrevious, btnNext, btnSubmit;
    private ProgressBar progressBar;

    private ApiService apiService;
    private AuthManager authManager;

    private List<Soal> soalList = new ArrayList<>();
    private List<String> jawabanUser = new ArrayList<>();
    private int currentSoalIndex = 0;
    private int kuisId;
    private String kuisTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jawab_soal);

        initViews();
        initRetrofit();
        initAuthManager();
        getIntentData();
        setupClickListeners();

        // Check authentication before proceeding
        if (!authManager.requireAuthentication(this)) {
            return;
        }

        // Check network connectivity
        if (!NetworkHelper.checkNetworkAndShowMessage(this)) {
            return;
        }

        fetchSoal();
    }

    private void initViews() {
        tvKuisTitle = findViewById(R.id.tvKuisTitle);
        tvSoalNumber = findViewById(R.id.tvSoalNumber);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgress = findViewById(R.id.tvProgress);
        rgOptions = findViewById(R.id.rgOptions);
        rbA = findViewById(R.id.rbA);
        rbB = findViewById(R.id.rbB);
        rbC = findViewById(R.id.rbC);
        rbD = findViewById(R.id.rbD);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnSubmit = findViewById(R.id.btnSubmit);
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
        kuisId = intent.getIntExtra("kuis_id", 0);
        kuisTitle = intent.getStringExtra("kuis_title");

        if (kuisTitle != null) {
            tvKuisTitle.setText(kuisTitle);
        }

        Log.d("JawabSoal", "Kuis ID: " + kuisId + ", Title: " + kuisTitle);
    }

    private void setupClickListeners() {
        btnPrevious.setOnClickListener(v -> previousSoal());
        btnNext.setOnClickListener(v -> nextSoal());
        btnSubmit.setOnClickListener(v -> showSubmitConfirmation());

        // Save answer when option is selected
        rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            saveCurrentAnswer();
        });
    }



    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnPrevious.setEnabled(!show);
        btnNext.setEnabled(!show);
        btnSubmit.setEnabled(!show);
    }

    private void fetchSoal() {
        if (!authManager.hasValidToken()) {
            Toast.makeText(this, ApiConstants.ERROR_UNAUTHORIZED, Toast.LENGTH_SHORT).show();
            authManager.logoutAndRedirect(this);
            return;
        }

        showLoading(true);
        Log.d("JawabSoal", "Fetching soal for kuis ID: " + kuisId);

        apiService.getSoalByKuisId(authManager.getAuthorizationHeader(), kuisId).enqueue(new Callback<SoalResponse>() {
            @Override
            public void onResponse(Call<SoalResponse> call, Response<SoalResponse> response) {
                showLoading(false);
                Log.d("JawabSoal", "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    SoalResponse soalResponse = response.body();
                    if (soalResponse.isSuccess()) {
                        soalList = soalResponse.getData();
                        Log.d("JawabSoal", "Loaded " + soalList.size() + " soal");

                        if (soalList.isEmpty()) {
                            Toast.makeText(JawabSoalActivity.this, "Tidak ada soal dalam kuis ini", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }

                        // Initialize jawaban list
                        jawabanUser = new ArrayList<>();
                        for (int i = 0; i < soalList.size(); i++) {
                            jawabanUser.add(""); // Empty answer initially
                        }

                        displayCurrentSoal();
                        updateNavigationButtons();

                    } else {
                        Toast.makeText(JawabSoalActivity.this, "Gagal memuat soal: " + soalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Log.e("JawabSoal", "Error " + response.code());
                    Toast.makeText(JawabSoalActivity.this, "Gagal mengambil data soal: " + response.code(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<SoalResponse> call, Throwable t) {
                showLoading(false);
                Log.e("JawabSoal", "onFailure: " + t.getMessage(), t);
                Toast.makeText(JawabSoalActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayCurrentSoal() {
        if (soalList.isEmpty() || currentSoalIndex >= soalList.size()) return;

        Soal currentSoal = soalList.get(currentSoalIndex);

        // Update UI
        tvSoalNumber.setText("Soal " + (currentSoalIndex + 1));
        tvQuestion.setText(currentSoal.getQuestion());
        tvProgress.setText((currentSoalIndex + 1) + " dari " + soalList.size());

        // Set options
        rbA.setText("A. " + currentSoal.getOptionA());
        rbB.setText("B. " + currentSoal.getOptionB());
        rbC.setText("C. " + currentSoal.getOptionC());
        rbD.setText("D. " + currentSoal.getOptionD());

        // Clear selection first
        rgOptions.clearCheck();

        // Set previous answer if exists
        String previousAnswer = jawabanUser.get(currentSoalIndex);
        if (!previousAnswer.isEmpty()) {
            switch (previousAnswer) {
                case "A":
                    rbA.setChecked(true);
                    break;
                case "B":
                    rbB.setChecked(true);
                    break;
                case "C":
                    rbC.setChecked(true);
                    break;
                case "D":
                    rbD.setChecked(true);
                    break;
            }
        }

        Log.d("JawabSoal", "Displaying soal " + (currentSoalIndex + 1) + ": " + currentSoal.getQuestion());
    }

    private void saveCurrentAnswer() {
        if (currentSoalIndex >= jawabanUser.size()) return;

        int selectedId = rgOptions.getCheckedRadioButtonId();
        String answer = "";

        if (selectedId == R.id.rbA) answer = "A";
        else if (selectedId == R.id.rbB) answer = "B";
        else if (selectedId == R.id.rbC) answer = "C";
        else if (selectedId == R.id.rbD) answer = "D";

        jawabanUser.set(currentSoalIndex, answer);
        Log.d("JawabSoal", "Saved answer for soal " + (currentSoalIndex + 1) + ": " + answer);
    }

    private void previousSoal() {
        if (currentSoalIndex > 0) {
            saveCurrentAnswer();
            currentSoalIndex--;
            displayCurrentSoal();
            updateNavigationButtons();
        }
    }

    private void nextSoal() {
        if (currentSoalIndex < soalList.size() - 1) {
            saveCurrentAnswer();
            currentSoalIndex++;
            displayCurrentSoal();
            updateNavigationButtons();
        }
    }

    private void updateNavigationButtons() {
        btnPrevious.setEnabled(currentSoalIndex > 0);
        btnNext.setEnabled(currentSoalIndex < soalList.size() - 1);

        // Show submit button on last question
        if (currentSoalIndex == soalList.size() - 1) {
            btnNext.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
        }
    }

    private void showSubmitConfirmation() {
        saveCurrentAnswer(); // Save current answer before checking

        // Check for unanswered questions
        int unansweredCount = 0;
        for (String answer : jawabanUser) {
            if (answer.isEmpty()) {
                unansweredCount++;
            }
        }

        String message = "Apakah Anda yakin ingin mengirim jawaban?";
        if (unansweredCount > 0) {
            message += "\n\nPeringatan: " + unansweredCount + " soal belum dijawab.";
        }

        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Submit")
                .setMessage(message)
                .setPositiveButton("Ya, Kirim", (dialog, which) -> submitJawaban())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void submitJawaban() {
        if (!authManager.hasValidToken()) {
            Toast.makeText(this, ApiConstants.ERROR_UNAUTHORIZED, Toast.LENGTH_SHORT).show();
            authManager.logoutAndRedirect(this);
            return;
        }

        int userId = authManager.getCurrentUserId();

        showLoading(true);

        // Create jawaban list
        List<Jawaban> jawabanList = new ArrayList<>();
        for (int i = 0; i < soalList.size(); i++) {
            Soal soal = soalList.get(i);
            String answer = jawabanUser.get(i);

            // Only add answered questions
            if (!answer.isEmpty()) {
                Jawaban jawaban = new Jawaban();
                jawaban.setSoalId(soal.getId());
                jawaban.setAnswer(answer);
                jawaban.setUserId(userId);
                jawabanList.add(jawaban);
            }
        }

        Log.d("JawabSoal", "Submitting " + jawabanList.size() + " answers out of " + soalList.size() + " questions");

        apiService.submitJawaban(authManager.getAuthorizationHeader(), jawabanList).enqueue(new Callback<JawabanResponse>() {
            @Override
            public void onResponse(Call<JawabanResponse> call, Response<JawabanResponse> response) {
                showLoading(false);
                Log.d("JawabSoal", "Submit response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    JawabanResponse jawabanResponse = response.body();
                    if (jawabanResponse.isSuccess()) {
                        showResultDialog(jawabanResponse);
                    } else {
                        Toast.makeText(JawabSoalActivity.this, "Gagal mengirim jawaban: " + jawabanResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("JawabSoal", "Submit error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("JawabSoal", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("JawabSoal", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(JawabSoalActivity.this, "Gagal mengirim jawaban: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JawabanResponse> call, Throwable t) {
                showLoading(false);
                Log.e("JawabSoal", "Submit failure: " + t.getMessage(), t);
                Toast.makeText(JawabSoalActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResultDialog(JawabanResponse response) {
        String resultMessage = "Jawaban berhasil dikirim!\n\n";

        if (response.getScore() != null) {
            resultMessage += "Skor: " + response.getScore();
        }

        if (response.getCorrectAnswers() != null && response.getTotalQuestions() != null) {
            resultMessage += "\nBenar: " + response.getCorrectAnswers() + " dari " + response.getTotalQuestions();
        }

        new AlertDialog.Builder(this)
                .setTitle("Hasil Kuis")
                .setMessage(resultMessage)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Go back to home or quiz list
                    Intent intent = new Intent(JawabSoalActivity.this, com.example.brainquiz.activities.HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}



