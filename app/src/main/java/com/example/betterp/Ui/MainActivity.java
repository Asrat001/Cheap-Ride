package com.example.betterp.Ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.betterp.Adapters.LocationAutoCompleteAdapter;
import com.example.betterp.Adapters.PriceItemAdaptor;
import com.example.betterp.Model.LocationEntity;
import com.example.betterp.Model.PriceItem;
import com.example.betterp.Model.RouteResponse;
import com.example.betterp.R;
import com.example.betterp.Utils.AppDatabase;
import com.example.betterp.Utils.DataLoader;
import com.example.betterp.Utils.DialogUtil;
import com.example.betterp.Utils.LocationDao;
import com.example.betterp.Utils.LocationViewModelFactory;
import com.example.betterp.Utils.ORSApiService;
import com.example.betterp.ViewModel.LocationViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MapListener   {
private MapView mapView;
private IMapController mapController;
private FusedLocationProviderClient fusedLocationClient;
private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
private ExecutorService executorService;
private AutoCompleteTextView autoCompleteTextView;
private LocationAutoCompleteAdapter locationAutoCompleteAdapter;
private LocationViewModel locationViewModel;
private GeoPoint  userCurrentLocation;
private ORSApiService orsApiService;
private MaterialDialog rideTypeDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openrouteservice.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        orsApiService = retrofit.create(ORSApiService.class);
        EdgeToEdge.enable(this);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getPackageName());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        AppDatabase db = AppDatabase.getInstance(this); // Assuming your AppDatabase instance is here
        LocationDao locationDao = db.locationDao();
        setContentView(R.layout.activity_main);
        LocationViewModelFactory factory = new LocationViewModelFactory(getApplication(), locationDao);
        autoCompleteTextView=findViewById(R.id.autoCompleteTextView);
        locationViewModel = new ViewModelProvider(this,factory).get(LocationViewModel.class);
        locationViewModel.loadData();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        locationAutoCompleteAdapter = new LocationAutoCompleteAdapter(this);
        autoCompleteTextView.setAdapter(locationAutoCompleteAdapter);
        autoCompleteTextView.setThreshold(3);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 3) {
                    locationViewModel.searchLocations("%" + s.toString() + "%").observe(MainActivity.this, locations -> {
                            locationAutoCompleteAdapter.setData(locations);
                            locationAutoCompleteAdapter.notifyDataSetChanged();

                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.orange));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        mapView=findViewById(R.id.map);
        MyLocationNewOverlay myLocationoverlay = new MyLocationNewOverlay(mapView);
        myLocationoverlay.enableFollowLocation();
        myLocationoverlay.enableMyLocation();
        mapView.setTileSource(new OnlineTileSourceBase("", 1, 20, 1080, ".png",
                new String[] { "https://a.tile.openstreetmap.org/" }) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl()
                        + MapTileIndex.getZoom(pMapTileIndex)
                        + "/" + MapTileIndex.getX(pMapTileIndex)
                        + "/" + MapTileIndex.getY(pMapTileIndex)
                        + mImageFilenameEnding;
            }
        });
        mapView.setMultiTouchControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getOverlays().add(myLocationoverlay);
        mapController=mapView.getController();
        mapController.setZoom(12.5);
        getLastLocation();
        // Modify this method to handle location selection
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            LocationEntity selectedLocation = locationAutoCompleteAdapter.getItem(position);
            autoCompleteTextView.setText("Trip to: "+selectedLocation.getName());
            // Hide the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


            GeoPoint destination = new GeoPoint(selectedLocation.getLat(), selectedLocation.getLon());
            // Draw the line between user's current location and selected location
            fetchORSRoute(userCurrentLocation,destination);
            // Optionally, move the map view to center the selected location
            updateMap(destination.getLatitude(),destination.getLongitude());
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Code to run after delay
                    showDialog();
                }
            }, 900);

        });

    }

    private void updateMap(double latitude, double longitude) {
        userCurrentLocation =  new GeoPoint(latitude, longitude);
        mapController.setCenter(userCurrentLocation);
        mapController.setZoom(13.0);

        // Add a marker
        Marker marker = new Marker(mapView);
        marker.setPosition(userCurrentLocation);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("You are here");
        mapView.getOverlays().clear();
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }


    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                 updateMap(latitude,longitude);
                                // Use the location data as needed
                            }
                        }
                    });
        } else {
            requestLocationPermission();
        }
    }

    private void  showDialog(){
    rideTypeDialog=new  DialogUtil().createCustomBottomDialog(this,R.layout.ride_dialog_layout);
        ArrayList<PriceItem> feresItem=new ArrayList<>();
        ArrayList<PriceItem> zayItems=new ArrayList<>();
        PriceItemAdaptor rvAdaptor=new PriceItemAdaptor(this);
        PriceItemAdaptor rvZayAdaptor=new PriceItemAdaptor(this);
        feresItem.add(new PriceItem("https://uploads.feres.co/ride/service_type_images/657454c19c9529727669a49aPftB.png","Classic","240 ETB"));
        feresItem.add(new PriceItem("https://uploads.feres.co/ride/service_type_images/5b3dd07a83d2e00bbfe8f125hAT9.png","MiniVan","260 ETB"));
        feresItem.add(new PriceItem("https://uploads.feres.co/ride/service_type_images/5e5e4a5003ad92596aae7e5cuu2O.png","MiniBus","250 ETB"));
        feresItem.add(new PriceItem("https://uploads.feres.co/ride/service_type_images/5a8ac66f5a1ac65394caae58FjT8.png","Economic","210 ETB"));
        feresItem.add(new PriceItem("https://uploads.feres.co/ride/service_type_images/5e5e48ad6066b9598f610f83WaGP.png","Lada","130 ETB"));
        zayItems.add(new PriceItem("https://uploads.feres.co/ride/service_type_images/657454c19c9529727669a49aPftB.png","Classic","280 ETB"));
        zayItems.add(new PriceItem("https://uploads.feres.co/ride/service_type_images/5b3dd07a83d2e00bbfe8f125hAT9.png","MiniVan","290 ETB"));
        zayItems.add(new PriceItem("https://uploads.feres.co/ride/service_type_images/5e5e4a5003ad92596aae7e5cuu2O.png","MiniBus","270 ETB"));
        zayItems.add(new PriceItem("https://uploads.feres.co/ride/service_type_images/5a8ac66f5a1ac65394caae58FjT8.png","Economic","250 ETB"));
        zayItems.add(new PriceItem("https://uploads.feres.co/ride/service_type_images/5e5e48ad6066b9598f610f83WaGP.png","Lada","130 ETB"));
        rvAdaptor.setPriceItems(feresItem);
        rvZayAdaptor.setPriceItems(zayItems);
        RecyclerView  feresRv=rideTypeDialog.getView().findViewById(R.id.feres_rv);
        RecyclerView  ZayRv=rideTypeDialog.getView().findViewById(R.id.zay_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager zlayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        feresRv.setLayoutManager(layoutManager);
        feresRv.setAdapter(rvAdaptor);
        ZayRv.setLayoutManager(zlayoutManager);
        ZayRv.setAdapter(rvZayAdaptor);

    rideTypeDialog.show();
    }
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to fetch route using OkHttp and retofit
    private void fetchORSRoute(GeoPoint origin, GeoPoint destination) {
        String apiKey = "5b3ce3597851110001cf624890944f95228b4522b2f4fdc299c36c36";
        String start = origin.getLongitude() + "," + origin.getLatitude();
        String end = destination.getLongitude() + "," + destination.getLatitude();
        Call<RouteResponse> call = orsApiService.getRoute(apiKey, start, end);
        call.enqueue(new Callback<RouteResponse>() {

            @Override
            public void onResponse(Call<RouteResponse> call, retrofit2.Response<RouteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ORS", "Request body " + response.body().getFeatures());
                    drawORSRoute(response.body());
                } else {
                    Log.e("ORS", "Request failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {
                Log.e("ORS", "API call failed: " + t.getMessage());
            }
        });
    }
    // Draw route on the map
    private void drawORSRoute(RouteResponse routeResponse) {
        try {
            if (routeResponse.getFeatures() != null && !routeResponse.getFeatures().isEmpty()) {
                List<List<Double>> coordinates = routeResponse.getFeatures().get(0).getGeometry().getCoordinates();
                List<GeoPoint> routePoints = new ArrayList<>();

                for (List<Double> coordinate : coordinates) {
                    routePoints.add(new GeoPoint(coordinate.get(1), coordinate.get(0))); // latitude, longitude
                }

                Polyline line = new Polyline();
                line.setPoints(routePoints);
                line.setWidth(15f);
                line.setColor(ContextCompat.getColor(this, R.color.bliue));
                mapController.setZoom(11.0);
                mapView.getOverlays().add(line);
                mapView.getMapCenter();
                mapView.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onScroll(ScrollEvent event) {
        return false;
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}