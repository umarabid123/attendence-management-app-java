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
               
               tvWelcome.setText("Welcome, " + (name != null ? name : "Student"));

               if (classId == null || classId.isEmpty()) {
                   tvClassInfo.setText("Not Enrolled");
                   return;
               }

               // Update Class Info
               db.collection("classes").document(classId).get().addOnSuccessListener(classDoc -> {
                   if (classDoc.exists()) {
                       String className = classDoc.getString("name_class"); 
                       String subjectName = classDoc.getString("name_subject");
                       if (className == null) className = "Class";
                       if (subjectName == null) subjectName = "Subject";
                       tvClassInfo.setText(className + " | " + subjectName);
                   }
               });
               
               // Use stored RegNo if available or from doc
               if (regNo == null || regNo.isEmpty()) {
                   regNo = sharedPreferences.getString("REG_NO", "");
               }

               if (regNo != null && !regNo.isEmpty()) {
                   calculateAttendance(classId, regNo);
               } else {
                   Toast.makeText(this, "Registration Number missing.", Toast.LENGTH_SHORT).show();
               }
           }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void calculateAttendance(String classId, String regNo) {
        if (classId == null) return;
        
        db.collection("classes").document(classId).collection("attendance_reports")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {


                        try {
                            if (task.isSuccessful() && task.getResult() != null) {
                                // Clear lists first if reloading
                                
                                // Re-initialize maps here
                                Map<String, int[]> subjectMap = new HashMap<>(); 
                                int totalAll = 0;
                                int presentAll = 0;
                                
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    try {
                                        AttendanceReport report = doc.toObject(AttendanceReport.class);
                                        String subj = report.getSubjectName();
                                        if (subj == null || subj.isEmpty()) subj = "General";

                                        if (report.getAttendanceList() != null) {
                                            for (AttendanceItem item : report.getAttendanceList()) {
                                                String r = item.getRegNo();
                                                if (r != null && regNo != null && r.trim().equalsIgnoreCase(regNo.trim())) {
                                                    
                                                    if (!subjectMap.containsKey(subj)) {
                                                        subjectMap.put(subj, new int[]{0, 0});
                                                    }
                                                    
                                                    // Increment Total
                                                    subjectMap.get(subj)[1]++;
                                                    totalAll++;
                                                    
                                                    // Check Present
                                                    String status = item.getStatus();
                                                    if (status != null && (status.equalsIgnoreCase("Present") || status.toUpperCase().startsWith("P"))) {
                                                        subjectMap.get(subj)[0]++;
                                                        presentAll++;
                                                    }
                                                    break; // Found student in this report
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace(); 
                                    }
                                }

                                // Update Stats List
                                statsList.clear();
                                for (Map.Entry<String, int[]> entry : subjectMap.entrySet()) {
                                    int p = entry.getValue()[0];
                                    int t = entry.getValue()[1];
                                    statsList.add(new SubjectStat(entry.getKey(), p, t));
                                }
                                adapter.notifyDataSetChanged();

                                // Overall
                                int overallPerc = (totalAll == 0) ? 0 : (int)((presentAll / (float)totalAll) * 100);
                                tvPercentage.setText(overallPerc + "%");
                                itemsProgressBar.setProgress(overallPerc);
                            } else {
                                Toast.makeText(this, "Failed to load attendance records.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception fatalE) {
                            fatalE.printStackTrace();
                            Toast.makeText(this, "Error processing data: " + fatalE.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                        
                    } else {
                        Toast.makeText(this, "Failed to load attendance records.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
