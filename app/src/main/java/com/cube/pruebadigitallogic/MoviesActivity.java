package com.cube.pruebadigitallogic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.cube.pruebadigitallogic.database.AppDatabase;
import com.cube.pruebadigitallogic.databinding.ActivityMoviesBinding;
import com.cube.pruebadigitallogic.interfaces.DaoMovie;
import com.cube.pruebadigitallogic.interfaces.RetrofitEndpoints;
import com.cube.pruebadigitallogic.models.Movie;
import com.cube.pruebadigitallogic.models.MovieEntity;
import com.cube.pruebadigitallogic.utils.Constants;
import com.cube.pruebadigitallogic.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesActivity extends AppCompatActivity {
    private static final String TAG = MoviesActivity.class.getSimpleName();

    ActivityMoviesBinding binding;
    FirebaseAuth auth;
    String userId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMoviesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        if (user != null)
            userId = user.getUid();

        Utils.saveUserId(getApplicationContext(), userId);

        checkSavedMovies(userId);

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.item_saved_movies) {
                //Saved Movies
                Intent intent = new Intent(MoviesActivity.this, SavedMoviesActivity.class);
                if (userId != null)
                    intent.putExtra(Constants.USER_ID, userId);
                startActivity(intent);
            }
            return false;
        });
        getMovies();
    }

    private void getMovies() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.MOVIE_API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitEndpoints endpoints = retrofit.create(RetrofitEndpoints.class);

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyNThlYzExYmY2OTIwMmVhNGJhNTQ3ZDRmZThmYTFiYyIsInN1YiI6IjY0Nzc3M2JiZTMyM2YzMDEwNjEzNWJlNyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.VFmHT736Y4T9bvmJcidiEcKdPIP-Wm1lncjRSoKcJJM";
//        Map<String, String> tokenMap = Utils.prepareTokenHeaders(token);
        Call<Movie> call = endpoints.getMovies("Bearer " +token, "application/json");

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.errorBody() != null) {
                    try {
                        Log.e(TAG, response.errorBody().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    Movie movie = response.body();

                    Log.d(TAG, String.valueOf("Total Result: " + movie.total_results));
                    List<String> paths = new ArrayList<>();
                    for (int i = 0; i < movie.results.size(); i++) {

                        paths.add(movie.results.get(i).poster_path);
                    }
                    GridAdapter adapter = new GridAdapter(getApplicationContext(), paths, movie.results, false);
                    binding.grid.setAdapter(adapter);

                    binding.grid.setOnItemClickListener((parent, view, position, id) -> {
                        int idMovie = movie.results.get(position).id;
                        Log.d(TAG, "IdMovie: " + idMovie);
                        Log.d(TAG, "Id: " + id);
                        Intent intent = new Intent(MoviesActivity.this, MovieDetailActivity.class);
                        intent.putExtra(Constants.MOVIE_ID, idMovie);
                        startActivity(intent);
                    });
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.e(TAG, "" + t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        auth.signOut();
        Log.e(TAG, "SignOut");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        checkSavedMovies(Utils.getUserId(getApplicationContext()));
    }

    private void checkSavedMovies(String userId) {
        AppDatabase db = AppDatabase.getInstance(this);
        DaoMovie daoMovie = db.movie();
        List<MovieEntity> movieEntities = daoMovie.getMovies(userId);
        Menu menu = binding.toolbar.getMenu();
        MenuItem menuItem = menu.findItem(R.id.item_saved_movies);
        menuItem.setEnabled(movieEntities.size() > 0);
    }
}