package com.ajstudios.easyattendance.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajstudios.easyattendance.R;
import com.ajstudios.easyattendance.model.SubjectStat;

import java.util.List;

public class SubjectStatsAdapter extends RecyclerView.Adapter<SubjectStatsAdapter.ViewHolder> {

    private List<SubjectStat> statsList;

    public SubjectStatsAdapter(List<SubjectStat> statsList) {
        this.statsList = statsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_stat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubjectStat stat = statsList.get(position);
        holder.tvSubjectName.setText(stat.getSubjectName());
        holder.tvSubjectPerc.setText(stat.getPercentage() + "%");
        holder.pbSubject.setProgress(stat.getPercentage());
        holder.tvStats.setText("Attended " + stat.getAttended() + "/" + stat.getTotal());
    }

    @Override
    public int getItemCount() {
        return statsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvSubjectPerc, tvStats;
        ProgressBar pbSubject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvSubjectPerc = itemView.findViewById(R.id.tvSubjectPerc);
            tvStats = itemView.findViewById(R.id.tvStats);
            pbSubject = itemView.findViewById(R.id.pbSubject);
        }
    }
}
