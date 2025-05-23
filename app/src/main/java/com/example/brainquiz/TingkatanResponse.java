package com.example.brainquiz;

import com.example.brainquiz.filter.Tingkatan;
import java.util.List;

public class TingkatanResponse {
    private List<Tingkatan> data;
    private String message;
    private boolean success;

    public List<Tingkatan> getData() {
        return data;
    }

    public void setData(List<Tingkatan> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
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