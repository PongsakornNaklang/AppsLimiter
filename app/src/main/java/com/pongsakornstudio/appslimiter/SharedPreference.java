package com.pongsakornstudio.appslimiter;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SharedPreference {
    public static final String LOCKED_APP = "locked_app";
    public static final String TIME_START = "time_start";
    public static final String MyPREFERENCES = "MyPreferences";

    public SharedPreference() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveLocked(Context context, List<String> lockedApp) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();

        String jsonLockedApp = gson.toJson(lockedApp);

        editor.putString(LOCKED_APP, jsonLockedApp);
        editor.commit();
//        Log.i("GSON","\n"+jsonLockedApp+"\n");
    }

    public void addLocked(Context context, String packagename){
        List<String> lockedApp = getLocked(context);
        if(lockedApp == null){
            lockedApp = new ArrayList<>();
        }

        lockedApp.add(packagename);
        saveLocked(context, lockedApp);
    }

    public void removeLocked(Context context, String packagename){
        ArrayList<String> locked = getLocked(context);
        if(locked != null){
            locked.remove(packagename);
            saveLocked(context, locked);
        }
    }

    public ArrayList<String> getLocked(Context context) {
        SharedPreferences settings;
        List<String> locked;

        settings = context.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);

        if (settings.contains(LOCKED_APP)) {
            String jsonLocked = settings.getString(LOCKED_APP, null);
            Gson gson = new Gson();
            String[] lockedItems = gson.fromJson(jsonLocked,String[].class);

            locked = Arrays.asList(lockedItems);
            locked = new ArrayList<>(locked);
        } else{
            return null;
        }
        return (ArrayList<String>) locked;
    }



    public void addStrTime(Context context, String StrTime,String Appname){
        SharedPreferences settings_time;
        SharedPreferences.Editor editor_time;

        settings_time = context.getSharedPreferences("time",Context.MODE_PRIVATE);
        editor_time = settings_time.edit();

        editor_time.putString(Appname, StrTime);
        editor_time.commit();
        Log.i("GSON","\naddStrTime"+settings_time.getString(Appname,null)+"\n");
    }

    public void removeStrTime(Context context,String Appname){
        SharedPreferences settings_time;
        SharedPreferences.Editor editor_time;

        settings_time = context.getSharedPreferences("time",Context.MODE_PRIVATE);
        editor_time = settings_time.edit();

        if(settings_time.contains(Appname)){
            editor_time.remove(Appname);
            editor_time.commit();
            Log.i("GSON","\n removeTiming-"+Appname+": "+settings_time.getString(Appname,null)+"\n");
        }
    }



}
