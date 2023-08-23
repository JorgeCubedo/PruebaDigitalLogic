package com.cube.pruebadigitallogic;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cube.pruebadigitallogic.database.AppDatabase;
import com.cube.pruebadigitallogic.databinding.ActivityMovieDetailBinding;
import com.cube.pruebadigitallogic.interfaces.DaoMovie;
import com.cube.pruebadigitallogic.interfaces.RetrofitEndpoints;
import com.cube.pruebadigitallogic.models.Movie;
import com.cube.pruebadigitallogic.models.MovieDetail;
import com.cube.pruebadigitallogic.models.MovieEntity;
import com.cube.pruebadigitallogic.utils.Constants;
import com.cube.pruebadigitallogic.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    ActivityMovieDetailBinding binding;
    MovieEntity movie = new MovieEntity();

    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavView.setEnabled(false);

        userId = Utils.getUserId(getApplicationContext());
        Bundle bundle = getIntent().getExtras();

        int movieId = bundle.getInt(Constants.MOVIE_ID, 0);

        boolean isSaved = isSaved(movieId);
        configMenu(isSaved);

        if (movieId != 0) {
            if (isSaved) {
                getMovieSavedDetail(movieId);
            } else {
                getMovieDetail(movieId);
            }
        } else {
            Toast.makeText(this, "Error showing the movie info.", Toast.LENGTH_SHORT).show();
            finish();
        }



        binding.bottomNavView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_save_movie:
                    saveMovie(movie);
                    break;
                case R.id.item_delete_movie:
                    deleteMovie(movie);
                    break;
                default:
                    break;
            }

            return false;
        });
    }

    private void getMovieDetail(int movieId) {
        AlertDialog dialog = dialogLoad();
        dialog.show();
//        getMovieSavedDetail(movieId);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.MOVIE_API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        RetrofitEndpoints endpoints = retrofit.create(RetrofitEndpoints.class);

        Call<MovieDetail> call = endpoints.getMovieById(movieId, "Bearer " + Constants.TOKEN, Constants.APP_JSON);
        call.enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                Log.d(TAG, response.toString());
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetail movieDetail = response.body();

                    Log.d(TAG, movieDetail.title);

                    binding.toolbar.setTitle(movieDetail.title);
                    binding.txtMovieDescription.setText(movieDetail.overview);
                    binding.txtMovieScore.setText(String.valueOf(setDecFormat(movieDetail.vote_average)));
                    Picasso.get().load(Constants.MOVIE_IMAGE_URL + movieDetail.poster_path).into(binding.imgPoster);

                    movie.setMovieId(movieDetail.id);
                    movie.setUserId(userId);
                    movie.setTitle(movieDetail.title);
                    movie.setPosterPath(movieDetail.poster_path);
                    movie.setDescription(movieDetail.overview);
                    movie.setScore(movieDetail.vote_average);

                    binding.bottomNavView.setEnabled(true);
                    dialog.dismiss();

                } else {
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, response.errorBody().string());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(MovieDetailActivity.this, "Error showing movie info.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                Log.e(TAG, "" +t.getMessage());
                t.printStackTrace();
                Toast.makeText(MovieDetailActivity.this, "Error showing movie info.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            }
        });
    }

    private void saveMovie(MovieEntity movie) {
        AppDatabase db = AppDatabase.getInstance(this);
        DaoMovie daoMovie = db.movie();

        try {
            daoMovie.insertMovie(movie);
            Log.d(TAG, "Movie Saved");
            configMenu(true);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deleteMovie(MovieEntity movie) {
        AppDatabase db = AppDatabase.getInstance(this);
        DaoMovie daoMovie = db.movie();

        try {
            daoMovie.deleteMovie(movie);
            Log.d(TAG, "Movie Deleted");
            configMenu(false);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMovieSavedDetail(int movieId) {
        AlertDialog dialog = dialogLoad();
        dialog.show();


        try {
            MovieEntity movie = Utils.getMovie(movieId, userId, getApplicationContext());

            binding.txtMovieScore.setText(String.valueOf(setDecFormat(movie.getScore())));
            binding.toolbar.setTitle(movie.getTitle());
            binding.txtMovieDescription.setText(movie.getDescription());

            Picasso.get().load(Constants.MOVIE_IMAGE_URL + movie.getPosterPath()).into(binding.imgPoster);
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MovieDetailActivity.this, "Error showing movie info.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            finish();
        }
    }

    private boolean isSaved(int movieId) {
        try {
            MovieEntity movieEntity = Utils.getMovie(movieId, userId, getApplicationContext());

            return movieEntity.getTitle() != null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private AlertDialog dialogLoad() {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_load, null);

        builder.setView(view);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    private double setDecFormat(double number) {
        DecimalFormat df =  new DecimalFormat("#.0");

        return Double.parseDouble(df.format(number));
    }

    private void configMenu(boolean isSaved) {
        Menu menu = binding.bottomNavView.getMenu();
        MenuItem saveItem = menu.findItem(R.id.item_save_movie);
        MenuItem deleteItem = menu.findItem(R.id.item_delete_movie);

        if (isSaved) {
            deleteItem.setVisible(true);
            saveItem.setVisible(false);
        } else {
            deleteItem.setVisible(false);
            saveItem.setVisible(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        Log.e(TAG, "SignedOut");
    }
}