package com.ajstudios.easyattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ajstudios.easyattendance.models.ClassItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Insert_class_Activity extends AppCompatActivity {

    Button create_button;
    EditText _className;
    EditText _subjectName;

    FirebaseFirestore db;

    private  String position_bg = "0";

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_class_);

        Toolbar toolbar = findViewById(R.id.toolbar_insert_class);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        create_button = findViewById(R.id.button_createClass);
        _className = findViewById(R.id.className_createClass);
        _subjectName = findViewById(R.id.subjectName_createClass);

        db = FirebaseFirestore.getInstance();

        final RadioButton button1 = (RadioButton) findViewById(R.id.button1);
        final RadioButton button2 = (RadioButton) findViewById(R.id.button2);
        final RadioButton button3 = (RadioButton) findViewById(R.id.button3);
        final RadioButton button4 = (RadioButton) findViewById(R.id.button4);
        final RadioButton button5 = (RadioButton) findViewById(R.id.button5);
        final RadioButton button6 = (RadioButton) findViewById(R.id.button6);

        RadioGroup group = (RadioGroup) findViewById(R.id.group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.button1) position_bg = "0";
                else if (checkedId == R.id.button2) position_bg = "1";
                else if (checkedId == R.id.button3) position_bg = "2";
                else if (checkedId == R.id.button4) position_bg = "3";
                else if (checkedId == R.id.button5) position_bg = "4";
                else if (checkedId == R.id.button6) position_bg = "5";
            }
        });

        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValid()) {

                    final ProgressDialog progressDialog = new ProgressDialog(Insert_class_Activity.this);
                    progressDialog.setMessage("Creating class...");
                    progressDialog.show();

                    // Use auto-generated ID to prevent invalid path characters
                    String id = db.collection("classes").document().getId();
                    String currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
                    
                    ClassItem classItem = new ClassItem(id, _className.getText().toString(), _subjectName.getText().toString(), position_bg, currentUserId);

                    db.collection("classes").document(id).set(classItem)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Insert_class_Activity.this, "Successfully created", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Insert_class_Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                } else {
                    Toast.makeText(Insert_class_Activity.this, "Fill all details", Toast.LENGTH_SHORT).show();
                }

                //-------

            }
        });



    }

    public boolean isValid(){

        return !_className.getText().toString().isEmpty() && !_subjectName.getText().toString().isEmpty();
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