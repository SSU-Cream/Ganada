package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowStudentInfo extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private TextView show_student_name;
    private TextView show_student_age;
    private TextView show_student_score;
    private EditText show_detail_record;
    private Button revise_student_info_button;
    //private Button delete_student_button;
    private Toolbar toolbar;
    UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_student_info);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        currentUser = (UserModel) intent.getSerializableExtra("user");
        StudentItem studentItem = (StudentItem) intent.getSerializableExtra("item");
        show_student_name = (TextView) findViewById(R.id.show_student_name);
        show_student_age = (TextView) findViewById(R.id.show_student_age);
        show_student_score = (TextView) findViewById(R.id.show_student_score);
        show_detail_record = (EditText) findViewById(R.id.show_detail_record);
        revise_student_info_button = (Button) findViewById(R.id.revise_student_info_button);
        //delete_student_button = (Button) findViewById(R.id.delete_student_button);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        toolbarText.setText(" ");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        set_student_info(studentItem);

        revise_student_info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),studentItem.getScores().get(0).getPracticeTime(), Toast.LENGTH_SHORT).show();
                revise_student(studentItem);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ManageActivity.class);
        intent.putExtra("user",currentUser);
        startActivity(intent);
        super.onBackPressed();
    }

    public void revise_student(StudentItem item) {
        item.setDetailedRecord(show_detail_record.getText().toString());
        mDatabase = FirebaseDatabase.getInstance().getReference("students").child(user.getUid());
        mDatabase.child(Integer.toString(item.getStudentNum())).setValue(item);
    }

    public void delete_student(StudentItem item) {
        mDatabase = FirebaseDatabase.getInstance().getReference("students").child(user.getUid());
        DatabaseReference dataRef = mDatabase.child(Integer.toString(item.getStudentNum()));
        Toast.makeText(getApplicationContext(),item.getName(),Toast.LENGTH_SHORT).show();
        dataRef.removeValue();
        onBackPressed();
    }

    public void set_student_info(StudentItem studentItem) {
        String allScore = "";
        show_student_name.setText(studentItem.getName());
        show_student_age.setText(Integer.toString(studentItem.getAge()) + "세");
        if(studentItem.getScores()==null || studentItem.getScores().size()==0) {
            allScore = "점수 없음";
        }
        else {
            for(int i=0; i<studentItem.getScores().size(); i++) {
                allScore += (studentItem.getScores().get(i).getPracticeTime().substring(0,13) + " : " + Integer.toString(studentItem.getScores().get(i).getScore()) + "점\n");
            }
        }
        show_student_score.setText(allScore);
        //show_student_score.setText(studentItem.getScores().get(0).getPracticeTime());
        show_detail_record.setText(studentItem.getDetailedRecord());
    }
}