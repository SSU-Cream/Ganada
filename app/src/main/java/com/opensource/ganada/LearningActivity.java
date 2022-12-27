package com.opensource.ganada;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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


public class LearningActivity extends AppCompatActivity {

    PreviewView previewView;
    String TAG = "LearningActivity";
    ProcessCameraProvider processCameraProvider;
    int lensFacing = CameraSelector.LENS_FACING_FRONT;
    //int lensFacing = CameraSelector.LENS_FACING_BACK;

    Button record_btn;
    boolean isRecording = false;

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

        Intent intent = getIntent();
        currentUser = (UserModel) intent.getSerializableExtra("user");

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

        record_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (isRecording) {

                    processCameraProvider.unbindAll();

                    // 파일 위치 찾아서 아무 영상이나 서버로 전송

                    file = new File(String.valueOf(R.raw.test));
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        intent.putExtra("user",currentUser);
        startActivity(intent);
        super.onBackPressed();
    }
}

