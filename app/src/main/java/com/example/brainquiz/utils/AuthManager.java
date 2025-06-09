package com.example.brainquiz.utils;

import android.content.Context;
import android.content.Intent;
import com.example.brainquiz.activities.LoginActivity;
import com.example.brainquiz.models.User;

/**
 * Manager class untuk authentication logic
 */
public class AuthManager {
    
    private static AuthManager instance;
    private Context context;
    
    private AuthManager(Context context) {
        this.context = context.getApplicationContext();
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }
    
    /**
     * Save user login data
     */
    public void saveUserLogin(User user, String token) {
        SharedPreferencesHelper.saveUserData(
            context,
            user.getId(),
            token,
            user.getUsername(),
            user.getEmail()
        );
    }
    
    /**
     * Save user login data with individual parameters
     */
    public void saveUserLogin(int userId, String token, String username, String email) {
        SharedPreferencesHelper.saveUserData(context, userId, token, username, email);
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return SharedPreferencesHelper.isLoggedIn(context);
    }
    
    /**
     * Get current user ID
     */
    public int getCurrentUserId() {
        return SharedPreferencesHelper.getUserId(context);
    }
    
    /**
     * Get current user token
     */
    public String getCurrentToken() {
        return SharedPreferencesHelper.getToken(context);
    }
    
    /**
     * Get current username
     */
    public String getCurrentUsername() {
        return SharedPreferencesHelper.getUsername(context);
    }
    
    /**
     * Get current user email
     */
    public String getCurrentEmail() {
        return SharedPreferencesHelper.getEmail(context);
    }
    
    /**
     * Get authorization header for API calls
     */
    public String getAuthorizationHeader() {
        String token = getCurrentToken();
        if (token.isEmpty()) {
            return "";
        }
        return "Bearer " + token;
    }
    
    /**
     * Check if token is valid (not empty)
     */
    public boolean hasValidToken() {
        return !getCurrentToken().isEmpty();
    }
    
    /**
     * Logout user and clear all data
     */
    public void logout() {
        SharedPreferencesHelper.clearUserData(context);
    }
    
    /**
     * Logout user and redirect to login screen
     */
    public void logoutAndRedirect(Context activityContext) {
        logout();
        Intent intent = new Intent(activityContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activityContext.startActivity(intent);
    }
    
    /**
     * Check authentication and redirect to login if not authenticated
     * @param activityContext Current activity context
     * @return true if authenticated, false if redirected to login
     */
    public boolean requireAuthentication(Context activityContext) {
        if (!isLoggedIn() || !hasValidToken()) {
            logoutAndRedirect(activityContext);
            return false;
        }
        return true;
    }
    
    /**
     * Update user token (for token refresh)
     */
    public void updateToken(String newToken) {
        SharedPreferencesHelper.saveString(context, AppConstants.PREF_TOKEN, newToken);
    }
    
    /**
     * Update user profile data
     */
    public void updateUserProfile(String username, String email) {
        SharedPreferencesHelper.saveString(context, AppConstants.PREF_USERNAME, username);
        SharedPreferencesHelper.saveString(context, AppConstants.PREF_EMAIL, email);
    }
    
    /**
     * Get current user as User object
     */
    public User getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }
        
        User user = new User();
        user.setId(getCurrentUserId());
        user.setUsername(getCurrentUsername());
        user.setEmail(getCurrentEmail());
        return user;
    }
    
    /**
     * Check if current user is admin (example implementation)
     */
    public boolean isAdmin() {
        // This is a placeholder implementation
        // You can implement actual admin check logic here
        return getCurrentUserId() == 1; // Assuming user ID 1 is admin
    }
    
    /**
     * Validate session (check if token is still valid)
     * This method can be extended to make API call to validate token
     */
    public boolean isSessionValid() {
        return isLoggedIn() && hasValidToken();
    }
    
    /**
     * Get user display name (username or email)
     */
    public String getUserDisplayName() {
        String username = getCurrentUsername();
        if (username.isEmpty()) {
            return getCurrentEmail();
        }
        return username;
    }
}
