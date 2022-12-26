package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommunityActivity extends AppCompatActivity
        implements MenuBottomDialog.BottomSheetListener {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    PostAdapter adapter;
    ImageView addPost;
    Button commonButton;
    Button roleButton;
    ArrayList<PostItem> postItems;
    private Toolbar toolbar;
    UserModel currentUser;
    public static Context context_community;
    Intent intent;
    String postingType = "공통 게시판";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        mAuth = FirebaseAuth.getInstance();
        intent = getIntent();
        currentUser = (UserModel) intent.getSerializableExtra("user");
        context_community = this;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        addPost = (ImageView) findViewById(R.id.addPost);
        commonButton = (Button) findViewById(R.id.common_post_button);
        roleButton = (Button) findViewById(R.id.role_post_button);
        commonButton.setOnClickListener(postType);
        roleButton.setOnClickListener(postType);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        postItems = new ArrayList<PostItem>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        toolbarText.setText(" ");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        if(currentUser.getRole().equals("Teacher")) { roleButton.setText("교사 게시판"); }
        else { roleButton.setText("학부모 게시판"); }

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), new LinearLayoutManager(this).getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.gray_line));
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new PostAdapter(getApplicationContext());

        recyclerView.setAdapter(adapter);
        getPostCommonDatas(adapter,postItems);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PostAdapter.ViewHolder holder, View view, int position) {
                PostItem item = adapter.getItem(position);
                Toast.makeText(getApplicationContext(), "아이템 선택됨 : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(getApplicationContext(), ShowPost.class);
                intent.putExtra("user",currentUser);
                intent.putExtra("item",item);
                intent.putExtra("type",postingType);
                startActivity(intent);
            }
        });

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(CommunityActivity.this, Posting.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
                //mStartForResult.launch(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        intent.putExtra("user",currentUser);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //추가된 소스, ToolBar에 menu.xml을 인플레이트함
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onButtonClicked(String text) {
        if(text.equals("logout")) {
            logOut();
        } else if(text.equals("signout")) {
            show_register_student_dlg();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu) {
            showBottomDlg();
            return true;
        } else {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showBottomDlg() {
        MenuBottomDialog menuBottomDialog = new MenuBottomDialog();
        menuBottomDialog.show(getSupportFragmentManager(), "menuBottomSheet");
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void deleteMember() {
        Toast.makeText(getApplicationContext(),"탈퇴 하였습니다",Toast.LENGTH_SHORT).show();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        mDatabase.removeValue();
        mAuth.getCurrentUser().delete();

        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void delete_all_data() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                mDatabase = FirebaseDatabase.getInstance().getReference("findData").child(user.getName()+user.getBirth());
                mDatabase.removeValue();
                mDatabase = FirebaseDatabase.getInstance().getReference("students").child(mAuth.getUid());
                mDatabase.removeValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void show_register_student_dlg() {
        AlertDialog.Builder deleteMemberDlg = new AlertDialog.Builder(CommunityActivity.this);
        deleteMemberDlg.setTitle("정말 탈퇴 하시겠습니까?");
        deleteMemberDlg.setIcon(R.drawable.pic1);
        deleteMemberDlg.setNegativeButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteMember();
                delete_all_data();
            }
        });
        deleteMemberDlg.setPositiveButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        deleteMemberDlg.show();
    }

    View.OnClickListener postType = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.common_post_button:
                    commonButton.setTextColor(Color.parseColor("#FFFFFF"));
                    roleButton.setTextColor(Color.parseColor("#BBBBBB"));
                    commonButton.setEnabled(false);
                    roleButton.setEnabled(true);
                    commonButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_color));
                    roleButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_color));
                    getPostCommonDatas(adapter,postItems);
                    postingType = "공통 게시판";
                    Toast.makeText(getApplicationContext(), "공통 게시판입니다.", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.role_post_button:
                    commonButton.setTextColor(Color.parseColor("#BBBBBB"));
                    roleButton.setTextColor(Color.parseColor("#FFFFFF"));
                    commonButton.setEnabled(true);
                    roleButton.setEnabled(false);
                    commonButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_color));
                    roleButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_color));
                    String r;
                    if(currentUser.getRole().equals("Teacher")) r = "교사";
                    else r = "학부모";
                    getPostRoleDatas(adapter,postItems);
                    postingType = r + " 게시판";
                    Toast.makeText(getApplicationContext(), r + " 게시판입니다.", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };

    public void getPostCommonDatas(PostAdapter adapter, ArrayList<PostItem> postItems) {
        mDatabase = FirebaseDatabase.getInstance().getReference("communityData").child("posts");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.clearItem();
                postItems.clear();
                for(DataSnapshot child : snapshot.getChildren()) {
                    PostItem postItem = child.getValue(PostItem.class);
                    if(postItem.getRole().equals("Common")) {
                        postItems.add(postItem);
                        adapter.addItem(postItem);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getPostRoleDatas(PostAdapter adapter, ArrayList<PostItem> postItems) {
        mDatabase = FirebaseDatabase.getInstance().getReference("communityData").child("posts");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.clearItem();
                postItems.clear();
                for(DataSnapshot child : snapshot.getChildren()) {
                    PostItem postItem = child.getValue(PostItem.class);
                    if(postItem.getRole().equals(currentUser.getRole())) {
                        postItems.add(postItem);
                        adapter.addItem(postItem);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}