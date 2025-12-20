package com.ajstudios.easyattendance.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.ajstudios.easyattendance.R;
import com.ajstudios.easyattendance.realm.Attendance_Reports;
import com.ajstudios.easyattendance.viewholders.ViewHolder_reports;

import io.realm.Realm;
import io.realm.RealmResults;
import androidx.recyclerview.widget.RecyclerView;

public class ReportsAdapter extends RecyclerView.Adapter<ViewHolder_reports> {

    private final Activity mActivity;
    RealmResults<Attendance_Reports> mList;
    String stuID, mroomID;
    Realm realm = Realm.getDefaultInstance();

    public ReportsAdapter(RealmResults<Attendance_Reports> list, Activity context, String roomID) {
        mActivity = context;
        mList = list;
        mroomID =roomID;
    }

    @NonNull
    @Override
    public ViewHolder_reports onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_adapter_item, parent, false);
        return new ViewHolder_reports(itemView, mActivity, mList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_reports holder, int position) {
        Attendance_Reports temp = mList.get(position);
        holder.month.setText(temp.getMonthOnly());
        holder.date.setText(temp.getDateOnly());

    }


    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }


}
