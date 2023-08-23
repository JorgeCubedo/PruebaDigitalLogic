package com.cube.pruebadigitallogic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cube.pruebadigitallogic.models.Movie;
import com.cube.pruebadigitallogic.models.MovieEntity;
import com.cube.pruebadigitallogic.models.Result;
import com.cube.pruebadigitallogic.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    Context context;
    List<String> posterPath;
    List<Result> results;
    List<MovieEntity> movies;
    boolean savedMovies;
    LayoutInflater layoutInflater;

    public GridAdapter(Context context, List<String> posterPath, List<Result> results, boolean savedMovies) {
        this.context = context;
        this.posterPath = posterPath;
        this.results = results;
        this.savedMovies = savedMovies;
    }

    public GridAdapter(Context context, List<MovieEntity> movies, boolean savedMovies) {
        this.context = context;
        this.movies = movies;
        this.savedMovies = savedMovies;
    }

    @Override
    public int getCount() {
        if (savedMovies) {
            return movies.size();
        } else {
            return posterPath.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (layoutInflater == null)
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item, null);
        }
        ImageView img = convertView.findViewById(R.id.img_poster);
        if (savedMovies) {
            Picasso.get().load(Constants.MOVIE_IMAGE_URL + movies.get(position).getPosterPath()).into(img);
        } else {
            Picasso.get().load(Constants.MOVIE_IMAGE_URL + results.get(position).poster_path).into(img);
        }


        return convertView;
    }
}
