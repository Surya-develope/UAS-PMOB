package com.example.brainquiz.models;

import com.example.brainquiz.filter.Pendidikan;
import java.util.List;

public class PendidikanResponse {
    private List<Pendidikan> data;
    private String message;
    private boolean success;

    public List<Pendidikan> getData() {
        return data;
    }

    public void setData(List<Pendidikan> data) {
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