package com.example.plantcare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.plantcare.myplants.MyPlantsFragment;
import com.example.plantcare.profile.ProfileFragment;
import com.example.plantcare.search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Activité principale avec BottomNavigationView.
 * Pour l’instant, chaque fragment contient juste un TextView.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment target;
            int id = item.getItemId();

            if (id == R.id.nav_myplants) {
                target = new MyPlantsFragment();
            } else if (id == R.id.nav_profile) {
                target = new ProfileFragment();
            } else {                      // nav_search (par défaut)
                target = new SearchFragment();
            }
            switchFragment(target);
            return true;
        });

        // Premier affichage : onglet Recherche
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_search);
        }
    }

    /** Remplace le contenu du FrameLayout par le fragment voulu */
    private void switchFragment(@NonNull Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainer, fragment);
        ft.commit();
    }
}
