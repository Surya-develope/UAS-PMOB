package com.example.brainquiz.utils;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

/**
 * Helper class untuk input validation operations
 */
public class ValidationHelper {
    
    /**
     * Validate email format
     * @param email Email string to validate
     * @return true if valid email format
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    /**
     * Validate password strength
     * @param password Password to validate
     * @return true if password meets minimum requirements
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }
    
    /**
     * Check if EditText is empty
     * @param editText EditText to check
     * @return true if empty
     */
    public static boolean isEmpty(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString().trim());
    }
    
    /**
     * Get trimmed text from EditText
     * @param editText EditText to get text from
     * @return Trimmed string
     */
    public static String getTrimmedText(EditText editText) {
        return editText.getText().toString().trim();
    }
    
    /**
     * Validate required field and show error if empty
     * @param editText EditText to validate
     * @param errorMessage Error message to show
     * @return true if valid (not empty)
     */
    public static boolean validateRequired(EditText editText, String errorMessage) {
        if (isEmpty(editText)) {
            editText.setError(errorMessage);
            editText.requestFocus();
            return false;
        }
        editText.setError(null);
        return true;
    }
    
    /**
     * Validate email field
     * @param editText EditText containing email
     * @return true if valid email
     */
    public static boolean validateEmail(EditText editText) {
        String email = getTrimmedText(editText);
        
        if (isEmpty(editText)) {
            editText.setError("Email tidak boleh kosong");
            editText.requestFocus();
            return false;
        }
        
        if (!isValidEmail(email)) {
            editText.setError("Format email tidak valid");
            editText.requestFocus();
            return false;
        }
        
        editText.setError(null);
        return true;
    }
    
    /**
     * Validate password field
     * @param editText EditText containing password
     * @return true if valid password
     */
    public static boolean validatePassword(EditText editText) {
        String password = getTrimmedText(editText);
        
        if (isEmpty(editText)) {
            editText.setError("Password tidak boleh kosong");
            editText.requestFocus();
            return false;
        }
        
        if (!isValidPassword(password)) {
            editText.setError("Password minimal 6 karakter");
            editText.requestFocus();
            return false;
        }
        
        editText.setError(null);
        return true;
    }
    
    /**
     * Validate confirm password field
     * @param passwordEditText Original password EditText
     * @param confirmPasswordEditText Confirm password EditText
     * @return true if passwords match
     */
    public static boolean validateConfirmPassword(EditText passwordEditText, EditText confirmPasswordEditText) {
        String password = getTrimmedText(passwordEditText);
        String confirmPassword = getTrimmedText(confirmPasswordEditText);
        
        if (isEmpty(confirmPasswordEditText)) {
            confirmPasswordEditText.setError("Konfirmasi password tidak boleh kosong");
            confirmPasswordEditText.requestFocus();
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Password tidak sama");
            confirmPasswordEditText.requestFocus();
            return false;
        }
        
        confirmPasswordEditText.setError(null);
        return true;
    }
    
    /**
     * Validate minimum length
     * @param editText EditText to validate
     * @param minLength Minimum required length
     * @param fieldName Name of the field for error message
     * @return true if meets minimum length
     */
    public static boolean validateMinLength(EditText editText, int minLength, String fieldName) {
        String text = getTrimmedText(editText);
        
        if (text.length() < minLength) {
            editText.setError(fieldName + " minimal " + minLength + " karakter");
            editText.requestFocus();
            return false;
        }
        
        editText.setError(null);
        return true;
    }
    
    /**
     * Validate numeric input
     * @param editText EditText to validate
     * @param fieldName Name of the field for error message
     * @return true if valid number
     */
    public static boolean validateNumeric(EditText editText, String fieldName) {
        String text = getTrimmedText(editText);
        
        if (isEmpty(editText)) {
            editText.setError(fieldName + " tidak boleh kosong");
            editText.requestFocus();
            return false;
        }
        
        try {
            Integer.parseInt(text);
            editText.setError(null);
            return true;
        } catch (NumberFormatException e) {
            editText.setError(fieldName + " harus berupa angka");
            editText.requestFocus();
            return false;
        }
    }
}
