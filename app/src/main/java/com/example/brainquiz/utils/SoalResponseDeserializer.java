package com.example.brainquiz.utils;

import com.example.brainquiz.filter.Soal;
import com.example.brainquiz.models.SoalResponse;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Gson deserializer untuk SoalResponse
 * Menangani kasus dimana data bisa berupa array atau single object
 */
public class SoalResponseDeserializer implements JsonDeserializer<SoalResponse> {
    
    @Override
    public SoalResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        
        JsonObject jsonObject = json.getAsJsonObject();
        SoalResponse response = new SoalResponse();
        
        try {
            // Parse success field
            if (jsonObject.has("success") && !jsonObject.get("success").isJsonNull()) {
                response.setSuccess(jsonObject.get("success").getAsBoolean());
            }
            
            // Parse message field
            if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                response.setMessage(jsonObject.get("message").getAsString());
            }
            
            // Parse data field - This is the critical part
            if (jsonObject.has("data") && !jsonObject.get("data").isJsonNull()) {
                JsonElement dataElement = jsonObject.get("data");
                List<Soal> soalList = parseDataElement(dataElement, context);
                response.setData(soalList);
            } else {
                // Set empty list if data is null
                response.setData(new ArrayList<>());
            }
            
        } catch (Exception e) {
            android.util.Log.e("SoalResponseDeserializer", "Error parsing SoalResponse: " + e.getMessage(), e);
            
            // Set default values if parsing fails
            if (response.getData() == null) {
                response.setData(new ArrayList<>());
            }
            if (response.getMessage() == null) {
                response.setMessage("Error parsing response");
            }
        }
        
        return response;
    }
    
    /**
     * Parse data element yang bisa berupa array atau single object
     */
    private List<Soal> parseDataElement(JsonElement dataElement, JsonDeserializationContext context) {
        List<Soal> soalList = new ArrayList<>();
        
        try {
            if (dataElement.isJsonArray()) {
                // Case 1: data is an array of Soal objects
                android.util.Log.d("SoalResponseDeserializer", "Parsing data as array");
                JsonArray dataArray = dataElement.getAsJsonArray();
                
                for (JsonElement element : dataArray) {
                    try {
                        Soal soal = context.deserialize(element, Soal.class);
                        if (soal != null) {
                            soalList.add(soal);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("SoalResponseDeserializer", "Error parsing Soal in array: " + e.getMessage(), e);
                        // Continue with other elements
                    }
                }
                
            } else if (dataElement.isJsonObject()) {
                // Case 2: data is a single Soal object
                android.util.Log.d("SoalResponseDeserializer", "Parsing data as single object");
                try {
                    Soal soal = context.deserialize(dataElement, Soal.class);
                    if (soal != null) {
                        soalList.add(soal);
                    }
                } catch (Exception e) {
                    android.util.Log.e("SoalResponseDeserializer", "Error parsing single Soal object: " + e.getMessage(), e);
                }
                
            } else if (dataElement.isJsonPrimitive()) {
                // Case 3: data is a primitive (shouldn't happen, but handle gracefully)
                android.util.Log.w("SoalResponseDeserializer", "Data is primitive, expected object or array");
                
            } else {
                // Case 4: Unknown format
                android.util.Log.w("SoalResponseDeserializer", "Unknown data format: " + dataElement.toString());
            }
            
        } catch (Exception e) {
            android.util.Log.e("SoalResponseDeserializer", "Error parsing data element: " + e.getMessage(), e);
        }
        
        return soalList;
    }
}
