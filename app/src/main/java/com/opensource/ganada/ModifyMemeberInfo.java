package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ModifyMemeberInfo extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private Button revise_button;
    private EditText revise_name;
    private DatePicker revise_dayspin;
    private EditText verify_pwd;
    private boolean is_checked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_memeber_info);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();
        revise_button = (Button) findViewById(R.id.revise_button);
        revise_name = (EditText) findViewById(R.id.revise_name);
        revise_dayspin = (DatePicker) findViewById(R.id.revise_dayspin);
        verify_pwd = (EditText) findViewById(R.id.verify_pwd);

        revise_dayspin.setMaxDate(System.currentTimeMillis());

        setUserData();

        revise_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyMember();
            }
        });

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
        Toast.makeText(ModifyMemeberInfo.this, "회원 정보가 수정 되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void find_email_pwd(String name, String birth) {
        String findKey = name + birth;
        mDatabase = FirebaseDatabase.getInstance().getReference("findData");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()) {
                    if(child.getKey().equals(findKey)) {
                        String user_email_pwd = child.getValue().toString();
                        int idx = user_email_pwd.indexOf("!");
                        String email = user_email_pwd.substring(0,idx);
                        String password = user_email_pwd.substring(idx+1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });
    }
}