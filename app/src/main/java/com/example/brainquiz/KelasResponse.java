package com.example.brainquiz;

import com.example.brainquiz.filter.Kelas;
import java.util.List;

public class KelasResponse {
    private List<Kelas> data;
    private int kelas;

    public List<Kelas> getData() {
        return data;
    }

    public void setData(List<Kelas> data) {
        this.data = data;
    }

    public int getKelas() {
        return kelas;
    }

    public void setKelas(int kelas) {
        this.kelas = kelas;
    }
}