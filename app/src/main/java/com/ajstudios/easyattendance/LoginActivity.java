package com.ajstudios.easyattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private RadioGroup roleToggle;
    private RadioButton rbStudent, rbTeacher;
    private TextInputLayout tilInput1, tilInput2;
    private TextInputEditText etInput1, etInput2;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("EasyAttendance", MODE_PRIVATE);

        // Check for existing session
        checkExistingSession();

        roleToggle = findViewById(R.id.roleToggle);
        rbStudent = findViewById(R.id.rbStudent);
        rbTeacher = findViewById(R.id.rbTeacher);
        tilInput1 = findViewById(R.id.tilInput1);
        tilInput2 = findViewById(R.id.tilInput2);
        etInput1 = findViewById(R.id.etInput1);
        etInput2 = findViewById(R.id.etInput2);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);

        setupToggle();

        btnLogin.setOnClickListener(v -> handleLogin());
        tvForgotPassword.setOnClickListener(v -> handleForgotPassword());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void checkExistingSession() {
        // Check Student Session
        String role = sharedPreferences.getString("ROLE", "");
        if ("STUDENT".equals(role)) {
            startActivity(new Intent(this, StudentDashboardActivity.class));
            finish();
            return;
        }

        // Check Teacher/Admin Session
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserRole(currentUser.getUid());
        }
    }

    private void setupToggle() {
        roleToggle.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbStudent) {
                tilInput1.setHint("Roll No");
                etInput1.setHint("Roll No");
                etInput1.setInputType(InputType.TYPE_CLASS_TEXT);
                tilInput2.setHint("Phone");
                etInput2.setHint("Phone");
                etInput2.setInputType(InputType.TYPE_CLASS_PHONE);
                tilInput2.setEndIconMode(TextInputLayout.END_ICON_NONE);
                tvForgotPassword.setVisibility(View.GONE);
                tvRegister.setVisibility(View.GONE);
            } else {
                tilInput1.setHint("Email");
                etInput1.setHint("Email");
                etInput1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                tilInput2.setHint("Password");
                etInput2.setHint("Password");
                tilInput2.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                etInput2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                tvForgotPassword.setVisibility(View.VISIBLE);
                tvRegister.setVisibility(View.VISIBLE);
            }
        });
    }

    private void handleLogin() {
        String input1 = etInput1.getText().toString().trim();
        String input2 = etInput2.getText().toString().trim();

        if (input1.isEmpty() || input2.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        if (rbStudent.isChecked()) {
            loginStudent(input1, input2);
        } else {
            if (input2.length() < 8) {
                tilInput2.setError("Password must be at least 8 characters");
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                return;
            }
            tilInput2.setError(null);
            loginTeacher(input1, input2);
        }
    }

    private void loginStudent(String rollNo, String mobileNo) {
        db.collection("students")
                .whereEqualTo("regNo", rollNo)
                .whereEqualTo("mobileNo", mobileNo)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        if (documents != null && !documents.isEmpty()) {
                            DocumentSnapshot student = documents.getDocuments().get(0);
                            
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("ROLE", "STUDENT");
                            editor.putString("STUDENT_ID", student.getId());
                            editor.putString("STUDENT_NAME", student.getString("name"));
                            editor.putString("CLASS_ID", student.getString("classId"));
                            // Also store regNo if needed later
                            editor.putString("REG_NO", rollNo);
                            editor.apply();

                            startActivity(new Intent(LoginActivity.this, StudentDashboardActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid Roll No or Mobile Number", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // TODO: Replace with the specific Gmail you want to have Super Admin access
    private static final String SUPER_ADMIN_EMAIL = "umarabid709@gmail.com";

    private void loginTeacher(String emailInput, String password) {
        String email = emailInput.toLowerCase();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && SUPER_ADMIN_EMAIL.equalsIgnoreCase(user.getEmail())) {
                            // Special Handling for Super Admin: Auto-provision if missing
                            db.collection("users").document(user.getUid()).get()
                                    .addOnCompleteListener(docTask -> {
                                        if (docTask.isSuccessful() && !docTask.getResult().exists()) {
                                            // Create missing Super Admin profile
                                            com.ajstudios.easyattendance.model.User adminParams = 
                                                    new com.ajstudios.easyattendance.model.User("Super Admin", email, "", "SUPER_ADMIN", true);
                                            adminParams.setId(user.getUid());

                                            db.collection("users").document(user.getUid()).set(adminParams)
                                                    .addOnSuccessListener(v -> checkUserRole(user.getUid()));
                                        } else {
                                            // Update role to ensure access and proceed
                                            db.collection("users").document(user.getUid())
                                                    .update("role", "SUPER_ADMIN")
                                                    .addOnCompleteListener(updateTask -> checkUserRole(user.getUid()));
                                        }
                                    });
                        } else {
                            // Regular flow
                            checkUserRole(user.getUid());
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);
                        
                        Exception exception = task.getException();
                        if (exception instanceof com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                            // User not in Auth. Check if they have a pending invite in Firestore.
                            db.collection("users").document(email).get().addOnSuccessListener(doc -> {
                                if (doc.exists()) {
                                    Toast.makeText(LoginActivity.this, "Account not activated. Please tap 'New Teacher? Activate Account' below.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Account does not exist.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication Failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserRole(String uid) {
        db.collection("users").document(uid).get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String role = document.getString("role");
                    // Route Admin and Super Admin to the Admin Dashboard
                    if ("SUPER_ADMIN".equals(role) || "ADMIN".equals(role)) {
                        startActivity(new Intent(this, SuperAdminActivity.class));
                    } else {
                        startActivity(new Intent(this, MainActivity.class));
                    }
                    finish();
                } else {
                   Toast.makeText(this, "User profile not found. Contact Admin.", Toast.LENGTH_SHORT).show();
                   mAuth.signOut();
                }
            } else {
                 Toast.makeText(this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
                 mAuth.signOut();
            }
        });
    }

    private void handleForgotPassword() {
        if (rbStudent.isChecked()) return;
        
        String email = etInput1.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
