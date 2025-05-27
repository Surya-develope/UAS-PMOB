package com.example.brainquiz.filter;

import com.google.gson.annotations.SerializedName;

public class Kuis {
    @SerializedName("ID")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("kategori_id")
    private int kategoriId;

    @SerializedName("Kategori")
    private Kategori kategori;

    @SerializedName("tingkatan_id")
    private int tingkatanId;

    @SerializedName("Tingkatan")
    private Tingkatan tingkatan;

    @SerializedName("kelas_id")
    private int kelasId;

    @SerializedName("Kelas")
    private Kelas kelas;

    @SerializedName("pendidikan_id")
    private int pendidikanId;

    @SerializedName("Pendidikan")
    private Pendidikan pendidikan;

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title != null ? title : "Kuis Tanpa Judul";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getKategoriId() {
        return kategoriId;
    }

    public void setKategoriId(int kategoriId) {
        this.kategoriId = kategoriId;
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public int getTingkatanId() {
        return tingkatanId;
    }

    public void setTingkatanId(int tingkatanId) {
        this.tingkatanId = tingkatanId;
    }

    public Tingkatan getTingkatan() {
        return tingkatan;
    }

    public void setTingkatan(Tingkatan tingkatan) {
        this.tingkatan = tingkatan;
    }

    public int getKelasId() {
        return kelasId;
    }

    public void setKelasId(int kelasId) {
        this.kelasId = kelasId;
    }

    public Kelas getKelas() {
        return kelas;
    }

    public void setKelas(Kelas kelas) {
        this.kelas = kelas;
    }

    public int getPendidikanId() {
        return pendidikanId;
    }

    public void setPendidikanId(int pendidikanId) {
        this.pendidikanId = pendidikanId;
    }

    public Pendidikan getPendidikan() {
        return pendidikan;
    }

    public void setPendidikan(Pendidikan pendidikan) {
        this.pendidikan = pendidikan;
    }
}