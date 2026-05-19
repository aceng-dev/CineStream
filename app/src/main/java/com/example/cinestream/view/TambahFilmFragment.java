package com.example.cinestream.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cinestream.R;
import com.example.cinestream.controller.FilmController;
import com.example.cinestream.model.FilmApi;

import java.util.List;

public class TambahFilmFragment extends Fragment {

    private EditText editJudul, editRingkasan, editPoster, editSampul, editRilis, editKategori, editRating, editTrailer;
    private Button btnSimpan;
    private FilmController filmController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tambah_film, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi View
        editJudul = view.findViewById(R.id.editJudul);
        editRingkasan = view.findViewById(R.id.editRingkasan);
        editPoster = view.findViewById(R.id.editPoster);
        editSampul = view.findViewById(R.id.editSampul);
        editRilis = view.findViewById(R.id.editRilis);
        editKategori = view.findViewById(R.id.editKategori);
        editRating = view.findViewById(R.id.editRating);
        editTrailer = view.findViewById(R.id.editTrailer);
        btnSimpan = view.findViewById(R.id.btnSimpanKeApi);

        filmController = new FilmController();

        btnSimpan.setOnClickListener(v -> simpanDataKeApi());
    }

    private void simpanDataKeApi() {
        // Ambil input dari user
        String judul = editJudul.getText().toString().trim();
        String ringkasan = editRingkasan.getText().toString().trim();
        String poster = editPoster.getText().toString().trim();
        String sampul = editSampul.getText().toString().trim();
        String rilis = editRilis.getText().toString().trim();
        String kategori = editKategori.getText().toString().trim();
        String ratingStr = editRating.getText().toString().trim();
        String trailer = editTrailer.getText().toString().trim();

        // Validasi sederhana
        if (judul.isEmpty() || ringkasan.isEmpty() || poster.isEmpty()) {
            Toast.makeText(getContext(), "Judul, Ringkasan, dan Poster wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        double rating = 0;
        try {
            if (!ratingStr.isEmpty()) rating = Double.parseDouble(ratingStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Rating harus berupa angka", Toast.LENGTH_SHORT).show();
            return;
        }

        // Siapkan objek FilmApi
        FilmApi filmBaru = new FilmApi();
        filmBaru.setJudul(judul);
        filmBaru.setRingkasan(ringkasan);
        filmBaru.setGambarPoster(poster);
        filmBaru.setGambarSampul(sampul);
        filmBaru.setTanggalRilis(rilis);
        filmBaru.setKategori(kategori);
        filmBaru.setSkorRating(rating);
        filmBaru.setUrlTrailer(trailer);

        // Kirim ke Controller
        btnSimpan.setEnabled(false);
        btnSimpan.setText("Mengirim...");

        filmController.tambahFilmKeApi(filmBaru, new FilmController.ApiCallback() {
            @Override
            public void onSuccess(List<FilmApi> daftarFilm) {
                // daftarFilm akan null untuk POST
                if (isAdded()) {
                    Toast.makeText(getContext(), "Film berhasil ditambahkan ke API!", Toast.LENGTH_SHORT).show();
                    // Kembali ke halaman Jelajah
                    getParentFragmentManager().popBackStack();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (isAdded()) {
                    btnSimpan.setEnabled(true);
                    btnSimpan.setText("SIMPAN KE SERVER API");
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}