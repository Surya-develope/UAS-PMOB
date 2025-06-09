package com.example.brainquiz.utils;

import com.example.brainquiz.filter.Soal;
import com.example.brainquiz.filter.Kuis;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom Gson deserializer untuk class Soal
 * Menangani konversi options_json dari String ke Map<String, String>
 */
public class SoalDeserializer implements JsonDeserializer<Soal> {
    
    @Override
    public Soal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        
        JsonObject jsonObject = json.getAsJsonObject();
        Soal soal = new Soal();
        
        try {
            // Parse ID
            if (jsonObject.has("ID") && !jsonObject.get("ID").isJsonNull()) {
                soal.setId(jsonObject.get("ID").getAsInt());
            }
            
            // Parse question
            if (jsonObject.has("question") && !jsonObject.get("question").isJsonNull()) {
                soal.setQuestion(jsonObject.get("question").getAsString());
            }
            
            // Parse correct_answer
            if (jsonObject.has("correct_answer") && !jsonObject.get("correct_answer").isJsonNull()) {
                soal.setCorrectAnswer(jsonObject.get("correct_answer").getAsString());
            }
            
            // Parse kuis_id
            if (jsonObject.has("kuis_id") && !jsonObject.get("kuis_id").isJsonNull()) {
                soal.setKuisId(jsonObject.get("kuis_id").getAsInt());
            }
            
            // Parse Kuis object if present
            if (jsonObject.has("Kuis") && !jsonObject.get("Kuis").isJsonNull()) {
                Kuis kuis = context.deserialize(jsonObject.get("Kuis"), Kuis.class);
                soal.setKuis(kuis);
            }
            
            // Parse options_json - This is the critical part
            if (jsonObject.has("options_json") && !jsonObject.get("options_json").isJsonNull()) {
                JsonElement optionsElement = jsonObject.get("options_json");
                Map<String, String> optionsMap = parseOptionsJson(optionsElement);
                soal.setOptionsJson(optionsMap);
            }
            
        } catch (Exception e) {
            // Log error but don't throw exception to prevent app crash
            android.util.Log.e("SoalDeserializer", "Error parsing Soal: " + e.getMessage(), e);
            
            // Set default empty options if parsing fails
            if (soal.getOptionsJson() == null) {
                Map<String, String> defaultOptions = new HashMap<>();
                defaultOptions.put("A", "");
                defaultOptions.put("B", "");
                defaultOptions.put("C", "");
                defaultOptions.put("D", "");
                soal.setOptionsJson(defaultOptions);
            }
        }
        
        return soal;
    }
    
    /**
     * Parse options_json yang bisa berupa String atau Object
     */
    private Map<String, String> parseOptionsJson(JsonElement optionsElement) {
        Map<String, String> optionsMap = new HashMap<>();
        
        try {
            if (optionsElement.isJsonObject()) {
                // Case 1: options_json is already a JSON object
                JsonObject optionsObject = optionsElement.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : optionsObject.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue().isJsonNull() ? "" : entry.getValue().getAsString();
                    optionsMap.put(key, value);
                }
                
            } else if (optionsElement.isJsonArray()) {
                // Case 2: options_json is an array (handle this case too)
                JsonArray optionsArray = optionsElement.getAsJsonArray();
                String[] labels = {"A", "B", "C", "D"};
                
                for (int i = 0; i < Math.min(optionsArray.size(), labels.length); i++) {
                    JsonElement element = optionsArray.get(i);
                    String value = element.isJsonNull() ? "" : element.getAsString();
                    optionsMap.put(labels[i], value);
                }
                
            } else if (optionsElement.isJsonPrimitive()) {
                // Case 3: options_json is a string (the problematic case)
                String optionsString = optionsElement.getAsString();
                optionsMap = parseOptionsFromString(optionsString);
                
            } else {
                // Case 4: Unknown format, set defaults
                android.util.Log.w("SoalDeserializer", "Unknown options_json format: " + optionsElement.toString());
                setDefaultOptions(optionsMap);
            }
            
        } catch (Exception e) {
            android.util.Log.e("SoalDeserializer", "Error parsing options_json: " + e.getMessage(), e);
            setDefaultOptions(optionsMap);
        }
        
        // Ensure all required keys exist
        ensureAllOptionsExist(optionsMap);
        
        return optionsMap;
    }
    
    /**
     * Parse options dari string JSON
     */
    private Map<String, String> parseOptionsFromString(String optionsString) {
        Map<String, String> optionsMap = new HashMap<>();
        
        try {
            if (optionsString != null && !optionsString.trim().isEmpty()) {
                // Try to parse as JSON object
                JsonElement element = JsonParser.parseString(optionsString);
                
                if (element.isJsonObject()) {
                    JsonObject optionsObject = element.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : optionsObject.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue().isJsonNull() ? "" : entry.getValue().getAsString();
                        optionsMap.put(key, value);
                    }
                } else if (element.isJsonArray()) {
                    JsonArray optionsArray = element.getAsJsonArray();
                    String[] labels = {"A", "B", "C", "D"};
                    
                    for (int i = 0; i < Math.min(optionsArray.size(), labels.length); i++) {
                        JsonElement arrayElement = optionsArray.get(i);
                        String value = arrayElement.isJsonNull() ? "" : arrayElement.getAsString();
                        optionsMap.put(labels[i], value);
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            android.util.Log.e("SoalDeserializer", "Invalid JSON in options_json string: " + optionsString, e);
            setDefaultOptions(optionsMap);
        }
        
        return optionsMap;
    }
    
    /**
     * Set default empty options
     */
    private void setDefaultOptions(Map<String, String> optionsMap) {
        optionsMap.put("A", "");
        optionsMap.put("B", "");
        optionsMap.put("C", "");
        optionsMap.put("D", "");
    }
    
    /**
     * Ensure all required option keys (A, B, C, D) exist
     */
    private void ensureAllOptionsExist(Map<String, String> optionsMap) {
        String[] requiredKeys = {"A", "B", "C", "D"};
        for (String key : requiredKeys) {
            if (!optionsMap.containsKey(key)) {
                optionsMap.put(key, "");
            }
        }
    }
}
