package com.example.brainquiz.utils;

import com.example.brainquiz.filter.HasilKuis;
import com.example.brainquiz.models.HasilKuisResponse;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Gson deserializer untuk HasilKuisResponse
 * Menangani kasus dimana data bisa berupa array atau single object
 */
public class HasilKuisResponseDeserializer implements JsonDeserializer<HasilKuisResponse> {
    
    @Override
    public HasilKuisResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        
        JsonObject jsonObject = json.getAsJsonObject();
        HasilKuisResponse response = new HasilKuisResponse();
        
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
                List<HasilKuis> hasilKuisList = parseDataElement(dataElement, context);
                response.setData(hasilKuisList);
            } else {
                // Set empty list if data is null
                response.setData(new ArrayList<>());
            }
            
        } catch (Exception e) {
            android.util.Log.e("HasilKuisResponseDeserializer", "Error parsing HasilKuisResponse: " + e.getMessage(), e);
            
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
    private List<HasilKuis> parseDataElement(JsonElement dataElement, JsonDeserializationContext context) {
        List<HasilKuis> hasilKuisList = new ArrayList<>();
        
        try {
            if (dataElement.isJsonArray()) {
                // Case 1: data is an array of HasilKuis objects
                android.util.Log.d("HasilKuisResponseDeserializer", "Parsing data as array");
                JsonArray dataArray = dataElement.getAsJsonArray();
                
                for (JsonElement element : dataArray) {
                    try {
                        HasilKuis hasilKuis = context.deserialize(element, HasilKuis.class);
                        if (hasilKuis != null) {
                            hasilKuisList.add(hasilKuis);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("HasilKuisResponseDeserializer", "Error parsing HasilKuis in array: " + e.getMessage(), e);
                        // Continue with other elements
                    }
                }
                
            } else if (dataElement.isJsonObject()) {
                // Case 2: data is a single HasilKuis object
                android.util.Log.d("HasilKuisResponseDeserializer", "Parsing data as single object");
                try {
                    HasilKuis hasilKuis = context.deserialize(dataElement, HasilKuis.class);
                    if (hasilKuis != null) {
                        hasilKuisList.add(hasilKuis);
                    }
                } catch (Exception e) {
                    android.util.Log.e("HasilKuisResponseDeserializer", "Error parsing single HasilKuis object: " + e.getMessage(), e);
                }
                
            } else if (dataElement.isJsonPrimitive()) {
                // Case 3: data is a primitive (shouldn't happen, but handle gracefully)
                android.util.Log.w("HasilKuisResponseDeserializer", "Data is primitive, expected object or array");
                
            } else {
                // Case 4: Unknown format
                android.util.Log.w("HasilKuisResponseDeserializer", "Unknown data format: " + dataElement.toString());
            }
            
        } catch (Exception e) {
            android.util.Log.e("HasilKuisResponseDeserializer", "Error parsing data element: " + e.getMessage(), e);
        }
        
        return hasilKuisList;
    }
}
