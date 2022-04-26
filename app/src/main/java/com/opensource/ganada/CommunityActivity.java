package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommunityActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    PostAdapter adapter;
    Button addPost;
    ArrayList<PostItem> postItems;
    public static Context context_community;
    public int var;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        context_community = this;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        addPost = (Button) findViewById(R.id.addPost);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        postItems = new ArrayList<PostItem>();

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PostAdapter(getApplicationContext());

        getPostDatas(adapter,postItems);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PostAdapter.ViewHolder holder, View view, int position) {
                PostItem item = adapter.getItem(position);
                Toast.makeText(getApplicationContext(), "아이템 선택됨 : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(getApplicationContext(), ShowPost.class);
                intent.putExtra("item",item);
                startActivity(intent);
            }
        });

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Posting.class);
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK) {
            Intent intent = getIntent();
            PostItem postItem = (PostItem) data.getSerializableExtra("item");
            adapter.addItem(postItem);
            adapter.notifyDataSetChanged();
        }
    }

    public void getPostDatas(PostAdapter adapter, ArrayList<PostItem> postItems) {
        mDatabase = FirebaseDatabase.getInstance().getReference("communityData").child("posts");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postItems.clear();
                for(DataSnapshot child : snapshot.getChildren()) {
                    PostItem postItem = child.getValue(PostItem.class);
                    postItems.add(postItem);
                    adapter.addItem(postItem);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}