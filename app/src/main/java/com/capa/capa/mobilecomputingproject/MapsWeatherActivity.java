package com.capa.capa.mobilecomputingproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
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
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

    }
    @SuppressLint("StaticFieldLeak")
    class WeatherData extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... addresses) {
            try {
                URL url = new URL(addresses[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream is = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(is);
                int data = inputStreamReader.read();
                String content = "";
                char ch;
                while (data !=-1){
                    ch = (char) data;
                    content = content+ch;
                    data = inputStreamReader.read();
                }
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

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
        MapsWeatherActivity.WeatherData weatherData = new MapsWeatherActivity.WeatherData();
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
            if(descriptionCondition.equals("clear sky")){
                weatherIcon.setImageResource(R.drawable.clearsky);
            }else if (descriptionCondition.equals("few clouds")){
                weatherIcon.setImageResource(R.drawable.fewclouds);
            }else if(descriptionCondition.equals("scattered clouds")){
                weatherIcon.setImageResource(R.drawable.scatteredclouds);
            }else if(descriptionCondition.equals("broken clouds")){
                weatherIcon.setImageResource(R.drawable.brokenclouds);
            }else if(descriptionCondition.equals("shower rain")){
                weatherIcon.setImageResource(R.drawable.showerrain);
            }else if(descriptionCondition.equals("rain")){
                weatherIcon.setImageResource(R.drawable.rain);
            }else if(descriptionCondition.equals("thunderstorm")){
                weatherIcon.setImageResource(R.drawable.thunderstorm);
            }else if((descriptionCondition.equals("snow"))){
                weatherIcon.setImageResource(R.drawable.snow);
            }else if((descriptionCondition.equals("mist"))){
                weatherIcon.setImageResource(R.drawable.mist);
            }else {
                weatherIcon.setImageResource(R.drawable.scatteredclouds);
            }
            String temp = weather.getString("temp");
            Log.i("mainTEMP",temp);
            updataUI(temp+"°");

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

}
