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

    // --- GETTER ---
    public String getId() { return id; }
    public String getJudul() { return judul; }
    public String getRingkasan() { return ringkasan; }
    public String getGambarPoster() { return gambar_poster; }
    public String getGambarSampul() { return gambar_sampul; }
    public String getTanggalRilis() { return tanggal_rilis; }
    public double getSkorRating() { return skor_rating; }
    public String getKategori() { return kategori; }
    public String getUrlTrailer() { return url_trailer; }

    // --- SETTER ---
    public void setId(String id) { this.id = id; }
    public void setJudul(String judul) { this.judul = judul; }
    public void setRingkasan(String ringkasan) { this.ringkasan = ringkasan; }
    public void setGambarPoster(String gambar_poster) { this.gambar_poster = gambar_poster; }
    public void setGambarSampul(String gambar_sampul) { this.gambar_sampul = gambar_sampul; }
    public void setTanggalRilis(String tanggal_rilis) { this.tanggal_rilis = tanggal_rilis; }
    public void setSkorRating(double skor_rating) { this.skor_rating = skor_rating; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setUrlTrailer(String url_trailer) { this.url_trailer = url_trailer; }
}