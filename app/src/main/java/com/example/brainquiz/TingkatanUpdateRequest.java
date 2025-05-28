package com.example.brainquiz;

public class TingkatanUpdateRequest {
    private String nama;
    private String deskripsi;

    public TingkatanUpdateRequest(String nama, String deskripsi) {
        this.nama = nama;
        this.deskripsi = deskripsi;
    }

    public String getNama() {
        return nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }
}
