package com.capa.capa.mobilecomputingproject;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;

class NotificationService extends ContextWrapper {

    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private NotificationManager mManager;

    public NotificationService(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();

        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    //Define what happen in notification
    public NotificationCompat.Builder getChannelNotification(String title,String content) {

        return new NotificationCompat.Builder(getApplicationContext(), channelID)

                //Notification Information
                .setContentTitle(title) //Title
                .setContentText(content) //Text Inside notification
                .setSmallIcon(R.drawable.ic_launcher_background); //Small Icon (res->drawable)

    }
}
