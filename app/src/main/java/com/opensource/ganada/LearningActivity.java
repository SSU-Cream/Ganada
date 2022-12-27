package com.opensource.ganada;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.CamcorderProfile;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LearningActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {

    Toolbar toolbar;
    private Camera camera;
    private MediaRecorder mediaRecorder;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private Button btn_record_start, btn_record_stop;

    UserModel currentUser;
    Intent intent;

    ArrayList<String> content = new ArrayList(Arrays.asList("ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"));
    ArrayList<String> contentText = new ArrayList(Arrays.asList("기역", "니은", "디귿", "리을", "미음", "비읍", "시옷", "이응", "지읒", "치읓", "키읔", "티읕", "피읖", "히읗"));

    // "ㅂ", "ㅅ", "ㅈ"
    // "비읍", "시옷", "지읒"
    private Integer idx = 0;
    private Integer score = 0;
    private Integer random_idx = 0;
    Random rand = new Random();

    private TextView q_content, q_contentText, childName, status;
    StudentItem item;
    private FirebaseUser user;
    private DatabaseReference mDatabase;

    File file;
    RequestBody fileBody;
    MultipartBody.Part filePart;

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://27.35.4.93:3389")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build();

    RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        btn_record_start = (Button) findViewById(R.id.btn_record_start);
        btn_record_stop = (Button) findViewById(R.id.btn_record_stop);

        btn_record_start.setOnClickListener(this);
        btn_record_stop.setOnClickListener(this);

        q_content = (TextView) findViewById(R.id.q_content);
        q_contentText = (TextView) findViewById(R.id.q_contentText);

        childName = (TextView) findViewById(R.id.childName);
        status = (TextView) findViewById(R.id.status);

        for (String s1 : content) {
            System.out.println("content : " + s1);
        }
        for (String s2 : contentText) {
            System.out.println("contentText : " + s2);
        }

        /*
        Intent intent = new Intent(getApplicationContext(), LearningActivity.class);
        intent.putExtra("video_file", file);
        startActivity(intent);
         */


        Intent intent = getIntent();
        item = (StudentItem) intent.getSerializableExtra("childName");
        System.out.println(item);
        childName.setText(item.getName());
        System.out.println(item.getName());

        user = FirebaseAuth.getInstance().getCurrentUser();


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

        Log.d("log", "log test");

        setQuestion();
    }

    private void setQuestion() {
        random_idx = rand.nextInt(14);

        q_content.setText(content.get(random_idx));
        q_contentText.setText(contentText.get(random_idx));

        status.setText("진행상태 : " + idx.toString() + "/3 \n맞은 개수 : " + score.toString() + "/3 ");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LearningSelectActivity.class);
        intent.putExtra("user",currentUser);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_record_start) {
            System.out.println("record start");
            Log.d("camera", "record onclick");

            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @SuppressLint("SdCardPath")
                @Override
                public void run() {
                    Toast.makeText(LearningActivity.this, "start record", Toast.LENGTH_SHORT).show();
                    try {
                        mediaRecorder = new MediaRecorder();

//                        camera.unlock();
                        mediaRecorder.setCamera(camera);
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
                        mediaRecorder.setOrientationHint(270);
                        // mediaRecorder.setOutputFile(file);
//                        mediaRecorder.setOutputFile("/sdcard/DCIM/Camera/recordtest.mp4");
//                        mediaRecorder.setOutputFile("./recordtest.mp4");
//                        mediaRecorder.setOutputFile("/data/user/0/");
                        mediaRecorder.setOutputFile("/storage/emulated/0/Download/test.mp4");
                        /*
                        File dir = Environment.getExternalStorageDirectory();;
                        File video = null;
                        try {
                            video = File.createTempFile("tmp", ".ts", dir);
                        } catch(IOException e) {
                            System.err.println("fail to create tmp file");
                            System.err.println(e);


                        }

                        boolean isCreated = video.exists();

                        if (isCreated) {
                            mediaRecorder.setOutputFile(file);
                            Toast.makeText(LearningActivity.this, "record -ing", Toast.LENGTH_LONG).show();
                            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } else {
                            System.err.println("failed to create file");
                        }

                         */

                    } catch (Exception e) {
                        Log.e("LearningActivity.java", "Error in 79" + e.getMessage());
                        e.printStackTrace();
                        mediaRecorder.release();
                    }

                }
            });
        } else if (v == btn_record_stop) {
                System.out.println("record stop");
                //Toast.makeText(LearningActivity.this, "record stop", Toast.LENGTH_SHORT).show();

                mediaRecorder.stop();
                mediaRecorder.release();
                camera.lock();

                /*
                if (idx < 2) {
                    idx++;
                    score++;
                } else if (idx == 2){
                    idx++;

                    //item.setScore(score);
                    //mDatabase = FirebaseDatabase.getInstance().getReference("students").child(user.getUid());
                    //mDatabase.child(Integer.toString(item.getStudentNum())).setValue(item);

                    Toast.makeText(LearningActivity.this, item.getName() + "은(는) 3개 중 " + score.toString() + "개 맞았습니다.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    startActivity(intent);
                }
                setQuestion();


                 */

                file = new File("/sdcard/DCIM/Camera/recordtest.mp4");
                fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);

                retrofitAPI.request(filePart).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            Log.d("retrofit", "POST 성공");
                            String result = response.body();
                            Log.d("retrofit", result);

                            if (result.equals("o"))
                                score++;
                            /*
                            if (result.equals(contentText.get(random_idx)))
                                score++;
                            */

                            Log.d("result", result);
                            Toast.makeText(LearningActivity.this, "문제 : " + contentText.get(random_idx) + ", 서버에서 받은 내용 : " + result + " => 서버 성공", Toast.LENGTH_LONG).show();

                            if (idx < 2) {
                                idx++;
                            } else if (idx == 2) {
                                idx++;

                                mDatabase = FirebaseDatabase.getInstance().getReference("students").child(user.getUid());
                                mDatabase.child(Integer.toString(item.getStudentNum())).setValue(item);

                                Toast.makeText(LearningActivity.this, item.getName() + "은(는) 3개 중 " + score.toString() + "개 맞았습니다.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                                startActivity(intent);
                            }
                            setQuestion();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            }
        }

    PermissionListener permission = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(LearningActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();

            camera = Camera.open(1);
            camera.setDisplayOrientation(90);
            surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
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

