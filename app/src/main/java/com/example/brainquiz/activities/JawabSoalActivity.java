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
import com.example.brainquiz.helpers.QuizNavigationHelper;
import com.example.brainquiz.helpers.QuizSubmissionHelper;

public class JawabSoalActivity extends AppCompatActivity implements QuizSubmissionHelper.QuizSubmissionListener {

    private TextView tvKuisTitle, tvSoalNumber, tvQuestion, tvProgress;
    private RadioGroup rgOptions;
    private RadioButton rbA, rbB, rbC, rbD;
    private Button btnPrevious, btnNext, btnSubmit;
    private ProgressBar progressBar;

    private ApiService apiService;
    private AuthManager authManager;
    private QuizNavigationHelper navigationHelper;
    private QuizSubmissionHelper submissionHelper;

    private List<Soal> soalList = new ArrayList<>();
    private List<String> jawabanUser = new ArrayList<>();
    private int kuisId;
    private String kuisTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jawab_soal);

        initViews();
        initRetrofit();
        initAuthManager();
        initHelpers();
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

    private void initHelpers() {
        navigationHelper = new QuizNavigationHelper(
            tvSoalNumber, tvQuestion, tvProgress, rgOptions,
            rbA, rbB, rbC, rbD, btnPrevious, btnNext, btnSubmit
        );

        submissionHelper = new QuizSubmissionHelper(this, apiService, authManager);
        submissionHelper.setListener(this);
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
        btnPrevious.setOnClickListener(v -> navigationHelper.previousSoal());
        btnNext.setOnClickListener(v -> navigationHelper.nextSoal());
        btnSubmit.setOnClickListener(v -> showSubmitConfirmation());

        // Save answer when option is selected
        rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            navigationHelper.saveCurrentAnswer();
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

                        // Setup navigation helper with data
                        navigationHelper.setSoalData(soalList, jawabanUser);
                        navigationHelper.displayCurrentSoal();
                        navigationHelper.updateNavigationButtons();

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

    private void showSubmitConfirmation() {
        navigationHelper.saveCurrentAnswer(); // Save current answer before checking
        submissionHelper.showSubmitConfirmation(jawabanUser, () -> submitJawaban());
    }

    private void submitJawaban() {
        submissionHelper.submitJawaban(soalList, jawabanUser);
    }

    // QuizSubmissionListener implementation
    @Override
    public void onSubmissionStart() {
        showLoading(true);
    }

    @Override
    public void onSubmissionComplete() {
        showLoading(false);
    }

    @Override
    public void onSubmissionSuccess(JawabanResponse response) {
        submissionHelper.showResultDialog(response);
    }

    @Override
    public void onSubmissionError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}



