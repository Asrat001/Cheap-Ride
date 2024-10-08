package com.example.betterp.Utils;
import com.example.betterp.Model.RouteResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface ORSApiService {
    @GET("v2/directions/driving-car")
    Call<RouteResponse> getRoute(
            @Query("api_key") String apiKey,
            @Query("start") String start,
            @Query("end") String end
    );
}
