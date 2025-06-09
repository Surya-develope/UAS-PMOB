package com.example.brainquiz.filter;

import com.google.gson.annotations.SerializedName;

public class HasilKuis {
    @SerializedName("ID")
    private int id;

    @SerializedName(value = "user_id", alternate = {"users_id"})
    private int userId;

    @SerializedName("kuis_id")
    private int kuisId;

    @SerializedName("score")
    private int score;

    @SerializedName("total_questions")
    private int totalQuestions;

    @SerializedName(value = "correct_answers", alternate = {"correct_answer"})
    private int correctAnswers;

    @SerializedName("completed_at")
    private String completedAt;

    @SerializedName(value = "created_at", alternate = {"CreatedAt"})
    private String createdAt;

    @SerializedName(value = "updated_at", alternate = {"UpdatedAt"})
    private String updatedAt;

    // Relasi dengan Kuis
    @SerializedName("Kuis")
    private Kuis kuis;

    // Constructors
    public HasilKuis() {}

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getKuisId() {
        return kuisId;
    }

    public void setKuisId(int kuisId) {
        this.kuisId = kuisId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public String getCompletedAt() {
        // Fallback ke updatedAt jika completedAt tidak ada
        if (completedAt != null && !completedAt.isEmpty()) {
            return completedAt;
        }
        return updatedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Kuis getKuis() {
        return kuis;
    }

    public void setKuis(Kuis kuis) {
        this.kuis = kuis;
    }

    // Helper methods
    public String getKuisTitle() {
        try {
            if (kuis != null && kuis.getTitle() != null && !kuis.getTitle().isEmpty()) {
                return kuis.getTitle();
            }
            return "Unknown Quiz";
        } catch (Exception e) {
            return "Unknown Quiz";
        }
    }

    public double getPercentage() {
        try {
            // Jika total_questions tidak ada atau 0, coba hitung dari score
            if (totalQuestions == 0) {
                // Asumsi: jika score 25 dan correct_answer 1, maka mungkin ada 4 soal (25*1=25, max 100)
                // Atau bisa juga score langsung dalam bentuk persentase
                if (score > 0 && score <= 100) {
                    return (double) score; // Score sudah dalam bentuk persentase
                }
                return 0.0;
            }
            return (double) correctAnswers / totalQuestions * 100.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public String getGrade() {
        try {
            double percentage = getPercentage();
            if (percentage >= 90) return "A";
            else if (percentage >= 80) return "B";
            else if (percentage >= 70) return "C";
            else if (percentage >= 60) return "D";
            else return "E";
        } catch (Exception e) {
            return "N/A";
        }
    }

    public String getStatus() {
        try {
            double percentage = getPercentage();
            if (percentage >= 70) return "LULUS";
            else return "TIDAK LULUS";
        } catch (Exception e) {
            return "TIDAK DIKETAHUI";
        }
    }
}
