package com.example.betterp.Utils;

import android.content.Context;

import com.example.betterp.Model.LocationEntity;
import com.example.betterp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DataLoader {
    public static void loadData(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        LocationDao locationDao = db.locationDao();

        try {
            InputStream is = context.getResources().openRawResource(R.raw.location_data);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                double lat = jsonObject.getDouble("lat");
                double lon = jsonObject.getDouble("long");

                LocationEntity location = new LocationEntity(name, lat, lon);
                locationDao.insert(location);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}