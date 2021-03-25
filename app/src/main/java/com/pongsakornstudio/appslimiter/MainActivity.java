package com.pongsakornstudio.appslimiter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView refresh_btn;
    private static String THIS_PACKAGE_NAME = "com.pongsakornstudio.appslimiter";

    private List<AppInfo> installedAppList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set all installed applications to list
        installedAppList = getListOfInstalledApp(this);

        //set recycle view for the list of all installed applications
        layoutManager = new LinearLayoutManager(this);
        myRecyclerView = findViewById(R.id.my_recycler_view);
        myRecyclerView.setLayoutManager(layoutManager);
        myAdapter = new AppListAdapter(installedAppList , this);
        myRecyclerView.setAdapter(myAdapter);

        //set img logo in main activity to be refresh button
        refresh_btn = findViewById(R.id.logo_main);
        refresh_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    //***get the list of all installed applications & return ArrayList of installed applications****
    public static List<AppInfo> getListOfInstalledApp(Context context){
        PackageManager packageManager = context.getPackageManager();
        ArrayList installedApps = new ArrayList();
        List<PackageInfo> apps = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        if(apps != null && !apps.isEmpty()){
            for(int i = 0; i < apps.size(); i++){
                PackageInfo p = apps.get(i);
                if(!p.packageName.equals(THIS_PACKAGE_NAME)){
                    try{
                        if(packageManager.getLaunchIntentForPackage(p.packageName)!= null){
                            AppInfo app = new AppInfo();
                            app.setName(p.applicationInfo.loadLabel(packageManager).toString());
                            app.setPackageName(p.packageName);
                            app.setIcon(p.applicationInfo.loadIcon(packageManager));
                            installedApps.add(app);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
            return installedApps;
        }
        return null;
    }
}
