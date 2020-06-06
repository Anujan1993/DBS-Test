package com.example.dbs.api;

import com.example.dbs.response.FullArticle;
import com.example.dbs.response.ListArticle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RequestInterface {
    @GET("article")
    Call<List<ListArticle>> getPostJson();

    @GET("article/{id}")
    Call<FullArticle> groupList(@Path("id") String groupId);

}
