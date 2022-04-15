package com.opensource.ganada;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowPost extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post);

        Button delete_post_button = (Button) findViewById(R.id.delete_post_button);
        Button revise_post_button = (Button) findViewById(R.id.revise_post_button);
        Intent intent = getIntent();
        PostItem postItem = (PostItem) intent.getSerializableExtra("item");
        TextView show_post_title = (TextView) findViewById(R.id.show_post_title);
        TextView show_post_writer = (TextView) findViewById(R.id.show_post_writer);
        TextView show_post_date = (TextView) findViewById(R.id.show_post_date);
        TextView show_post_content = (TextView) findViewById(R.id.show_post_content);
        show_post_title.setText(postItem.getTitle());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(!postItem.isAnnoymity())
            show_post_writer.setText(postItem.getWriter());
        else
            show_post_writer.setText("anonymous");
        show_post_date.setText(postItem.getDate());
        show_post_content.setText(postItem.getContent());

        if(!user.getEmail().equals(postItem.getWriter())) {
            delete_post_button.setVisibility(View.INVISIBLE);
            revise_post_button.setVisibility(View.INVISIBLE);
        }

        delete_post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase = FirebaseDatabase.getInstance().getReference("communityData").child("posts").child(postItem.getPost_key());
                mDatabase.removeValue();
                finish();
                Intent intent = new Intent(getApplicationContext(), CommunityActivity.class);
                startActivity(intent);
            }
        });

        revise_post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RevisePost.class);
                intent.putExtra("item",postItem);
                finish();
                startActivity(intent);
            }
        });

    }
}