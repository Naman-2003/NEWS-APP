package com.example.newsapp;

import com.example.newsapp.Model.Headlines;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    // Define your base URL
    String BASE_URL = "https://newsapi.org/v2/";

    @GET("top-headlines")
    Call<Headlines> getHeadlines(
            @Query("country") String country,
            @Query("apiKey") String apiKey
    );

    @GET("everything")
    Call<Headlines> getSpecificData(
            @Query("q") String query,
            @Query("apiKey") String apiKey
    );

    // If you need to make a request to a specific URL, use the @Url annotation
    @GET
    Call<Headlines> getDataFromUrl(@Url String url);
}
