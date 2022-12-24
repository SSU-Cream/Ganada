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
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private UserModel currentUser = new UserModel();
    private View headerView;
    long pressedTime = 0L;
    TextView menuName;
    TextView menuRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mAuth = FirebaseAuth.getInstance();
        View manageButton = (View) findViewById(R.id.manageButton);
        View practiceButton = (View) findViewById(R.id.practiceButton);
        View communityButton = (View) findViewById(R.id.communityButton);
        View learningButton = (View) findViewById(R.id.learningButton);
        menuName = (TextView) findViewById(R.id.menu_name);
        menuRole = (TextView)findViewById(R.id.menu_role);
        setSideNavBar();
        get_user_info();
        get_current_user();

        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                get_current_user();
                Intent intent = new Intent(getApplicationContext(), ManageActivity.class);
                intent.putExtra("user",currentUser);
                startActivity(intent);
            }
        });

        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                get_current_user();
                Intent intent = new Intent(getApplicationContext(), PracticeActivity.class);
                intent.putExtra("user",currentUser);
                startActivity(intent);
            }
        });

        learningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                get_current_user();
                Intent intent = new Intent(getApplicationContext(), LearningSelectActivity.class);
                intent.putExtra("user",currentUser);
                startActivity(intent);
            }
        });

        communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                get_current_user();
                Intent intent = new Intent(getApplicationContext(), CommunityActivity.class);
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
        Intent urlintent;
        switch (item.getItemId()) {
            case R.id.menu_item1:
                Toast.makeText(getApplicationContext(),"로그아웃 하였습니다",Toast.LENGTH_SHORT).show();
                signOut();
                break;
            case R.id.menu_item2:
                get_current_user();
                Intent intent = new Intent(getApplicationContext(), ModifyMemeberInfo.class);
                intent.putExtra("user",currentUser);
                startActivity(intent);
                break;
            case R.id.menu_item3:
                show_register_student_dlg();
                break;
            case R.id.LipNet_link:
                urlintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rizkiarm/LipNet"));
                startActivity(urlintent);
                break;
            case R.id.koreanlipnet_link:
                urlintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kykymouse/koreanLipNet"));
                startActivity(urlintent);
                break;
            case R.id.practice_youtube_link:
                urlintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=JMfCyvMQ-JM"));
                startActivity(urlintent);
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if(pressedTime == 0L) {
                Toast.makeText(MenuActivity.this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                pressedTime = System.currentTimeMillis();
            } else {
                long seconds = (long) (System.currentTimeMillis() - pressedTime);
                if(seconds > 2000L) {
                    Toast.makeText(MenuActivity.this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                    pressedTime = 0L;
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    public void setSideNavBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        toolbar.setBackgroundColor(Color.parseColor("#FCEDE6"));
        toolbarText.setText(" ");

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

    public void get_user_info() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                set_header_content(user);
                menuName.setText(user.getName()+"님");
                if(user.getRole().equals("Teacher")) {
                    menuRole.setText("교사");
                    menuRole.setBackgroundColor(Color.parseColor("#FF9A51"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void set_header_content(UserModel userModel) {
        headerView = navigationView.getHeaderView(0);
        TextView headerName = (TextView) headerView.findViewById(R.id.header_name);
        TextView headerEmail = (TextView) headerView.findViewById(R.id.header_email);
        TextView headerBirth = (TextView) headerView.findViewById(R.id.header_birth);
        TextView headerRole = (TextView) headerView.findViewById(R.id.header_role);
        headerName.setText(userModel.getName());
        headerEmail.setText(mAuth.getCurrentUser().getEmail());
        headerBirth.setText(userModel.getBirth());
        headerRole.setText(userModel.getRole());
    }

    public void get_current_user() {
        headerView = navigationView.getHeaderView(0);
        TextView headerName = (TextView) headerView.findViewById(R.id.header_name);
        TextView headerBirth = (TextView) headerView.findViewById(R.id.header_birth);
        TextView headerRole = (TextView) headerView.findViewById(R.id.header_role);
        currentUser.setName(headerName.getText().toString());
        currentUser.setBirth(headerBirth.getText().toString());
        currentUser.setRole(headerRole.getText().toString());
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

    private void show_register_student_dlg() {
        AlertDialog.Builder deleteMemberDlg = new AlertDialog.Builder(MenuActivity.this);
        deleteMemberDlg.setTitle("정말 탈퇴 하시겠습니까?");
        deleteMemberDlg.setIcon(R.drawable.pic1);
        deleteMemberDlg.setNegativeButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteMember();
                get_current_user();
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