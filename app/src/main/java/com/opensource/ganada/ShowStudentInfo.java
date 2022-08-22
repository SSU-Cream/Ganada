package com.opensource.ganada;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    Toolbar toolbar;

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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        toolbar.setBackgroundColor(Color.parseColor("#F8CACC"));
        toolbarText.setText("학습 아동 세부 관리");

        set_student_info(studentItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //추가된 소스, ToolBar에 menu.xml을 인플레이트함
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //추가된 소스, ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
        switch (item.getItemId()) {
            case R.id.all_menu:
                Toast.makeText(getApplicationContext(), "기능 더보기", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);

            default:
                Toast.makeText(getApplicationContext(), "뒤로가기 버튼 클릭됨", Toast.LENGTH_SHORT).show();
                return true;
        }
    }

    public void set_student_info(StudentItem studentItem) {
        show_student_name.setText(studentItem.getName());
        show_student_age.setText(Integer.toString(studentItem.getAge()));
        show_student_score.setText("기록 없음");
        show_detail_record.setText(studentItem.getDetailedRecord());
    }
}