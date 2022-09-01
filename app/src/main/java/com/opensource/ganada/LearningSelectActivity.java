package com.opensource.ganada;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LearningSelectActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
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
        setContentView(R.layout.activity_learning_select);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        Intent intent = getIntent();
        currentUser = (UserModel) intent.getSerializableExtra("user");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();
        recyclerView = (RecyclerView) findViewById(R.id.studentRecyclerView);
        studentItems = new ArrayList<StudentItem>();

        setSideNavBar();
        set_header_content();

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new StudentAdapter(getApplicationContext());

        getStudentsDatas(adapter,studentItems);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new StudentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(StudentAdapter.ViewHolder holder, View view, int position) {
                StudentItem item = adapter.getItem(position);
                Toast.makeText(getApplicationContext(),"아이템 선택됨 : " + item.getName(), Toast.LENGTH_SHORT).show();

                finish();
                Intent intent = new Intent(getApplicationContext(), LearningActivity.class);
                intent.putExtra("childName",item);
                intent.putExtra("user",currentUser);
                startActivity(intent);
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
                Intent intent = new Intent(getApplicationContext(), LearningActivity.class);
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
            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
            intent.putExtra("user",currentUser);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    public void setSideNavBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        toolbarText.setText("연습할 아동 선택");
        toolbar.setBackgroundColor(Color.parseColor("#F8CACC"));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger);

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
        headerName.setText(currentUser.getName());
        headerEmail.setText(mAuth.getCurrentUser().getEmail());
        headerBirth.setText(currentUser.getBirth());
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

    public void delete_all_data(String deleteKey) {
        mDatabase = FirebaseDatabase.getInstance().getReference("findData").child(deleteKey);
        mDatabase.removeValue();
        mDatabase = FirebaseDatabase.getInstance().getReference("students").child(mAuth.getUid());
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
                delete_all_data(currentUser.getName()+currentUser.getBirth());
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
}