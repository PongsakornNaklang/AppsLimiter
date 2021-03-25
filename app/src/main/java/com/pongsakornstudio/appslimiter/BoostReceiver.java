package com.pongsakornstudio.appslimiter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


public class BoostReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent LockAppServices_intent = new Intent(context, LockAppServices.class);
        Intent CountdownTimeService_intent = new Intent(context, CountdownTimeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(LockAppServices_intent);
            context.startForegroundService(CountdownTimeService_intent);
        } else {
            context.startService(LockAppServices_intent);
            context.startService(CountdownTimeService_intent);
    }
    }
}
