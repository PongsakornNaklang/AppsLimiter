package com.pongsakornstudio.appslimiter;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;



public class LockAppServices extends Service {

    public static final String TAG = "LockAppServices";
    private Context context = null;
    private Timer timer;
    public  View myView;
    private WindowManager windowManager ;
    private Dialog dialog;
    private static String currentApp = "";
    private static String previousApp = "";
    private SharedPreference sharedPreference;
    private List<String> packageName;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sharedPreference = new SharedPreference();

        if (sharedPreference != null) {
            packageName = sharedPreference.getLocked(context);
        }

        timer = new Timer("_AppCheckServices");
        timer.schedule(updateTask, 1000, 1000);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        myView = new View(this);
        myView.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);

            params.gravity = Gravity.START | Gravity.TOP;
            params.x = ((getApplicationContext().getResources().getDisplayMetrics().widthPixels) / 2);
            params.y = ((getApplicationContext().getResources().getDisplayMetrics().heightPixels) / 2);
            windowManager.addView(myView, params);

        } else {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);


            params.gravity = Gravity.START | Gravity.TOP;
            params.x = ((getApplicationContext().getResources().getDisplayMetrics().widthPixels) / 2);
            params.y = ((getApplicationContext().getResources().getDisplayMetrics().heightPixels) / 2);
            windowManager.addView(myView, params);
        }
    }

    private TimerTask updateTask = new TimerTask(){
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void run(){
            if(sharedPreference != null){
                packageName = sharedPreference.getLocked(context);
            }
            if(isConcernedAppIsInForeground()){
                Log.d("isConcernedAppIsInFrgnd", "true");
                if(myView != null){
                    myView.post(new Runnable(){
                        public void run(){
                            if(!currentApp.matches(previousApp)){

                                showLockDialog();

                                previousApp = currentApp;
                            }else{
                                Log.d("isConcernedAppIsInFrgnd", "currentApp matches previous App");
                            }
                        }
                    });
                }
            }else{
                Log.d("isConcernedAppIsInFrgnd", "false");
                if(myView != null){
                    myView.post( new Runnable(){
                        public void run(){
                            hideLockDialog();
                        }
                    });
                }
            }
        }
    };

    private void showLockDialog() {
        showDialog();
    }

    private void hideLockDialog() {
        previousApp = "";
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showDialog() {
        if (context == null){
            context = getApplicationContext();
        }

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.locked_screen, null, false);
        TextView textView = promptsView.findViewById(R.id.locked_text);

        dialog = new Dialog(context, android.R.style.Theme_NoTitleBar);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(promptsView);
        dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.show();
    }

    @Override
    public IBinder onBind(Intent intent){
        return  null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        onTaskRemoved(intent);
        return START_STICKY;
    }

    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean isConcernedAppIsInForeground(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(5);
        if(Build.VERSION.SDK_INT <= 20){
            if(task.size() > 0){
                ComponentName componentInfo = task.get(0).topActivity;
                for(int i = 0; packageName != null && i < packageName.size(); i++){
                    if(componentInfo.getPackageName().equals(packageName.get(i))){
                        currentApp = packageName.get(i);
                        return true;
                    }
                }
            }
        }else{
            String mpackageName = manager.getRunningAppProcesses().get(0).processName;
            UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, time);
            if(stats != null){
                SortedMap<Long, UsageStats> runningTask = new TreeMap<>();
                for(UsageStats usageStats : stats){
                    runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if(runningTask.isEmpty()){
                    Log.d(TAG,"isEmpty Yes");
                    mpackageName = "";
                }else{
                    mpackageName = runningTask.get(runningTask.lastKey()).getPackageName();
                    Log.d(TAG,"isEmpty No : "+mpackageName);
                    Log.i("LAG","isEmpty No : "+runningTask);
                }
            }

            for(int i = 0; packageName != null && i < packageName.size(); i++){
                Log.d("AppCheckService", "pakageName Size" + packageName.size());
                if(mpackageName.equals(packageName.get(i))){
                    currentApp = packageName.get(i);
                    return true;
                }
            }
        }
        return false;
    }



    @Override
    public void onDestroy(){
    }
}
