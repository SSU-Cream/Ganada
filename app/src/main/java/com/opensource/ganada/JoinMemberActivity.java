package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinMemberActivity extends AppCompatActivity {

    ActionCodeSettings actionCodeSettings =
            ActionCodeSettings.newBuilder()
                    // URL you want to redirect back to. The domain (www.example.com) for this
                    // URL must be whitelisted in the Firebase Console.
                    .setUrl("https://test-68ea4.firebaseapp.com")
                    // This must be true
                    .setHandleCodeInApp(true)
                    //.setIOSBundleId("com.example.ios")
                    .setAndroidPackageName(
                            "com.opensource.ganada",
                            true, /* installIfNotAvailable */
                            "28"    /* minimumVersion */)
                    .build();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText sign_name;
    private EditText passwd_check;
    private DatePicker dayPicker;
    private CheckBox isTeacher;
    private CheckBox isParent;
    private boolean is_checked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_member);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        TextView join_back = (TextView) findViewById(R.id.join_back);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        sign_name = (EditText) findViewById(R.id.sign_name);
        passwd_check = (EditText) findViewById(R.id.passwd_check);
        dayPicker = (DatePicker) findViewById(R.id.revise_dayspin);
        isTeacher = (CheckBox) findViewById(R.id.is_teacher);
        isParent = (CheckBox) findViewById(R.id.is_parent);
        Button joinMemberButton = (Button) findViewById(R.id.joinMemberButton);
        Button passwd_check_button = (Button) findViewById(R.id.find_email_pwd_button);
        isTeacher.setOnClickListener(roleCheckBox);
        isParent.setOnClickListener(roleCheckBox);


        dayPicker.setMaxDate(System.currentTimeMillis());        // DatePicker 최대선택날짜 현재시간으로 제한

        /*
        Re-execute the password verification button when changing the password.
        비밀번호 변경시 비밀번호 확인 버튼 재실행
        */
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                is_checked = false;
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        };
        editTextPassword.addTextChangedListener(textWatcher);
        passwd_check.addTextChangedListener(textWatcher);

        join_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        joinMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                if(isTeacher.isChecked()) {
                    sendVerticalEmail();
                    addWaitPeople(editTextEmail.getText().toString());
                }
            }
        });

        passwd_check_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextPassword.getText().toString().equals(passwd_check.getText().toString())) {
                    Toast.makeText(JoinMemberActivity.this, "비밀번호가 일치합니다", Toast.LENGTH_SHORT).show();
                    is_checked = true;
                }
                else {
                    Toast.makeText(JoinMemberActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    View.OnClickListener roleCheckBox = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.is_teacher:
                    isParent.setChecked(false);
                    break;
                case R.id.is_parent:
                    isTeacher.setChecked(false);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    /*
    The code to sign up through email and password.
    이메일, 비밀번호를 통해 회원가입하는 코드
    */
    public void createUser(String email, String password) {
        // Exception to allow all Edittexts to be entered
        String name = sign_name.getText().toString();
        String birth = String.format("%d-%d-%d", dayPicker.getYear(),dayPicker.getMonth()+1, dayPicker.getDayOfMonth());
        String role;
        if (name.equals("")) { Toast.makeText(JoinMemberActivity.this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show(); return; }
        if (email.equals("")) { Toast.makeText(JoinMemberActivity.this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show(); return; }
        if (password.equals("")) { Toast.makeText(JoinMemberActivity.this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show(); return; }
        if (isTeacher.isChecked()) { role = "Teacher"; }
        else if (isParent.isChecked()) { role = "Parent"; }
        else { Toast.makeText(JoinMemberActivity.this, "역할을 선택해 주세요", Toast.LENGTH_SHORT).show(); return; }
        if (is_checked == false) { Toast.makeText(JoinMemberActivity.this, "비밀번호 확인을 실행해주세요", Toast.LENGTH_SHORT).show(); return; }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final String uid = task.getResult().getUser().getUid();     // UID(Unified ID) 생성
                            UserModel userModel = new UserModel(name, birth, uid, role);
                            mDatabase.child("users").child(uid).setValue(userModel);    // 데이터베이스에 (UID,이름,생년월일) 저장
                            mDatabase.child("findData").child(name+birth).setValue(email+"!"+password);
                            Toast.makeText(JoinMemberActivity.this, "회원가입을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(JoinMemberActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void sendVerticalEmail() {
        String email = "eric8758@naver.com";
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, "qwer1234")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                        } else {
                        }
                    }
                });
        auth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("성공입니당");
                        }
                    }
                });
    }

    public void addWaitPeople(String email) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int index = 0;
                for(DataSnapshot child : snapshot.getChildren()) {
                    if(child.getKey() != String.valueOf(index)) {
                        break;
                    }
                    index++;
                }
                mDatabase.child("waitToApprove").child(String.valueOf(index)).setValue(email);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}