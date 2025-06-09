package com.example.brainquiz.utils;

/**
 * Constants class untuk application-wide constants
 */
public class AppConstants {
    
    // App Information
    public static final String APP_NAME = "BrainQuiz";
    public static final String APP_VERSION = "1.0.0";
    
    // SharedPreferences Keys
    public static final String PREF_NAME = "BrainQuizPrefs";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_TOKEN = "token";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_IS_LOGGED_IN = "is_logged_in";
    public static final String PREF_FIRST_TIME = "first_time";
    public static final String PREF_LAST_SYNC = "last_sync";
    
    // Intent Extra Keys
    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_KUIS_ID = "extra_kuis_id";
    public static final String EXTRA_SOAL_ID = "extra_soal_id";
    public static final String EXTRA_KATEGORI_ID = "extra_kategori_id";
    public static final String EXTRA_KELAS_ID = "extra_kelas_id";
    public static final String EXTRA_PENDIDIKAN_ID = "extra_pendidikan_id";
    public static final String EXTRA_TINGKATAN_ID = "extra_tingkatan_id";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_MODE = "extra_mode";
    public static final String EXTRA_DATA = "extra_data";
    
    // Activity Modes
    public static final String MODE_ADD = "add";
    public static final String MODE_EDIT = "edit";
    public static final String MODE_VIEW = "view";
    
    // Quiz Settings
    public static final int DEFAULT_QUIZ_TIME_LIMIT = 30; // minutes
    public static final int MIN_QUIZ_QUESTIONS = 1;
    public static final int MAX_QUIZ_QUESTIONS = 50;
    public static final int DEFAULT_QUIZ_QUESTIONS = 10;
    
    // Scoring System
    public static final int POINTS_CORRECT_ANSWER = 10;
    public static final int POINTS_WRONG_ANSWER = 0;
    public static final double PASSING_GRADE = 60.0; // percentage
    
    // Grade Levels
    public static final String GRADE_A = "A";
    public static final String GRADE_B = "B";
    public static final String GRADE_C = "C";
    public static final String GRADE_D = "D";
    public static final String GRADE_E = "E";
    
    // Grade Thresholds
    public static final double GRADE_A_THRESHOLD = 90.0;
    public static final double GRADE_B_THRESHOLD = 80.0;
    public static final double GRADE_C_THRESHOLD = 70.0;
    public static final double GRADE_D_THRESHOLD = 60.0;
    // Below 60 is Grade E
    
    // UI Constants
    public static final int SPLASH_DELAY = 2000; // milliseconds
    public static final int ANIMATION_DURATION = 300; // milliseconds
    public static final int TOAST_DURATION_SHORT = 2000; // milliseconds
    public static final int TOAST_DURATION_LONG = 3500; // milliseconds
    
    // List View Constants
    public static final int ITEMS_PER_PAGE = 20;
    public static final int LOAD_MORE_THRESHOLD = 5;
    
    // Validation Constants
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 50;
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 30;
    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
    
    // File Constants
    public static final String IMAGE_DIRECTORY = "BrainQuiz/Images";
    public static final String CACHE_DIRECTORY = "BrainQuiz/Cache";
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    
    // Date Format Constants
    public static final String DATE_FORMAT_DISPLAY = "dd/MM/yyyy";
    public static final String DATE_FORMAT_API = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT_DISPLAY = "dd/MM/yyyy HH:mm";
    public static final String DATETIME_FORMAT_API = "yyyy-MM-dd HH:mm:ss";
    
    // Error Codes
    public static final int ERROR_CODE_NETWORK = 1001;
    public static final int ERROR_CODE_SERVER = 1002;
    public static final int ERROR_CODE_TIMEOUT = 1003;
    public static final int ERROR_CODE_UNAUTHORIZED = 1004;
    public static final int ERROR_CODE_VALIDATION = 1005;
    public static final int ERROR_CODE_UNKNOWN = 1999;
    
    // Request Codes
    public static final int REQUEST_CODE_LOGIN = 2001;
    public static final int REQUEST_CODE_REGISTER = 2002;
    public static final int REQUEST_CODE_EDIT = 2003;
    public static final int REQUEST_CODE_ADD = 2004;
    public static final int REQUEST_CODE_DELETE = 2005;
    
    // Result Codes
    public static final int RESULT_CODE_SUCCESS = 3001;
    public static final int RESULT_CODE_ERROR = 3002;
    public static final int RESULT_CODE_CANCELLED = 3003;
    
    // Default Values
    public static final int DEFAULT_USER_ID = 1;
    public static final String DEFAULT_TOKEN = "";
    public static final String DEFAULT_USERNAME = "Guest";
    public static final String DEFAULT_EMAIL = "";
    
    // Private constructor to prevent instantiation
    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
