package com.example.brainquiz.models;

import com.example.brainquiz.filter.Soal;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SoalResponse {
    @SerializedName("data")
    private List<Soal> data;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private boolean success;

    // Getters and Setters
    public List<Soal> getData() {
        return data != null ? data : new java.util.ArrayList<>();
    }

    public void setData(List<Soal> data) {
        this.data = data;
    }

    public String getMessage() {
        return message != null ? message : "";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
