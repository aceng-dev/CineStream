package com.example.cinestream.controller;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    public FilmController() {
        // Menambahkan timeout agar tidak stuck loading selamanya
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public void ambilDataFilm(ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API_ERROR", "Gagal koneksi: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure("Gagal terhubung ke server (Cek Internet)")
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Type typeList = new TypeToken<List<FilmApi>>(){}.getType();
                    List<FilmApi> daftarFilm = gson.fromJson(jsonResponse, typeList);

                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(daftarFilm));
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("Server bermasalah: " + response.code())
                    );
                }
                response.close();
            }
        });
    }

    public void tambahFilmKeApi(FilmApi filmBaru, ApiCallback callback) {
        String jsonString = gson.toJson(filmBaru);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                jsonString,
                okhttp3.MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure("Gagal kirim: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(null));
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("Gagal simpan: " + response.code())
                    );
                }
                response.close();
            }
        });
    }
}