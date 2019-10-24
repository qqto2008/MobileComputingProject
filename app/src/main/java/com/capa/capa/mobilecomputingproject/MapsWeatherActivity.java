package com.capa.capa.mobilecomputingproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MapsWeatherActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    TextView degreeTextView;
    Button goToNotification;
    ImageView weatherIcon;
    //b6907d289e10d714a6e88b30761fae22 api key

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_weather);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        degreeTextView = findViewById(R.id.degreeTextView);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                getTemp(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
            }
        });

        weatherIcon = findViewById(R.id.weatherIcon);


        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.i("Success Location", String.valueOf(location.getLatitude()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Failed Location", Objects.requireNonNull(e.getMessage()));
            }
        });
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(200000);
        locationRequest.setFastestInterval(200000);
        locationCallBack = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult==null){
                    return;
                }
                for (Location location:locationResult.getLocations()){

                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

                    String lat = String.valueOf(location.getLatitude());
                    String lng = String.valueOf(location.getLongitude());

                    getTemp(lat,lng);
                }
            }
        };

        goToNotification = findViewById(R.id.goToNotificationBtn);
        goToNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsWeatherActivity.this,SetupNotification.class);
                startActivity(intent);
            }
        });
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        mMap = googleMap;
    }
    @SuppressLint("StaticFieldLeak")


    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack, Looper.myLooper());
    }
    private void updataUI(String tempInfo){
        degreeTextView.setText(tempInfo);
    }
    private void getTemp(String lat, String lng){
        String content;
        WeatherData weatherData = new WeatherData();
        try {
            content = weatherData
                    .execute("https://openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lng+"&appid=b6907d289e10d714a6e88b30761fae22")
                    .get();
            JSONObject jsonObject = new JSONObject(content);
            JSONObject weather = jsonObject.getJSONObject("main");
            JSONArray weatherCondition = jsonObject.getJSONArray("weather");
            JSONObject description = weatherCondition.getJSONObject(0);
            String descriptionCondition = description.getString("description");
            Log.i("descriptionqqqq",descriptionCondition);
            switch (descriptionCondition) {
                case "clear sky":
                    weatherIcon.setImageResource(R.drawable.clearsky);
                    break;
                case "few clouds":
                    weatherIcon.setImageResource(R.drawable.fewclouds);
                    break;
                case "scattered clouds":
                    weatherIcon.setImageResource(R.drawable.scatteredclouds);
                    break;
                case "broken clouds":
                    weatherIcon.setImageResource(R.drawable.brokenclouds);
                    break;
                case "shower rain":
                    weatherIcon.setImageResource(R.drawable.showerrain);
                    break;
                case "rain":
                    weatherIcon.setImageResource(R.drawable.rain);
                    break;
                case "thunderstorm":
                    weatherIcon.setImageResource(R.drawable.thunderstorm);
                    break;
                case "snow":
                    weatherIcon.setImageResource(R.drawable.snow);
                    break;
                case "mist":
                    weatherIcon.setImageResource(R.drawable.mist);
                    break;
                default:
                    weatherIcon.setImageResource(R.drawable.scatteredclouds);
                    break;
            }
            String temp = weather.getString("temp");
            Log.i("mainTEMP",temp);
            updataUI(temp+"°");

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

}
