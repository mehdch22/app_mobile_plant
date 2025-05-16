package com.example.plantcare.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.plantcare.R;
import com.example.plantcare.login.LoginActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private TextView tvEmail;
    private SwitchCompat switchTheme;
    private Button btnLogout, btnChangePassword;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        tvEmail           = v.findViewById(R.id.tvUserEmail);
        switchTheme       = v.findViewById(R.id.switchTheme);
        btnChangePassword = v.findViewById(R.id.btnChangePassword);
        btnLogout         = v.findViewById(R.id.btnLogout);

        // Affiche l’email courant
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvEmail.setText(user.getEmail());
        }

        // Initialise l’état du switch selon le mode actuel
        boolean night = AppCompatDelegate.getDefaultNightMode()
                == AppCompatDelegate.MODE_NIGHT_YES;
        switchTheme.setChecked(night);

        // Quand on change le switch, on active/désactive le mode sombre
        switchTheme.setOnCheckedChangeListener((btn, isChecked) ->
                AppCompatDelegate.setDefaultNightMode(
                        isChecked
                                ? AppCompatDelegate.MODE_NIGHT_YES
                                : AppCompatDelegate.MODE_NIGHT_NO
                )
        );

        // Changer mot de passe
        btnChangePassword.setOnClickListener(zz -> showChangePasswordDialog());

        // Logout
        btnLogout.setOnClickListener(zz -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(requireContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        return v;
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText etCurrent = dialogView.findViewById(R.id.etCurrentPassword);
        EditText etNew1    = dialogView.findViewById(R.id.etNewPassword);
        EditText etNew2    = dialogView.findViewById(R.id.etConfirmPassword);

        new AlertDialog.Builder(requireContext())
                .setTitle("Changer le mot de passe")
                .setView(dialogView)
                .setPositiveButton("Valider", (d, w) -> {
                    String curr = etCurrent.getText().toString().trim();
                    String np1  = etNew1.getText().toString().trim();
                    String np2  = etNew2.getText().toString().trim();
                    if (curr.isEmpty() || np1.isEmpty() || np2.isEmpty()) {
                        Toast.makeText(getContext(),
                                "Veuillez remplir tous les champs",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!np1.equals(np2)) {
                        Toast.makeText(getContext(),
                                "Les nouveaux mots de passe ne correspondent pas",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updatePassword(curr, np1);
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void updatePassword(String currentPw, String newPw) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        AuthCredential cred = EmailAuthProvider
                .getCredential(user.getEmail(), currentPw);

        user.reauthenticate(cred)
                .addOnSuccessListener(x ->
                        user.updatePassword(newPw)
                                .addOnSuccessListener(y ->
                                        Toast.makeText(getContext(),
                                                "Mot de passe mis à jour",
                                                Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(err ->
                                        Toast.makeText(getContext(),
                                                "Erreur : " + err.getMessage(),
                                                Toast.LENGTH_LONG).show()
                                )
                )
                .addOnFailureListener(err ->
                        Toast.makeText(getContext(),
                                "Échec de la réauthentification",
                                Toast.LENGTH_LONG).show()
                );
    }
}