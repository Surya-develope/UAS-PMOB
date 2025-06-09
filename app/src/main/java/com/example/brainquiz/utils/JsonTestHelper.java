package com.example.brainquiz.utils;

import android.util.Log;
import com.example.brainquiz.filter.Soal;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Helper class untuk testing JSON parsing dengan custom deserializer
 */
public class JsonTestHelper {
    
    private static final String TAG = "JsonTestHelper";
    
    /**
     * Test parsing dengan berbagai format options_json
     */
    public static void testSoalParsing() {
        Gson gson = GsonHelper.getGson();
        
        // Test Case 1: options_json sebagai string JSON object
        String jsonCase1 = "{\n" +
                "  \"ID\": 1,\n" +
                "  \"question\": \"Test Question 1\",\n" +
                "  \"correct_answer\": \"A\",\n" +
                "  \"kuis_id\": 1,\n" +
                "  \"options_json\": \"{\\\"A\\\":\\\"Option A\\\",\\\"B\\\":\\\"Option B\\\",\\\"C\\\":\\\"Option C\\\",\\\"D\\\":\\\"Option D\\\"}\"\n" +
                "}";
        
        // Test Case 2: options_json sebagai JSON object langsung
        String jsonCase2 = "{\n" +
                "  \"ID\": 2,\n" +
                "  \"question\": \"Test Question 2\",\n" +
                "  \"correct_answer\": \"B\",\n" +
                "  \"kuis_id\": 1,\n" +
                "  \"options_json\": {\"A\":\"Option A\",\"B\":\"Option B\",\"C\":\"Option C\",\"D\":\"Option D\"}\n" +
                "}";
        
        // Test Case 3: options_json sebagai array
        String jsonCase3 = "{\n" +
                "  \"ID\": 3,\n" +
                "  \"question\": \"Test Question 3\",\n" +
                "  \"correct_answer\": \"C\",\n" +
                "  \"kuis_id\": 1,\n" +
                "  \"options_json\": [\"Option A\", \"Option B\", \"Option C\", \"Option D\"]\n" +
                "}";
        
        // Test Case 4: options_json null atau kosong
        String jsonCase4 = "{\n" +
                "  \"ID\": 4,\n" +
                "  \"question\": \"Test Question 4\",\n" +
                "  \"correct_answer\": \"D\",\n" +
                "  \"kuis_id\": 1,\n" +
                "  \"options_json\": null\n" +
                "}";
        
        Log.d(TAG, "=== Testing Soal JSON Parsing ===");
        
        // Test each case
        testParseCase(gson, "Case 1 (String JSON)", jsonCase1);
        testParseCase(gson, "Case 2 (Direct Object)", jsonCase2);
        testParseCase(gson, "Case 3 (Array)", jsonCase3);
        testParseCase(gson, "Case 4 (Null)", jsonCase4);
        
        Log.d(TAG, "=== Testing Complete ===");
    }
    
