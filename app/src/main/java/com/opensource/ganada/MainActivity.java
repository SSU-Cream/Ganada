package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final int RC_SIGN_IN = 10;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private CheckBox id_check;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1035480627652-romkooth1rh9m4ge8u44p89jceokrf2g.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        Button joinMemberButton = (Button) findViewById(R.id.joinMemberButton);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button forget_idpw = (Button) findViewById(R.id.forget_idpw);
        id_check = (CheckBox) findViewById(R.id.id_check);
        SignInButton googleLoginButton = (SignInButton) findViewById(R.id.googleLoginButton);

        SharedPreferences pref = getSharedPreferences("pref", 0);
        String email = pref.getString("email", "");
        String password = pref.getString("password", "");
        boolean cb = pref.getBoolean("cb", false);
        editTextEmail.setText(email);
        editTextPassword.setText(password);
        id_check.setChecked(cb);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id_check.isChecked())
                    saveID();
                else {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();
                }
                loginUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
            }
        });

        joinMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(getApplicationContext(), JoinMemberActivity.class);
                startActivity(intent);
            }
        });

        forget_idpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_email_pwd_dlg(null,null);
            }
        });

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton faceLoginButton = findViewById(R.id.faceLoginButton);
        faceLoginButton.setReadPermissions("email", "public_profile");
        faceLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                finish();
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

    }

    /*
    The code to log in with email and password.
    아이디, 비밀번호를 통해 로그인하는 코드
    */
    public void loginUser(String email, String password) {
        if (email.equals("")) { Toast.makeText(MainActivity.this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show(); return; }
        if (password.equals("")) { Toast.makeText(MainActivity.this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show(); return; }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "가입되지 않은 계정입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*
    the code to check if you're a Google user.
    구글 사용자인지 확인하는 코드
    */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
            }
        }
    }

    /*
    The code that saves Google accounts on the firebase.
    파이어베이스에 구글계정을 저장하는 코드
    */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            startActivity(intent);
                        } else {
                        }
                    }
                });
    }

    /*
    A code that imports a user's token from Facebook, exchanges it with firebase user information, and authenticates it.
    페이스북에서 사용자의 토큰은 가져와 파이어베이스 사용자 정보로 교환하고 인증을 받는 코드
     */
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                        }
                    }
                });
    }

    public void saveID() {
        SharedPreferences pref = getSharedPreferences("pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", editTextEmail.getText().toString());
        editor.putString("password", editTextPassword.getText().toString());
        editor.putBoolean("cb", id_check.isChecked());
        editor.commit();
    }

    /*
    Create a dialog that shows the lost email and password.
    잃버버린 이메일, 비밀번호를 보여주는 다이얼로그 생성
    */
    public void show_email_pwd_dlg(String email, String password) {
        View dlgView = (View)View.inflate(MainActivity.this, R.layout.find_member, null);
        AlertDialog.Builder find_email_dlg = new AlertDialog.Builder(MainActivity.this);
        find_email_dlg.setTitle("이메일/비밀번호 찾기");
        find_email_dlg.setIcon(R.drawable.pic1);
        find_email_dlg.setView(dlgView);
        if(email!=null) {
            final TextView show_email = dlgView.findViewById(R.id.show_email);
            final TextView show_password = dlgView.findViewById(R.id.show_password);
            show_email.setText(email);
            show_password.setText(password);
        }
        Button find_email_pwd_button = dlgView.findViewById(R.id.find_email_pwd_button);
        find_email_pwd_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText dlg_user_name = dlgView.findViewById(R.id.dlg_user_name);
                final DatePicker dlg_dayspin = dlgView.findViewById(R.id.dlg_dayspin);
                String find_name = dlg_user_name.getText().toString();
                String find_birth = String.format("%d-%d-%d", dlg_dayspin.getYear(), dlg_dayspin.getMonth()+1,dlg_dayspin.getDayOfMonth());
                find_email_pwd(find_name, find_birth);
            }
        });
        find_email_dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        find_email_dlg.show();
    }

    /*
    A code that finds an email and password with membership information.
    회원 정보로 이메일과 비밀번호를 찾아주는 코드
     */
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
                        show_email_pwd_dlg(email,password);
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