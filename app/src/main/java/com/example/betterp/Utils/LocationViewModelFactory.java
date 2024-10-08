package com.example.betterp.Utils;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.betterp.ViewModel.LocationViewModel;

public class LocationViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final LocationDao locationDao;

    public LocationViewModelFactory(Application application, LocationDao locationDao) {
        this.application = application;
        this.locationDao = locationDao;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LocationViewModel.class)) {
            return (T) new LocationViewModel(locationDao, application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}