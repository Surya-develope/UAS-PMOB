package com.example.brainquiz;

import com.example.brainquiz.filter.Kategori;

import java.util.List;

public class KategoriResponse {
    private boolean success;
    private List<Kategori> data;

    public boolean isSuccess() {
        return success;
    }

    public List<Kategori> getData() {
        return data;
    }
}