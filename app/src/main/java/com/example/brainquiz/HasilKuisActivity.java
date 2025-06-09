package com.example.brainquiz;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.brainquiz.filter.Kuis;
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

public class HasilKuisActivity extends AppCompatActivity {

    private EditText etSearch;
    private GridLayout gridHasil;

    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";

    private List<Kuis> kuisList = new ArrayList<>();
    private List<HasilKuis> hasilKuisList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_kuis);

        initViews();
        initRetrofit();
        setupSearchListener();
        setupNavigation();
        fetchKuisList();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        gridHasil = findViewById(R.id.gridHasil);
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
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

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private int getUserId() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        int userId = sp.getInt("user_id", 0);
        if (userId == 0) {
            userId = 1; // Fallback
        }
        return userId;
    }

    private void fetchKuisList() {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("HasilKuis", "Fetching kuis list...");

        apiService.getKuis("Bearer " + token).enqueue(new Callback<KuisResponse>() {
            @Override
            public void onResponse(Call<KuisResponse> call, Response<KuisResponse> response) {
                Log.d("HasilKuis", "Kuis response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    KuisResponse kuisResponse = response.body();
                    if (kuisResponse.isSuccess()) {
                        kuisList = kuisResponse.getData();
                        Log.d("HasilKuis", "Loaded " + kuisList.size() + " kuis");

                        // Fetch hasil for each kuis
                        fetchAllHasilKuis();

                    } else {
                        Toast.makeText(HasilKuisActivity.this, "Gagal memuat kuis: " + kuisResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("HasilKuis", "Error " + response.code());
                    Toast.makeText(HasilKuisActivity.this, "Gagal mengambil data kuis: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KuisResponse> call, Throwable t) {
                Log.e("HasilKuis", "onFailure: " + t.getMessage(), t);
                Toast.makeText(HasilKuisActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAllHasilKuis() {
        hasilKuisList.clear();
        int userId = getUserId();
        String token = getToken();

        Log.d("HasilKuis", "=== DEBUGGING HASIL KUIS ===");
        Log.d("HasilKuis", "User ID: " + userId);
        Log.d("HasilKuis", "Token: " + (token.isEmpty() ? "EMPTY" : token.substring(0, Math.min(20, token.length())) + "..."));
        Log.d("HasilKuis", "Total kuis to check: " + kuisList.size());

        if (kuisList.isEmpty()) {
            Log.w("HasilKuis", "No kuis found, displaying empty results");
            displayHasil(hasilKuisList);
            return;
        }

        // Counter untuk tracking completed requests
        final int[] completedRequests = {0};
        final int totalRequests = kuisList.size();

        for (Kuis kuis : kuisList) {
            Log.d("HasilKuis", "Checking hasil for kuis ID: " + kuis.getId() + " (" + kuis.getTitle() + ")");

            try {
                apiService.getHasilKuis("Bearer " + token, userId, kuis.getId()).enqueue(new Callback<HasilKuisResponse>() {
                    @Override
                    public void onResponse(Call<HasilKuisResponse> call, Response<HasilKuisResponse> response) {
                        try {
                            completedRequests[0]++;

                            Log.d("HasilKuis", "Response for kuis '" + kuis.getTitle() + "': " + response.code());

                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    try {
                                        HasilKuisResponse hasilResponse = response.body();
                                        Log.d("HasilKuis", "Response success: " + hasilResponse.isSuccess());
                                        Log.d("HasilKuis", "Response message: " + hasilResponse.getMessage());

                                        if (hasilResponse.getData() != null) {
                                            Log.d("HasilKuis", "Data size: " + hasilResponse.getData().size());

                                            if (hasilResponse.isSuccess() && !hasilResponse.getData().isEmpty()) {
                                                // Add hasil kuis to list
                                                hasilKuisList.addAll(hasilResponse.getData());
                                                Log.d("HasilKuis", "✅ Added " + hasilResponse.getData().size() + " hasil for kuis: " + kuis.getTitle());

                                                // Log detail hasil
                                                for (HasilKuis hasil : hasilResponse.getData()) {
                                                    Log.d("HasilKuis", "  - Hasil ID: " + hasil.getId() + ", Score: " + hasil.getScore() + ", Grade: " + hasil.getGrade());
                                                }
                                            } else {
                                                Log.w("HasilKuis", "❌ No hasil data for kuis: " + kuis.getTitle());
                                            }
                                        } else {
                                            Log.w("HasilKuis", "❌ Response data is null for kuis: " + kuis.getTitle());
                                        }
                                    } catch (Exception e) {
                                        Log.e("HasilKuis", "❌ Error parsing response body for kuis '" + kuis.getTitle() + "': " + e.getMessage(), e);
                                    }
                                } else {
                                    Log.w("HasilKuis", "❌ Response body is null for kuis: " + kuis.getTitle());
                                }
                            } else {
                                Log.e("HasilKuis", "❌ Error response for kuis '" + kuis.getTitle() + "': " + response.code());

                                // Handle specific error codes
                                String errorMessage = "";
                                switch (response.code()) {
                                    case 500:
                                        errorMessage = "Server Error (500) - Ada masalah di server";
                                        Log.e("HasilKuis", "Server Error 500 for kuis ID: " + kuis.getId() + " with user ID: " + userId);
                                        break;
                                    case 404:
                                        errorMessage = "Not Found (404) - Endpoint atau data tidak ditemukan";
                                        Log.e("HasilKuis", "404 Error - URL: /hasil-kuis/" + userId + "/" + kuis.getId());
                                        break;
                                    case 401:
                                        errorMessage = "Unauthorized (401) - Token tidak valid";
                                        break;
                                    case 403:
                                        errorMessage = "Forbidden (403) - Akses ditolak";
                                        break;
                                    default:
                                        errorMessage = "HTTP Error " + response.code();
                                        break;
                                }

                                try {
                                    if (response.errorBody() != null) {
                                        String errorBody = response.errorBody().string();
                                        Log.e("HasilKuis", "Error body: " + errorBody);

                                        // Show detailed error for 500
                                        if (response.code() == 500) {
                                            Log.e("HasilKuis", "=== SERVER ERROR 500 DETAILS ===");
                                            Log.e("HasilKuis", "Kuis ID: " + kuis.getId());
                                            Log.e("HasilKuis", "User ID: " + userId);
                                            Log.e("HasilKuis", "Full URL: /hasil-kuis/" + userId + "/" + kuis.getId());
                                            Log.e("HasilKuis", "Error Response: " + errorBody);
                                            Log.e("HasilKuis", "================================");
                                        }
                                    } else {
                                        Log.e("HasilKuis", "No error body available for " + response.code());
                                    }
                                } catch (Exception e) {
                                    Log.e("HasilKuis", "Error reading error body: " + e.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            Log.e("HasilKuis", "❌ Unexpected error in onResponse for kuis '" + kuis.getTitle() + "': " + e.getMessage(), e);
                        } finally {
                            // If all requests completed, display results
                            if (completedRequests[0] == totalRequests) {
                                Log.d("HasilKuis", "=== ALL REQUESTS COMPLETED ===");
                                Log.d("HasilKuis", "Total hasil found: " + hasilKuisList.size());
                                runOnUiThread(() -> displayHasil(hasilKuisList));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<HasilKuisResponse> call, Throwable t) {
                        try {
                            completedRequests[0]++;
                            Log.e("HasilKuis", "❌ Network failure for kuis '" + kuis.getTitle() + "': " + t.getClass().getSimpleName() + " - " + t.getMessage(), t);

                            // Log specific error types
                            if (t instanceof java.lang.IllegalStateException) {
                                Log.e("HasilKuis", "IllegalStateException details: " + t.getMessage());
                                Log.e("HasilKuis", "This usually indicates a problem with response parsing or Retrofit configuration");
                            }
                        } catch (Exception e) {
                            Log.e("HasilKuis", "❌ Error in onFailure handler: " + e.getMessage(), e);
                        } finally {
                            // If all requests completed, display results
                            if (completedRequests[0] == totalRequests) {
                                Log.d("HasilKuis", "=== ALL REQUESTS COMPLETED ===");
                                Log.d("HasilKuis", "Total hasil found: " + hasilKuisList.size());
                                runOnUiThread(() -> displayHasil(hasilKuisList));
                            }
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("HasilKuis", "❌ Error creating API call for kuis '" + kuis.getTitle() + "': " + e.getMessage(), e);
                completedRequests[0]++;

                // If all requests completed, display results
                if (completedRequests[0] == totalRequests) {
                    Log.d("HasilKuis", "=== ALL REQUESTS COMPLETED ===");
                    Log.d("HasilKuis", "Total hasil found: " + hasilKuisList.size());
                    runOnUiThread(() -> displayHasil(hasilKuisList));
                }
            }
        }
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

            // Debug buttons container
            LinearLayout debugContainer = new LinearLayout(this);
            debugContainer.setOrientation(LinearLayout.HORIZONTAL);
            debugContainer.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams debugContainerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            debugContainerParams.topMargin = (int) (16 * density);
            debugContainer.setLayoutParams(debugContainerParams);

            // Debug API button
            android.widget.Button debugButton = new android.widget.Button(this);
            debugButton.setText("Debug API");
            debugButton.setBackgroundColor(Color.parseColor("#FF9800"));
            debugButton.setTextColor(Color.WHITE);
            LinearLayout.LayoutParams debugParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            debugParams.rightMargin = (int) (8 * density);
            debugButton.setLayoutParams(debugParams);
            debugButton.setOnClickListener(v -> debugApiCall());
            debugContainer.addView(debugButton);

            // Show Info button
            android.widget.Button infoButton = new android.widget.Button(this);
            infoButton.setText("Show Info");
            infoButton.setBackgroundColor(Color.parseColor("#2196F3"));
            infoButton.setTextColor(Color.WHITE);
            LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            infoParams.leftMargin = (int) (8 * density);
            infoButton.setLayoutParams(infoParams);
            infoButton.setOnClickListener(v -> showDebugInfo());
            debugContainer.addView(infoButton);

            emptyLayout.addView(debugContainer);

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
            card.setBackgroundResource(R.drawable.bg_card_white);

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
            tvTitle.setTextColor(Color.parseColor("#333333"));
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
            tvDate.setTextColor(Color.parseColor("#666666"));
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
            tvScore.setTextColor(Color.parseColor("#2196F3"));
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
            tvGrade.setTextColor(getGradeColor(hasil.getGrade()));
            tvGrade.setTypeface(null, android.graphics.Typeface.BOLD);
            scoreLayout.addView(tvGrade);

            card.addView(scoreLayout);

            // Details section
            TextView tvDetails = new TextView(this);
            String details = String.format("Benar: %d dari %d soal (%.1f%%)",
                    hasil.getCorrectAnswers(),
                    hasil.getTotalQuestions(),
                    hasil.getPercentage());
            tvDetails.setText(details);
            tvDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvDetails.setTextColor(Color.parseColor("#666666"));
            tvDetails.setPadding(0, 0, 0, (int) (8 * density));
            card.addView(tvDetails);

            // Status
            TextView tvStatus = new TextView(this);
            tvStatus.setText(hasil.getStatus());
            tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvStatus.setTextColor(hasil.getStatus().equals("LULUS") ?
                Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));
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
            // Assuming the date format from API is ISO format
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
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

    private void debugApiCall() {
        int userId = getUserId();
        String token = getToken();

        Log.d("HasilKuis", "=== MANUAL DEBUG API CALL ===");
        Log.d("HasilKuis", "User ID: " + userId);
        Log.d("HasilKuis", "Token: " + (token.isEmpty() ? "EMPTY" : "EXISTS"));

        if (kuisList.isEmpty()) {
            Toast.makeText(this, "No kuis available for testing", Toast.LENGTH_LONG).show();
            return;
        }

        // Test dengan kuis pertama
        Kuis testKuis = kuisList.get(0);
        Log.d("HasilKuis", "Testing with kuis ID: " + testKuis.getId() + " (" + testKuis.getTitle() + ")");

        Toast.makeText(this, "Testing API call for: " + testKuis.getTitle(), Toast.LENGTH_SHORT).show();

        try {
            apiService.getHasilKuis("Bearer " + token, userId, testKuis.getId()).enqueue(new Callback<HasilKuisResponse>() {
                @Override
                public void onResponse(Call<HasilKuisResponse> call, Response<HasilKuisResponse> response) {
                    try {
                        Log.d("HasilKuis", "=== DEBUG API RESPONSE ===");
                        Log.d("HasilKuis", "Response code: " + response.code());
                        Log.d("HasilKuis", "Response successful: " + response.isSuccessful());

                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                try {
                                    HasilKuisResponse hasilResponse = response.body();
                                    Log.d("HasilKuis", "Response success flag: " + hasilResponse.isSuccess());
                                    Log.d("HasilKuis", "Response message: " + hasilResponse.getMessage());

                                    int dataCount = hasilResponse.getData() != null ? hasilResponse.getData().size() : 0;
                                    Log.d("HasilKuis", "Data count: " + dataCount);

                                    String message = String.format("API Response:\nCode: %d\nSuccess: %s\nMessage: %s\nData count: %d",
                                            response.code(),
                                            hasilResponse.isSuccess(),
                                            hasilResponse.getMessage(),
                                            dataCount);

                                    runOnUiThread(() -> Toast.makeText(HasilKuisActivity.this, message, Toast.LENGTH_LONG).show());

                                    if (hasilResponse.getData() != null && !hasilResponse.getData().isEmpty()) {
                                        HasilKuis hasil = hasilResponse.getData().get(0);
                                        Log.d("HasilKuis", "First result - Score: " + hasil.getScore() + ", Grade: " + hasil.getGrade());
                                    }
                                } catch (Exception e) {
                                    Log.e("HasilKuis", "Error parsing debug response: " + e.getMessage(), e);
                                    runOnUiThread(() -> Toast.makeText(HasilKuisActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show());
                                }
                            } else {
                                Log.w("HasilKuis", "Response body is null");
                                runOnUiThread(() -> Toast.makeText(HasilKuisActivity.this, "Response body is null", Toast.LENGTH_LONG).show());
                            }
                        } else {
                            String errorMsg = "Error " + response.code();
                            String detailedError = "";

                            // Handle specific error codes
                            switch (response.code()) {
                                case 500:
                                    errorMsg = "Server Error (500)";
                                    detailedError = "Ada masalah di server saat mengakses hasil kuis";
                                    Log.e("HasilKuis", "=== DEBUG SERVER ERROR 500 ===");
                                    Log.e("HasilKuis", "Test Kuis ID: " + testKuis.getId());
                                    Log.e("HasilKuis", "User ID: " + userId);
                                    Log.e("HasilKuis", "URL: /hasil-kuis/" + userId + "/" + testKuis.getId());
                                    break;
                                case 404:
                                    errorMsg = "Not Found (404)";
                                    detailedError = "Endpoint atau data tidak ditemukan";
                                    Log.e("HasilKuis", "404 Error - URL: /hasil-kuis/" + userId + "/" + testKuis.getId());
                                    break;
                                case 401:
                                    errorMsg = "Unauthorized (401)";
                                    detailedError = "Token tidak valid atau expired";
                                    break;
                                default:
                                    detailedError = "HTTP Error";
                                    break;
                            }

                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    Log.e("HasilKuis", "Error body: " + errorBody);
                                    errorMsg += "\nDetails: " + errorBody;
                                }
                            } catch (Exception e) {
                                errorMsg += "\nError reading details: " + e.getMessage();
                            }

                            Log.e("HasilKuis", errorMsg);
                            final String finalErrorMsg = errorMsg + "\n" + detailedError;
                            runOnUiThread(() -> Toast.makeText(HasilKuisActivity.this, finalErrorMsg, Toast.LENGTH_LONG).show());
                        }
                    } catch (Exception e) {
                        Log.e("HasilKuis", "Unexpected error in debug onResponse: " + e.getMessage(), e);
                        runOnUiThread(() -> Toast.makeText(HasilKuisActivity.this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }

                @Override
                public void onFailure(Call<HasilKuisResponse> call, Throwable t) {
                    Log.e("HasilKuis", "=== DEBUG API FAILURE ===");
                    Log.e("HasilKuis", "Error type: " + t.getClass().getSimpleName());
                    Log.e("HasilKuis", "Error message: " + t.getMessage(), t);

                    String errorMessage = "API Failure: " + t.getClass().getSimpleName();
                    if (t.getMessage() != null) {
                        errorMessage += " - " + t.getMessage();
                    }

                    if (t instanceof java.lang.IllegalStateException) {
                        errorMessage += "\n(Response parsing error)";
                        Log.e("HasilKuis", "IllegalStateException in debug call - likely response parsing issue");
                    }

                    final String finalErrorMessage = errorMessage;
                    runOnUiThread(() -> Toast.makeText(HasilKuisActivity.this, finalErrorMessage, Toast.LENGTH_LONG).show());
                }
            });
        } catch (Exception e) {
            Log.e("HasilKuis", "Error creating debug API call: " + e.getMessage(), e);
            Toast.makeText(this, "Error creating API call: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showDebugInfo() {
        int userId = getUserId();
        String token = getToken();

        StringBuilder info = new StringBuilder();
        info.append("=== DEBUG INFORMATION ===\n\n");
        info.append("User ID: ").append(userId).append("\n");
        info.append("Token: ").append(token.isEmpty() ? "EMPTY" : "EXISTS (" + token.length() + " chars)").append("\n");
        info.append("Base URL: ").append(BASE_URL).append("\n");
        info.append("Total Kuis: ").append(kuisList.size()).append("\n\n");

        if (!kuisList.isEmpty()) {
            info.append("Available Kuis:\n");
            for (int i = 0; i < Math.min(kuisList.size(), 5); i++) {
                Kuis kuis = kuisList.get(i);
                info.append("- ID: ").append(kuis.getId())
                    .append(", Title: ").append(kuis.getTitle()).append("\n");
                info.append("  URL: /hasil-kuis/").append(userId).append("/").append(kuis.getId()).append("\n");
            }
            if (kuisList.size() > 5) {
                info.append("... and ").append(kuisList.size() - 5).append(" more\n");
            }
        } else {
            info.append("No kuis available\n");
        }

        info.append("\n=== TROUBLESHOOTING ===\n");
        info.append("1. Pastikan sudah login\n");
        info.append("2. Pastikan sudah mengerjakan kuis\n");
        info.append("3. Cek server status\n");
        info.append("4. Coba login ulang jika error 401\n");

        if (userId == 1) {
            info.append("\n⚠️ Using fallback User ID (1)\n");
            info.append("Login ulang untuk mendapatkan User ID yang benar\n");
        }

        Log.d("HasilKuis", info.toString());

        // Show in dialog
        new android.app.AlertDialog.Builder(this)
                .setTitle("Debug Information")
                .setMessage(info.toString())
                .setPositiveButton("OK", null)
                .setNeutralButton("Copy to Log", (dialog, which) -> {
                    Log.i("HasilKuis", "=== USER REQUESTED DEBUG INFO ===\n" + info.toString());
                    Toast.makeText(this, "Debug info copied to Logcat", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void setupNavigation() {
        // Initialize bottom navigation
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navKuis = findViewById(R.id.nav_kuis);
        LinearLayout navJawabSoal = findViewById(R.id.nav_jawab_soal);
        LinearLayout navHasil = findViewById(R.id.nav_hasil);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        navKuis.setOnClickListener(v -> {
            startActivity(new Intent(this, KuisActivity.class));
            finish();
        });

        navJawabSoal.setOnClickListener(v -> {
            startActivity(new Intent(this, JawabSoalMainActivity.class));
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