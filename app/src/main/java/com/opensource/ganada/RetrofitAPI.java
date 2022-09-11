package com.opensource.ganada;

import com.google.gson.annotations.SerializedName;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitAPI {

    @Multipart
    @POST("/predict")
    Call<String> request(
            @Part MultipartBody.Part file
    );
}
