package com.example.plantcare.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plantcare.MainActivity;
import com.example.plantcare.R;
import com.example.plantcare.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Affiche le logo ~0,8 s puis redirige :
 *  – si l'utilisateur est déjà connecté → MainActivity
 *  – sinon → LoginActivity
 */
public class SplashActivity extends AppCompatActivity {

    private static final long DELAY_MS = 800;   // Durée du splash en millisecondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Après un délai, on choisit la prochaine Activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Si l'utilisateur Firebase est déjà connecté → MainActivity
            // sinon → LoginActivity
            Class<?> next = (FirebaseAuth.getInstance().getCurrentUser() != null)
                    ? MainActivity.class
                    : LoginActivity.class;

            startActivity(new Intent(SplashActivity.this, next));
            finish();  // On ferme la splash pour qu'elle ne reste pas dans le back-stack

        }, DELAY_MS);
    }
}
