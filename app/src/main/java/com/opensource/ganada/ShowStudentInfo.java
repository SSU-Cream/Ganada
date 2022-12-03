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

public class ShowStudentInfo extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private TextView show_student_name;
    private TextView show_student_age;
    private TextView show_student_score;
    private EditText show_detail_record;
    private Button revise_student_info_button;
    private Button delete_student_button;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private View headerView;
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
        delete_student_button = (Button) findViewById(R.id.delete_student_button);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        setSideNavBar();
        set_header_content();
        set_student_info(studentItem);

        revise_student_info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),studentItem.getScores().get(0).getPracticeTime(), Toast.LENGTH_SHORT).show();
                revise_student(studentItem);
            }
        });

        delete_student_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_student(studentItem);
            }
        });

        headerView = navigationView.getHeaderView(0);
        Button headerBack = (Button) headerView.findViewById(R.id.header_back);
        headerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //추가된 소스, ToolBar에 menu.xml을 인플레이트함
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.back) {
            onBackPressed();
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item1:
                Toast.makeText(getApplicationContext(),"로그아웃 하였습니다",Toast.LENGTH_SHORT).show();
                signOut();
                break;
            case R.id.menu_item2:
                Intent intent = new Intent(getApplicationContext(), ModifyMemeberInfo.class);
                intent.putExtra("user",currentUser);
                startActivity(intent);
                break;
            case R.id.menu_item3:
                show_delete_member_dlg();
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(getApplicationContext(), ManageActivity.class);
            intent.putExtra("user",currentUser);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    public void setSideNavBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger);
        toolbar.setBackgroundColor(Color.parseColor("#F8CACC"));
        toolbarText.setText("학습 아동 세부 관리");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_menu_layout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void set_header_content() {
        headerView = navigationView.getHeaderView(0);
        TextView headerName = (TextView) headerView.findViewById(R.id.header_name);
        TextView headerEmail = (TextView) headerView.findViewById(R.id.header_email);
        TextView headerBirth = (TextView) headerView.findViewById(R.id.header_birth);
        TextView headerRole = (TextView) headerView.findViewById(R.id.header_role);
        headerName.setText(currentUser.getName());
        headerEmail.setText(mAuth.getCurrentUser().getEmail());
        headerBirth.setText(currentUser.getBirth());
        headerRole.setText(currentUser.getRole());
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void deleteMember() {
        Toast.makeText(getApplicationContext(),"탈퇴 하였습니다",Toast.LENGTH_SHORT).show();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        mDatabase.removeValue();
        mAuth.getCurrentUser().delete();

        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void delete_find_data(String deleteKey) {
        mDatabase = FirebaseDatabase.getInstance().getReference("findData").child(deleteKey);
        mDatabase.removeValue();
    }

    private void show_delete_member_dlg() {
        AlertDialog.Builder deleteMemberDlg = new AlertDialog.Builder(this);
        deleteMemberDlg.setTitle("정말 탈퇴 하시겠습니까?");
        deleteMemberDlg.setIcon(R.drawable.pic1);
        deleteMemberDlg.setNegativeButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteMember();
                delete_find_data(currentUser.getName()+currentUser.getBirth());
            }
        });
        deleteMemberDlg.setPositiveButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        deleteMemberDlg.show();
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
        show_student_age.setText(Integer.toString(studentItem.getAge()));
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