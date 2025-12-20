package com.ajstudios.easyattendance.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ajstudios.easyattendance.R;
import com.ajstudios.easyattendance.model.Student;
import com.ajstudios.easyattendance.viewholders.StudentViewHolder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentAdapter extends RecyclerView.Adapter<StudentViewHolder> {
    private List<Student> studentList;
    private Context context;
    private Map<String, String> attendanceMap = new HashMap<>(); // regNo -> "Present"/"Absent"

    public StudentAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    public void updateList(List<Student> newList) {
        this.studentList = newList;
        notifyDataSetChanged();
    }
    
    public Map<String, String> getAttendanceMap() {
        return attendanceMap;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_attendance_adapter, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.student_name.setText(student.getName());
        holder.student_regNo.setText(student.getRegNo());
        
        // Reset listeners to avoid recycling issues
        holder.radioButton_present.setOnCheckedChangeListener(null);
        holder.radioButton_absent.setOnCheckedChangeListener(null);

        String status = attendanceMap.get(student.getRegNo());
        if ("Present".equals(status)) {
            holder.radioButton_present.setChecked(true);
            holder.radioButton_absent.setChecked(false);
        } else if ("Absent".equals(status)) {
            holder.radioButton_absent.setChecked(true);
            holder.radioButton_present.setChecked(false);
        } else {
            holder.radioGroup.clearCheck();
        }

        holder.radioButton_present.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                attendanceMap.put(student.getRegNo(), "Present");
            }
        });

        holder.radioButton_absent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                attendanceMap.put(student.getRegNo(), "Absent");
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0;
    }
}
