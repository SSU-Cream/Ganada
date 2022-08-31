package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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


public class ModifyMemeberInfo extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private Button revise_button;
    private EditText revise_name;
    private DatePicker revise_dayspin;
    private EditText verify_pwd;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private View headerView;
    UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_memeber_info);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();
        Intent intent = getIntent();
        currentUser = (UserModel) intent.getSerializableExtra("user");
        revise_button = (Button) findViewById(R.id.revise_button);
        revise_name = (EditText) findViewById(R.id.revise_name);
        revise_dayspin = (DatePicker) findViewById(R.id.revise_dayspin);
        verify_pwd = (EditText) findViewById(R.id.verify_pwd);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSideNavBar();
        set_header_content();

        revise_dayspin.setMaxDate(System.currentTimeMillis());

        setUserData();

        revise_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm_password();
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
                Toast.makeText(getApplicationContext(),"현재 페이지입니다.", Toast.LENGTH_SHORT).show();
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
            super.onBackPressed();
        }
    }

    public void setSideNavBar() {
        setSupportActionBar(toolbar);

        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        toolbarText.setText("게시글");

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger);
        toolbar.setBackgroundColor(Color.parseColor("#8DA4D0"));

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
        androidx.appcompat.app.AlertDialog.Builder deleteMemberDlg = new androidx.appcompat.app.AlertDialog.Builder(this);
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

    private void setUserData() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                revise_name.setText(user.getName());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
                String birth = user.getBirth();
                String[] birthInt = birth.split("-");
                revise_dayspin.updateDate(Integer.parseInt(birthInt[0]),Integer.parseInt(birthInt[1])-1,Integer.parseInt(birthInt[2]));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void modifyMember() {
        String name = revise_name.getText().toString();
        String birth = String.format("%d-%d-%d", revise_dayspin.getYear(),revise_dayspin.getMonth()+1, revise_dayspin.getDayOfMonth());
        //String pwd = verify_pwd.getText().toString();
        if (name.equals("")) { Toast.makeText(ModifyMemeberInfo.this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show(); return; }
        //if (pwd.equals("")) { Toast.makeText(ModifyMemeberInfo.this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show(); return; }
        UserModel newUser = new UserModel(name,birth, user.getUid());
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        mDatabase.setValue(newUser);
        currentUser.setName(name);
        currentUser.setBirth(birth);
        Toast.makeText(ModifyMemeberInfo.this, "회원 정보가 수정 되었습니다.", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    public void modify_find_data(String deleteKey,String pwd) {
        mDatabase = FirebaseDatabase.getInstance().getReference("findData").child(deleteKey);
        mDatabase.removeValue();
        mDatabase = FirebaseDatabase.getInstance().getReference("findData");
        String name = revise_name.getText().toString();
        String birth = String.format("%d-%d-%d", revise_dayspin.getYear(),revise_dayspin.getMonth()+1, revise_dayspin.getDayOfMonth());
        mDatabase.child(name+birth).setValue(mAuth.getCurrentUser().getEmail()+"!"+pwd);
    }

    public void confirm_password () {
        String pwd_check = verify_pwd.getText().toString();
        String findKey = currentUser.getName() + currentUser.getBirth();
        mDatabase = FirebaseDatabase.getInstance().getReference("findData");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exist_pwd=false;
                for(DataSnapshot child : snapshot.getChildren()) {
                    if(child.getKey().equals(findKey)) {
                        String user_email_pwd = child.getValue().toString();
                        int idx = user_email_pwd.indexOf("!");
                        String password = user_email_pwd.substring(idx+1);
                        if(password.equals(pwd_check)) {
                            modifyMember();
                            modify_find_data(findKey,pwd_check);
                            exist_pwd = true;
                        }
                        break;
                    }
                }
                if(exist_pwd == false) {
                    Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });
    }
}