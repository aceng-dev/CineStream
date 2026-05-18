package com.example.cinestream.view;

import com.example.cinestream.R;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.cinestream.fragment.JelajahFragment;
import com.example.cinestream.fragment.WatchlistFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView =
                findViewById(R.id.bottomNavigation);

        // Fragment awal
        replaceFragment(new JelajahFragment());

        bottomNavigationView
                .setOnItemSelectedListener(item -> {

                    if (item.getItemId() == R.id.nav_jelajah) {

                        replaceFragment(
                                new JelajahFragment()
                        );

                    } else if (item.getItemId()
                            == R.id.nav_watchlist) {

                        replaceFragment(
                                new WatchlistFragment()
                        );
                    }

                    return true;
                });
    }

    private void replaceFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragment_container,
                        fragment
                )
                .commit();
    }
}