package com.ajstudios.easyattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ajstudios.easyattendance.Adapter.Reports_Detail_Adapter;
import com.ajstudios.easyattendance.model.AttendanceItem;
import com.ajstudios.easyattendance.model.AttendanceReport;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Reports_Detail_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    Reports_Detail_Adapter mAdapter;
    List<AttendanceItem> attendanceList = new ArrayList<>();

    TextView subj, className, toolbar_title;
    FirebaseFirestore db;
    String reportId, room_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports__detail);
        
        db = FirebaseFirestore.getInstance();

        reportId = getIntent().getStringExtra("REPORT_ID");
        room_ID = getIntent().getStringExtra("room_ID");
        String classnameStr = getIntent().getStringExtra("class");
        String subjNameStr = getIntent().getStringExtra("subject");
        String dateStr = getIntent().getStringExtra("date");

        Toolbar toolbar = findViewById(R.id.toolbar_reports_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView_reports_detail);
        subj = findViewById(R.id.subjName_report_detail);
        className = findViewById(R.id.classname_report_detail);
        toolbar_title = findViewById(R.id.toolbar_title);
        
        toolbar_title.setText(dateStr);
        subj.setText(subjNameStr);
        className.setText(classnameStr);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new Reports_Detail_Adapter(this, attendanceList);
        recyclerView.setAdapter(mAdapter);
        
        fetchReportDetails();
    }

    private void fetchReportDetails() {
        if (room_ID == null || reportId == null) return;
        
        db.collection("classes").document(room_ID).collection("attendance_reports").document(reportId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        AttendanceReport report = documentSnapshot.toObject(AttendanceReport.class);
                        if (report != null && report.getAttendanceList() != null) {
                            attendanceList.clear();
                            // Sort by name if desired
                            attendanceList.addAll(report.getAttendanceList());
                            mAdapter.updateList(attendanceList);
                        }
                    } else {
                        Toast.makeText(this, "Report not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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