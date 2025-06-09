package com.example.brainquiz.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class untuk SharedPreferences operations
 */
public class SharedPreferencesHelper {
    
    private static final String PREF_NAME = "BrainQuizPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Save user login data
     */
    public static void saveUserData(Context context, int userId, String token, String username, String email) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }
    
    /**
     * Get user ID
     */
    public static int getUserId(Context context) {
        return getSharedPreferences(context).getInt(KEY_USER_ID, 1); // Default fallback to 1
    }
    
    /**
     * Get token
     */
    public static String getToken(Context context) {
        return getSharedPreferences(context).getString(KEY_TOKEN, "");
    }
    
    /**
     * Get username
     */
    public static String getUsername(Context context) {
        return getSharedPreferences(context).getString(KEY_USERNAME, "");
    }
    
    /**
     * Get email
     */
    public static String getEmail(Context context) {
        return getSharedPreferences(context).getString(KEY_EMAIL, "");
    }
    
    /**
     * Check if user is logged in
     */
    public static boolean isLoggedIn(Context context) {
        return getSharedPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * Clear all user data (logout)
     */
    public static void clearUserData(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }
    
    /**
     * Save string value
     */
    public static void saveString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.apply();
    }
    
    /**
     * Get string value
     */
    public static String getString(Context context, String key, String defaultValue) {
        return getSharedPreferences(context).getString(key, defaultValue);
    }
    
    /**
     * Save integer value
     */
    public static void saveInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.apply();
    }
    
    /**
     * Get integer value
     */
    public static int getInt(Context context, String key, int defaultValue) {
        return getSharedPreferences(context).getInt(key, defaultValue);
    }
    
    /**
     * Save boolean value
     */
    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    
    /**
     * Get boolean value
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getSharedPreferences(context).getBoolean(key, defaultValue);
    }
}
