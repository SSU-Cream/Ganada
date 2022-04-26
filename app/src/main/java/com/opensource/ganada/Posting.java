package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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

    private DatabaseReference mDatabase;
    private EditText postTitle;
    private EditText postContent;
    private CheckBox annoymity_checkBox;
    private Button posting_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        postTitle = (EditText) findViewById(R.id.show_post_title);
        postContent = (EditText) findViewById(R.id.postContent);
        annoymity_checkBox = (CheckBox) findViewById(R.id.annoymity_checkBox);
        posting_button = (Button) findViewById(R.id.posting_button);

        posting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = postTitle.getText().toString();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String user_email = user.getEmail();
                String content = postContent.getText().toString();
                String date = getDate();
                boolean is_annoymity = annoymity_checkBox.isChecked();
                PostItem postItem = new PostItem("1", title, user_email, content, date, "0", is_annoymity);
                savePostData(postItem);
            }
        });
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
                    if(!child.getKey().toString().equals(Integer.toString(idx))) {
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
        Intent intent = new Intent();
        intent.putExtra("item",postItem);
        setResult(RESULT_OK, intent);
        super.finish();
    }

}