package com.example.brainquiz;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class TestConnectionActivity extends AppCompatActivity {

    private TextView tvResult;
    private Button btnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_connection);

        tvResult = findViewById(R.id.tvResult);
        btnTest = findViewById(R.id.btnTest);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testConnection();
            }
        });
    }

    private void testConnection() {
        String url = "https://brainquiz0.up.railway.app/";
        
        tvResult.setText("Testing connection...");
        btnTest.setEnabled(false);
        
        Log.d("TestConnection", "Testing URL: " + url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TestConnection", "Success! Response: " + response);
                        tvResult.setText("✅ Connection SUCCESS!\n\nServer Response:\n" + response);
                        btnTest.setEnabled(true);
                        Toast.makeText(TestConnectionActivity.this, "Server is reachable!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TestConnection", "Error: " + error.toString());
                        
                        String errorMsg = "❌ Connection FAILED!\n\n";
                        if (error.networkResponse != null) {
                            errorMsg += "Error Code: " + error.networkResponse.statusCode + "\n";
                            errorMsg += "Error Data: " + new String(error.networkResponse.data);
                        } else {
                            errorMsg += "Error: " + error.getMessage();
                        }
                        
                        tvResult.setText(errorMsg);
                        btnTest.setEnabled(true);
                        Toast.makeText(TestConnectionActivity.this, "Connection failed!", Toast.LENGTH_SHORT).show();
                    }
                });

        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                10000, // 10 seconds timeout
                0, // no retries
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(this).addToRequestQueue(request);
    }
}
