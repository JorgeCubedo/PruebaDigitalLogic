package com.cube.pruebadigitallogic;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.cube.pruebadigitallogic.database.AppDatabase;
import com.cube.pruebadigitallogic.databinding.ActivitySavedMoviesBinding;
import com.cube.pruebadigitallogic.interfaces.DaoMovie;
import com.cube.pruebadigitallogic.models.MovieEntity;
import com.cube.pruebadigitallogic.utils.Constants;
import com.cube.pruebadigitallogic.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class SavedMoviesActivity extends AppCompatActivity {
    private static final String TAG = SavedMoviesActivity.class.getSimpleName();
    ActivitySavedMoviesBinding binding;
    List<MovieEntity> movies;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedMoviesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = Utils.getUserId(getApplicationContext());

        binding.grid.setOnItemClickListener((parent, view, position, id) -> {
            int idMovie = movies.get(position).getMovieId();

            Intent intent = new Intent(SavedMoviesActivity.this, MovieDetailActivity.class);
            intent.putExtra(Constants.MOVIE_ID, idMovie);
            intent.putExtra(Constants.IS_SAVED, true);
            startActivity(intent);
        });

        showSavedMovies();

        configSearch();
    }

    private void configSearch() {
        Menu menu = binding.toolbar.getMenu();
        MenuItem searchItem = menu.findItem(R.id.item_search);

        SearchManager sm = (SearchManager) SavedMoviesActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }

        if (searchView != null){
            searchView.setSearchableInfo(sm.getSearchableInfo(SavedMoviesActivity.this.getComponentName()));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.d(TAG, "Query: " + query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.d(TAG, "newText: " + newText);
                    getMoviesByName(newText);
                    return true;
                }
            });
        }
    }

    private void showSavedMovies() {

        try {
            movies = Utils.getMoviesDb(userId, getApplicationContext());

            GridAdapter adapter = new GridAdapter(this, movies, true);
            binding.grid.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMoviesByName(String title) {
        try {
            if (title.isEmpty())
                movies = Utils.getMoviesDb(userId, getApplicationContext());
            else
                movies = Utils.getMoviesByName(userId, title, getApplicationContext());
            GridAdapter adapter = new GridAdapter(this, movies, true);
            binding.grid.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
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