package com.example.brainquiz;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {

    EditText etNama, etDeskripsi;
    TextView tvJudul;
    Button btnSimpanPerubahan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        tvJudul = findViewById(R.id.tvJudul);
        etNama = findViewById(R.id.etNama);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        btnSimpanPerubahan = findViewById(R.id.btnSimpanPerubahan);

        // Ambil data dari Intent
        String nama = getIntent().getStringExtra("nama");
        String deskripsi = getIntent().getStringExtra("deskripsi");

        // Set judul dinamis
        tvJudul.setText("Edit Tingkatan: " + nama);

        etNama.setText("");
        etDeskripsi.setText("");

        btnSimpanPerubahan.setOnClickListener(v -> {
            // Logika simpan perubahan (contoh)
            String namaBaru = etNama.getText().toString().trim();
            String deskripsiBaru = etDeskripsi.getText().toString().trim();

            // Contoh validasi sederhana
            if (namaBaru.isEmpty() || deskripsiBaru.isEmpty()) {
                Toast.makeText(this, "Nama dan deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            // Misal simpan ke database di sini...

            Toast.makeText(this, "Perubahan disimpan untuk: " + namaBaru, Toast.LENGTH_SHORT).show();

            finish(); // tutup activity dan kembali ke halaman sebelumnya
        });
    }
}

