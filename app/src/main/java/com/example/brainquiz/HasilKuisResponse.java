package com.example.brainquiz;

import com.example.brainquiz.filter.HasilKuis;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HasilKuisResponse {
    @SerializedName("data")
    private List<HasilKuis> data;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private boolean success;

    // Getters and Setters
    public List<HasilKuis> getData() {
        return data != null ? data : new java.util.ArrayList<>();
    }

    public void setData(List<HasilKuis> data) {
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
