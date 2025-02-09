package com.example.betterp.Utils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.betterp.Model.LocationEntity;

@Database(entities = {LocationEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract LocationDao locationDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "location_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
