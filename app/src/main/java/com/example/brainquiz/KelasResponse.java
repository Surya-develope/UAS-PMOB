package com.example.brainquiz;

import com.example.brainquiz.filter.Kelas;
import java.util.List;

public class KelasResponse {
    private List<Kelas> data;
    private String message;
    private boolean success;

    public List<Kelas> getData() {
        return data;
    }

    public void setData(List<Kelas> data) {
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
