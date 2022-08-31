package com.opensource.ganada;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.CamcorderProfile;
import android.os.Bundle;
import android.view.Surface;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;



import android.Manifest;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class LearningActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    Toolbar toolbar;
    private Camera camera;
    private MediaRecorder mediaRecorder;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private boolean recording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        toolbar.setBackgroundColor(Color.parseColor("#8DA4D0"));
        toolbarText.setText("연습하기");

        // camera
        TedPermission.with(this)
                .setPermissionListener(permission)
                .setRationaleMessage("Allow permission plz")
                .setDeniedMessage("You denied permission. Go to setting > permission and allow permission")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .check();

        Button btn_record = (Button) findViewById(R.id.btn_record);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording) {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    camera.lock();
                    recording = false;
                } else {
                    runOnUiThread(new Runnable() {
                       @SuppressLint("SdCardPath")
                       @Override
                       public void run() {
                           Toast.makeText(LearningActivity.this, "start record", Toast.LENGTH_SHORT).show();
                           try {
                               mediaRecorder = new MediaRecorder();
                               camera.unlock();
                               mediaRecorder.setCamera(camera);
                               mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                               mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                               mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
                               mediaRecorder.setOrientationHint(90);
                               mediaRecorder.setOutputFile("/sdcard/recordtest.mp4");
                               mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
                               mediaRecorder.prepare();
                               mediaRecorder.start();
                               recording = true;
                           } catch (Exception e) {
                               e.printStackTrace();
                               mediaRecorder.release();
                           }
                       }
                    });
                }
            }
        });
    }

    PermissionListener permission = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(LearningActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();

            camera = Camera.open();
            camera.setDisplayOrientation(90);
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(LearningActivity.this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(LearningActivity.this, "권한 거부", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    private void refreshCamera(Camera camera) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setCamera(camera);
    }

    private void setCamera(Camera cam) {

        camera = cam;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera(camera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}

