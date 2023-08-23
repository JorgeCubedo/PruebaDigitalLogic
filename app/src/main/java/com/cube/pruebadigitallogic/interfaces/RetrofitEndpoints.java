package com.cube.pruebadigitallogic.interfaces;

import com.cube.pruebadigitallogic.models.Movie;
import com.cube.pruebadigitallogic.models.MovieDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitEndpoints {
    @GET("discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc")
    Call<Movie> getMovies(@Header("Authorization") String token, @Header("accept") String value);

    @GET("movie/{movieId}?language=en-US'")
    Call<MovieDetail> getMovieById(@Path("movieId") int movieId, @Header("Authorization") String token, @Header("accept") String value);
}
