package com.pongsakornstudio.appslimiter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class CountdownTimeService extends Service {

    private final static String TAG = "CountdownTimeService";
    private Context context = null;
    private SharedPreference sharedPreference;
    private int _time_for_use ;
    private int _time_for_lock;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        sharedPreference = new SharedPreference();


    }

    public void startCountdown(Intent intent) {
        _time_for_use = intent.getIntExtra("time_for_use",0);
        _time_for_lock = intent.getIntExtra("time_for_lock",0);
        final String _package = intent.getStringExtra("package_name");
        String _appname = intent.getStringExtra("app_name");
                new CountDownTimer(_time_for_use, 1000) {
                    public void onTick(long millisUntilFinished) {
                        String strTime = String.format("%d" ,(millisUntilFinished / 1000)/60);
                        sharedPreference.addStrTime(context,"จะล็อคในอีก "+strTime+" นาที",_package);
                        Log.d("CountDownTimer",_package+"cd1 "+strTime);
                    }
                    public void onFinish() {
                        sharedPreference.addLocked(context, _package);
                        new CountDownTimer(_time_for_lock, 1000) {
                            public void onTick(long millisUntilFinished) {
                                String strTime = String.format("%d" ,(millisUntilFinished / 1000)/60);
                                sharedPreference.addStrTime(context,"จะปลดล็อคในอีก "+strTime+" นาที",_package);
                                Log.d("CountDownTimer",_package+"cd2 "+strTime);
                            }
                            public void onFinish() {
                                sharedPreference.removeLocked(context, _package);
                                sharedPreference.removeStrTime(context,_package);
                                Log.d("CountDownTimer",_package+"finish "+sharedPreference.getLocked(context).toArray());
                            }
                        }.start();
                    }
                }.start();
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startCountdown(intent);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            SharedPreferences settings = context.getSharedPreferences("time",Context.MODE_PRIVATE);
//            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,new MyOwnForeground().NOTIFICATION_CHANNEL_ID);
//            Notification notification = notificationBuilder.setOngoing(true)
//                    .setContentTitle("Apps Limiter")
//                    .setContentText(_appname+" "+settings.getString(_appname,null))
//                    .setSmallIcon(R.drawable.biglogo)
//                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
//                    .setCategory(Notification.CATEGORY_SERVICE)
//                    .build();
//            startForeground(2, notification);
//        }

        return START_STICKY;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
