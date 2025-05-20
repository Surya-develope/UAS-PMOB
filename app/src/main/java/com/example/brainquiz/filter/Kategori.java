package com.example.brainquiz.filter;

import com.google.gson.annotations.SerializedName;

public class Kategori {

    @SerializedName("id")
    private String id;

    @SerializedName("nama")
    private String nama;

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}