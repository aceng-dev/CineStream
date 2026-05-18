package com.example.cinestream.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinestream.R;
import com.example.cinestream.adapter.FilmAdapter;
import com.example.cinestream.controller.FilmController;
import com.example.cinestream.model.FilmApi;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.List;

public class JelajahFragment extends Fragment {

    RecyclerView recyclerView;

    ProgressBar progressBar;

    FilmController controller;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.fragment_jelajah,
                container,
                false
        );

        recyclerView =
                view.findViewById(R.id.recyclerJelajah);

        progressBar =
                view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 2)
        );

        controller = new FilmController();

        ambilDataFilm();

        return view;
    }

    private void ambilDataFilm() {

        progressBar.setVisibility(View.VISIBLE);

        controller.ambilDataFilm(
                new FilmController.ApiCallback() {

                    @Override
                    public void onSuccess(
                            List<FilmApi> daftarFilm
                    ) {

                        progressBar.setVisibility(View.GONE);

                        FilmAdapter adapter =
                                new FilmAdapter(daftarFilm);

                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(
                            String errorMessage
                    ) {

                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(
                                getContext(),
                                errorMessage,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}