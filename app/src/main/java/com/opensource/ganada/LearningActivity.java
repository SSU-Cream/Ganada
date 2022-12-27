package com.opensource.ganada;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import java.util.concurrent.ExecutionException;


<<<<<<< Updated upstream
    Toolbar toolbar;
    private Camera camera;
    private MediaRecorder mediaRecorder;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private Button btn_record_start, btn_record_stop;
    UserModel currentUser;
    Intent intent;
=======
public class LearningActivity extends AppCompatActivity {

    PreviewView previewView;
    String TAG = "LearningActivity";
    ProcessCameraProvider processCameraProvider;
    int lensFacing = CameraSelector.LENS_FACING_FRONT;
    //int lensFacing = CameraSelector.LENS_FACING_BACK;

    Button record_btn;
    boolean isRecording = false;
>>>>>>> Stashed changes

    ArrayList<String> content = new ArrayList(Arrays.asList("ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"));
    ArrayList<String> contentText = new ArrayList(Arrays.asList("기역", "니은", "디귿", "리을", "미음", "비읍", "시옷", "이응", "지읒", "치읓", "키읔", "티읕", "피읖", "히읗"));

    // "ㅂ", "ㅅ", "ㅈ"
    // "비읍", "시옷", "지읒"
    private Integer idx = 0;
    private Integer score = 0;
    private Integer random_idx = 0;
    Random rand = new Random();

    TextView score1, score2, score3;

    private TextView q_content, q_contentText;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        previewView = findViewById(R.id.previewView);
        record_btn = findViewById(R.id.record_btn);

        score1 = findViewById(R.id.score1);
        score2 = findViewById(R.id.score2);
        score3 = findViewById(R.id.score3);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);

        try {
            processCameraProvider = ProcessCameraProvider.getInstance(this).get();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

<<<<<<< Updated upstream
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


=======
        record_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (isRecording) {

                    processCameraProvider.unbindAll();

                    // 파일 위치 찾아서 아무 영상이나 서버로 전송

                    file = new File();
                    fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);

                    retrofitAPI.request(filePart).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                Log.d("retrofit", "POST 성공");
                                String result = response.body();
                                Log.d("retrofit", result);

                                if (result.equals("o")){
                                    score++;
                                    if (idx == 0)
                                        score1.setTextColor(Integer.parseInt("#03fc0b"));
                                    else if (idx == 1)
                                        score2.setTextColor(Integer.parseInt("#03fc0b"));
                                    else
                                        score3.setTextColor(Integer.parseInt("#03fc0b"));
                                } else {
                                    if (idx == 0)
                                        score1.setTextColor(Integer.parseInt("#fc0303"));
                                    else if (idx == 1)
                                        score2.setTextColor(Integer.parseInt("#fc0303"));
                                    else
                                        score3.setTextColor(Integer.parseInt("#fc0303"));
                                }

                                Log.d("result", result);
                                Toast.makeText(LearningActivity.this, "문제 : " + contentText.get(random_idx) + ", 서버에서 받은 내용 : " + result + " => 서버 성공", Toast.LENGTH_LONG).show();

                                if (idx < 2) {
                                    idx++;
                                } else if (idx == 2) {
                                    idx++;

                                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                                    startActivity(intent);
                                }
                                setQuestion();
                                isRecording = false;
                            }
>>>>>>> Stashed changes
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });


                } else {
                    isRecording = true;
                    Log.d("recording", "record btn pressed");

                    if (ActivityCompat.checkSelfPermission(LearningActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        bindPreview();
                    }
                }
            }
        });

        q_content = findViewById(R.id.q_content);
        q_contentText = findViewById(R.id.q_contentText);

        for (String s1 : content) {
            System.out.println("content : " + s1);
        }
        for (String s2 : contentText) {
            System.out.println("contentText : " + s2);
        }


        setQuestion();
    }

    private void setQuestion() {
        random_idx = rand.nextInt(14);

        q_content.setText(content.get(random_idx));
        q_contentText.setText(contentText.get(random_idx));

        // status.setText("진행상태 : " + idx.toString() + "/3 \n맞은 개수 : " + score.toString() + "/3 ");
    }

    @SuppressLint("NewApi")
    void bindPreview() {
        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();
        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3) //디폴트 표준 비율
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        processCameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPause() {
        super.onPause();
        processCameraProvider.unbindAll();
    }
}

