package com.example.brainquiz.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Helper class untuk network connectivity operations
 */
public class NetworkHelper {
    
    /**
     * Check apakah device terhubung ke internet
     * @param context Context aplikasi
     * @return true jika terhubung, false jika tidak
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
    
    /**
     * Show toast message jika tidak ada koneksi internet
     * @param context Context aplikasi
     * @return true jika ada koneksi, false jika tidak ada
     */
    public static boolean checkNetworkAndShowMessage(Context context) {
        if (!isNetworkAvailable(context)) {
            Toast.makeText(context, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    
    /**
     * Get network type string
     * @param context Context aplikasi
     * @return String network type (WiFi, Mobile, None)
     */
    public static String getNetworkType(Context context) {
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return "WiFi";
                } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return "Mobile";
                }
            }
        }
        return "None";
    }
}
