package com.example.plantcare.signup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plantcare.MainActivity;
import com.example.plantcare.R;
import com.example.plantcare.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

/** Création de compte : Java 11, même style que LoginActivity */
public class SignupActivity extends AppCompatActivity {

    private EditText nameET, emailET, passET, confirmET;
    private Button   signupBtn;
    private TextView loginLink;
    private FirebaseAuth mAuth;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        nameET    = findViewById(R.id.nameEditText);
        emailET   = findViewById(R.id.emailEditText);
        passET    = findViewById(R.id.passwordEditText);
        confirmET = findViewById(R.id.confirmEditText);
        signupBtn = findViewById(R.id.signupButton);
        loginLink = findViewById(R.id.loginLink);

        signupBtn.setOnClickListener(v -> registerUser());
        loginLink.setOnClickListener(v ->
                startActivity(new Intent(SignupActivity.this, LoginActivity.class)));
    }

    private void registerUser() {
        String name  = nameET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        String pass  = passET.getText().toString().trim();
        String conf  = confirmET.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
            toast("Veuillez remplir tous les champs");
            return;
        }
        if (!pass.equals(conf)) {
            toast("Les mots de passe ne correspondent pas");
            return;
        }
        if (pass.length() < 6) {
            toast("Mot de passe min. 6 caractères");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        if (!name.isEmpty()) {                // enregistre le prénom
                            UserProfileChangeRequest req =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name).build();
                            mAuth.getCurrentUser().updateProfile(req);
                        }
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        toast("Échec inscription : "
                                + task.getException().getMessage());
                    }
                });
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
