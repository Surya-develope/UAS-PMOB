package com.example.brainquiz.utils;

import com.example.brainquiz.filter.Soal;
import com.example.brainquiz.models.SoalResponse;
import com.example.brainquiz.models.HasilKuisResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Helper class untuk membuat Gson instance dengan custom deserializers
 */
public class GsonHelper {
    
    private static Gson gson;
    
    /**
     * Get Gson instance dengan custom deserializers
     */
    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Soal.class, new SoalDeserializer())
                    .registerTypeAdapter(SoalResponse.class, new SoalResponseDeserializer())
                    .registerTypeAdapter(HasilKuisResponse.class, new HasilKuisResponseDeserializer())
                    .setLenient() // Allow lenient parsing
                    .create();
        }
        return gson;
    }
    
    /**
     * Create new Gson instance (for testing purposes)
     */
    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Soal.class, new SoalDeserializer())
                .registerTypeAdapter(SoalResponse.class, new SoalResponseDeserializer())
                .registerTypeAdapter(HasilKuisResponse.class, new HasilKuisResponseDeserializer())
                .setLenient()
                .create();
    }
}
