package com.example.android.moviesapplication.rest;

import com.example.android.moviesapplication.model.MovieResponse;
import com.example.android.moviesapplication.model.ReviewResponse;
import com.example.android.moviesapplication.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("movie/{preference}")
    Call<MovieResponse> getMovies(@Path("preference") String preference,
                                  @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<TrailerResponse> getTrailers(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewResponse> getReviews(@Path("id") int id, @Query("api_key") String apiKey);

//    @GET("movie/{id}")
//    Call<MoviesResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);
}
