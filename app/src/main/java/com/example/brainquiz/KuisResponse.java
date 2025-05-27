package com.example.brainquiz;

import com.example.brainquiz.filter.Kuis;
import java.util.List;

public class KuisResponse {
    private List<Kuis> data;
    private String message;
    private boolean success;

    public List<Kuis> getData() { return data; }
    public void setData(List<Kuis> data) { this.data = data; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}