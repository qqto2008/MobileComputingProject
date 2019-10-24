package com.capa.capa.mobilecomputingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

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

    public void pushNotification(int hour, int min) {

        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmStartTime.set(Calendar.MINUTE, min);
        alarmStartTime.set(Calendar.SECOND, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //Call AlarmReceiver
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void onClickBack(View v) {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }

    public void onDeleteAlarm(View v) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        assert alarmManager != null;
        alarmManager.cancel(pendingIntent);
    }
}
