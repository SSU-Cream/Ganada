package com.opensource.ganada;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowStudentInfo extends AppCompatActivity {
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private TextView show_student_name;
    private TextView show_student_age;
    private TextView show_student_score;
    private EditText show_detail_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_student_info);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        StudentItem studentItem = (StudentItem) intent.getSerializableExtra("item");
        show_student_name = (TextView) findViewById(R.id.show_student_name);
        show_student_age = (TextView) findViewById(R.id.show_student_age);
        show_student_score = (TextView) findViewById(R.id.show_student_score);
        show_detail_record = (EditText) findViewById(R.id.show_detail_record);
        //Toast.makeText(getApplicationContext(),studentItem.getName(),Toast.LENGTH_SHORT).show();
        set_student_info(studentItem);
    }

    public void set_student_info(StudentItem studentItem) {
        show_student_name.setText(studentItem.getName());
        show_student_age.setText(Integer.toString(studentItem.getAge()));
        show_student_score.setText("점수 : 0");
        show_detail_record.setText(studentItem.getDetailedRecord());
    }
}