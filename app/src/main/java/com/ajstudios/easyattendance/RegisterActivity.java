package com.ajstudios.easyattendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ajstudios.easyattendance.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPass;
    private Button btnActivate;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirmPass = findViewById(R.id.etRegConfirmPass);
        btnActivate = findViewById(R.id.btnActivate);

        btnActivate.setOnClickListener(v -> handleActivation());
    }

    private void handleActivation() {
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirmPass.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if pre-authorized
        db.collection("users").document(email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                User user = task.getResult().toObject(User.class);
                if (user != null && !user.isRegistered()) {
                    createAccount(email, password, user);
                } else {
                    Toast.makeText(this, "Account already registered or invalid.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Invite not found. Ask Admin to 'Add Teacher' first.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createAccount(String email, String password, User user) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        finalizeRegistration(mAuth.getCurrentUser().getUid(), user, email);
                    } else {
                        // Handle User Collision (Already in Auth but seemingly not in Firestore or trying to re-activate)
                        if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                            // Try to sign in instead
                            mAuth.signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener(authResult -> {
                                        finalizeRegistration(authResult.getUser().getUid(), user, email);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegisterActivity.this, "Account exists but password mismatch or error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void finalizeRegistration(String uid, User user, String oldDocId) {
        user.setId(uid);
        user.setRegistered(true);

        db.collection("users").document(uid).set(user).addOnSuccessListener(v -> {
            // Delete old doc (Invite)
            db.collection("users").document(oldDocId).delete();

            Toast.makeText(RegisterActivity.this, "Account Activated!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
    }
}
