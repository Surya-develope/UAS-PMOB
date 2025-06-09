package com.example.brainquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnTestConnection;
    private TextView tvToRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize the views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnTestConnection = findViewById(R.id.btnTestConnection);
        tvToRegister = findViewById(R.id.tvToRegister);
        progressBar = findViewById(R.id.progressBar);

        // Set OnClickListener for login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Navigate to RegisterActivity when "Daftar Sekarang" is clicked
        tvToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // Navigate to TestConnectionActivity when "Test Connection" is clicked
        btnTestConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, TestConnectionActivity.class));
            }
        });
    }

    private void loginUser() {
        // Get the email and password entered by the user
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate the fields
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check internet connection
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show();
            return;
        }

        // Show loading indicator
        showLoading(true);

        // Log the request data for debugging
        Log.d("LoginRequest", "Starting login process...");
        Log.d("LoginRequest", "Email: " + email + ", Password: [HIDDEN]");

        // Send login request to the API
        String url = "https://brainquiz0.up.railway.app/user/login";  // Your API endpoint

        // Prepare the request body as JSON
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a POST request using Volley
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showLoading(false);
                Log.d("LoginResponse", "Raw Response: " + response);

                try {
                    // Parse respons JSON
                    JSONObject jsonResponse = new JSONObject(response);

                    // Cek apakah login berhasil
                    boolean success = jsonResponse.getBoolean("success");
                    String message = jsonResponse.getString("message");

                    if (success) {
                        // Ambil data dari objek "data" yang berisi token dan user info
                        JSONObject data = jsonResponse.getJSONObject("data");
                        String token = data.getString("token");

                        // Coba ambil user_id jika ada
                        int userId = 0;
                        if (data.has("user_id")) {
                            userId = data.getInt("user_id");
                        } else if (data.has("user")) {
                            // Jika user_id ada di dalam objek user
                            JSONObject user = data.getJSONObject("user");
                            if (user.has("id")) {
                                userId = user.getInt("id");
                            } else if (user.has("ID")) {
                                userId = user.getInt("ID");
                            }
                        }

                        // Menyimpan token dan user_id ke SharedPreferences
                        saveLoginData(token, userId);

                        // Log untuk debugging
                        Log.d("LoginResponse", "Login successful! Token and User ID saved.");
                        Log.d("LoginResponse", "User ID: " + userId);

                        // Warning jika user_id tidak ditemukan
                        if (userId == 0) {
                            Log.w("LoginResponse", "Warning: User ID not found in response. Using default ID.");
                            Toast.makeText(LoginActivity.this, "Login berhasil (User ID tidak ditemukan)", Toast.LENGTH_SHORT).show();
                        }

                        // Menangani login sukses
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Redirect ke HomeActivity setelah login sukses
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Menangani login gagal
                        Log.e("LoginResponse", "Login failed: " + message);
                        Toast.makeText(LoginActivity.this, "Login Failed: " + message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // Menangani kesalahan parsing JSON
                    Log.e("LoginResponse", "JSON parsing error: " + e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoading(false);
                // Handle error during the API request
                Log.e("VolleyError", "Network error occurred");

                if (error.networkResponse != null) {
                    Log.e("VolleyError", "Error code: " + error.networkResponse.statusCode);
                    Log.e("VolleyError", "Error response: " + new String(error.networkResponse.data));
                    Toast.makeText(LoginActivity.this, "Server Error: " + error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                } else {
                    Log.e("VolleyError", "Error message: " + error.getMessage());
                    String errorMsg = "Network Error";
                    if (error.getMessage() != null) {
                        errorMsg = error.getMessage();
                    }
                    Toast.makeText(LoginActivity.this, "Connection Error: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        // Set timeout for the request
        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                30000, // 30 seconds timeout
                0, // no retries
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the Volley request queue
        Log.d("LoginRequest", "Sending request to: " + url);
        AppSingleton.getInstance(this).addToRequestQueue(request);
    }

    // Menyimpan token dan user_id setelah login berhasil
    private void saveLoginData(String token, int userId) {
        Log.d("saveLoginData", "Token disimpan: " + token);
        Log.d("saveLoginData", "User ID disimpan: " + userId);

        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.putInt("user_id", userId);
        editor.apply();

        Log.d("saveLoginData", "Login data saved successfully");
    }

    // Method untuk menampilkan/menyembunyikan loading
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        btnLogin.setEnabled(!show);
        btnLogin.setText(show ? "Logging in..." : "Login");
    }

    // Method untuk mengecek koneksi internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

}
