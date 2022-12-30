package com.example.aplikasibiodata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "biodatacustomer.db";
    private static final int DATABASE_VERSION = 1;
    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table biodata(no integer primary key AUTOINCREMENT, nama text null, tlp text null, tgl text null, jk text null, alamat text null);";
        Log.d("Data", "onCreate: " + sql);
        db.execSQL(sql);
        sql = "INSERT INTO biodata (nama, tlp, tgl, jk, alamat) VALUES ('Muhammad Fauzan Muharram', '123-456-7890', '2022-01-01', 'Laki-Laki', '123 Main Street'), ('Angga Pranadia Saputro', '987-654-3210', '2022-02-01', 'Laki-Laki', '456 Park Avenue'), ('Syifa', '555-555-5555', '2022-03-01', 'Perempuan', '789 Maple Street');";
        db.execSQL(sql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }
}
