package com.example.cinestream.model;

public class FilmApi {
    private String id;
    private String judul;
    private String ringkasan;
    private String gambar_poster;
    private String gambar_sampul;
    private String tanggal_rilis;
    private double skor_rating;
    private String kategori;
    private String url_trailer;


    public String getId() {
        return id;
    }

    public String getJudul() {
        return judul;
    }

    public String getRingkasan() {
        return ringkasan;
    }

    public String getGambarPoster() {
        return gambar_poster;
    }

    public String getGambarSampul() {
        return gambar_sampul;
    }

    public String getTanggalRilis() {
        return tanggal_rilis;
    }

    public double getSkorRating() {
        return skor_rating;
    }

    public String getKategori() {
        return kategori;
    }

    public String getUrlTrailer() {
        return url_trailer;
    }
}