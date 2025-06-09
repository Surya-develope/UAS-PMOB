package com.example.brainquiz.utils;

/**
 * Constants class untuk API endpoints dan API-related constants
 */
public class ApiConstants {
    
    // Base URLs
    public static final String BASE_URL = "http://192.168.1.100/brainquiz/";
    public static final String API_BASE_URL = BASE_URL + "api/";
    
    // Authentication Endpoints
    public static final String ENDPOINT_LOGIN = "login";
    public static final String ENDPOINT_REGISTER = "register";
    public static final String ENDPOINT_LOGOUT = "logout";
    
    // Data Management Endpoints
    public static final String ENDPOINT_TINGKATAN = "tingkatan";
    public static final String ENDPOINT_KATEGORI = "kategori";
    public static final String ENDPOINT_KELAS = "kelas";
    public static final String ENDPOINT_PENDIDIKAN = "pendidikan";
    public static final String ENDPOINT_KUIS = "kuis";
    public static final String ENDPOINT_SOAL = "soal";
    public static final String ENDPOINT_JAWABAN = "jawaban";
    public static final String ENDPOINT_HASIL_KUIS = "hasil-kuis";
    
    // HTTP Methods
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";
    
    // Request Headers
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT = "Accept";
    
    // Content Types
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    
    // Response Status Codes
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    
    // API Response Keys
    public static final String KEY_SUCCESS = "success";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_DATA = "data";
    public static final String KEY_ERROR = "error";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_USER = "user";
    
    // Request Parameters
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_USER_ID = "user_id";
    public static final String PARAM_KUIS_ID = "kuis_id";
    public static final String PARAM_SOAL_ID = "soal_id";
    public static final String PARAM_JAWABAN_ID = "jawaban_id";
    
    // Timeout Settings (in milliseconds)
    public static final int CONNECT_TIMEOUT = 30000; // 30 seconds
    public static final int READ_TIMEOUT = 30000; // 30 seconds
    public static final int WRITE_TIMEOUT = 30000; // 30 seconds
    
    // Retry Settings
    public static final int MAX_RETRY_ATTEMPTS = 3;
    public static final int RETRY_DELAY_MS = 1000; // 1 second
    
    // Cache Settings
    public static final int CACHE_SIZE = 10 * 1024 * 1024; // 10 MB
    public static final int CACHE_MAX_AGE = 60; // 1 minute
    public static final int CACHE_MAX_STALE = 60 * 60 * 24 * 7; // 1 week
    
    // Error Messages
    public static final String ERROR_NETWORK = "Tidak ada koneksi internet";
    public static final String ERROR_SERVER = "Terjadi kesalahan pada server";
    public static final String ERROR_TIMEOUT = "Koneksi timeout";
    public static final String ERROR_UNKNOWN = "Terjadi kesalahan yang tidak diketahui";
    public static final String ERROR_INVALID_RESPONSE = "Response tidak valid";
    public static final String ERROR_UNAUTHORIZED = "Sesi telah berakhir, silakan login kembali";
    
    // Success Messages
    public static final String SUCCESS_LOGIN = "Login berhasil";
    public static final String SUCCESS_REGISTER = "Registrasi berhasil";
    public static final String SUCCESS_LOGOUT = "Logout berhasil";
    public static final String SUCCESS_DATA_SAVED = "Data berhasil disimpan";
    public static final String SUCCESS_DATA_UPDATED = "Data berhasil diupdate";
    public static final String SUCCESS_DATA_DELETED = "Data berhasil dihapus";
    
    // Validation Messages
    public static final String VALIDATION_EMAIL_REQUIRED = "Email tidak boleh kosong";
    public static final String VALIDATION_EMAIL_INVALID = "Format email tidak valid";
    public static final String VALIDATION_PASSWORD_REQUIRED = "Password tidak boleh kosong";
    public static final String VALIDATION_PASSWORD_MIN_LENGTH = "Password minimal 6 karakter";
    public static final String VALIDATION_USERNAME_REQUIRED = "Username tidak boleh kosong";
    
    // Private constructor to prevent instantiation
    private ApiConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
