package com.example.aplikasibiodata;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    String[] daftar;
    ListView lvData;
    protected Cursor cursor;
    DataHelper dataHelper;
    @SuppressLint("StaticFieldLeak")
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Ambil objek SharedPreferences
        SharedPreferences preferences = getSharedPreferences("welcome_pref", MODE_PRIVATE);
        boolean firstLaunch = preferences.getBoolean("first_launch", true);

        if (firstLaunch) {
            // Jika ini adalah pertama kali aplikasi dijalankan,
            // maka buka halaman welcome page
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        // Tambahkan kode untuk menyimpan key "first_launch" dengan nilai "false"
        // setelah pengguna selesai menggunakan halaman welcome page
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("first_launch", false);
        editor.apply();

        ExtendedFloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(arg0 -> {
            Intent intent = new Intent(MainActivity.this, BuatBiodataActivity.class);
            startActivity(intent);
        });

        // Dapatkan referensi ke tombol
        Button btnExport = findViewById(R.id.btnExport);

// Tambahkan listener untuk menangani event klik tombol
        btnExport.setOnClickListener(v -> {
            // Buat objek CSVWriter
            CSVWriter writer = null;
            try {
                // Buat file CSV di folder Download dengan nama "biodata.csv"
                File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(exportDir, "biodata.csv");

                // Buat objek FileWriter dan CSVWriter
                FileWriter fw = new FileWriter(file);
                writer = new CSVWriter(fw);

                // Buat judul kolom
                String[] columns = {"Nama", "No Telepon", "Tgl Lahir", "Jenis Kelamin", "Alamat", };
                writer.writeNext(columns);

                // Dapatkan semua data dari database
                SQLiteDatabase sqLiteDatabase = dataHelper.getReadableDatabase();
                cursor = sqLiteDatabase.rawQuery("SELECT * FROM biodata", null);

                // Tuliskan data ke file CSV
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String[] row = {
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5)
                    };
                    writer.writeNext(row);
                    cursor.moveToNext();
                }

                // Tampilkan toast untuk mengonfirmasi file telah tersimpan
                Toast.makeText(getApplicationContext(), "Data berhasil diekspor ke Download/biodata.csv", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                // Tampilkan pesan error jika terjadi kesalahan
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } finally {
                // Tutup objek CSVWriter jika masih terbuka
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        mainActivity = this;
        dataHelper = new DataHelper(this);
        RefreshList();
    }
    public void RefreshList() {
        SQLiteDatabase sqLiteDatabase = dataHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM biodata", null);
        daftar = new String[cursor.getCount()];
        cursor.moveToFirst();

        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            daftar[cc] = cursor.getString(1);
        }

        lvData = findViewById(R.id.lvData);
        lvData.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, daftar));
        lvData.setSelected(true);
        lvData.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            final String selection = daftar[arg2];
            final CharSequence[] dialogitem = {"Lihat Biodata", "Update Biodata", "Hapus Biodata", "Kirim ke WhatsApp"};
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Pilihan");
            builder.setItems(dialogitem, (dialog, item) -> {
                switch (item) {
                    case 0:
                        Intent i = new Intent(getApplicationContext(), LihatBiodataActivity.class);
                        i.putExtra("nama", selection);
                        startActivity(i);
                        break;
                    case 1:
                        Intent in = new Intent(getApplicationContext(), UpdateBiodataActivity.class);
                        in.putExtra("nama", selection);
                        startActivity(in);
                        break;
                    case 2:
                        SQLiteDatabase sqLiteDatabase1 = dataHelper.getWritableDatabase();
                        sqLiteDatabase1.execSQL("delete from biodata where nama = '" + selection + "'");
                        RefreshList();
                        break;
                    case 3:
                        // Ambil data yang akan dikirim ke WhatsApp
                        SQLiteDatabase db = dataHelper.getReadableDatabase();
                        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM biodata WHERE nama = '" + selection + "'", null);
                        cursor.moveToFirst();
                        String nama = cursor.getString(1g);
                        String tlp = cursor.getString(2);
                        String tgl = cursor.getString(3);
                        String jk = cursor.getString(4);
                        String alamat = cursor.getString(5);
                        // Siapkan pesan yang akan dikirim ke WhatsApp
                        String pesan = "Biodata Customer Warung SPG:\n\nNama: " + nama + "\nNo. Telp: " + tlp + "\nTanggal Lahir: " + tgl + "\nJenis Kelamin: " + jk + "\nAlamat: " + alamat;
                        // Kirim pesan ke WhatsApp
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, pesan);
                        sendIntent.setType("text/plain");
                        sendIntent.setPackage("com.whatsapp");
                        startActivity(sendIntent);
                        break;
                }
            });
            builder.create().show();
        });
        ((ArrayAdapter<?>) lvData.getAdapter()).notifyDataSetInvalidated();
    }
}