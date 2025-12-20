package com.ajstudios.easyattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ajstudios.easyattendance.Adapter.ReportsAdapter;
import com.ajstudios.easyattendance.model.AttendanceReport;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Reports_Activity extends AppCompatActivity {

    String subjectName, className, room_ID;
    RecyclerView recyclerView;
    ReportsAdapter mAdapter;
    List<AttendanceReport> reportList = new ArrayList<>();
    FirebaseFirestore db;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        db = FirebaseFirestore.getInstance();

        subjectName = getIntent().getStringExtra("subject_name");
        className = getIntent().getStringExtra("class_name");
        room_ID = getIntent().getStringExtra("room_ID");

        recyclerView = findViewById(R.id.recyclerView_reports);
        
        // Ensure you have a progress bar in activity_reports.xml if possible.
        // If not, we can treat visibility of recyclerView or add one dynamically, or just use Toast.
        // For now, I'll check if layout has it, otherwise skip it.
        // Previous Activity didn't use one explicitly in the code shown, but Class_Detail did.
        // I will just fetch quietly.

        Toolbar toolbar = findViewById(R.id.toolbar_reports);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(subjectName);
            getSupportActionBar().setSubtitle(className);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);

        mAdapter = new ReportsAdapter(this, reportList);
        recyclerView.setAdapter(mAdapter);
        
        fetchReports();
    }

    private void fetchReports() {
        db.collection("classes").document(room_ID).collection("attendance_reports")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(Reports_Activity.this, "Error fetching reports", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    reportList.clear();
                    if (snapshots != null) {
                         for (QueryDocumentSnapshot doc : snapshots) {
                             AttendanceReport report = doc.toObject(AttendanceReport.class);
                             report.setId(doc.getId());
                             reportList.add(report);
                         }
                    }
                    
                    // Sort by Date probably? Or leave as is. Firestore snapshot might not be sorted.
                    // Let's sort manually if needed, but strings like "12-Dec-2025" are hard to sort alphabetically.
                    // Ideally we should store timestamp. For now, just display.
                    
                    mAdapter.updateList(reportList);
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.only_dot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}