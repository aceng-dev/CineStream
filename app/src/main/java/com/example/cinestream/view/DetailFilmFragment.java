package com.example.cinestream.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.cinestream.R;

public class DetailFilmFragment extends Fragment {

    public DetailFilmFragment() {
        // wajib empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_film, container, false);

        // 🔥 AMBIL DATA DARI BUNDLE
        Bundle bundle = getArguments();

        if (bundle != null) {

            String judul = bundle.getString("film_judul");
            String deskripsi = bundle.getString("film_deskripsi");
            String tanggal = bundle.getString("film_rilis");
            String gambar = bundle.getString("film_gambar");

            // 🔥 HUBUNGKAN KE VIEW
            TextView txtJudul = view.findViewById(R.id.txtJudul);
            TextView txtTanggal = view.findViewById(R.id.txtTanggal);
            TextView txtDeskripsi = view.findViewById(R.id.txtDeskripsi);
            ImageView imgFilm = view.findViewById(R.id.imgFilm);

            // 🔥 SET DATA
            txtJudul.setText(judul);
            txtTanggal.setText(tanggal);
            txtDeskripsi.setText(deskripsi);

            // 🔥 LOAD GAMBAR
            Glide.with(requireContext())
                    .load(gambar)
                    .into(imgFilm);
        }

        return view;
    }
}