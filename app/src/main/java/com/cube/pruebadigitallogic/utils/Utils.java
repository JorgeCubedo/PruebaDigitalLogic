package com.cube.pruebadigitallogic.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Dao;

import com.cube.pruebadigitallogic.database.AppDatabase;
import com.cube.pruebadigitallogic.interfaces.DaoMovie;
import com.cube.pruebadigitallogic.models.MovieEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static Map<String,String> prepareTokenHeaders(String token){

        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization","Bearer " + token);

        return headers;
    }

    public static void saveUserId(Context context, String userId) {
        SharedPreferences preferences = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(Constants.USER_ID, userId);

        editor.apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);

        return preferences.getString(Constants.USER_ID, "");
    }

    public static List<MovieEntity> getMoviesDb(String userId, Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        DaoMovie daoMovie = db.movie();

        return daoMovie.getMovies(userId);
    }

    public static List<MovieEntity> getMoviesByName(String userId, String title, Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        DaoMovie daoMovie = db.movie();

        return daoMovie.getMoviesByName(userId, title);
    }

    public static MovieEntity getMovie(int movieId, String userId, Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        DaoMovie daoMovie = db.movie();

        return daoMovie.getMovie(movieId, userId);
    }
}
