package com.example.brainquiz.filter;

import com.google.gson.annotations.SerializedName;

public class Jawaban {
    @SerializedName("soal_id")
    private int soalId;

    @SerializedName("answer")
    private String answer;

    @SerializedName("user_id")
    private int userId;

    // Constructors
    public Jawaban() {}

    public Jawaban(int soalId, String answer, int userId) {
        this.soalId = soalId;
        this.answer = answer;
        this.userId = userId;
    }

    // Getters and Setters
    public int getSoalId() {
        return soalId;
    }

    public void setSoalId(int soalId) {
        this.soalId = soalId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
