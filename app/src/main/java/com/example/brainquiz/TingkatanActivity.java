package com.example.brainquiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainquiz.filter.Tingkatan;
import com.example.brainquiz.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TingkatanActivity extends AppCompatActivity {

    // Kartu + text di layout
    private LinearLayout cardMudah, cardSedang, cardSulit, cardSangatSulit;
    private TextView tvMudah, tvSedang, tvSulit, tvSangatSulit;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tingkatan);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // --- inisialisasi view ---
        cardMudah        = findViewById(R.id.card_mudah);        // tambahkan id di XML
        cardSedang       = findViewById(R.id.card_sedang);       // "
        cardSulit        = findViewById(R.id.card_sulit);        // "
        cardSangatSulit  = findViewById(R.id.card_sangat_sulit); // "

        tvMudah        = findViewById(R.id.tv_mudah);        // tambahkan id pada TextView "Mudah"
        tvSedang       = findViewById(R.id.tv_sedang);       // "
        tvSulit        = findViewById(R.id.tv_sulit);        // "
        tvSangatSulit  = findViewById(R.id.tv_sangat_sulit); // "

        // --- Retrofit ---
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://brainquiz0.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // ambil data
        fetchTingkatan();

        // contoh klik
        cardMudah.setOnClickListener(v -> Toast.makeText(this,"Mudah diklik",Toast.LENGTH_SHORT).show());
        cardSedang.setOnClickListener(v -> Toast.makeText(this,"Sedang diklik",Toast.LENGTH_SHORT).show());
        cardSulit.setOnClickListener(v -> Toast.makeText(this,"Sulit diklik",Toast.LENGTH_SHORT).show());
        cardSangatSulit.setOnClickListener(v -> Toast.makeText(this,"Sangat Sulit diklik",Toast.LENGTH_SHORT).show());
    }

    private String getToken(){
        SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sp.getString("token","");
    }

    private void fetchTingkatan(){
        String token = getToken();
        if(token.isEmpty()){
            Toast.makeText(this,"Token tidak ditemukan",Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getTingkatan("Bearer " + token).enqueue(new Callback<TingkatanResponse>() {
            @Override
            public void onResponse(Call<TingkatanResponse> call, Response<TingkatanResponse> res) {
                if(res.isSuccessful() && res.body()!=null){
                    List<Tingkatan> data = res.body().getData();
                    Toast.makeText(TingkatanActivity.this,"Dapat "+data.size()+" tingkatan",Toast.LENGTH_SHORT).show();
                    bindDataToCards(data);
                }else{
                    Log.e("TingkatanActivity","Error "+res.code());
                    Toast.makeText(TingkatanActivity.this,"Gagal mengambil data",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<TingkatanResponse> call, Throwable t) {
                Toast.makeText(TingkatanActivity.this,"Error: "+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** masukkan nama tingkatan ke TextView sesuai urutan index 0‑3  */
    private void bindDataToCards(List<Tingkatan> list){
        if(list.size()>=1) tvMudah.setText(list.get(0).getNama());
        if(list.size()>=2) tvSedang.setText(list.get(1).getNama());
        if(list.size()>=3) tvSulit.setText(list.get(2).getNama());
        if(list.size()>=4) tvSangatSulit.setText(list.get(3).getNama());
    }
}
