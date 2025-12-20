package com.ajstudios.easyattendance.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ajstudios.easyattendance.R;
import com.ajstudios.easyattendance.model.AttendanceItem;
import java.util.List;

public class Reports_Detail_Adapter extends RecyclerView.Adapter<Reports_Detail_Adapter.ViewHolder> {

    private Context context;
    private List<AttendanceItem> list;

    public Reports_Detail_Adapter(Context context, List<AttendanceItem> list) {
        this.context = context;
        this.list = list;
    }
    
    public void updateList(List<AttendanceItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.report_detail_adapter_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceItem item = list.get(position);
        holder.namE.setText(item.getStudentName());
        holder.regNo.setText(item.getRegNo());
        
        if ("Present".equalsIgnoreCase(item.getStatus())) {
            holder.status.setText("P");
            holder.circle.setCardBackgroundColor(context.getResources().getColor(R.color.green_new));
        } else {
            holder.status.setText("A");
            holder.circle.setCardBackgroundColor(context.getResources().getColor(R.color.red_new));
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView namE, regNo, status;
        CardView circle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namE = itemView.findViewById(R.id.student_name_report_detail_adapter);
            regNo = itemView.findViewById(R.id.student_regNo_report_detail_adapter);
            status = itemView.findViewById(R.id.status_report_detail_adapter);
            circle = itemView.findViewById(R.id.cardView_report_detail_adapter);
        }
    }
}
