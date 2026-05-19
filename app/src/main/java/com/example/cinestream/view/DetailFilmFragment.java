package com.example.cinestream.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.cinestream.R;
import com.example.cinestream.model.DatabaseHelper;

public class DetailFilmFragment extends Fragment {

    private DatabaseHelper dbHelper;

    public DetailFilmFragment() {
        // wajib empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_film, container, false);
        dbHelper = new DatabaseHelper(requireContext());

        // 🔥 AMBIL DATA DARI BUNDLE (Kunci disesuaikan dengan JelajahFragment)
        Bundle bundle = getArguments();

        if (bundle != null) {
            String idApi = bundle.getString("film_id");
            String judul = bundle.getString("film_judul");
            String ringkasan = bundle.getString("film_ringkasan");
            String tanggal = bundle.getString("film_rilis");
            String poster = bundle.getString("film_poster");
            String urlTrailer = bundle.getString("film_trailer"); // Ambil URL Trailer

            // 🔥 HUBUNGKAN KE VIEW
            TextView txtJudul = view.findViewById(R.id.txtJudul);
            TextView txtTanggal = view.findViewById(R.id.txtTanggal);
            TextView txtDeskripsi = view.findViewById(R.id.txtDeskripsi);
            ImageView imgFilm = view.findViewById(R.id.imgFilm);
            
            Button btnTambah = view.findViewById(R.id.btnTambahWatchlist);
            Button btnTonton = view.findViewById(R.id.btnTontonTrailer); // Hubungkan tombol trailer
            Button btnHapus = view.findViewById(R.id.btnHapusWatchlist);
            android.widget.CheckBox cbSudahNonton = view.findViewById(R.id.cbSudahNonton);

            // 🔥 SET DATA KE UI
            txtJudul.setText(judul);
            txtTanggal.setText("Rilis: " + tanggal);
            txtDeskripsi.setText(ringkasan);

            Glide.with(requireContext())
                    .load(poster)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgFilm);

            // 🔥 CEK STATUS AWAL (Apakah sudah ada di Watchlist?)
            updateUIWatchlist(idApi, btnTambah, btnHapus, cbSudahNonton);

            // 🔥 LOGIKA TOMBOL TAMBAH
            btnTambah.setOnClickListener(v -> {
                boolean berhasil = dbHelper.addToWatchlist(idApi, judul, poster, urlTrailer);
                if (berhasil) {
                    Toast.makeText(getContext(), "Berhasil ditambah!", Toast.LENGTH_SHORT).show();
                    updateUIWatchlist(idApi, btnTambah, btnHapus, cbSudahNonton);
                } else {
                    Toast.makeText(getContext(), "Film sudah ada di Watchlist", Toast.LENGTH_SHORT).show();
                }
            });

            // 🔥 LOGIKA TOMBOL TONTON TRAILER
            btnTonton.setOnClickListener(v -> {
                if (urlTrailer != null && !urlTrailer.isEmpty()) {
                    try {
                        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                        intent.setData(android.net.Uri.parse(urlTrailer));
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "URL tidak valid atau tidak bisa dibuka", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Trailer tidak tersedia", Toast.LENGTH_SHORT).show();
                }
            });

            // 🔥 LOGIKA TOMBOL HAPUS
            btnHapus.setOnClickListener(v -> {
                boolean berhasil = dbHelper.deleteFromWatchlistByApiId(idApi);
                if (berhasil) {
                    Toast.makeText(getContext(), "Dihapus dari Watchlist", Toast.LENGTH_SHORT).show();
                    updateUIWatchlist(idApi, btnTambah, btnHapus, cbSudahNonton);
                }
            });

            // 🔥 LOGIKA CHECKBOX (UPDATE STATUS NONTON)
            cbSudahNonton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int status = isChecked ? 1 : 0;
                dbHelper.updateWatchlistStatus(idApi, status);
            });
        }

        return view;
    }

    private void updateUIWatchlist(String idApi, Button btnAdd, Button btnRemove, android.widget.CheckBox cb) {
        boolean exists = dbHelper.isFilmInWatchlist(idApi);
        if (exists) {
            btnAdd.setVisibility(View.GONE);
            btnRemove.setVisibility(View.VISIBLE);
            cb.setVisibility(View.VISIBLE);
            
            // Set status checkbox dari database
            int status = dbHelper.getWatchlistStatus(idApi);
            cb.setChecked(status == 1);
        } else {
            btnAdd.setVisibility(View.VISIBLE);
            btnRemove.setVisibility(View.GONE);
            cb.setVisibility(View.GONE);
        }
    }
}