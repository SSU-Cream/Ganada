package com.opensource.ganada;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowPost extends AppCompatActivity {

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
        if(!postItem.isAnnoymity())
            show_post_writer.setText(postItem.getWriter());
        else
            show_post_writer.setText("anonymous");
        show_post_date.setText(postItem.getDate());
        show_post_content.setText(postItem.getContent());

        delete_post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        revise_post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }
}