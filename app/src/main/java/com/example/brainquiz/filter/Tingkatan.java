package com.example.brainquiz.filter;

import com.google.gson.annotations.SerializedName;

public class Tingkatan {
    @SerializedName("ID")
    private int id; // Ubah menjadi int jika server mengharapkan integer

    @SerializedName("name")
    private String nama;

    @SerializedName("description")
    private String description;

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}