package com.ajstudios.easyattendance.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajstudios.easyattendance.R;
import com.ajstudios.easyattendance.Reports_Detail_Activity;
import com.ajstudios.easyattendance.model.AttendanceReport;
import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder> {
    private Context context;
    private List<AttendanceReport> list;

    public ReportsAdapter(Context context, List<AttendanceReport> list) {
        this.context = context;
        this.list = list;
    }

    public void updateList(List<AttendanceReport> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reports_adapter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceReport report = list.get(position);
        
        String dateStr = report.getDate();
        String displayDate = "";
        String displayMonth = "";
        if (dateStr != null && dateStr.contains("-")) {
             String[] parts = dateStr.split("-");
             if (parts.length >= 2) {
                 displayDate = parts[0];
                 displayMonth = parts[1];
             }
        }
        
        holder.date.setText(displayDate);
        holder.month.setText(displayMonth);

        holder.itemView.setOnClickListener(v -> {
             Intent intent = new Intent(context, Reports_Detail_Activity.class);
             intent.putExtra("REPORT_ID", report.getId()); 
             intent.putExtra("class", report.getClassName());
             intent.putExtra("subject", report.getSubjectName());
             intent.putExtra("date", report.getDate());
             intent.putExtra("room_ID", report.getClassId());
             context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView month, date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            month = itemView.findViewById(R.id.month_report_adapter);
            date = itemView.findViewById(R.id.date_report_adapter);
        }
    }
}
