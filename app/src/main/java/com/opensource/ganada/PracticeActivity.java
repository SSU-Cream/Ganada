package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class PracticeActivity extends AppCompatActivity implements View.OnClickListener {
    private VideoView videoView;
    private ImageView ex1_img, ex2_img;
    private TextView ex1_text, ex2_text, content, contentText;
    private Button btn_previous, btn_next;
    Toolbar toolbar;

    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private ArrayList<LearningContents> arrayList = new ArrayList<>();

    private Integer idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        toolbar.setBackgroundColor(Color.parseColor("#F8CACC"));
        toolbarText.setText("학습하기");

        btn_previous = findViewById(R.id.previous);
        btn_next = findViewById(R.id.next);

        btn_previous.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        ex1_img = (ImageView)findViewById(R.id.ex1_img);
        ex2_img = (ImageView)findViewById(R.id.ex2_img);
        ex1_text = (TextView)findViewById(R.id.ex1_text);
        ex2_text = (TextView)findViewById(R.id.ex2_text);
        content = (TextView)findViewById(R.id.content);
        contentText = (TextView)findViewById(R.id.contentText);

        videoView = findViewById(R.id.videoView);

        arrayList = new ArrayList<>(); // learning contents 객체를 담을 어레이 리스트

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("learningData"); // DB 테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); // 기존 배열리스트가 존재하지않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터 List를 추출해냄
                    LearningContents learningContents = snapshot.getValue(LearningContents.class); // 만들어뒀던 learning contents 객체에 데이터를 담는다.

                    arrayList.add(learningContents); // 담은 데이터들을 배열리스트에 넣기
                }

                for (LearningContents content : arrayList) {
                    System.out.println("contents : " + content.getContents());
                }

                setContents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("database", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        /*

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference pathReference2 = storageReference.child("video");

        if (pathReference2 == null) {
            Toast.makeText(PracticeActivity.this, "저장소에 영상이 없습니다." ,Toast.LENGTH_SHORT).show();
        } else {
            StorageReference submitProfile2 = storageReference.child("video/test.mp4");
            submitProfile2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //Glide.with(PracticeActivity.this).load(uri).into(load);
                    videoView.setVideoURI(uri);
                    System.out.println("success");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Video Uri
                    Uri videoUri = Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
                    videoView.setVideoURI(videoUri);
                    System.out.println("fail");
                }
            });
        }

         */

        // set the prepared listener

        // controll bar setting
        videoView.setMediaController(new MediaController(this));


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });

        // video pause
        if(videoView != null && videoView.isPlaying())
            videoView.pause();

        // video stop
        if(videoView != null)
            videoView.stopPlayback();


        /*
        //load = (ImageView)findViewById(R.id.loadimg);

        StorageReference pathReference = storageReference.child("photo");

        if (pathReference == null) {
            Toast.makeText(PracticeActivity.this, "저장소에 사진이 없습니다." ,Toast.LENGTH_SHORT).show();
        } else {
            StorageReference submitProfile = storageReference.child("photo/learning_temp_img1.png");
            submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(PracticeActivity.this).load(uri).into(load);
                    System.out.println("success");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("fail");
                }
            });
        }

         */
    }

    private void setContents() {
        LearningContents singleContent = arrayList.get(idx);

        Uri videoUrl = Uri.parse(singleContent.getVideoUrl());
        videoView.setVideoURI(videoUrl);

        Uri ex1_imgUrl = Uri.parse(singleContent.getEx1_imgUrl());
        Glide.with(PracticeActivity.this).load(ex1_imgUrl).into(ex1_img);

        Uri ex2_imgUrl = Uri.parse(singleContent.getEx2_imgUrl());
        Glide.with(PracticeActivity.this).load(ex2_imgUrl).into(ex2_img);

        ex1_text.setText(singleContent.getEx1_text());
        ex2_text.setText(singleContent.getEx2_text());

        content.setText(singleContent.getContents());
        contentText.setText(singleContent.getContentsText());
    }

    @Override
    public void onClick(View v) {
        if (v == btn_previous) {
            System.out.println("previous");
            if (idx != 0)
                idx--;
        } else if (v == btn_next) {
            System.out.println("next");
            if (idx != 3)
                idx++;
        }
        setContents();
    }
}