package com.torrydo.floating.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.torrydo.floating.Utils;
import com.torrydo.floating.ViewUtils;
import com.torrydo.floating.helper.NotificationHelper;

public abstract class FloatingBubbleService extends Service {

    protected void startNotificationForeground() {
        NotificationHelper noti = new NotificationHelper(this);
        noti.createNotificationChannel();
        startForeground(noti.getNotificationId(), noti.defaultNotification());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!ViewUtils.canDrawOverlays(this)) {
            throw new SecurityException("Permission Denied: \"display over other app\" permission IS NOT granted!");
        }

        Utils.sez.with(this.getApplicationContext());

        startNotificationForeground();
        setup();
    }

    public abstract void setup();

    public abstract void removeAll();

    @Override
    public void onDestroy() {
        removeAll();
        super.onDestroy();
    }
}