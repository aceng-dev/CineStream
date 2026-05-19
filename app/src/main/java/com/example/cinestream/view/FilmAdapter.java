package com.example.cinestream.view;

// =====================================================================
// FILE: FilmAdapter.java
// LOKASI: com/example/cinestream/view/FilmAdapter.java
// FUNGSI: Menghubungkan List<FilmApi> ke tampilan item_film.xml
//         dan menggunakan Glide untuk load gambar poster dari internet.
//
// SINKRON DENGAN:
//   - FilmApi.java       → sumber data (getJudul, getGambarPoster, dll)
//   - DatabaseHelper.java → dipakai saat user klik "Tambah ke Watchlist"
//   - item_film.xml      → layout tampilan satu baris film
// =====================================================================

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;                    // Library load gambar dari internet
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.cinestream.R;
import com.example.cinestream.model.DatabaseHelper; // Untuk fitur Tambah ke Watchlist
import com.example.cinestream.model.FilmApi;
import java.util.List;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {

    // ================================================================
    // VARIABEL UTAMA
    // ================================================================
    private Context context;
    private List<FilmApi> listFilm;        // Data dari API dosen (via FilmController)
    private DatabaseHelper dbHelper;       // Untuk operasi Watchlist lokal (SQLite)

    // ================================================================
    // INTERFACE LISTENER - Dipanggil saat item film diklik
    // Fragment/Activity yang pakai adapter ini wajib implement ini
    // ================================================================
    public interface OnItemClickListener {
        void onItemClick(FilmApi film);
    }

    private OnItemClickListener listener;

    // ================================================================
    // CONSTRUCTOR
    // ================================================================
    public FilmAdapter(Context context, List<FilmApi> listFilm, OnItemClickListener listener) {
        this.context = context;
        this.listFilm = listFilm;
        this.listener = listener;
        this.dbHelper = new DatabaseHelper(context); // Inisialisasi SQLite
    }

    // ================================================================
    // onCreateViewHolder
    // Tugas: "Cetak" layout item_film.xml menjadi objek View
    // ================================================================
    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_film, parent, false);
        return new FilmViewHolder(view);
    }

    // ================================================================
    // onBindViewHolder
    // Tugas: Isi data dari FilmApi ke View (item_film.xml)
    //        Dipanggil otomatis untuk setiap item yang tampil di layar
    // ================================================================
    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {

        // Ambil data film sesuai posisi di list
        FilmApi film = listFilm.get(position);

        // ---------------------------------------------------
        // ISI TEKS
        // Nama method sesuai getter di FilmApi.java milik tim
        // ---------------------------------------------------
        holder.txtJudul.setText(film.getJudul());
        holder.txtKategori.setText(film.getKategori());
        holder.txtRating.setText(String.valueOf(film.getSkorRating()));

        // ---------------------------------------------------
        // TAMPILKAN STATUS NONTON (LOKAL)
        // ---------------------------------------------------
        if (dbHelper.isFilmInWatchlist(film.getId())) {
            int status = dbHelper.getWatchlistStatus(film.getId());
            if (status == 1) {
                holder.txtStatusNonton.setVisibility(View.VISIBLE);
                holder.txtStatusNonton.setText("✓ Ditonton");
            } else {
                holder.txtStatusNonton.setVisibility(View.VISIBLE);
                holder.txtStatusNonton.setText("+ Watchlist");
                holder.txtStatusNonton.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            }
        } else {
            holder.txtStatusNonton.setVisibility(View.GONE);
        }

        // ---------------------------------------------------
        // LOAD GAMBAR POSTER dengan GLIDE
        // Mengambil URL dari FilmApi.getGambarPoster()
        // Glide otomatis: download → cache → tampil ke ImageView
        // ---------------------------------------------------
        Glide.with(context)
                .load(film.getGambarPoster())                          // URL dari API dosen
                .transition(DrawableTransitionOptions.withCrossFade()) // Animasi fade halus
                .placeholder(R.drawable.ic_launcher_background)        // Gambar sementara saat loading
                .error(android.R.drawable.ic_menu_gallery)             // Gambar jika URL gagal load
                .centerCrop()
                .into(holder.imgPoster);

        // ---------------------------------------------------
        // KLIK ITEM → buka halaman detail film
        // Data film dikirim ke Fragment/Activity via listener
        // ---------------------------------------------------
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(film); // Kirim objek FilmApi ke Fragment pemanggil
            }
        });

        // ---------------------------------------------------
        // KLIK TAHAN (Long Press) → Tambah ke Watchlist langsung
        // ---------------------------------------------------
        holder.itemView.setOnLongClickListener(v -> {
            // Cek dulu apakah sudah ada
            if (dbHelper.isFilmInWatchlist(film.getId())) {
                Toast.makeText(context, "Film ini sudah ada di Watchlist", Toast.LENGTH_SHORT).show();
            } else {
                boolean berhasil = dbHelper.addToWatchlist(
                        film.getId(),
                        film.getJudul(),
                        film.getGambarPoster(),
                        film.getUrlTrailer() // Tambahkan trailer di sini
                );

                if (berhasil) {
                    Toast.makeText(context, "\"" + film.getJudul() + "\" berhasil ditambah ✓", Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position); // Refresh item ini untuk update UI (jika ada indikator)
                }
            }
            return true;
        });
    }

    // ================================================================
    // getItemCount
    // Tugas: Kasih tahu RecyclerView berapa total item yang ada
    // ================================================================
    @Override
    public int getItemCount() {
        return listFilm != null ? listFilm.size() : 0;
    }

    // ================================================================
    // FUNGSI TAMBAHAN - Untuk update data dari Fragment
    // Dipanggil setelah FilmController selesai ambil data dari API
    // ================================================================
    public void updateData(List<FilmApi> dataBaruDariApi) {
        this.listFilm = dataBaruDariApi;
        notifyDataSetChanged(); // Paksa RecyclerView refresh tampilan
    }

    // ================================================================
    // ViewHolder
    // Tugas: "Pegang" referensi View dari item_film.xml
    //        agar tidak bolak-balik mencari View (hemat performa)
    // ================================================================
    public static class FilmViewHolder extends RecyclerView.ViewHolder {

        // ID View harus sama persis dengan android:id di item_film.xml
        ImageView imgPoster;
        TextView txtJudul;
        TextView txtKategori;
        TextView txtRating;
        TextView txtStatusNonton; // Tambah ini

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster    = itemView.findViewById(R.id.imgPoster);    // ImageView poster
            txtJudul     = itemView.findViewById(R.id.txtJudul);     // Judul film
            txtKategori  = itemView.findViewById(R.id.txtKategori);  // Kategori/genre
            txtRating    = itemView.findViewById(R.id.txtRating);    // Skor rating
            txtStatusNonton = itemView.findViewById(R.id.txtStatusNonton); // Status nonton
        }
    }
}
