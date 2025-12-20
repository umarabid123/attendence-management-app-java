package com.ajstudios.easyattendance.viewholders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ajstudios.easyattendance.R;

public class StudentViewHolder extends RecyclerView.ViewHolder {
    public final TextView student_name;
    public final TextView student_regNo;
    public final RadioGroup radioGroup;
    public final RadioButton radioButton_present, radioButton_absent;
    public final LinearLayout layout;

    public StudentViewHolder(@NonNull View itemView) {
        super(itemView);
        student_name = itemView.findViewById(R.id.student_name_adapter);
        student_regNo = itemView.findViewById(R.id.student_regNo_adapter);
        radioGroup = itemView.findViewById(R.id.radioGroup);
        radioButton_present = itemView.findViewById(R.id.radio_present);
        radioButton_absent = itemView.findViewById(R.id.radio_absent);
        layout = itemView.findViewById(R.id.layout_click);
    }
}
