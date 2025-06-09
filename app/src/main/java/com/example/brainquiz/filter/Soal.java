package com.example.brainquiz.filter;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class Soal {
    @SerializedName("ID")
    private int id;

    @SerializedName("question")
    private String question;

    @SerializedName("options_json")
    private Map<String, String> optionsJson;

    @SerializedName("correct_answer")
    private String correctAnswer;

    @SerializedName("kuis_id")
    private int kuisId;

    @SerializedName("Kuis")
    private Kuis kuis;

    // Constructors
    public Soal() {}

    public Soal(String question, Map<String, String> optionsJson, String correctAnswer, int kuisId) {
        this.question = question;
        this.optionsJson = optionsJson;
        this.correctAnswer = correctAnswer;
        this.kuisId = kuisId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<String, String> getOptionsJson() {
        return optionsJson;
    }

    public void setOptionsJson(Map<String, String> optionsJson) {
        this.optionsJson = optionsJson;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getKuisId() {
        return kuisId;
    }

    public void setKuisId(int kuisId) {
        this.kuisId = kuisId;
    }

    public Kuis getKuis() {
        return kuis;
    }

    public void setKuis(Kuis kuis) {
        this.kuis = kuis;
    }

    // Helper methods
    public String getOptionA() {
        return optionsJson != null ? optionsJson.get("A") : "";
    }

    public String getOptionB() {
        return optionsJson != null ? optionsJson.get("B") : "";
    }

    public String getOptionC() {
        return optionsJson != null ? optionsJson.get("C") : "";
    }

    public String getOptionD() {
        return optionsJson != null ? optionsJson.get("D") : "";
    }

    public void setOptions(String optionA, String optionB, String optionC, String optionD) {
        if (optionsJson == null) {
            optionsJson = new java.util.HashMap<>();
        }
        optionsJson.put("A", optionA);
        optionsJson.put("B", optionB);
        optionsJson.put("C", optionC);
        optionsJson.put("D", optionD);
    }
}
