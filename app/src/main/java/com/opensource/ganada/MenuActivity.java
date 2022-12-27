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
import android.util.Log;
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

public class MenuActivity extends AppCompatActivity implements MenuBottomDialog.BottomSheetListener {
        //implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private final UserModel currentUser = new UserModel();
    private View headerView;
    long pressedTime = 0L;
    TextView menuName;
    TextView menuRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mAuth = FirebaseAuth.getInstance();
        View manageButton = findViewById(R.id.manageButton);
        View practiceButton = findViewById(R.id.practiceButton);
        View communityButton = findViewById(R.id.communityButton);
        View learningButton = findViewById(R.id.learningButton);
        menuName = findViewById(R.id.menu_name);
        menuRole = findViewById(R.id.menu_role);
        //setSideNavBar();
        get_user_info();
        //get_current_user();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = findViewById(R.id.toolbar_title);
        toolbar.setBackgroundColor(Color.parseColor("#FCEDE6"));
        toolbarText.setText(" ");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertView("ma");
            }
        });

        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertView("pr");
            }
        });

        learningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertView("lc");
            }
        });

        communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertView("co");
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
        } else {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void get_user_info() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
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

    public void convertView(String viewName) {
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                finish();
                Intent intent;
                if(viewName.equals("ma")) { intent = new Intent(getApplicationContext(), ManageActivity.class); }
                else if(viewName.equals("co")) { intent = new Intent(getApplicationContext(), CommunityActivity.class); }
                else if(viewName.equals("pr")) { intent = new Intent(getApplicationContext(), PracticeActivity.class); }
                else { intent = new Intent(getApplicationContext(), LearningSelectActivity.class); }
                intent.putExtra("user",user);
                startActivity(intent);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
        AlertDialog.Builder deleteMemberDlg = new AlertDialog.Builder(MenuActivity.this);
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
}