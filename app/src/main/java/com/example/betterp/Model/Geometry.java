package com.example.betterp.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Geometry {
    @SerializedName("coordinates")
    private List<List<Double>> coordinates;

    public List<List<Double>> getCoordinates() {
        return coordinates; // Ensure this method is public as well
    }
}
