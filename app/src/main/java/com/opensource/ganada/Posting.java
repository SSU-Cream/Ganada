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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
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

import java.util.Date;
import java.text.SimpleDateFormat;

public class Posting extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText postTitle;
    private EditText postContent;
    private CheckBox annoymity_checkBox;
    private CheckBox only_role_checkBox;
    private Button posting_button;
    private Toolbar toolbar;
    UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        Intent intent = getIntent();
        currentUser = (UserModel) intent.getSerializableExtra("user");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        postTitle = findViewById(R.id.show_post_title);
        postContent = findViewById(R.id.postContent);
        annoymity_checkBox = findViewById(R.id.annoymity_checkBox);
        only_role_checkBox = findViewById(R.id.only_role_checkBox);
        posting_button = findViewById(R.id.posting_button);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = findViewById(R.id.toolbar_title);
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        toolbarText.setText(" ");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        posting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = postTitle.getText().toString();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String user_email = user.getEmail();
                String content = postContent.getText().toString();
                String date = getDate();
                boolean is_annoymity = annoymity_checkBox.isChecked();
                String role;
                if(only_role_checkBox.isChecked()) {
                    role = currentUser.getRole();
                } else {
                    role = "Common";
                }
                PostItem postItem = new PostItem("1", title, user_email, content, date, "0", role, is_annoymity);
                savePostData(postItem);
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), CommunityActivity.class);
        intent.putExtra("user",currentUser);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(false) {
        } else {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private String getDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = dateFormat.format(date);
        return getTime;
    }

    public void savePostData(PostItem postItem) {
        if(postItem.getTitle().equals("")) { Toast.makeText(this, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show(); return;}
        if(postItem.getContent().equals("")) { Toast.makeText(this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show(); return;}

        mDatabase = FirebaseDatabase.getInstance().getReference("communityData").child("posts");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int idx = 1;
                for(DataSnapshot child : snapshot.getChildren()) {
                    if(!child.getKey().equals(Integer.toString(idx))) {
                        break;
                    }
                    idx++;
                }
                postItem.setPost_key(Integer.toString(idx));
                mDatabase.child(postItem.getPost_key()).setValue(postItem);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}