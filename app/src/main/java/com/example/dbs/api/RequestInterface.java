package com.example.dbs.api;

import com.example.dbs.response.ListArticle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestInterface {
    @GET("article")
    Call<List<ListArticle>> getPostJson();
}
