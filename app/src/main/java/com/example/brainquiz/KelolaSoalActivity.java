package com.example.brainquiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Soal;
import com.example.brainquiz.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KelolaSoalActivity extends AppCompatActivity {

    private GridLayout gridSoal;
    private Button btnTambahSoal;
    private TextView tvKuisTitle;
    
    private ApiService apiService;
    private static final String BASE_URL = "https://brainquiz0.up.railway.app/";
    private static final int REQUEST_CODE_ADD = 100;
    private static final int REQUEST_CODE_EDIT = 101;
    
    private List<Soal> soalList = new ArrayList<>();
    private int kuisId;
    private String kuisTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_soal);

        initViews();
        initRetrofit();
        getIntentData();
        setupClickListeners();
        fetchSoal();
    }

    private void initViews() {
        gridSoal = findViewById(R.id.gridSoal);
        btnTambahSoal = findViewById(R.id.btnTambahSoal);
        tvKuisTitle = findViewById(R.id.tvKuisTitle);
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
            tvKuisTitle.setText("Kelola Soal: " + kuisTitle);
        }
    }

    private void setupClickListeners() {
        btnTambahSoal.setOnClickListener(v -> {
            Intent intent = new Intent(this, TambahSoalActivity.class);
            intent.putExtra("kuis_id", kuisId);
            intent.putExtra("kuis_title", kuisTitle);
            startActivityForResult(intent, REQUEST_CODE_ADD);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSoal();
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token", "");
    }

    private void fetchSoal() {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("KelolaSoal", "Fetching soal for kuis ID: " + kuisId);
        
        apiService.getSoalByKuisId("Bearer " + token, kuisId).enqueue(new Callback<SoalResponse>() {
            @Override
            public void onResponse(Call<SoalResponse> call, Response<SoalResponse> response) {
                Log.d("KelolaSoal", "Response Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    SoalResponse soalResponse = response.body();
                    Log.d("KelolaSoal", "Response success: " + soalResponse.isSuccess());
                    
                    if (soalResponse.isSuccess()) {
                        soalList = soalResponse.getData();
                        Log.d("KelolaSoal", "Loaded " + soalList.size() + " soal");
                        displaySoal();
                        
                        if (soalList.isEmpty()) {
                            Toast.makeText(KelolaSoalActivity.this, "Belum ada soal untuk kuis ini", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(KelolaSoalActivity.this, "Berhasil memuat " + soalList.size() + " soal", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(KelolaSoalActivity.this, "Gagal memuat soal: " + soalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("KelolaSoal", "Error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("KelolaSoal", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("KelolaSoal", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(KelolaSoalActivity.this, "Gagal mengambil data soal: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SoalResponse> call, Throwable t) {
                Log.e("KelolaSoal", "onFailure: " + t.getMessage(), t);
                Toast.makeText(KelolaSoalActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySoal() {
        gridSoal.removeAllViews();
        gridSoal.setColumnCount(1);

        final float density = getResources().getDisplayMetrics().density;

        if (soalList.isEmpty()) {
            // Show empty state
            TextView emptyText = new TextView(this);
            emptyText.setText("Belum ada soal.\nKlik 'Tambah Soal' untuk menambah soal baru.");
            emptyText.setTextSize(16);
            emptyText.setTextColor(Color.GRAY);
            emptyText.setGravity(Gravity.CENTER);
            emptyText.setPadding(32, 64, 32, 64);
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.MATCH_PARENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            emptyText.setLayoutParams(params);
            
            gridSoal.addView(emptyText);
            return;
        }

        for (int index = 0; index < soalList.size(); index++) {
            Soal soal = soalList.get(index);
            final int finalIndex = index; // Make index effectively final for lambda

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

            // Header with question number and menu
            LinearLayout headerLayout = new LinearLayout(this);
            headerLayout.setOrientation(LinearLayout.HORIZONTAL);
            headerLayout.setGravity(Gravity.CENTER_VERTICAL);

            // Question number
            TextView tvNumber = new TextView(this);
            tvNumber.setText("Soal " + (finalIndex + 1));
            tvNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tvNumber.setTextColor(Color.parseColor("#2196F3"));
            tvNumber.setTypeface(null, android.graphics.Typeface.BOLD);

            LinearLayout.LayoutParams numberParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            );
            tvNumber.setLayoutParams(numberParams);
            headerLayout.addView(tvNumber);

            // Menu icon
            ImageView menuIcon = new ImageView(this);
            menuIcon.setImageResource(R.drawable.ic_more_vert);
            menuIcon.setColorFilter(Color.parseColor("#666666"));
            LinearLayout.LayoutParams menuParams = new LinearLayout.LayoutParams(
                    (int) (24 * density),
                    (int) (24 * density)
            );
            menuIcon.setLayoutParams(menuParams);
            menuIcon.setOnClickListener(v -> showSoalMenu(soal, finalIndex));
            headerLayout.addView(menuIcon);
            
            card.addView(headerLayout);

            // Question text
            TextView tvQuestion = new TextView(this);
            tvQuestion.setText(soal.getQuestion());
            tvQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvQuestion.setTextColor(Color.parseColor("#333333"));
            tvQuestion.setPadding(0, (int) (8 * density), 0, (int) (8 * density));
            card.addView(tvQuestion);

            // Options
            String[] optionLabels = {"A", "B", "C", "D"};
            String[] optionValues = {
                soal.getOptionA(),
                soal.getOptionB(), 
                soal.getOptionC(),
                soal.getOptionD()
            };

            for (int i = 0; i < optionLabels.length; i++) {
                LinearLayout optionLayout = new LinearLayout(this);
                optionLayout.setOrientation(LinearLayout.HORIZONTAL);
                optionLayout.setPadding(0, (int) (4 * density), 0, 0);

                TextView optionLabel = new TextView(this);
                optionLabel.setText(optionLabels[i] + ". ");
                optionLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                optionLabel.setTextColor(soal.getCorrectAnswer().equals(optionLabels[i]) ? 
                    Color.parseColor("#4CAF50") : Color.parseColor("#666666"));
                optionLabel.setTypeface(null, soal.getCorrectAnswer().equals(optionLabels[i]) ? 
                    android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);

                TextView optionText = new TextView(this);
                optionText.setText(optionValues[i]);
                optionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                optionText.setTextColor(soal.getCorrectAnswer().equals(optionLabels[i]) ? 
                    Color.parseColor("#4CAF50") : Color.parseColor("#666666"));
                optionText.setTypeface(null, soal.getCorrectAnswer().equals(optionLabels[i]) ? 
                    android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);

                optionLayout.addView(optionLabel);
                optionLayout.addView(optionText);
                card.addView(optionLayout);
            }

            // Correct answer indicator
            TextView tvCorrect = new TextView(this);
            tvCorrect.setText("Jawaban Benar: " + soal.getCorrectAnswer());
            tvCorrect.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tvCorrect.setTextColor(Color.parseColor("#4CAF50"));
            tvCorrect.setTypeface(null, android.graphics.Typeface.BOLD);
            tvCorrect.setPadding(0, (int) (8 * density), 0, 0);
            card.addView(tvCorrect);

            gridSoal.addView(card);
        }
    }

    private void showSoalMenu(Soal soal, int position) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_soal_menu);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Edit option
        LinearLayout menuEdit = dialog.findViewById(R.id.menu_edit_soal);
        if (menuEdit != null) {
            menuEdit.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditSoalActivity.class);
                intent.putExtra("soal_id", soal.getId());
                intent.putExtra("question", soal.getQuestion());
                intent.putExtra("option_a", soal.getOptionA());
                intent.putExtra("option_b", soal.getOptionB());
                intent.putExtra("option_c", soal.getOptionC());
                intent.putExtra("option_d", soal.getOptionD());
                intent.putExtra("correct_answer", soal.getCorrectAnswer());
                intent.putExtra("kuis_id", kuisId);
                startActivityForResult(intent, REQUEST_CODE_EDIT);
                dialog.dismiss();
            });
        }

        // Delete option
        LinearLayout menuDelete = dialog.findViewById(R.id.menu_delete_soal);
        if (menuDelete != null) {
            menuDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Apakah Anda yakin ingin menghapus soal ini?")
                        .setPositiveButton("Ya", (dialogConfirm, which) -> deleteSoal(soal.getId()))
                        .setNegativeButton("Tidak", null)
                        .show();
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void deleteSoal(int soalId) {
        String token = getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.deleteSoal("Bearer " + token, soalId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(KelolaSoalActivity.this, "Soal berhasil dihapus", Toast.LENGTH_SHORT).show();
                    fetchSoal(); // Refresh list
                } else {
                    Toast.makeText(KelolaSoalActivity.this, "Gagal menghapus soal: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(KelolaSoalActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            fetchSoal(); // Refresh list when returning from add/edit
        }
    }
}
