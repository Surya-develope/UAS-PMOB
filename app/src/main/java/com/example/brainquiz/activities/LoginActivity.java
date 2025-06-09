package com.example.brainquiz.activities;
import com.example.brainquiz.R;

import android.content.Intent;
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

import com.example.brainquiz.utils.AppSingleton;
import com.example.brainquiz.utils.ValidationHelper;
import com.example.brainquiz.utils.NetworkHelper;
import com.example.brainquiz.utils.AuthManager;
import com.example.brainquiz.utils.ApiConstants;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
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
                startActivity(new Intent(LoginActivity.this, com.example.brainquiz.activities.RegisterActivity.class));
            }
        });


    }

    private void loginUser() {
        // Validate input fields using ValidationHelper
        if (!ValidationHelper.validateEmail(etEmail)) {
            return;
        }

        if (!ValidationHelper.validatePassword(etPassword)) {
            return;
        }

        // Check internet connection using NetworkHelper
        if (!NetworkHelper.checkNetworkAndShowMessage(this)) {
            return;
        }

        // Get validated input
        String email = ValidationHelper.getTrimmedText(etEmail);
        String password = ValidationHelper.getTrimmedText(etPassword);

        // Show loading indicator
        showLoading(true);

        // Send login request to the API
        String url = "https://brainquiz0.up.railway.app/user/login";  // Your API endpoint

        // Prepare the request body as JSON
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put(ApiConstants.PARAM_EMAIL, email);
            requestBody.put(ApiConstants.PARAM_PASSWORD, password);
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
                        JSONObject data = jsonResponse.getJSONObject(ApiConstants.KEY_DATA);
                        String token = data.getString(ApiConstants.KEY_TOKEN);

                        // Coba ambil user_id jika ada
                        int userId = 0;
                        if (data.has(ApiConstants.PARAM_USER_ID)) {
                            userId = data.getInt(ApiConstants.PARAM_USER_ID);
                        } else if (data.has(ApiConstants.KEY_USER)) {
                            // Jika user_id ada di dalam objek user
                            JSONObject user = data.getJSONObject(ApiConstants.KEY_USER);
                            if (user.has("id")) {
                                userId = user.getInt("id");
                            } else if (user.has("ID")) {
                                userId = user.getInt("ID");
                            }
                        }

                        // Menyimpan token dan user_id menggunakan AuthManager
                        AuthManager authManager = AuthManager.getInstance(LoginActivity.this);
                        authManager.saveUserLogin(userId, token, email, email);

                        // Warning jika user_id tidak ditemukan
                        if (userId == 0) {
                            Toast.makeText(LoginActivity.this, "Login berhasil (User ID tidak ditemukan)", Toast.LENGTH_SHORT).show();
                        }

                        // Menangani login sukses
                        Toast.makeText(LoginActivity.this, ApiConstants.SUCCESS_LOGIN, Toast.LENGTH_SHORT).show();

                        // Redirect ke HomeActivity setelah login sukses
                        Intent intent = new Intent(LoginActivity.this, com.example.brainquiz.activities.HomeActivity.class);
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



    // Method untuk menampilkan/menyembunyikan loading
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        btnLogin.setEnabled(!show);
        btnLogin.setText(show ? "Logging in..." : "Login");
    }



}


