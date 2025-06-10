package com.example.brainquiz.activities;
import com.example.brainquiz.R;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.HasilKuis;
import com.example.brainquiz.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.brainquiz.models.HasilKuisResponse;
import com.example.brainquiz.utils.AuthManager;
import com.example.brainquiz.utils.NetworkHelper;
import com.example.brainquiz.utils.ApiConstants;
import com.example.brainquiz.utils.DateTimeHelper;
import com.example.brainquiz.utils.GsonHelper;
import com.example.brainquiz.utils.JsonTestHelper;

public class HasilKuisActivity extends AppCompatActivity {

    private EditText etSearch;
    private GridLayout gridHasil;

    private ApiService apiService;
    private AuthManager authManager;

    private List<HasilKuis> hasilKuisList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_kuis);

        initViews();
        initRetrofit();
        initAuthManager();
        setupSearchListener();
        setupNavigation();

        // Check authentication before proceeding
        if (!authManager.requireAuthentication(this)) {
            return;
        }

        // Check network connectivity
        if (!NetworkHelper.checkNetworkAndShowMessage(this)) {
            return;
        }

        fetchMyResults();

        // Test JSON parsing for HasilKuis response (for development only)
        // JsonTestHelper.testHasilKuisResponse();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        gridHasil = findViewById(R.id.gridHasil);
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

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterHasil(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }



    private void fetchMyResults() {
        if (!authManager.hasValidToken()) {
            Toast.makeText(this, ApiConstants.ERROR_UNAUTHORIZED, Toast.LENGTH_SHORT).show();
            authManager.logoutAndRedirect(this);
            return;
        }

        Log.d("HasilKuis", "Fetching my results using single endpoint...");

        apiService.getMyResults(authManager.getAuthorizationHeader()).enqueue(new Callback<HasilKuisResponse>() {
            @Override
            public void onResponse(Call<HasilKuisResponse> call, Response<HasilKuisResponse> response) {
                Log.d("HasilKuis", "My results response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    HasilKuisResponse hasilResponse = response.body();
                    if (hasilResponse.isSuccess()) {
                        hasilKuisList = hasilResponse.getData();
                        Log.d("HasilKuis", "Loaded " + hasilKuisList.size() + " hasil kuis");

                        // Langsung tampilkan hasil tanpa perlu multiple API calls
                        displayHasil(hasilKuisList);

                        if (hasilKuisList.isEmpty()) {
                            Toast.makeText(HasilKuisActivity.this, "Belum ada hasil kuis tersedia", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(HasilKuisActivity.this, "Gagal memuat hasil kuis: " + hasilResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("HasilKuis", "Error " + response.code());
                    if (response.code() == 404) {
                        // 404 berarti user belum pernah mengerjakan kuis
                        hasilKuisList.clear();
                        displayHasil(hasilKuisList);
                        Toast.makeText(HasilKuisActivity.this, "Belum ada hasil kuis tersedia", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HasilKuisActivity.this, "Gagal mengambil data hasil kuis: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<HasilKuisResponse> call, Throwable t) {
                Log.e("HasilKuis", "onFailure: " + t.getMessage(), t);
                Toast.makeText(HasilKuisActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void displayHasil(List<HasilKuis> hasilListToShow) {
        gridHasil.removeAllViews();
        gridHasil.setColumnCount(1);

        final float density = getResources().getDisplayMetrics().density;

        if (hasilListToShow.isEmpty()) {
            // Show empty state
            LinearLayout emptyLayout = new LinearLayout(this);
            emptyLayout.setOrientation(LinearLayout.VERTICAL);
            emptyLayout.setGravity(Gravity.CENTER);
            emptyLayout.setPadding(32, 64, 32, 64);

            ImageView emptyIcon = new ImageView(this);
            emptyIcon.setImageResource(R.drawable.question);
            emptyIcon.setColorFilter(Color.GRAY);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                    (int) (80 * density), (int) (80 * density)
            );
            iconParams.gravity = Gravity.CENTER;
            iconParams.bottomMargin = (int) (16 * density);
            emptyIcon.setLayoutParams(iconParams);
            emptyLayout.addView(emptyIcon);

            TextView emptyText = new TextView(this);
            emptyText.setText("Belum ada hasil kuis.\nMulai jawab kuis untuk melihat hasil.");
            emptyText.setTextSize(16);
            emptyText.setTextColor(Color.GRAY);
            emptyText.setGravity(Gravity.CENTER);
            emptyLayout.addView(emptyText);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.MATCH_PARENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            emptyLayout.setLayoutParams(params);

            gridHasil.addView(emptyLayout);
            return;
        }

        for (HasilKuis hasil : hasilListToShow) {
            if (hasil == null) continue;

            // Container Card
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(
                    (int) (16 * density),
                    (int) (16 * density),
                    (int) (16 * density),
                    (int) (16 * density)
            );
            card.setBackgroundResource(R.drawable.bg_tingkatan_card);

            GridLayout.LayoutParams cardParams = new GridLayout.LayoutParams();
            cardParams.width = GridLayout.LayoutParams.MATCH_PARENT;
            cardParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            cardParams.setMargins(
                    (int) (8 * density),
                    (int) (8 * density),
                    (int) (8 * density),
                    (int) (8 * density)
            );
            card.setLayoutParams(cardParams);

            // Header with quiz title and date
            LinearLayout headerLayout = new LinearLayout(this);
            headerLayout.setOrientation(LinearLayout.HORIZONTAL);
            headerLayout.setGravity(Gravity.CENTER_VERTICAL);

            // Quiz title
            TextView tvTitle = new TextView(this);
            tvTitle.setText(hasil.getKuisTitle());
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            tvTitle.setTextColor(Color.WHITE);
            tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);

            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            );
            tvTitle.setLayoutParams(titleParams);
            headerLayout.addView(tvTitle);

            // Date
            TextView tvDate = new TextView(this);
            String dateStr = formatDate(hasil.getCompletedAt());
            tvDate.setText(dateStr);
            tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tvDate.setTextColor(Color.parseColor("#E0E0E0"));
            headerLayout.addView(tvDate);

            card.addView(headerLayout);

            // Score section
            LinearLayout scoreLayout = new LinearLayout(this);
            scoreLayout.setOrientation(LinearLayout.HORIZONTAL);
            scoreLayout.setPadding(0, (int) (12 * density), 0, (int) (8 * density));

            // Score
            TextView tvScore = new TextView(this);
            tvScore.setText("Skor: " + hasil.getScore());
            tvScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tvScore.setTextColor(Color.WHITE);
            tvScore.setTypeface(null, android.graphics.Typeface.BOLD);

            LinearLayout.LayoutParams scoreParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            );
            tvScore.setLayoutParams(scoreParams);
            scoreLayout.addView(tvScore);

            // Grade
            TextView tvGrade = new TextView(this);
            tvGrade.setText("Nilai: " + hasil.getGrade());
            tvGrade.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tvGrade.setTextColor(Color.WHITE);
            tvGrade.setTypeface(null, android.graphics.Typeface.BOLD);
            scoreLayout.addView(tvGrade);

            card.addView(scoreLayout);

            // Details section
            TextView tvDetails = new TextView(this);
            String details;
            if (hasil.getTotalQuestions() > 0) {
                details = String.format("Benar: %d dari %d soal (%.1f%%)",
                        hasil.getCorrectAnswers(),
                        hasil.getTotalQuestions(),
                        hasil.getPercentage());
            } else {
                // Jika total_questions tidak ada, tampilkan info yang tersedia
                details = String.format("Jawaban benar: %d | Persentase: %.1f%%",
                        hasil.getCorrectAnswers(),
                        hasil.getPercentage());
            }
            tvDetails.setText(details);
            tvDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvDetails.setTextColor(Color.parseColor("#E0E0E0"));
            tvDetails.setPadding(0, 0, 0, (int) (8 * density));
            card.addView(tvDetails);

            // Status
            TextView tvStatus = new TextView(this);
            tvStatus.setText(hasil.getStatus());
            tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvStatus.setTextColor(Color.WHITE);
            tvStatus.setTypeface(null, android.graphics.Typeface.BOLD);
            card.addView(tvStatus);

            gridHasil.addView(card);
        }
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "Tanggal tidak diketahui";
        }

        try {
            // Use DateTimeHelper for consistent date formatting
            return DateTimeHelper.formatTimestamp(
                java.sql.Timestamp.valueOf(dateString.replace("T", " ")).getTime(),
                "dd MMM yyyy, HH:mm"
            );
        } catch (Exception e) {
            Log.e("HasilKuis", "Error parsing date: " + e.getMessage());
            return dateString; // Return original if parsing fails
        }
    }

    private int getGradeColor(String grade) {
        switch (grade) {
            case "A":
                return Color.parseColor("#4CAF50"); // Green
            case "B":
                return Color.parseColor("#8BC34A"); // Light Green
            case "C":
                return Color.parseColor("#FF9800"); // Orange
            case "D":
                return Color.parseColor("#FF5722"); // Deep Orange
            case "E":
                return Color.parseColor("#F44336"); // Red
            default:
                return Color.parseColor("#666666"); // Gray
        }
    }

    private void filterHasil(String query) {
        List<HasilKuis> filteredList = new ArrayList<>();
        for (HasilKuis hasil : hasilKuisList) {
            if (hasil == null || hasil.getKuisTitle() == null) continue;
            if (hasil.getKuisTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(hasil);
            }
        }
        displayHasil(filteredList);
    }

    private void setupNavigation() {
        // Initialize bottom navigation
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navKuis = findViewById(R.id.nav_kuis);
        LinearLayout navJawabSoal = findViewById(R.id.nav_jawab_soal);
        LinearLayout navHasil = findViewById(R.id.nav_hasil);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.brainquiz.activities.HomeActivity.class));
            finish();
        });

        navKuis.setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.brainquiz.activities.KuisActivity.class));
            finish();
        });

        navJawabSoal.setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.brainquiz.activities.JawabSoalMainActivity.class));
            finish();
        });

        navHasil.setOnClickListener(v -> {
            // Already in HasilKuisActivity, do nothing
            showToast("Anda sudah berada di Hasil Kuis");
        });
    }

    private void showToast(String pesan) {
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
    }
}


