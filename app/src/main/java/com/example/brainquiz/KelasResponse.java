package com.example.brainquiz;

import com.example.brainquiz.filter.Kelas;

import java.util.List;

public class KelasResponse {
    private boolean success;
    private List<Kelas> data;

    public boolean isSuccess() { return success; }
    public List<Kelas> getData() { return data; }
}
