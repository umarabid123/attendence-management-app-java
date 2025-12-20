package com.ajstudios.easyattendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajstudios.easyattendance.Adapter.TeacherAdapter;
import com.ajstudios.easyattendance.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminActivity extends AppCompatActivity {

    private RecyclerView recyclerTeachers;
    private FloatingActionButton fabAddTeacher;
    private TeacherAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbarAdmin);
        setSupportActionBar(toolbar);

        recyclerTeachers = findViewById(R.id.recyclerTeachers);
        fabAddTeacher = findViewById(R.id.fabAddTeacher);

        recyclerTeachers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeacherAdapter(userList);
        recyclerTeachers.setAdapter(adapter);

        // Fetch Teachers
        fetchTeachers();

        fabAddTeacher.setOnClickListener(v -> showAddTeacherDialog());
    }

    private void fetchTeachers() {
        db.collection("users")
                .whereEqualTo("role", "TEACHER")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        return;
                    }
                    userList.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            User user = doc.toObject(User.class);
                            user.setId(doc.getId());
                            userList.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void showAddTeacherDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_teacher, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etTeacherName);
        EditText etEmail = dialogView.findViewById(R.id.etTeacherEmail);
        EditText etPhone = dialogView.findViewById(R.id.etTeacherPhone);

        builder.setPositiveButton("Add", (dialog, id) -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (!name.isEmpty() && !email.isEmpty()) {
                addTeacherToDB(name, email, phone);
            } else {
                Toast.makeText(this, "Name and Email are required", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }

    private void addTeacherToDB(String name, String email, String phone) {
        // Create a User object. We use email as the ID initially or auto-gen ID.
        // Using Auth UID is best, but user doesn't exist yet.
        // We will store it with Auto-ID, but include 'email' field for lookup.
        // When user Registers, we update this doc with the real Auth UID or merge.
        // BETTER STRATEGY: Use Email as Document ID.
        
        User newUser = new User(name, email, phone, "TEACHER", false);

        db.collection("users").document(email).set(newUser) // Use email as doc ID for easy lookup
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Teacher Added. They can now register.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error adding teacher", Toast.LENGTH_SHORT).show());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.only_dot, menu); // Reuse existing menu or create new
        menu.add(0, 1, 0, "Logout");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
