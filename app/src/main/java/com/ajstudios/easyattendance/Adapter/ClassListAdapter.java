package com.ajstudios.easyattendance.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajstudios.easyattendance.R;
import com.ajstudios.easyattendance.models.ClassItem;
import com.ajstudios.easyattendance.viewholders.ViewHolder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ClassListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private final Activity mActivity;
    List<ClassItem> mList;

    public ClassListAdapter(List<ClassItem> list, Activity context) {
        mActivity = context;
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_adapter, parent, false);
        return new ViewHolder(itemView, mActivity, mList);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final ClassItem temp = mList.get(position);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        // Fetch student count asynchronously
        db.collection("Students")
                .whereEqualTo("class_id", temp.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                         if (queryDocumentSnapshots != null) {
                             holder.total_students.setText("Students : " + queryDocumentSnapshots.size());
                         } else {
                             holder.total_students.setText("Students : 0");
                         }
                    }
                });
        
        // Placeholder while loading
        holder.total_students.setText("Students : ...");
        
        holder.class_name.setText(temp.getName_class());
        holder.subject_name.setText(temp.getName_subject());

        if (temp.getPosition_bg() != null) {
            switch (temp.getPosition_bg()) {
                case "0":
                    holder.imageView_bg.setImageResource(R.drawable.asset_bg_paleblue);
                    holder.frameLayout.setBackgroundResource(R.drawable.gradient_color_1);
                    break;
                case "1":
                    holder.imageView_bg.setImageResource(R.drawable.asset_bg_green);
                    holder.frameLayout.setBackgroundResource(R.drawable.gradient_color_2);
                    break;
                case "2":
                    holder.imageView_bg.setImageResource(R.drawable.asset_bg_yellow);
                    holder.frameLayout.setBackgroundResource(R.drawable.gradient_color_3);
                    break;
                case "3":
                    holder.imageView_bg.setImageResource(R.drawable.asset_bg_palegreen);
                    holder.frameLayout.setBackgroundResource(R.drawable.gradient_color_4);
                    break;
                case "4":
                    holder.imageView_bg.setImageResource(R.drawable.asset_bg_paleorange);
                    holder.frameLayout.setBackgroundResource(R.drawable.gradient_color_5);
                    break;
                case "5":
                    holder.imageView_bg.setImageResource(R.drawable.asset_bg_white);
                    holder.frameLayout.setBackgroundResource(R.drawable.gradient_color_6);
                    holder.subject_name.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_color_secondary));
                    holder.class_name.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_color_secondary));
                    holder.total_students.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_color_secondary));
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

