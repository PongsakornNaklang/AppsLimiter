package com.pongsakornstudio.appslimiter;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

class MyOwnForeground extends Application {
    public  String NOTIFICATION_CHANNEL_ID = "com.pongsakornstudio.appslimiter";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        startMyOwnForeground();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){

        String channelName = "MyChannelName";
        NotificationChannel chan = null;

        chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(chan);
        }

    }
}
