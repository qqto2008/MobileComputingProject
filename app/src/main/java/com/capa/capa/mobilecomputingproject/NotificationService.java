package com.capa.capa.mobilecomputingproject;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

class NotificationService extends ContextWrapper {

    public NotificationService(Context base) {
        super(base);

    }

    private void createChannel() {
    }
}
