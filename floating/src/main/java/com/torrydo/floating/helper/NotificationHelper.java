package com.torrydo.floating.helper;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.torrydo.floating.AndroidVersions;
import com.torrydo.floating.R;

public class NotificationHelper {
    
    private final Context context;
    private final String channelId;
    private final String channelName;
    private final int notificationId;

    public NotificationHelper(Context context) {
        this(context, "bubble_service", "floating bubble", 101);
    }

    public NotificationHelper(Context context, String channelId, String channelName, int notificationId) {
        this.context = context;
        this.channelId = channelId;
        this.channelName = channelName;
        this.notificationId = notificationId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public int getNotificationId() {
        return notificationId;
    }

    /**
     * update notification if already exists
     */
    @SuppressLint("MissingPermission")
    public void notify(Notification notification) {
        NotificationManagerCompat.from(context).notify(notificationId, notification);
    }

    /**
     * create notification channel on android 8 and above
     */
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= AndroidVersions.VERSION_8) {
            createNotificationChannel(channelId, channelName);
        }
    }

    /**
     * Default notification for FloatingBubbleService foreground service.
     * In case you don't have time :)
     */
    public Notification defaultNotification() {
        return new NotificationCompat.Builder(context, channelId)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_rounded_blue_diamond)
                .setContentTitle("bubble is running")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSilent(true)
                .build();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName) {
        NotificationChannel channel = new NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT   // IMPORTANCE_NONE recreate the notification if update
        );
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManagerCompat.from(context).createNotificationChannel(channel);
    }
}