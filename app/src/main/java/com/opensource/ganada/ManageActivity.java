package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageActivity extends AppCompatActivity {
    Button button3;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    StudentAdapter adapter;
    ArrayList<StudentItem> studentItems;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();
        recyclerView = (RecyclerView) findViewById(R.id.studentRecyclerView);
        button3 = (Button) findViewById(R.id.button3);
        studentItems = new ArrayList<StudentItem>();


        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new StudentAdapter(getApplicationContext());

        getStudentsDatas(adapter,studentItems);

        //adapter.addItem(new StudentItem("아기1", 5, "한글 개 못함"));
        adapter.addItem(new StudentItem("아기2", 7, "혀가 2CM"));

        recyclerView.setAdapter(adapter);

        /*button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(getApplicationContext(), ModifyMemeberInfo.class);
                startActivity(intent);
            }
        });*/
    }

    public void getStudentsDatas(StudentAdapter adapter, ArrayList<StudentItem> studentItems) {
        mDatabase = FirebaseDatabase.getInstance().getReference("students").child(user.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentItems.clear();
                for(DataSnapshot child : snapshot.getChildren()) {
                    StudentItem studentItem = child.getValue(StudentItem.class);
                    studentItems.add(studentItem);
                    adapter.addItem(studentItem);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}