package com.capa.capa.mobilecomputingproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String description = intent.getStringExtra("discription");
        NotificationService notificationHelper = new NotificationService(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(title,content,description);
        notificationHelper.getManager().notify(1, nb.build());

    }
}
