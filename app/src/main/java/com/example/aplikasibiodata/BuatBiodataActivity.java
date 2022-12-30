package com.example.aplikasibiodata;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Calendar;



public class BuatBiodataActivity extends AppCompatActivity {
    DataHelper dataHelper;
    ExtendedFloatingActionButton fabCreate;
    EditText text1;
    EditText text2;
    EditText text3;
    EditText text5;
    private String gender;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_biodata);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Buat Biodata");
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inisialisasi field EditText untuk tanggal
        @SuppressLint("CutPasteId") final EditText etDate = findViewById(R.id.editText3);

// Menampilkan DatePickerDialog saat EditText diklik
        etDate.setOnClickListener(v -> {
            // Menampilkan kalender tanggal
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(BuatBiodataActivity.this,
                    (view, year1, month1, dayOfMonth1) -> {
                        // Menampilkan tanggal yang dipilih di EditText
                        etDate.setText(dayOfMonth1 + "/" + (month1 + 1) + "/" + year1);
                    }, year, month, dayOfMonth);
            datePickerDialog.show();
        });

        Spinner spinner = findViewById(R.id.spinnergender);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = (String) parent.getItemAtPosition(position);
            }

            @Override

            public void onNothingSelected(AdapterView<? > parent) {
                // nothing is selected
            }
        });


        dataHelper = new DataHelper(this);
        text1 = findViewById(R.id.editText1);
        text2 = findViewById(R.id.editText2);
        text3 = findViewById(R.id.editText3);
        text5 = findViewById(R.id.editText5);
        fabCreate = findViewById(R.id.fabCreate);


        fabCreate.setOnClickListener(v -> {
            // Validasi nama dan telepon kosong
            if (text1.getText().toString().isEmpty()) {
                text1.setError("Nama tidak boleh kosong");
                return;
            }
            if (text2.getText().toString().isEmpty()) {
                text2.setError("Telepon tidak boleh kosong");
                return;
            }
            if (text3.getText().toString().isEmpty()) {
                text3.setError("Tanggal Lahir tidak boleh kosong");
                return;
            }
            if (spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(BuatBiodataActivity.this, "Silakan pilih jenis kelamin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (text5.getText().toString().isEmpty()) {
                text5.setError("Alamat tidak boleh kosong");
                return;
            }

            // Tampilkan alert dialog untuk konfirmasi penyimpanan
            AlertDialog.Builder builder = new AlertDialog.Builder(BuatBiodataActivity.this);
            builder.setMessage("Apakah Anda yakin ingin menyimpan form ini ke database?");
            builder.setPositiveButton("Ya", (dialog, which) -> {
                // Masukkan form ke database
                SQLiteDatabase db = dataHelper.getWritableDatabase();
                db.execSQL("insert into biodata(nama, tlp, tgl, jk, alamat) values('" +
                        text1.getText().toString()+"','"+
                        text2.getText().toString() +"','" +
                        text3.getText().toString()+"','"+
                        gender +"','" +
                        text5.getText().toString()+"')");
                Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_LONG).show();
                MainActivity.mainActivity.RefreshList();
                finish();
            });
            builder.setNegativeButton("Tidak", (dialog, which) -> {
                // Tutup dialog
                dialog.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });


    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
