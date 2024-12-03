package com.example.mini_projet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnSignUp;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialisation des vues
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etSignupEmail);
        etPassword = findViewById(R.id.etSignupPassword);
        btnSignUp = findViewById(R.id.btnSignup);

        // Initialisation de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Action du bouton "S'inscrire"
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                } else {
                    createUser(name, email, password);
                }
            }
        });
    }

    private void createUser(String name, String email, String password) {
        FirebaseAuth smAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = smAuth.getCurrentUser().getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("email", email);
                        user.put("password", password);
                        user.put("favorites", new ArrayList<>());
// Create a document with the user's UID as the document ID
                        db.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "User profile created successfully.");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error creating user profile", e);
                                });
                        // Inscription réussie
                        Toast.makeText(SignupActivity.this, "Inscription réussie", Toast.LENGTH_SHORT).show();

                        // Rediriger vers l'écran de connexion
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Échec de l'inscription
                        Toast.makeText(SignupActivity.this, "Échec de l'inscription : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
