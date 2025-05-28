package com.example.brainquiz;

import com.example.brainquiz.filter.Tingkatan;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TingkatanResponse {
    @SerializedName("data")
    private List<Tingkatan> data;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private boolean success;

    public List<Tingkatan> getData() {
        return data != null ? data : List.of();
    }

    public String getMessage() {
        return message != null ? message : "";
    }

    public boolean isSuccess() {
        return success;
    }
}