package com.example.cinestream.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nama file database yang akan disimpan di memori HP
    private static final String DATABASE_NAME = "db_cinestream.db";
    private static final int DATABASE_VERSION = 1;

    // Nama Tabel
    public static final String TABLE_WATCHLIST = "tbl_watchlist";

    // Nama-nama Kolom Tabel Lokal
    public static final String COL_ID_LOKAL = "id_lokal";             // ID otomatis (1, 2, 3...)
    public static final String COL_ID_FILM_API = "id_film_api";       // Menyimpan ID dari API Dosen
    public static final String COL_JUDUL = "judul";
    public static final String COL_GAMBAR_POSTER = "gambar_poster";
    public static final String COL_STATUS_NONTON = "status_nonton";   // 0 = Belum nonton, 1 = Sudah nonton
    public static final String COL_CATATAN_USER = "catatan_user";     // Review/catatan pribadi dari user

    // Constructor database
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Fungsi yang otomatis berjalan membuat tabel saat aplikasi pertama kali di-install
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_WATCHLIST + " ("
                + COL_ID_LOKAL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ID_FILM_API + " TEXT, "
                + COL_JUDUL + " TEXT, "
                + COL_GAMBAR_POSTER + " TEXT, "
                + COL_STATUS_NONTON + " INTEGER DEFAULT 0, "
                + COL_CATATAN_USER + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WATCHLIST);
        onCreate(db);
    }

    // ==========================================
    // LOGIKA CRUD INTERNAL (OPERASI LOKAL)
    // ==========================================

    // 🟢 [C] CREATE - Menambah film ke Watchlist lokal HP
    public boolean addToWatchlist(String idApi, String judul, String poster) {
        if (isFilmInWatchlist(idApi)) {
            return false; // Sudah ada, jangan tambah lagi
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_ID_FILM_API, idApi);
        values.put(COL_JUDUL, judul);
        values.put(COL_GAMBAR_POSTER, poster);
        values.put(COL_STATUS_NONTON, 0); // Default awal: belum ditonton
        values.put(COL_CATATAN_USER, ""); // Catatan awal kosong

        long hasil = db.insert(TABLE_WATCHLIST, null, values);
        db.close();
        return hasil != -1; // Kalau -1 artinya gagal, kalau sukses return true
    }

    // Fungsi tambahan untuk mengecek apakah film sudah ada di Watchlist
    public boolean isFilmInWatchlist(String idApi) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_WATCHLIST + " WHERE " + COL_ID_FILM_API + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{idApi});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // 🔵 [R] READ - Mengambil semua film yang ada di Watchlist HP
    public ArrayList<HashMap<String, String>> getAllWatchlist() {
        ArrayList<HashMap<String, String>> listWatchlist = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_WATCHLIST;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(COL_ID_LOKAL, cursor.getString(cursor.getColumnIndexOrThrow(COL_ID_LOKAL)));
                map.put(COL_ID_FILM_API, cursor.getString(cursor.getColumnIndexOrThrow(COL_ID_FILM_API)));
                map.put(COL_JUDUL, cursor.getString(cursor.getColumnIndexOrThrow(COL_JUDUL)));
                map.put(COL_GAMBAR_POSTER, cursor.getString(cursor.getColumnIndexOrThrow(COL_GAMBAR_POSTER)));
                map.put(COL_STATUS_NONTON, cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS_NONTON)));
                map.put(COL_CATATAN_USER, cursor.getString(cursor.getColumnIndexOrThrow(COL_CATATAN_USER)));
                listWatchlist.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listWatchlist;
    }


    // Mendapatkan status nonton berdasarkan ID API
    public int getWatchlistStatus(String idApi) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_STATUS_NONTON + " FROM " + TABLE_WATCHLIST + " WHERE " + COL_ID_FILM_API + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{idApi});
        int status = 0;
        if (cursor.moveToFirst()) {
            status = cursor.getInt(0);
        }
        cursor.close();
        return status;
    }

    public boolean updateWatchlistStatus(String idApi, int statusNonton) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS_NONTON, statusNonton);

        int hasil = db.update(TABLE_WATCHLIST, values, COL_ID_FILM_API + " = ?", new String[]{idApi});
        db.close();
        return hasil > 0;
    }


    public boolean deleteFromWatchlistByApiId(String idApi) {
        SQLiteDatabase db = this.getWritableDatabase();
        int hasil = db.delete(TABLE_WATCHLIST, COL_ID_FILM_API + " = ?", new String[]{idApi});
        db.close();
        return hasil > 0;
    }
}