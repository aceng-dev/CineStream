package com.example.cinestream.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinestream.R;
import com.example.cinestream.model.DatabaseHelper;
import com.example.cinestream.model.FilmApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WatchlistFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView txtEmpty;
    private DatabaseHelper dbHelper;
    private FilmAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_watchlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerWatchlist);
        txtEmpty = view.findViewById(R.id.txtEmptyWatchlist);
        dbHelper = new DatabaseHelper(getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWatchlistData(); // Refresh data setiap kali halaman dibuka kembali
    }

    private void loadWatchlistData() {
        ArrayList<HashMap<String, String>> localData = dbHelper.getAllWatchlist();

        if (localData.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            txtEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Konversi dari HashMap (SQLite) ke List<FilmApi> agar bisa pakai FilmAdapter yang sama
            List<FilmApi> listFilm = new ArrayList<>();
            for (HashMap<String, String> map : localData) {
                FilmApi f = new FilmApi();
                f.setId(map.get(DatabaseHelper.COL_ID_FILM_API));
                f.setJudul(map.get(DatabaseHelper.COL_JUDUL));
                f.setGambarPoster(map.get(DatabaseHelper.COL_GAMBAR_POSTER));
                f.setUrlTrailer(map.get(DatabaseHelper.COL_URL_TRAILER)); // Ambil Trailer dari DB
                // Data lain bisa disesuaikan jika perlu
                listFilm.add(f);
            }

            adapter = new FilmAdapter(getContext(), listFilm, film -> {
                // Navigasi ke detail jika diinginkan
                bukaHalamanDetail(film);
            });
            recyclerView.setAdapter(adapter);
        }
    }

    private void bukaHalamanDetail(FilmApi film) {
        Bundle bundle = new Bundle();
        bundle.putString("film_id",       film.getId());
        bundle.putString("film_judul",    film.getJudul());
        bundle.putString("film_ringkasan",film.getRingkasan());
        bundle.putString("film_poster",   film.getGambarPoster());
        bundle.putString("film_sampul",   film.getGambarSampul());
        bundle.putString("film_kategori", film.getKategori());
        bundle.putDouble("film_rating",      film.getSkorRating());
        bundle.putString("film_rilis",      film.getTanggalRilis());
        bundle.putString("film_trailer",    film.getUrlTrailer()); // Kirim Trailer ke Detail

        DetailFilmFragment detailFragment = new DetailFilmFragment();
        detailFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}