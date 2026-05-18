package com.example.cinestream.view;

// =====================================================================
// FILE: JelajahFragment.java
// LOKASI: com/example/cinestream/view/JelajahFragment.java
// FUNGSI: Halaman "Jelajah" yang menampilkan daftar film dari API Dosen
//         menggunakan FilmController (OkHttp) → FilmAdapter → RecyclerView
//
// SINKRON DENGAN:
//   - FilmController.java → ambilDataFilm() via OkHttp ke BASE_URL
//   - FilmAdapter.java    → menampilkan List<FilmApi> ke RecyclerView
//   - FilmApi.java        → model data dari server
//   - fragment_jelajah.xml → layout halaman ini
// =====================================================================

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinestream.model.FilmApi;

import com.example.cinestream.R;
import com.example.cinestream.controller.FilmController;

import java.util.List;

public class JelajahFragment extends Fragment {

    // ================================================================
    // VARIABEL VIEW (dari fragment_jelajah.xml)
    // ================================================================
    private RecyclerView recyclerView;
    private ProgressBar progressBar;       // Loading spinner saat fetch API
    private LinearLayout layoutError;     // Kontainer pesan error (jika gagal)
    private TextView txtPesanError;       // Teks detail error
    private TextView btnCobaTampil;       // Tombol retry

    // ================================================================
    // VARIABEL LOGIKA
    // ================================================================
    private FilmAdapter filmAdapter;       // Adapter penghubung data ↔ tampilan
    private FilmController filmController; // Pengambil data dari API Dosen

    // ================================================================
    // onCreateView - "Cetak" layout fragment ke layar
    // ================================================================
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Hubungkan fragment ini dengan file layout fragment_jelajah.xml
        return inflater.inflate(R.layout.fragment_jelajah, container, false);
    }

    // ================================================================
    // onViewCreated - Jalankan logika setelah View siap di layar
    // ================================================================
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. Sambungkan variabel ke View di XML ---
        recyclerView   = view.findViewById(R.id.recyclerJelajah);
        progressBar    = view.findViewById(R.id.progressBarJelajah);
        layoutError    = view.findViewById(R.id.layoutErrorJelajah);
        txtPesanError  = view.findViewById(R.id.txtPesanErrorJelajah);
        btnCobaTampil  = view.findViewById(R.id.btnCobaLagiJelajah);

        // --- 2. Siapkan RecyclerView (scroll vertikal, satu kolom) ---
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- 3. Inisialisasi Controller ---
        filmController = new FilmController();

        // --- 4. Langsung ambil data saat fragment pertama dibuka ---
        ambilDataDariApi();

        // --- 5. Tombol "Coba Lagi" jika gagal koneksi ---
        btnCobaTampil.setOnClickListener(v -> ambilDataDariApi());
    }

    // ================================================================
    // ambilDataDariApi()
    // Memanggil FilmController.ambilDataFilm() menggunakan OkHttp
    // Mengelola 3 kondisi UI: Loading → Sukses / Gagal
    // ================================================================
    private void ambilDataDariApi() {

        // Tampilkan loading, sembunyikan error
        tampilkanLoading(true);
        layoutError.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        // Panggil fungsi dari FilmController (OkHttp ke mockAPI dosen)
        filmController.ambilDataFilm(new FilmController.ApiCallback() {

            // ✅ SUKSES: Data berhasil diambil dari server
            @Override
            public void onSuccess(List<FilmApi> daftarFilm) {
                tampilkanLoading(false);

                if (daftarFilm == null || daftarFilm.isEmpty()) {
                    // Server merespon tapi data kosong
                    tampilkanError("Data film masih kosong dari server.");
                    return;
                }

                // Buat adapter dengan data yang baru datang dari API
                // Listener klik → buka halaman Detail Film
                filmAdapter = new FilmAdapter(getContext(), daftarFilm, film -> {
                    bukaHalamanDetail(film);
                });

                recyclerView.setAdapter(filmAdapter);
                recyclerView.setVisibility(View.VISIBLE);
            }

            // ❌ GAGAL: Koneksi error atau server down
            @Override
            public void onFailure(String errorMessage) {
                tampilkanLoading(false);
                tampilkanError(errorMessage);
            }
        });
    }

    // ================================================================
    // bukaHalamanDetail()
    // Dipanggil saat user klik satu item film di RecyclerView
    // Kirim data FilmApi ke DetailFilmFragment menggunakan Bundle
    // ================================================================
    private void bukaHalamanDetail(FilmApi film) {
        // Siapkan Bundle untuk kirim data ke halaman Detail
        Bundle bundle = new Bundle();
        bundle.putString("film_id",       film.getId());
        bundle.putString("film_judul",    film.getJudul());
        bundle.putString("film_ringkasan",film.getRingkasan());
        bundle.putString("film_poster",   film.getGambarPoster());
        bundle.putString("film_sampul",   film.getGambarSampul());
        bundle.putString("film_kategori", film.getKategori());
        bundle.putDouble("film_rating",      film.getSkorRating());
        bundle.putString("film_rilis",      film.getTanggalRilis());

        // Navigasi ke DetailFilmFragment
        // Ganti "DetailFilmFragment" dengan nama class detail yang dibuat tim nanti
        DetailFilmFragment detailFragment = new DetailFilmFragment();
        detailFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment) // ID container di activity_main.xml
                .addToBackStack(null) // Tombol back HP akan kembali ke sini
                .commit();
    }

    // ================================================================
    // HELPER UI - Tampilkan/sembunyikan loading spinner
    // ================================================================
    private void tampilkanLoading(boolean sedangLoading) {
        progressBar.setVisibility(sedangLoading ? View.VISIBLE : View.GONE);
    }

    // ================================================================
    // HELPER UI - Tampilkan pesan error + tombol retry
    // ================================================================
    private void tampilkanError(String pesan) {
        layoutError.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        txtPesanError.setText(pesan);
    }
}
