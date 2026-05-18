package com.example.cinestream.controller;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.example.cinestream.model.FilmApi;

public class FilmController {

    private final OkHttpClient client;
    private final Gson gson;
    private static final String BASE_URL = "https://68ff8dfbe02b16d1753e765d.mockapi.io/film";


    public interface ApiCallback {
        void onSuccess(List<FilmApi> daftarFilm);
        void onFailure(String errorMessage);
    }

    // Constructor untuk inisialisasi objek OkHttp dan GSON
    public FilmController() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }


    public void ambilDataFilm(ApiCallback callback) {


        Request request = new Request.Builder()
                .url(BASE_URL)
                .get()
                .build();

        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Jika koneksi internet RTO, tidak ada sinyal, atau salah URL
                // Kita gunakan Handler agar pesan error berjalan di Main Thread (UI)
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure("Koneksi gagal: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Jika server memberikan respon balik (bisa sukses, bisa error 404/500)
                if (response.isSuccessful() && response.body() != null) {

                    // 3. Mengambil String JSON mentah dari body response
                    String jsonResponse = response.body().string();

                    // 4. Memparsing String JSON menjadi List objek Java otomatis menggunakan GSON
                    Type typeList = new TypeToken<List<FilmApi>>(){}.getType();
                    List<FilmApi> daftarFilm = gson.fromJson(jsonResponse, typeList);

                    // 5. Kembalikan data hasil parsing ke Main Thread agar bisa langsung ditampilkan ke layar
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onSuccess(daftarFilm)
                    );

                } else {
                    // Jika server merespon tapi error (misal token habis atau server down)
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("Server Error. Kode: " + response.code())
                    );
                }

                // Wajib menutup response body untuk mencegah kebocoran memori (memory leak)
                if (response.body() != null) {
                    response.body().close();
                }
            }
        });
    }
}