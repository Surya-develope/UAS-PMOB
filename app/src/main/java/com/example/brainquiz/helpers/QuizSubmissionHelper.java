package com.example.brainquiz.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.brainquiz.filter.Jawaban;
import com.example.brainquiz.filter.Soal;
import com.example.brainquiz.models.JawabanResponse;
import com.example.brainquiz.network.ApiService;
import com.example.brainquiz.utils.ApiConstants;
import com.example.brainquiz.utils.AuthManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Helper class untuk menangani submission jawaban quiz
 */
public class QuizSubmissionHelper {
    
    private Context context;
    private ApiService apiService;
    private AuthManager authManager;
    private QuizSubmissionListener listener;
    
    public interface QuizSubmissionListener {
        void onSubmissionStart();
        void onSubmissionComplete();
        void onSubmissionSuccess(JawabanResponse response);
        void onSubmissionError(String message);
    }
    
    public QuizSubmissionHelper(Context context, ApiService apiService, AuthManager authManager) {
        this.context = context;
        this.apiService = apiService;
        this.authManager = authManager;
    }
    
    public void setListener(QuizSubmissionListener listener) {
        this.listener = listener;
    }
    
    public void showSubmitConfirmation(List<String> jawabanUser, Runnable onConfirm) {
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

        new AlertDialog.Builder(context)
                .setTitle("Konfirmasi Submit")
                .setMessage(message)
                .setPositiveButton("Ya, Kirim", (dialog, which) -> onConfirm.run())
                .setNegativeButton("Batal", null)
                .show();
    }
    
    public void submitJawaban(List<Soal> soalList, List<String> jawabanUser) {
        if (!authManager.hasValidToken()) {
            Toast.makeText(context, ApiConstants.ERROR_UNAUTHORIZED, Toast.LENGTH_SHORT).show();
            authManager.logoutAndRedirect(context);
            return;
        }

        int userId = authManager.getCurrentUserId();

        if (listener != null) {
            listener.onSubmissionStart();
        }

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

        Log.d("QuizSubmission", "Submitting " + jawabanList.size() + " answers out of " + soalList.size() + " questions");

        apiService.submitJawaban(authManager.getAuthorizationHeader(), jawabanList).enqueue(new Callback<JawabanResponse>() {
            @Override
            public void onResponse(Call<JawabanResponse> call, Response<JawabanResponse> response) {
                if (listener != null) {
                    listener.onSubmissionComplete();
                }
                
                Log.d("QuizSubmission", "Submit response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    JawabanResponse jawabanResponse = response.body();
                    if (jawabanResponse.isSuccess()) {
                        if (listener != null) {
                            listener.onSubmissionSuccess(jawabanResponse);
                        }
                    } else {
                        String errorMsg = "Gagal mengirim jawaban: " + jawabanResponse.getMessage();
                        if (listener != null) {
                            listener.onSubmissionError(errorMsg);
                        }
                    }
                } else {
                    Log.e("QuizSubmission", "Submit error " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("QuizSubmission", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("QuizSubmission", "Error reading error body: " + e.getMessage());
                        }
                    }
                    String errorMsg = "Gagal mengirim jawaban: " + response.code();
                    if (listener != null) {
                        listener.onSubmissionError(errorMsg);
                    }
                }
            }

            @Override
            public void onFailure(Call<JawabanResponse> call, Throwable t) {
                if (listener != null) {
                    listener.onSubmissionComplete();
                }
                
                Log.e("QuizSubmission", "Submit failure: " + t.getMessage(), t);
                String errorMsg = "Error: " + t.getMessage();
                if (listener != null) {
                    listener.onSubmissionError(errorMsg);
                }
            }
        });
    }
    
    public void showResultDialog(JawabanResponse response) {
        String resultMessage = "Jawaban berhasil dikirim!\n\n";

        if (response.getScore() != null) {
            resultMessage += "Skor: " + response.getScore();
        }

        if (response.getCorrectAnswers() != null && response.getTotalQuestions() != null) {
            resultMessage += "\nBenar: " + response.getCorrectAnswers() + " dari " + response.getTotalQuestions();
        }

        new AlertDialog.Builder(context)
                .setTitle("Hasil Kuis")
                .setMessage(resultMessage)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Go back to home
                    Intent intent = new Intent(context, com.example.brainquiz.activities.HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).finish();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
