package com.capa.capa.mobilecomputingproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

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

public class Weather extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    //b6907d289e10d714a6e88b30761fae22 api key
    class WeatherData extends AsyncTask<String,Void,String>{
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        String content;
        WeatherData weatherData = new WeatherData();
        try {
            content = weatherData
                    .execute("https://openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22")
                    .get();
            JSONObject jsonObject = new JSONObject(content);
            String weather = jsonObject.getString("weather");
            JSONArray array = new JSONArray(weather);
            String main = "";
            String description="";
            for(int i =0;i<array.length();i++){
                JSONObject weatherPart = array.getJSONObject(i);
                main = weatherPart.getString("main");
                description = weatherPart.getString("description");
            }

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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

    }
}
