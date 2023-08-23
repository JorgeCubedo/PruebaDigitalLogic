package com.cube.pruebadigitallogic.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.cube.pruebadigitallogic.interfaces.DaoMovie;
import com.cube.pruebadigitallogic.models.MovieEntity;

@Database(
        entities = {
                MovieEntity.class
        },
        version = 2
)
public abstract class AppDatabase extends RoomDatabase {
    public static AppDatabase instance = null;

    public abstract DaoMovie movie();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context,
                    AppDatabase.class,
                    "dbMovies.db"
            ).allowMainThreadQueries().build();
        }

        return instance;
    }
}
