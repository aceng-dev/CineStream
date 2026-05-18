package com.example.cinestream.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinestream.R;
import com.example.cinestream.model.FilmApi;

import java.util.List;

public class FilmAdapter
        extends RecyclerView.Adapter<FilmAdapter.ViewHolder> {

    private List<FilmApi> daftarFilm;

    public FilmAdapter(List<FilmApi> daftarFilm) {
        this.daftarFilm = daftarFilm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(
                R.layout.item_movie,
                parent,
                false
        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        FilmApi film = daftarFilm.get(position);

        holder.txtJudul.setText(
                film.getJudul()
        );

        Glide.with(holder.itemView.getContext())
                .load(film.getGambarPoster())
                .into(holder.imgPoster);
    }

    @Override
    public int getItemCount() {
        return daftarFilm.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgPoster;
        TextView txtJudul;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPoster =
                    itemView.findViewById(R.id.imgPoster);

            txtJudul =
                    itemView.findViewById(R.id.txtJudul);
        }
    }
}