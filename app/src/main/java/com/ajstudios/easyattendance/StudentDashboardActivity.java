package com.ajstudios.easyattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajstudios.easyattendance.Adapter.SubjectStatsAdapter;
import com.ajstudios.easyattendance.model.AttendanceItem;
import com.ajstudios.easyattendance.model.AttendanceReport;
import com.ajstudios.easyattendance.model.SubjectStat;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvClassInfo, tvPercentage;
    private ProgressBar itemsProgressBar;
    private RecyclerView recyclerSubjects;
    private android.widget.ImageButton btnLogout;
    private SubjectStatsAdapter adapter;
    private List<SubjectStat> statsList = new ArrayList<>();

    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("EasyAttendance", MODE_PRIVATE);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvClassInfo = findViewById(R.id.tvClassInfo);
        tvPercentage = findViewById(R.id.tvPercentage);
        itemsProgressBar = findViewById(R.id.itemsProgressBar);
        recyclerSubjects = findViewById(R.id.recyclerSubjects);
        btnLogout = findViewById(R.id.btnLogout);

        recyclerSubjects.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectStatsAdapter(statsList);
        recyclerSubjects.setAdapter(adapter);

        loadStudentData();

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(StudentDashboardActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadStudentData() {
        String name = sharedPreferences.getString("STUDENT_NAME", "Student");
        String classId = sharedPreferences.getString("CLASS_ID", "");
        String regNo = sharedPreferences.getString("STUDENT_ID", ""); // Actually we store 'id' as key, so lets check LoginActivity.. 
        // Wait, LoginActivity stored: editor.putString("STUDENT_ID", student.getId()); 
        // But AttendanceItem uses 'regNo'. 
        // We probably need the 'regNo' string itself.
        // Let's re-fetch or assume regNo is available.
        // Currently I didn't store actual "RegNo" in prefs in LoginActivity, just the doc ID.
        // I will fix this by fetching the student doc again or storing it in Login.
        // *Correction*: LoginActivity keys: "STUDENT_NAME", "CLASS_ID", "STUDENT_ID".
        // AtttendanceItem stores 'regNo' (String). 
        // I should have stored 'regNo' in prefs. 
        // I'll fetch it by ID here since 'regNo' is critical.
    }
    
    // Override loadStudentData to fetch profile then reports
    @Override
    protected void onResume() {
        super.onResume();
        fetchProfileAndReports();
    }
    
    private void fetchProfileAndReports() {
        String docId = sharedPreferences.getString("STUDENT_ID", "");
        if (docId.isEmpty()) return;

        db.collection("students").document(docId).get().addOnSuccessListener(doc -> {
           if (doc.exists()) {
               String name = doc.getString("name");
               String regNo = doc.getString("regNo");
               String classId = doc.getString("classId");
               
               tvWelcome.setText("Welcome, " + name);
               // Fetch Class Name
               db.collection("classes").document(classId).get().addOnSuccessListener(classDoc -> {
                   if (classDoc.exists()) {
                       String className = classDoc.getString("className");
                       String subjectName = classDoc.getString("subjectName");
                       tvClassInfo.setText(className + " | " + subjectName);
                   }
               });
               
               calculateAttendance(classId, regNo);
           }
        });
    }

    private void calculateAttendance(String classId, String regNo) {
        db.collection("classes").document(classId).collection("attendance_reports")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Map<String, int[]> subjectMap = new HashMap<>(); // Name -> [Present, Total]
                        int totalAll = 0;
                        int presentAll = 0;

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            AttendanceReport report = doc.toObject(AttendanceReport.class);
                            String subj = report.getSubjectName();
                            if (subj == null) subj = "General";

                            // Find student status
                            boolean found = false;
                            boolean present = false;
                            
                            if (report.getAttendanceList() != null) {
                                for (AttendanceItem item : report.getAttendanceList()) {
                                    if (item.getRegNo() != null && item.getRegNo().equals(regNo)) {
                                        found = true;
                                        present = "Present".equalsIgnoreCase(item.getStatus());
                                        break;
                                    }
                                }
                            }

                            if (found) {
                                if (!subjectMap.containsKey(subj)) {
                                    subjectMap.put(subj, new int[]{0, 0});
                                }
                                subjectMap.get(subj)[1]++; // Increment Total
                                totalAll++;
                                if (present) {
                                    subjectMap.get(subj)[0]++; // Increment Present
                                    presentAll++;
                                }
                            }
                        }

                        // Update UI
                        statsList.clear();
                        for (Map.Entry<String, int[]> entry : subjectMap.entrySet()) {
                            statsList.add(new SubjectStat(entry.getKey(), entry.getValue()[0], entry.getValue()[1]));
                        }
                        adapter.notifyDataSetChanged();

                        int overallPerc = (totalAll == 0) ? 0 : (int)((presentAll / (float)totalAll) * 100);
                        tvPercentage.setText(overallPerc + "%");
                        itemsProgressBar.setProgress(overallPerc);
                    } else {
                        Toast.makeText(this, "Failed to load attendance", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
