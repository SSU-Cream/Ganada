package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManageActivity extends AppCompatActivity
        implements MenuBottomDialog.BottomSheetListener {
    Button register_button;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    StudentAdapter adapter;
    ArrayList<StudentItem> studentItems;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private UserModel currentUser;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        toolbar.setBackgroundColor(Color.parseColor("#FCEDE6"));
        toolbarText.setText(" ");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        Intent intent = getIntent();
        currentUser = (UserModel) intent.getSerializableExtra("user");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();
        recyclerView = (RecyclerView) findViewById(R.id.studentRecyclerView);
        register_button = (Button) findViewById(R.id.register_student_button);
        studentItems = new ArrayList<StudentItem>();


        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new StudentAdapter(getApplicationContext());

        getStudentsDatas(adapter,studentItems);

        recyclerView.setAdapter(adapter);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_register_student_dlg(adapter);
            }
        });

        adapter.setOnItemClickListener(new StudentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(StudentAdapter.ViewHolder holder, View view, int position) {
                StudentItem item = adapter.getItem(position);
                List<Score> scores = item.getScores();
                Toast.makeText(getApplicationContext(),"아이템 선택됨 : " + item.getName(), Toast.LENGTH_SHORT).show();

                finish();
                Intent intent = new Intent(getApplicationContext(), ShowStudentInfo.class);
                intent.putExtra("item",item);
                intent.putExtra("user",currentUser);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        intent.putExtra("user",currentUser);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //추가된 소스, ToolBar에 menu.xml을 인플레이트함
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onButtonClicked(String text) {
        if(text.equals("logout")) {
            logOut();
        } else if(text.equals("signout")) {
            show_register_student_dlg();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu) {
            showBottomDlg();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showBottomDlg() {
        MenuBottomDialog menuBottomDialog = new MenuBottomDialog();
        menuBottomDialog.show(getSupportFragmentManager(), "menuBottomSheet");
    }

    private void logOut() {
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

    public void delete_all_data() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                mDatabase = FirebaseDatabase.getInstance().getReference("findData").child(user.getName()+user.getBirth());
                mDatabase.removeValue();
                mDatabase = FirebaseDatabase.getInstance().getReference("students").child(mAuth.getUid());
                mDatabase.removeValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void show_register_student_dlg() {
        AlertDialog.Builder deleteMemberDlg = new AlertDialog.Builder(ManageActivity.this);
        deleteMemberDlg.setTitle("정말 탈퇴 하시겠습니까?");
        deleteMemberDlg.setIcon(R.drawable.pic1);
        deleteMemberDlg.setNegativeButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteMember();
                delete_all_data();
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

    public void getStudentsDatas(StudentAdapter adapter, ArrayList<StudentItem> studentItems) {
        mDatabase = FirebaseDatabase.getInstance().getReference("students").child(user.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentItems.clear();
                int idx=0;
                String names="";
                for(DataSnapshot child : snapshot.getChildren()) {
                    StudentItem studentItem = child.getValue(StudentItem.class);
                    studentItems.add(studentItem);
                    adapter.addItem(studentItem);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void show_register_student_dlg(StudentAdapter adapter) {
        View dlgView = (View)View.inflate(ManageActivity.this, R.layout.register_student, null);
        AlertDialog.Builder register_student_dlg = new AlertDialog.Builder(ManageActivity.this);
        register_student_dlg.setTitle("학생등록");
        register_student_dlg.setIcon(R.drawable.pic1);
        register_student_dlg.setView(dlgView);
        final EditText student_name = dlgView.findViewById(R.id.register_student_name);
        final EditText student_age = dlgView.findViewById(R.id.register_student_age);
        register_student_dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = student_name.getText().toString();
                String age = student_age.getText().toString();
                if(name.equals("")) Toast.makeText(getApplicationContext(), "등록할 학생의 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                else if(age.equals("")) Toast.makeText(getApplicationContext(), "등록할 학생의 나이를 입력하세요.", Toast.LENGTH_SHORT).show();
                else {
                    register_student(name,Integer.parseInt(age),adapter);
                    Toast.makeText(getApplicationContext(), "등록 되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        register_student_dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        register_student_dlg.show();
    }

    public void register_student(String name, int age, StudentAdapter adapter) {
        StudentItem item = new StudentItem(name,age,"",null);
        mDatabase = FirebaseDatabase.getInstance().getReference("students").child(user.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int idx = 1;
                for(DataSnapshot child : snapshot.getChildren()) {
                    if(!child.getKey().toString().equals(Integer.toString(idx))) {
                        break;
                    }
                    idx++;
                }
                item.setStudentNum(idx);
                mDatabase.child(Integer.toString(idx)).setValue(item);
                adapter.addItem(item);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}