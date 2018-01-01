package com.example.root.firebasetest.data.remote;

import com.example.root.firebasetest.data.model.Post;
import com.example.root.firebasetest.data.model.SendData;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by root on 29/12/17.
 */

public interface APIService {
    @Headers({"Content-Type: application/json", "Cache-Control: max-age=64000"})
    @POST("/api/note/")
    Call<Post> savePost(@Body SendData data);


}