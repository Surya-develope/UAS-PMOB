package com.example.brainquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize the views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvToRegister = findViewById(R.id.tvToRegister);

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

        // Log the request data for debugging
        Log.d("LoginRequest", "Email: " + email + ", Password: " + password);

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
                Log.d("LoginResponse", "Raw Response: " + response);

                try {
                    // Parse respons JSON
                    JSONObject jsonResponse = new JSONObject(response);

                    // Cek apakah login berhasil
                    boolean success = jsonResponse.getBoolean("success");
                    String message = jsonResponse.getString("message");

                    if (success) {
                        // Ambil data dari objek "data" yang berisi token
                        JSONObject data = jsonResponse.getJSONObject("data");
                        String token = data.getString("token");

                        // Menyimpan token ke SharedPreferences
                        saveToken(token);

                        // Log token untuk debugging
                        Log.d("LoginResponse", "Token: " + token);  // Periksa token yang disimpan

                        // Menangani login sukses
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Redirect ke HomeActivity setelah login sukses
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                        // Opsional, Anda bisa menyelesaikan activity login agar pengguna tidak bisa kembali
                        finish();
                    } else {
                        // Menangani login gagal
                        Toast.makeText(LoginActivity.this, "Login Failed: " + message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // Menangani kesalahan parsing JSON
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error during the API request
                if (error.networkResponse != null) {
                    Log.e("VolleyError", "Error code: " + error.networkResponse.statusCode);
                    Log.e("VolleyError", "Error response: " + new String(error.networkResponse.data));
                }
                Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        // Add the request to the Volley request queue
        AppSingleton.getInstance(this).addToRequestQueue(request);
    }

    // Menyimpan token setelah login berhasil
    private void saveToken(String token) {
        Log.d("saveToken", "Token disimpan: " + token);
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

}
