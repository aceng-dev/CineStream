package com.example.cinestream.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.cinestream.R; // 🔥 WAJIB ADA
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fragment awal
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new JelajahFragment())
                    .commit();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setOnItemSelectedListener(item -> {

            Fragment fragmentTerpilih = null;

            if (item.getItemId() == R.id.nav_jelajah) {
                fragmentTerpilih = new JelajahFragment();

            } else if (item.getItemId() == R.id.nav_watchlist) {
                fragmentTerpilih = new WatchlistFragment();
            }

            if (fragmentTerpilih != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, fragmentTerpilih)
                        .commit();
                return true;
            }

            return false;
        });
    }
}