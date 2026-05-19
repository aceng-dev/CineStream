package com.example.cinestream.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.cinestream.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBack = findViewById(R.id.btnBack);

        // Logika tombol back
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        // Pantau perubahan Fragment (Stack) untuk muncul/sembunyi tombol back
        getSupportFragmentManager().addOnBackStackChangedListener(this::updateBackButtonVisibility);

        // Fragment awal (Jelajah)
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new JelajahFragment())
                    .commit();
        }

        updateBackButtonVisibility();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragmentTerpilih = null;

            // Reset backstack saat pindah tab utama agar tidak menumpuk
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

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

    private void updateBackButtonVisibility() {
        // Tampilkan tombol back jika ada fragment di dalam tumpukan (backstack)
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            btnBack.setVisibility(View.VISIBLE);
        } else {
            btnBack.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}