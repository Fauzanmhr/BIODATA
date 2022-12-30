package com.example.aplikasibiodata;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
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

public class UpdateBiodataActivity extends AppCompatActivity {

    protected Cursor cursor;
    DataHelper dataHelper;
    ExtendedFloatingActionButton fabUpdate;
    EditText text1, text2, text3, text5;
    private String gender;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_biodata);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Update Biodata");
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
            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateBiodataActivity.this,
                    (view, year1, month1, dayOfMonth1) -> {
                        // Menampilkan tanggal yang dipilih di EditText
                        etDate.setText(dayOfMonth1 + "/" + (month1 + 1) + "/" + year1);
                    }, year, month, dayOfMonth);
            datePickerDialog.show();
        });
        Spinner spinner = findViewById(R.id.spinnergender);
        // Set listener untuk item yang dipilih di spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing is selected
            }
        });

        dataHelper = new DataHelper(this);
        text1 = findViewById(R.id.editText1);
        text2 = findViewById(R.id.editText2);
        text3 = findViewById(R.id.editText3);
        text5 = findViewById(R.id.editText5);

        SQLiteDatabase sqLiteDatabase = dataHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM biodata WHERE nama = '" +
                getIntent().getStringExtra("nama") + "'", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.moveToPosition(0);
            text1.setText(cursor.getString(1));
            text2.setText(cursor.getString(2));
            text3.setText(cursor.getString(3));
            if (cursor.getString(4).equals("Laki-laki")) {
                spinner.setSelection(1);
            } else {
                spinner.setSelection(2);
            }
            text5.setText(cursor.getString(5));
        }

        fabUpdate = findViewById(R.id.fabUpdate);
        fabUpdate.setOnClickListener(view -> {
            // Check if any of the EditText fields is empty
            if (text1.getText().toString().isEmpty() || text2.getText().toString().isEmpty() ||
                    text3.getText().toString().isEmpty() || text5.getText().toString().isEmpty()) {
                // If any of the fields is empty, show a toast message
                Toast.makeText(UpdateBiodataActivity.this, "Semua data harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                // If all fields are filled, show the confirmation dialog
                new AlertDialog.Builder(UpdateBiodataActivity.this)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Anda yakin ingin Merubah Data ini di database?")
                        .setPositiveButton("Ya", (dialog, which) -> {
                            // User clicked "Yes", update the data
                            SQLiteDatabase sqLiteDatabase1 = dataHelper.getWritableDatabase();
                            sqLiteDatabase1.execSQL("update biodata set nama='" +
                                    text1.getText().toString() + "', tlp='" +
                                    text2.getText().toString() + "', tgl='" +
                                    text3.getText().toString() + "', jk='" +
                                    gender + "', alamat='" +
                                    text5.getText().toString() + "' where nama='" +
                                    text1.getText().toString() + "'");
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                            MainActivity.mainActivity.RefreshList();
                            finish();
                        })
                        .setNegativeButton("Tidak", (dialog, which) -> {
                            // User clicked "No", dismiss the dialog
                            dialog.dismiss();
                        })
                        .show();
            }
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


