package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
    Toolbar toolbar;
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        toolbar.setBackgroundColor(Color.parseColor("#8DA4D0"));
        toolbarText.setText("커뮤니티");

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), new LinearLayoutManager(this).getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.blue_line));
        recyclerView.addItemDecoration(dividerItemDecoration);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //추가된 소스, ToolBar에 menu.xml을 인플레이트함
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //추가된 소스, ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
        switch (item.getItemId()) {
            case R.id.all_menu:
                Toast.makeText(getApplicationContext(), "기능 더보기", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);

            default:
                Toast.makeText(getApplicationContext(), "뒤로가기 버튼 클릭됨", Toast.LENGTH_SHORT).show();
                return true;
        }
    }

    public void getPostDatas(final PostAdapter adapter, final ArrayList<PostItem> postItems) {
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