package com.example.brainquiz;

import com.example.brainquiz.filter.Kategori;

import java.util.List;

public class KategoriResponse {
    private boolean success;
    private String message; // Tambahkan field message
    private List<Kategori> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Kategori> getData() {
        return data;
    }

    public void setData(List<Kategori> data) {
        this.data = data;
    }
}