package com.opensource.ganada;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

public class PracticeActivity extends AppCompatActivity implements View.OnClickListener {

    private View layout_select;
    private View layout_section;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        layout_select = (LinearLayout)findViewById(R.id.selectList);
        layout_section = (LinearLayout)findViewById(R.id.practiceSection);
        videoView = findViewById(R.id.videoView);
    }

    @Override
    public void onClick(View v) {
        layout_select.setVisibility(v.INVISIBLE);
        layout_section.setVisibility(v.VISIBLE);

        // Video Uri
        Uri videoUri = Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");

        // set the uri
        videoView.setVideoURI(videoUri);
        // set the prepared listener

        // controll bar setting
        videoView.setMediaController(new MediaController(this));


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });

        /*

        switch (v.getId()){
            case R.id.randomButton:
                break;
            case R.id.sequentialButton:
                break;
            case R.id.selectAlphaButton:
                break;
        }


        // video pause
        if(videoView != null && videoView.isPlaying())
            videoView.pause();
        // video stop
        if(videoView != null)
            videoView.stopPlayback();

         */
    }
}