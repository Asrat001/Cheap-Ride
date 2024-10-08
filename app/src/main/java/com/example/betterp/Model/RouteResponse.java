package com.example.betterp.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RouteResponse {
    @SerializedName("features")
    private List<Feature> features;

    public List<Feature> getFeatures() {
        return features;
    }
}





