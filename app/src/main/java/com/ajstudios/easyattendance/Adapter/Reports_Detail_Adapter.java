package com.ajstudios.easyattendance.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.ajstudios.easyattendance.R;
import com.ajstudios.easyattendance.realm.Attendance_Students_List;
import com.ajstudios.easyattendance.viewholders.ViewHolder_reports_detail;

import io.realm.Realm;
import io.realm.RealmResults;
import androidx.recyclerview.widget.RecyclerView;

public class Reports_Detail_Adapter extends RecyclerView.Adapter<ViewHolder_reports_detail> {

    private final Activity mActivity;
    RealmResults<Attendance_Students_List> mList;
    String stuID, mroomID;
    Realm realm = Realm.getDefaultInstance();

    public Reports_Detail_Adapter(RealmResults<Attendance_Students_List> list, Activity context, String roomID) {
        mActivity = context;
        mList = list;
        mroomID =roomID;
    }

    @NonNull
    @Override
    public ViewHolder_reports_detail onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_detail_adapter_item, parent, false);
        return new ViewHolder_reports_detail(itemView, mActivity, mList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_reports_detail holder, int position) {
        Attendance_Students_List temp = mList.get(position);
        holder.namE.setText(temp.getStudentName());
        holder.regNo.setText(temp.getStudentRegNo());
        if (temp.getAttendance().equals("Present")){
            holder.status.setText("P");
            holder.circle.setCardBackgroundColor(mActivity.getResources().getColor(R.color.green_new));
        }else{
            holder.status.setText("A");
            holder.circle.setCardBackgroundColor(mActivity.getResources().getColor(R.color.red_new));
        }
    }


    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }


}
