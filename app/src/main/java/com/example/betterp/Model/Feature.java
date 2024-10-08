package com.example.betterp.Model;

import com.google.gson.annotations.SerializedName;

public class Feature {
    @SerializedName("geometry")
    private Geometry geometry;

    public Geometry getGeometry() {
        return geometry; // Make sure this method is public
    }
}