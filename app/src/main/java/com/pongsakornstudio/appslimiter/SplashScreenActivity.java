package com.pongsakornstudio.appslimiter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

public class SplashScreenActivity extends AppCompatActivity {
    public Context context;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    public static int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_splash_screen);
        checkPermissions();
    }

    public void checkPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!Settings.canDrawOverlays(this)){
                OverlayPermissionDialogFragment dialogFragment = new OverlayPermissionDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "Overlay Permission");
            }else if(!hasUsageStatsPermission()){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                UsageAccessDialogFragment dialogFragment = new UsageAccessDialogFragment();
                ft.add(dialogFragment, null);
                ft.commitAllowingStateLoss();
            }else{
                startService();
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super().
    }

    //start service -> 'LockAppServices'
    public void startService(){
        startService(new Intent(SplashScreenActivity.this, LockAppServices.class));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(i);
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        checkPermissions();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager)getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    //dialog for Overlay permission
    public static class OverlayPermissionDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("กรุณากดปุ่มอนุญาตเพื่อใช้งานแอพพลิเคชั่นนี้")
                    .setTitle("สิทธิ์การซ้อนทับ")
                    .setPositiveButton("อนุญาต", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                                    ,Uri.parse("package:" + getActivity().getPackageName()))
                                    , OVERLAY_PERMISSION_REQ_CODE);
                        }
                    });
            return builder.create();
        }
    }

    //dialog for Usage Access permission
    public static class UsageAccessDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("กรุณากดปุ่มอนุญาตเพื่อใช้งานแอพพลิเคชั่นนี้")
                    .setTitle("สิทธิ์การเข้าถึงการใช้งาน")
                    .setPositiveButton("อนุญาต", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                                    ,MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
                        }
                    });
            return builder.create();
        }
    }
}
