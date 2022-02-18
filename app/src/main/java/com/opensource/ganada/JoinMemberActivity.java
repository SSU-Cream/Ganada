package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class JoinMemberActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText sign_name;
    private EditText sign_birth;
    private EditText sign_birth2;
    private EditText sign_birth3;
    private EditText passwd_check;
    private boolean is_checked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_member);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        sign_name = (EditText) findViewById(R.id.sign_name);
        sign_birth = (EditText) findViewById(R.id.sign_birth);
        sign_birth2 = (EditText) findViewById(R.id.sign_birth2);
        sign_birth3 = (EditText) findViewById(R.id.sign_birth3);
        passwd_check = (EditText) findViewById(R.id.passwd_check);
        Button joinMemberButton = (Button) findViewById(R.id.joinMemberButton);
        Button passwd_check_button = (Button) findViewById(R.id.passwd_check_button);

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

        joinMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
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

    // The code to sign up through email and password.
    public void createUser(String email, String password) {
        String name = sign_name.getText().toString();
        if (name.equals("")) { Toast.makeText(JoinMemberActivity.this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show(); return; }
        if (email.equals("")) { Toast.makeText(JoinMemberActivity.this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show(); return; }
        if (sign_birth.getText().toString().equals("") || sign_birth2.getText().toString().equals("") || sign_birth3.getText().toString().equals("")) {
            Toast.makeText(JoinMemberActivity.this, "생년월일을 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return;}
        if (is_checked == false) { Toast.makeText(JoinMemberActivity.this, "비밀번호 확인을 실행해주세요", Toast.LENGTH_SHORT).show(); return; }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(JoinMemberActivity.this, "회원가입을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(JoinMemberActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}