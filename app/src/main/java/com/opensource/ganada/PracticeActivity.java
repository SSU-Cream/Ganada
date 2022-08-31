package com.opensource.ganada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class PracticeActivity extends AppCompatActivity implements View.OnClickListener {
    private VideoView videoView;
    private ImageView load;
    private Button btn_previous, btn_next;
    Toolbar toolbar;

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

        videoView = findViewById(R.id.videoView);

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


        load = (ImageView)findViewById(R.id.loadimg);

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
    }

    @Override
    public void onClick(View v) {
        if (v == btn_previous) {
            System.out.println("previous");
            Toast.makeText(PracticeActivity.this, "previous" ,Toast.LENGTH_SHORT).show();
        } else if (v == btn_next) {
            System.out.println("next");
            Toast.makeText(PracticeActivity.this, "next" ,Toast.LENGTH_SHORT).show();
        }
    }
}