    private static void testParseCase(Gson gson, String caseName, String json) {
        try {
            Log.d(TAG, "\n--- " + caseName + " ---");
            Log.d(TAG, "Input JSON: " + json);
            
            Soal soal = gson.fromJson(json, Soal.class);
            
            if (soal != null) {
                Log.d(TAG, "✅ Parsing SUCCESS");
                Log.d(TAG, "ID: " + soal.getId());
                Log.d(TAG, "Question: " + soal.getQuestion());
                Log.d(TAG, "Correct Answer: " + soal.getCorrectAnswer());
                Log.d(TAG, "Kuis ID: " + soal.getKuisId());
                
                if (soal.getOptionsJson() != null) {
                    Log.d(TAG, "Options:");
                    for (String key : soal.getOptionsJson().keySet()) {
                        Log.d(TAG, "  " + key + ": " + soal.getOptionsJson().get(key));
                    }
                } else {
                    Log.w(TAG, "⚠️ Options is null");
                }
            } else {
                Log.e(TAG, "❌ Parsing returned null");
            }
            
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "❌ JsonSyntaxException: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Unexpected error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Test parsing response yang bermasalah (yang menyebabkan error sebelumnya)
     */
    public static void testProblematicResponse() {
        Gson gson = GsonHelper.getGson();
        
        // Simulasi response yang menyebabkan error sebelumnya
        String problematicJson = "{\n" +
                "  \"success\": true,\n" +
                "  \"message\": \"Data retrieved successfully\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"ID\": 1,\n" +
                "      \"question\": \"What is the capital of Indonesia?\",\n" +
                "      \"correct_answer\": \"A\",\n" +
                "      \"kuis_id\": 1,\n" +
                "      \"options_json\": \"[\\\"Jakarta\\\", \\\"Bandung\\\", \\\"Surabaya\\\", \\\"Medan\\\"]\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        
        Log.d(TAG, "\n=== Testing Problematic Response ===");
        Log.d(TAG, "Response JSON: " + problematicJson);
        
        try {
            // Parse sebagai SoalResponse
            com.example.brainquiz.models.SoalResponse response = gson.fromJson(problematicJson, com.example.brainquiz.models.SoalResponse.class);
            
            if (response != null && response.isSuccess() && response.getData() != null) {
                Log.d(TAG, "✅ SoalResponse parsing SUCCESS");
                Log.d(TAG, "Success: " + response.isSuccess());
                Log.d(TAG, "Message: " + response.getMessage());
                Log.d(TAG, "Data count: " + response.getData().size());
                
                for (int i = 0; i < response.getData().size(); i++) {
                    Soal soal = response.getData().get(i);
                    Log.d(TAG, "\nSoal " + (i + 1) + ":");
                    Log.d(TAG, "  ID: " + soal.getId());
                    Log.d(TAG, "  Question: " + soal.getQuestion());
                    Log.d(TAG, "  Correct Answer: " + soal.getCorrectAnswer());
                    
                    if (soal.getOptionsJson() != null) {
                        Log.d(TAG, "  Options:");
                        for (String key : soal.getOptionsJson().keySet()) {
                            Log.d(TAG, "    " + key + ": " + soal.getOptionsJson().get(key));
                        }
                    }
                }
            } else {
                Log.e(TAG, "❌ SoalResponse parsing failed or empty");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error parsing problematic response: " + e.getMessage(), e);
        }
        
        Log.d(TAG, "=== Problematic Response Test Complete ===");
    }

    /**
     * Test parsing SoalResponse dengan data sebagai single object (EditSoal case)
     */
    public static void testEditSoalResponse() {
        Gson gson = GsonHelper.getGson();

        // Simulasi response dari updateSoal API yang mengembalikan single object
        String editSoalResponseJson = "{\n" +
                "  \"success\": true,\n" +
                "  \"message\": \"Soal updated successfully\",\n" +
                "  \"data\": {\n" +
                "    \"ID\": 1,\n" +
                "    \"question\": \"Updated question?\",\n" +
                "    \"correct_answer\": \"A\",\n" +
                "    \"kuis_id\": 1,\n" +
                "    \"options_json\": \"{\\\"A\\\":\\\"Updated Option A\\\",\\\"B\\\":\\\"Updated Option B\\\",\\\"C\\\":\\\"Updated Option C\\\",\\\"D\\\":\\\"Updated Option D\\\"}\"\n" +
                "  }\n" +
                "}";

        Log.d(TAG, "\n=== Testing EditSoal Response (Single Object) ===");
        Log.d(TAG, "Response JSON: " + editSoalResponseJson);

        try {
            com.example.brainquiz.models.SoalResponse response = gson.fromJson(editSoalResponseJson, com.example.brainquiz.models.SoalResponse.class);

            if (response != null) {
                Log.d(TAG, "✅ EditSoal SoalResponse parsing SUCCESS");
                Log.d(TAG, "Success: " + response.isSuccess());
                Log.d(TAG, "Message: " + response.getMessage());
                Log.d(TAG, "Data count: " + response.getData().size());

                if (!response.getData().isEmpty()) {
                    Soal soal = response.getData().get(0);
                    Log.d(TAG, "\nUpdated Soal:");
                    Log.d(TAG, "  ID: " + soal.getId());
                    Log.d(TAG, "  Question: " + soal.getQuestion());
                    Log.d(TAG, "  Correct Answer: " + soal.getCorrectAnswer());

                    if (soal.getOptionsJson() != null) {
                        Log.d(TAG, "  Options:");
                        for (String key : soal.getOptionsJson().keySet()) {
                            Log.d(TAG, "    " + key + ": " + soal.getOptionsJson().get(key));
                        }
                    }
                } else {
                    Log.w(TAG, "⚠️ Data list is empty");
                }
            } else {
                Log.e(TAG, "❌ EditSoal SoalResponse parsing returned null");
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error parsing EditSoal response: " + e.getMessage(), e);
        }

        Log.d(TAG, "=== EditSoal Response Test Complete ===");
    }

    /**
     * Test parsing HasilKuisResponse dengan data sebagai single object
     */
    public static void testHasilKuisResponse() {
        Gson gson = GsonHelper.getGson();

        // Test Case 1: HasilKuisResponse dengan data sebagai single object
        String singleObjectResponseJson = "{\n" +
                "  \"success\": true,\n" +
                "  \"message\": \"Result found\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 1,\n" +
                "    \"user_id\": 1,\n" +
                "    \"kuis_id\": 1,\n" +
                "    \"score\": 85,\n" +
                "    \"grade\": \"A\",\n" +
                "    \"correct_answers\": 17,\n" +
                "    \"total_questions\": 20,\n" +
                "    \"percentage\": 85.0,\n" +
                "    \"status\": \"LULUS\",\n" +
                "    \"completed_at\": \"2024-01-15T10:30:00\",\n" +
                "    \"kuis_title\": \"Quiz Matematika Dasar\"\n" +
                "  }\n" +
                "}";

        // Test Case 2: HasilKuisResponse dengan data sebagai array
        String arrayResponseJson = "{\n" +
                "  \"success\": true,\n" +
                "  \"message\": \"Results found\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"user_id\": 1,\n" +
                "      \"kuis_id\": 1,\n" +
                "      \"score\": 85,\n" +
                "      \"grade\": \"A\",\n" +
                "      \"correct_answers\": 17,\n" +
                "      \"total_questions\": 20,\n" +
                "      \"percentage\": 85.0,\n" +
                "      \"status\": \"LULUS\",\n" +
                "      \"completed_at\": \"2024-01-15T10:30:00\",\n" +
                "      \"kuis_title\": \"Quiz Matematika Dasar\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Test Case 3: 404 Response (no data)
        String notFoundResponseJson = "{\n" +
                "  \"success\": false,\n" +
                "  \"message\": \"Result not found\",\n" +
                "  \"data\": null\n" +
                "}";

        Log.d(TAG, "\n=== Testing HasilKuisResponse Parsing ===");

        // Test each case
        testHasilKuisCase(gson, "Single Object Response", singleObjectResponseJson);
        testHasilKuisCase(gson, "Array Response", arrayResponseJson);
        testHasilKuisCase(gson, "404 Not Found Response", notFoundResponseJson);

        Log.d(TAG, "=== HasilKuisResponse Test Complete ===");
    }

    private static void testHasilKuisCase(Gson gson, String caseName, String json) {
        try {
            Log.d(TAG, "\n--- " + caseName + " ---");
            Log.d(TAG, "Input JSON: " + json);

            com.example.brainquiz.models.HasilKuisResponse response = gson.fromJson(json, com.example.brainquiz.models.HasilKuisResponse.class);

            if (response != null) {
                Log.d(TAG, "✅ Parsing SUCCESS");
                Log.d(TAG, "Success: " + response.isSuccess());
                Log.d(TAG, "Message: " + response.getMessage());
                Log.d(TAG, "Data count: " + response.getData().size());

                if (!response.getData().isEmpty()) {
                    for (int i = 0; i < response.getData().size(); i++) {
                        com.example.brainquiz.filter.HasilKuis hasil = response.getData().get(i);
                        Log.d(TAG, "\nHasil " + (i + 1) + ":");
                        Log.d(TAG, "  ID: " + hasil.getId());
                        Log.d(TAG, "  Score: " + hasil.getScore());
                        Log.d(TAG, "  Grade: " + hasil.getGrade());
                        Log.d(TAG, "  Status: " + hasil.getStatus());
                        Log.d(TAG, "  Kuis Title: " + hasil.getKuisTitle());
                    }
                } else {
                    Log.d(TAG, "No hasil data (expected for 404 responses)");
                }
            } else {
                Log.e(TAG, "❌ Parsing returned null");
            }

        } catch (JsonSyntaxException e) {
            Log.e(TAG, "❌ JsonSyntaxException: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Unexpected error: " + e.getMessage(), e);
        }
    }
}
