package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
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
    Button joinMemberButton;
    FragmentManager fragmentManager;
    FirstJoinFragment fragment1;
    SecondJoinFragment fragment2;
    boolean isLastFragment = false;
    UserModel userModel = new UserModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_member);
        fragmentManager = getSupportFragmentManager();
        fragment1 = new FirstJoinFragment();
        fragment2 = new SecondJoinFragment();
        FragmentView(1);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ImageView join_back = findViewById(R.id.join_back);
        joinMemberButton = findViewById(R.id.joinMemberButton);

        joinMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isLastFragment) {
                    FirstJoinFragment fragment = (FirstJoinFragment) fragmentManager.findFragmentById(R.id.fragment_container);
                    showNextFragment(fragment.getBirthText(), fragment.getRole());
                } else {
                    SecondJoinFragment fragment = (SecondJoinFragment) fragmentManager.findFragmentById(R.id.fragment_container);
                    if(fragment.getPwd().equals(fragment.getCheckPwd())) {
                        createUser(fragment.getName(),fragment.getEmail(),fragment.getPwd());
                    } else {
                        Toast.makeText(JoinMemberActivity.this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        join_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        /*
        Re-execute the password verification button when changing the password.
        비밀번호 변경시 비밀번호 확인 버튼 재실행
        */
        /*final TextWatcher textWatcher = new TextWatcher() {
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
        passwd_check.addTextChangedListener(textWatcher);*/
    }

    private void FragmentView(int fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (fragment) {
            case 1:
                transaction.replace(R.id.fragment_container, fragment1);
                transaction.commit();
                break;
            case 2:
                transaction.replace(R.id.fragment_container, fragment2);
                transaction.commit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    public void showNextFragment(String birth, int role) {
        if(birth.equals("")) {
            Toast.makeText(getApplicationContext(),"생년월일을 선택해 주세요.", Toast.LENGTH_SHORT).show(); return;
        }
        userModel.setBirth(birth);
        if(role == 0) {
            Toast.makeText(getApplicationContext(),"교사, 학부모 여부를 선택해 주세요.", Toast.LENGTH_SHORT).show(); return;
        } else if(role == 1) {
            userModel.setRole("Teacher");
        } else {
            userModel.setRole("Parent");
        }
        isLastFragment = true;
        joinMemberButton.setText("회원가입");
        FragmentView(2);
    }

    /*
    The code to sign up through email and password.
    이메일, 비밀번호를 통해 회원가입하는 코드
    */
    public void createUser(String name, String email, String password) {
        // Exception to allow all Edittexts to be entered
        if (name.equals("")) { Toast.makeText(JoinMemberActivity.this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show(); return; }
        if (email.equals("")) { Toast.makeText(JoinMemberActivity.this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show(); return; }
        if (password.equals("")) { Toast.makeText(JoinMemberActivity.this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show(); return; }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final String uid = task.getResult().getUser().getUid();     // UID(Unified ID) 생성
                            userModel.setUid(uid); userModel.setName(name);
                            mDatabase.child("users").child(uid).setValue(userModel);    // 데이터베이스에 (UID,이름,생년월일) 저장
                            mDatabase.child("findData").child(name+userModel.getBirth()).setValue(email+"!"+password);
                            Toast.makeText(JoinMemberActivity.this, "회원가입을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(JoinMemberActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        if(userModel.getRole().equals("Teacher")) {
            sendVerticalEmail();
            addWaitPeople(email);
        }
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