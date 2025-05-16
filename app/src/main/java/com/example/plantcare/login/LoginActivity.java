package com.example.plantcare.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plantcare.MainActivity;
import com.example.plantcare.R;
import com.example.plantcare.signup.SignupActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Écran de connexion : e‑mail / mot de passe + lien inscription + réinitialisation.
 * Compatible Java 11, aucun ViewBinding.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button   loginButton;
    private TextView signupButton, resetPasswordButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();              // Firebase Auth

        // FindViewById
        emailEditText     = findViewById(R.id.emailEditText);
        passwordEditText  = findViewById(R.id.passwordEditText);
        loginButton       = findViewById(R.id.loginButton);
        signupButton      = findViewById(R.id.signupButton);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        // Actions
        loginButton.setOnClickListener(v -> loginUser());
        signupButton.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
        resetPasswordButton.setOnClickListener(v -> resetPassword());
    }

    /** Connexion Firebase e‑mail / mot de passe */
    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String pass  = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();                       // ferme LoginActivity
                    } else {
                        Toast.makeText(this,
                                "Échec de la connexion : " +
                                        task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /** Envoie un e‑mail de réinitialisation de mot de passe */
    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this,
                    "Entrez votre adresse email pour réinitialiser le mot de passe",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Email de réinitialisation envoyé",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,
                                "Erreur lors de l'envoi de l'email",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
