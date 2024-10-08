package com.example.betterp.Utils;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.betterp.Model.LocationEntity;

import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    void insert(LocationEntity location);

    @Query("SELECT * FROM locations WHERE name LIKE :query LIMIT 10")
    LiveData<List<LocationEntity>> searchByName(String query);

    @Query("SELECT * FROM locations")
    LiveData<List<LocationEntity>> getAllLocations();
}