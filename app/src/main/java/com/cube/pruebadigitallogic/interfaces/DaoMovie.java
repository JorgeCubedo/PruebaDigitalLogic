package com.cube.pruebadigitallogic.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.cube.pruebadigitallogic.models.Movie;
import com.cube.pruebadigitallogic.models.MovieEntity;

import java.util.List;

@Dao
public interface DaoMovie {

    @Query("SELECT * FROM MovieEntity WHERE userId = :userId")
    List<MovieEntity> getMovies(String userId);

    @Query("SELECT * FROM MovieEntity WHERE userId = :userId AND title LIKE '%' || :title || '%'")
    List<MovieEntity> getMoviesByName(String userId, String title);

    @Query("SELECT * FROM MovieEntity WHERE MovieId =:movieId AND userId = :userId")
    MovieEntity getMovie(int movieId, String userId);
    @Insert
    void insertMovie(MovieEntity movie);

    @Delete
    void deleteMovie(MovieEntity movie);
}
