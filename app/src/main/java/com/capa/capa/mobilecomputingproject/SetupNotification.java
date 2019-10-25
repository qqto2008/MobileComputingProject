package com.capa.capa.mobilecomputingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class SetupNotification extends AppCompatActivity {

    //Variable
    TimePicker timePicker1;
    TextView time;
    Calendar calendar;
    String format = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_notification);

        timePicker1 = findViewById(R.id.timePicker1);
        time = findViewById(R.id.textView1);
        calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        showTime(hour, min);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public void onClickSetupNotification(View v) {

        int hour = timePicker1.getHour();
        int min = timePicker1.getMinute();
        showTime(hour, min);

        pushNotification(hour, min);
    }

    public void showTime(int hour, int min) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        if (min < 10) {
            time.setText(new StringBuilder().append(hour).append(" :0").append(min)
                    .append(" ").append(format));
        } else
            time.setText(new StringBuilder().append(hour).append(" : ").append(min)
                    .append(" ").append(format));
    }

    public void pushNotification(final int hour, final int min) {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Calendar alarmStartTime = Calendar.getInstance();
                alarmStartTime.set(Calendar.HOUR_OF_DAY, hour);
                alarmStartTime.set(Calendar.MINUTE, min);
                alarmStartTime.set(Calendar.SECOND, 0);
                String content;
                WeatherData weatherData = new WeatherData();
                try {
                    content = weatherData
                            .execute("https://openweathermap.org/data/2.5/weather?lat="+location.getLatitude()+"&lon="+location.getLongitude()+"&appid=b6907d289e10d714a6e88b30761fae22")
                            .get();
                    JSONObject jsonObject = new JSONObject(content);
                    JSONObject weather = jsonObject.getJSONObject("main");
                    JSONArray weatherCondition = jsonObject.getJSONArray("weather");
                    JSONObject description = weatherCondition.getJSONObject(0);
                    String descriptionCondition = description.getString("description");
                    String temp = weather.getString("temp");
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    //Call AlarmReceiver
                    Intent intent = new Intent(SetupNotification.this, AlarmReceiver.class);
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

                    }else if(descriptionCondition.equals("shower rain")){
                        notificationContent+="\nAlso, it is rainy, don't forget your umbrella";
                    }else if(descriptionCondition.equals("scattered clouds")){
                        notificationContent+="\nAlso, it may rain, don't forget your umbrella";
                    }else if(descriptionCondition.equals("broken clouds")){
                        notificationContent+="\nAlso, it may rain, don't forget your umbrella";
                    }else if(descriptionCondition.equals("rain")){
                        notificationContent+="\nAlso, it is rainy, don't forget your umbrella, stay dry!";
                    }else if(descriptionCondition.equals("thunderstorm")){
                        notificationContent+="\nEXTREME WEATHER CONDITION!!, STAY AT HOME IF POSSIBLE";
                    }else if(descriptionCondition.equals("snow")){
                        notificationContent+="\nENJOY THE SNOW, STAY WARM!";
                    }else if(descriptionCondition.equals("mist")){
                        notificationContent+="\nMIST CONDITION!, DON'T FORGET TURN ON YOUR MIST LIGHT";
                    }
                    intent.putExtra("title", descriptionCondition+"  "+temp+"°");
                    intent.putExtra("content",notificationContent);
                    intent.putExtra("description",descriptionCondition);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(SetupNotification.this, 1, intent, 0);
                    assert alarmManager != null;
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            } catch (InterruptedException | ExecutionException | JSONException e) {
                    Log.i("Failed Notification","Failed to add notification");
                    e.printStackTrace();
                }
            }});

    }

    public void onClickBack(View v) {
        Intent intent = new Intent(this,MapsWeatherActivity.class);
        startActivity(intent);
    }

    public void onDeleteAlarm(View v) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        assert alarmManager != null;
        alarmManager.cancel(pendingIntent);
    }
}
