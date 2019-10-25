package com.capa.capa.mobilecomputingproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
//https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=mongolian%20grill&inputtype=textquery&fields=photos,formatted_address,name,opening_hours,rating&locationbias=circle:2000@47.6918452,-122.2226413&key=AIzaSyBG6Dz2-h3rVSE0pzXZ7yBFq1Dmv7cixhc
public class MapsWeatherActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    TextView degreeTextView,notificationBoardTextView;
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
        notificationBoardTextView = findViewById(R.id.notificationboard);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                getTemp(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
                LatLng sydney = new LatLng(location.getLatitude(),location.getLongitude());

                int height = 100;
                int width = 100;
                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.mite);
                Bitmap b=bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                mMap.addMarker(new MarkerOptions().position(sydney)
                        .title("My Location").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

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
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

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
    private void updateUI(String tempInfo){
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
            Log.i("fuckingshit",descriptionCondition);
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
            String notificationContent = "";
            double temperature = Double.parseDouble(temp);
            if(temperature<0){
                notificationContent+="you need to keep your head and ears warm when you are inactive with at least two layers, such as a beanie or balaclava under the hood of your jacket";
            }else if(0<=temperature&&temperature<10){
                notificationContent+="\nOuterwear: Padded or Puffer Coat, Overcoats (Trench Coats, Fur or Faux Fur Coats), Down Jackets\n" +
                        "Tops: Sweaters, Jumpers, Turtlenecks\n" +
                        "Bottoms: Jeans, Trousers";
            }else if(temperature>=10&&temperature<20){
                notificationContent+="\nTops (for Layering): Shirts, Hoodies, Dresses\n" +
                        "Lightweight Outerwear: Leather jackets, Biker jackets, Parkas, Pea Coats\n" +
                        "Bottoms: Jeans, Trousers, Skirts Shoes: Sneakers, Boots";

            }else if(temperature>=20&&temperature<30){
                notificationContent+="This is summer weather – no need to wear layers of clothing or thick fabric. Instead, find something that will keep you fresh. It can get humid at this time of the year too, which will make you feel even hotter. ";
            }else if(temperature>=30&&temperature<40){
                notificationContent+="You need to adjust your wardrobe and wear airy clothes. Avoid wearing tight garments as they may lead to skin irritation due to friction and heat.";
            }else if(temperature>=40){
                notificationContent+="You need to adjust your wardrobe and wear airy clothes. Avoid wearing tight garments as they may lead to skin irritation due to friction and heat.";
            }
            if(descriptionCondition.equals("few clouds")){
                notificationContent+="\nAlso, it may rain, don't forget your umbrella";
            }else if(descriptionCondition.equals("scattered clouds")){
                notificationContent+="\nAlso, it may rain, don't forget your umbrella";
            }else if(descriptionCondition.equals("broken clouds")){
                notificationContent+="\nAlso, it may rain, don't forget your umbrella";
            }else if(descriptionCondition.equals("rain")){
                notificationContent+="\nAlso, it is rainy, don't forget your umbrella, stay dry!";
            }else if(descriptionCondition.equals("shower rain")){
                notificationContent="it is rainy, don't forget your umbrella, stay dry!";
            } else if(descriptionCondition.equals("thunderstorm")){
                notificationContent+="\nEXTREME WEATHER CONDITION!!, STAY AT HOME IF POSSIBLE";
            }else if(descriptionCondition.equals("snow")){
                notificationContent+="\nENJOY THE SNOW, STAY WARM!";
            }else if(descriptionCondition.equals("mist")){
                notificationContent+="\nMIST CONDITION!, DON'T FORGET TURN ON YOUR MIST LIGHT";
            }
            Log.i("mainTEMP",temp);
            updateUI(temp+"°");
            notificationBoardTextView.setText(notificationContent);
            getPlaces(lat,lng,temperature);
            Log.i("COLD DRINK","COLD DRINK");

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }
    private void getPlaces(String lat, String lng, double temp) throws ExecutionException, InterruptedException, JSONException {
        Log.i("COLD DRINK","COLD DRINK");
        String content;
        BitmapDescriptor icon;
        WeatherData placeData = new WeatherData();
        String keyWord;
        if (temp>25){
            keyWord="Cold Drink";

            icon = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("cold",100,100));
        }else if (temp<20){
            keyWord="Hot Drink";
            icon = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("coffee",100,100));
        }else {
            keyWord="Drink";
            icon = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("coffee",100,100));
        }
        String httpsconections = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input="+keyWord+"&inputtype=textquery&fields=photos,formatted_address,name,opening_hours,rating&locationbias=circle:2000@"+lat+","+lng+"&key=AIzaSyBG6Dz2-h3rVSE0pzXZ7yBFq1Dmv7cixhc";

        content = placeData
                .execute(httpsconections)
                .get();

        JSONObject jsonObject = new JSONObject(content);
        Log.i("json shit", String.valueOf(jsonObject));
        JSONArray candicates = jsonObject.getJSONArray("candidates");
        JSONObject candicatesObject = candicates.getJSONObject(0);
        String drinkString = candicatesObject.getString("formatted_address");
        String shopName = candicatesObject.getString("name");
        LatLng shopLocation = getLocationFromAddress(this,drinkString);


        mMap.addMarker(new MarkerOptions().position(shopLocation).title(shopName).icon(icon));


    }
    public LatLng getLocationFromAddress(Context context, String strAddress)
    {
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;

    }
    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

}
