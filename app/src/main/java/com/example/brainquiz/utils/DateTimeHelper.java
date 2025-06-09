package com.example.brainquiz.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Helper class untuk date/time formatting operations
 */
public class DateTimeHelper {
    
    // Common date formats
    public static final String FORMAT_DATE_TIME = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMAT_DATE = "dd/MM/yyyy";
    public static final String FORMAT_TIME = "HH:mm:ss";
    public static final String FORMAT_DATE_TIME_SHORT = "dd/MM/yy HH:mm";
    public static final String FORMAT_API_DATETIME = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * Get current date time as formatted string
     * @param format Date format pattern
     * @return Formatted date time string
     */
    public static String getCurrentDateTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date());
    }
    
    /**
     * Get current date time with default format
     * @return Formatted date time string (dd/MM/yyyy HH:mm:ss)
     */
    public static String getCurrentDateTime() {
        return getCurrentDateTime(FORMAT_DATE_TIME);
    }
    
    /**
     * Get current date only
     * @return Formatted date string (dd/MM/yyyy)
     */
    public static String getCurrentDate() {
        return getCurrentDateTime(FORMAT_DATE);
    }
    
    /**
     * Get current time only
     * @return Formatted time string (HH:mm:ss)
     */
    public static String getCurrentTime() {
        return getCurrentDateTime(FORMAT_TIME);
    }
    
    /**
     * Format timestamp to readable string
     * @param timestamp Timestamp in milliseconds
     * @param format Date format pattern
     * @return Formatted date string
     */
    public static String formatTimestamp(long timestamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * Format timestamp with default format
     * @param timestamp Timestamp in milliseconds
     * @return Formatted date string (dd/MM/yyyy HH:mm:ss)
     */
    public static String formatTimestamp(long timestamp) {
        return formatTimestamp(timestamp, FORMAT_DATE_TIME);
    }
    
    /**
     * Get time ago string (e.g., "2 minutes ago", "1 hour ago")
     * @param timestamp Timestamp in milliseconds
     * @return Time ago string
     */
    public static String getTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        
        if (diff < TimeUnit.MINUTES.toMillis(1)) {
            return "Baru saja";
        } else if (diff < TimeUnit.HOURS.toMillis(1)) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            return minutes + " menit yang lalu";
        } else if (diff < TimeUnit.DAYS.toMillis(1)) {
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            return hours + " jam yang lalu";
        } else if (diff < TimeUnit.DAYS.toMillis(7)) {
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            return days + " hari yang lalu";
        } else {
            return formatTimestamp(timestamp, FORMAT_DATE);
        }
    }
    
    /**
     * Calculate duration between two timestamps
     * @param startTime Start timestamp in milliseconds
     * @param endTime End timestamp in milliseconds
     * @return Duration string (e.g., "2 menit 30 detik")
     */
    public static String calculateDuration(long startTime, long endTime) {
        long diff = endTime - startTime;
        
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60;
        
        StringBuilder duration = new StringBuilder();
        
        if (hours > 0) {
            duration.append(hours).append(" jam ");
        }
        if (minutes > 0) {
            duration.append(minutes).append(" menit ");
        }
        if (seconds > 0 || duration.length() == 0) {
            duration.append(seconds).append(" detik");
        }
        
        return duration.toString().trim();
    }
    
    /**
     * Format duration in seconds to readable string
     * @param durationInSeconds Duration in seconds
     * @return Formatted duration string
     */
    public static String formatDuration(int durationInSeconds) {
        int hours = durationInSeconds / 3600;
        int minutes = (durationInSeconds % 3600) / 60;
        int seconds = durationInSeconds % 60;
        
        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        }
    }
    
    /**
     * Check if date is today
     * @param timestamp Timestamp to check
     * @return true if date is today
     */
    public static boolean isToday(long timestamp) {
        String today = getCurrentDate();
        String dateToCheck = formatTimestamp(timestamp, FORMAT_DATE);
        return today.equals(dateToCheck);
    }
    
    /**
     * Get timestamp for start of today
     * @return Timestamp for 00:00:00 today
     */
    public static long getStartOfToday() {
        long now = System.currentTimeMillis();
        long dayInMillis = TimeUnit.DAYS.toMillis(1);
        return (now / dayInMillis) * dayInMillis;
    }
}
