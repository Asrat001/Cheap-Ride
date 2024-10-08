package com.example.betterp.ViewModel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.betterp.Model.LocationEntity;
import com.example.betterp.Utils.AppDatabase;
import com.example.betterp.Utils.DataLoader;
import com.example.betterp.Utils.LocationDao;

import java.util.List;

public class LocationViewModel extends ViewModel {
    private LocationDao locationDao;
    private LiveData<List<LocationEntity>> allLocations;
    private Application application;
    private LiveData<List<LocationEntity>> searchResults;

    // Constructor takes a LocationDao
    public LocationViewModel(LocationDao locationDao ,Application application) {
        this.locationDao = locationDao;
        this.application=application;
        allLocations = locationDao.getAllLocations(); // Assuming this returns LiveData

    }
    public LiveData<List<LocationEntity>> getAllLocations() {
        return allLocations;
    }

    public LiveData<List<LocationEntity>> searchLocations(String query) {
        if (searchResults == null) {
            searchResults = locationDao.searchByName(query);
        } else {
            searchResults = locationDao.searchByName(query);
        }
        return searchResults;
    }

    public void loadData() {
        // Load data in a background thread if needed
        new Thread(() -> {
            if (locationDao.getAllLocations().getValue()==null) {
                DataLoader.loadData(application);
            }
        }).start();
    }
}