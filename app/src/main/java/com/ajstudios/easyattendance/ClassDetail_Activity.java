package com.ajstudios.easyattendance;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ajstudios.easyattendance.Adapter.StudentAdapter;
import com.ajstudios.easyattendance.model.AttendanceItem;
import com.ajstudios.easyattendance.model.AttendanceReport;
import com.ajstudios.easyattendance.model.Student;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ClassDetail_Activity extends AppCompatActivity {

    private ImageView themeImage;
    private TextView className, total_students, place_holder;
    private CardView addStudent, reports_open;
    private Button submit_btn;
    private EditText student_name, reg_no, mobile_no;
    private LinearLayout layout_attendance_taken;
    private RecyclerView mRecyclerview;

    String room_ID, subject_Name, class_Name;

    public static final String TAG = "ClassDetail_Activity";

    private StudentAdapter mAdapter;
    private List<Student> studentList = new ArrayList<>();
    
    ProgressBar progressBar;
    
    private FirebaseFirestore db;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail_);

        getWindow().setExitTransition(null);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        final String theme = getIntent().getStringExtra("theme");
        class_Name = getIntent().getStringExtra("className");
        subject_Name = getIntent().getStringExtra("subjectName");
        room_ID = getIntent().getStringExtra("classroom_ID");


        Toolbar toolbar = findViewById(R.id.toolbar_class_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_disease_detail);
        collapsingToolbarLayout.setTitle(subject_Name);

        themeImage = findViewById(R.id.image_disease_detail);
        className = findViewById(R.id.classname_detail);
        total_students = findViewById(R.id.total_students_detail);
        layout_attendance_taken = findViewById(R.id.attendance_taken_layout);
        layout_attendance_taken.setVisibility(View.GONE);
        addStudent = findViewById(R.id.add_students);
        reports_open = findViewById(R.id.reports_open_btn);
        className.setText(class_Name);
        mRecyclerview = findViewById(R.id.recyclerView_detail);
        progressBar = findViewById(R.id.progressbar_detail);
        place_holder = findViewById(R.id.placeholder_detail);
        place_holder.setVisibility(View.GONE);
        submit_btn = findViewById(R.id.submit_attendance_btn);
        submit_btn.setVisibility(View.GONE);

        if (theme != null) {
            switch (theme) {
                case "0":
                    themeImage.setImageResource(R.drawable.asset_bg_paleblue);
                    break;
                case "1":
                    themeImage.setImageResource(R.drawable.asset_bg_green);
                    break;
                case "2":
                    themeImage.setImageResource(R.drawable.asset_bg_yellow);
                    break;
                case "3":
                    themeImage.setImageResource(R.drawable.asset_bg_palegreen);
                    break;
                case "4":
                    themeImage.setImageResource(R.drawable.asset_bg_paleorange);
                    break;
                case "5":
                    themeImage.setImageResource(R.drawable.asset_bg_white);
                    break;
            }
        }

        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new StudentAdapter(this, studentList);
        mRecyclerview.setAdapter(mAdapter);

        loadStudents();

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAttendance();
            }
        });

        reports_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClassDetail_Activity.this, Reports_Activity.class);
                intent.putExtra("class_name", class_Name);
                intent.putExtra("subject_name", subject_Name);
                intent.putExtra("room_ID", room_ID);
                startActivity(intent);
            }
        });

        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(ClassDetail_Activity.this);
                final View view1 = inflater.inflate(R.layout.popup_add_student, null);
                student_name = view1.findViewById(R.id.name_student_popup);
                reg_no = view1.findViewById(R.id.regNo_student_popup);
                mobile_no = view1.findViewById(R.id.mobileNo_student_popup);

                AlertDialog.Builder builder = new AlertDialog.Builder(ClassDetail_Activity.this);
                builder.setView(view1);
                builder.setTitle("Add Student");
                builder.setCancelable(false);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = student_name.getText().toString();
                        String regNo = reg_no.getText().toString();
                        String mobNo = mobile_no.getText().toString();

                        if (isValid()) {
                            addStudentMethod(name, regNo, mobNo);
                        } else {
                            Toast.makeText(ClassDetail_Activity.this, "Please fill all the details..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private void loadStudents() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("classes").document(room_ID).collection("students")
                .addSnapshotListener((snapshots, e) -> {
                    progressBar.setVisibility(View.GONE);
                    if (e != null) {
                        Toast.makeText(ClassDetail_Activity.this, "Listen failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    studentList.clear();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Student s = doc.toObject(Student.class);
                            s.setId(doc.getId());
                            studentList.add(s);
                        }
                    }

                    // Sort by Name
                    Collections.sort(studentList, new Comparator<Student>() {
                        @Override
                        public int compare(Student o1, Student o2) {
                            if (o1.getName() == null || o2.getName() == null) return 0;
                            return o1.getName().compareToIgnoreCase(o2.getName());
                        }
                    });

                    mAdapter.updateList(studentList);
                    total_students.setText("Total Students : " + studentList.size());

                    if (studentList.isEmpty()) {
                        place_holder.setVisibility(View.VISIBLE);
                        submit_btn.setVisibility(View.GONE);
                    } else {
                        place_holder.setVisibility(View.GONE);
                        checkAttendanceToday(); // Check if we should show submit button
                    }
                });
    }

    private void checkAttendanceToday() {
        String date = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());
        db.collection("classes").document(room_ID).collection("attendance_reports")
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        layout_attendance_taken.setVisibility(View.VISIBLE);
                        submit_btn.setVisibility(View.GONE);
                    } else {
                        layout_attendance_taken.setVisibility(View.GONE);
                         // Only show submit button if there are students
                        if (!studentList.isEmpty()) {
                            submit_btn.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                     // On failure, maybe just show button if students exist to be safe
                     if (!studentList.isEmpty()) submit_btn.setVisibility(View.VISIBLE);
                });
    }

    public void addStudentMethod(final String studentName, final String regNo, final String mobileNo) {
        // Optimistic update: Don't block user | ProgressDialog removed
        Toast.makeText(ClassDetail_Activity.this, "Adding Student...", Toast.LENGTH_SHORT).show();

        Student student = new Student(studentName, regNo, mobileNo, room_ID);

        db.collection("classes").document(room_ID).collection("students")
                .add(student)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(ClassDetail_Activity.this, "Student Added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ClassDetail_Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void submitAttendance() {
        Map<String, String> attendanceMap = mAdapter.getAttendanceMap();
        
        // Simple validation: check if all students have a status
        // Note: Map might contain unchecked students as null or just missing
        if (attendanceMap.size() < studentList.size()) {
             Toast.makeText(ClassDetail_Activity.this, "Please mark attendance for all students", Toast.LENGTH_SHORT).show();
             return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(ClassDetail_Activity.this);
        progressDialog.setMessage("Submitting Attendance..");
        progressDialog.show();

        String date = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());
        
        List<AttendanceItem> items = new ArrayList<>();
        for (Student s : studentList) {
            String status = attendanceMap.get(s.getRegNo());
            if (status == null) status = "Absent"; // Default or error? Adapter check above should catch this but safety first
            items.add(new AttendanceItem(s.getName(), s.getRegNo(), status));
        }

        AttendanceReport report = new AttendanceReport(date, room_ID, class_Name, subject_Name, items);

        db.collection("classes").document(room_ID).collection("attendance_reports")
                .add(report)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        Toast.makeText(ClassDetail_Activity.this, "Attendance Submitted", Toast.LENGTH_SHORT).show();
                        checkAttendanceToday(); // Update UI
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ClassDetail_Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public boolean isValid() {
        return !student_name.getText().toString().isEmpty() && 
               !reg_no.getText().toString().isEmpty() && 
               !mobile_no.getText().toString().isEmpty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_class_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}