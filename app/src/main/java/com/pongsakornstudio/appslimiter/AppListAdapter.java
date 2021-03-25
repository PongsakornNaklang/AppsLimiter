package com.pongsakornstudio.appslimiter;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter  extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {
    private List<AppInfo> installedApps = new ArrayList();
    private Context context;
    private SharedPreference sharedPreference = new SharedPreference();
    private EditText time_for_use;
    private EditText time_for_lock;
    public static final String COUNTDOWN_BR = "com.pongsakornstudio.appslimiter";
    Intent bi = new Intent(COUNTDOWN_BR);


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView applicationName;
        public CardView cardView;
        public ImageView icon;
        public Switch switchView;

        public ViewHolder( View v) {
            super(v);
            applicationName =  v.findViewById(R.id.applicationName);
            cardView =  v.findViewById(R.id.card_view);
            icon = v.findViewById(R.id.icon);
            switchView =  v.findViewById(R.id.switchView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AppListAdapter(List<AppInfo> appInfoList, Context context) {
        installedApps = appInfoList;
        this.context = context;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public AppListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.apps_list_item,parent,false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final AppInfo appInfo = installedApps.get(position);
        holder.applicationName.setText(appInfo.getName());
        holder.applicationName.setTextColor(Color.WHITE);
        holder.icon.setBackgroundDrawable(appInfo.getIcon());

        holder.switchView.setOnCheckedChangeListener(null);
        holder.cardView.setOnClickListener(null);
        checkTimeItem(appInfo.getPackageName(),holder);

        holder.switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    createDialog(appInfo,holder,context);
                } else {
//                    sharedPreference.removeLocked(context, appInfo.getPackageName());
//                    sharedPreference.removeStrTime(context,appInfo.getPackageName());
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.switchView.performClick();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return installedApps.size();
    }

    /*Checks whether a particular app exists in SharedPreferences*/
    public void checkTimeItem(String checkTime,ViewHolder holder) {
        SharedPreferences settings_time;
        settings_time = context.getSharedPreferences("time",Context.MODE_PRIVATE);
        if (settings_time.contains(checkTime)){
            holder.switchView.setChecked(true);
        }else {
            holder.switchView.setChecked(false);
        }
    }

    //**************************Dialog set time to block app ***************************************************
    static Dialog dialog;
    private static int convert_to_minute = 60000;
    public void createDialog(final AppInfo appInfo, final ViewHolder holder, final Context context){
        dialog = new Dialog(context);
        dialog.setTitle("");
        dialog.setContentView(R.layout.set_time_dialog);
        dialog.setCancelable(false);
        time_for_use = dialog.findViewById(R.id.time_for_use);
        time_for_lock = dialog.findViewById(R.id.time_for_lock);
        Button buttonCancel = dialog.findViewById(R.id.button_cancel);
        Button buttonSubmit = dialog.findViewById(R.id.button_submit);

        time_for_use.setText("1");
        time_for_lock.setText("1");

        buttonCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                holder.switchView.setChecked(false);
                dialog.dismiss();
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final int _time_for_use = convert_to_minute*Integer.parseInt(time_for_use.getText().toString());
                final int _time_for_lock = convert_to_minute*Integer.parseInt(time_for_lock.getText().toString());

                Intent intentCountdownTimeService = new Intent(context, CountdownTimeService.class);
                intentCountdownTimeService.putExtra("time_for_use", _time_for_use);
                intentCountdownTimeService.putExtra("time_for_lock", _time_for_lock);
                intentCountdownTimeService.putExtra("package_name", appInfo.getPackageName());
                intentCountdownTimeService.putExtra("app_name", appInfo.getName());
                context.startService(intentCountdownTimeService);

                Toast.makeText(context, "เวลาที่ใช้งานได้ "+time_for_use.getText()+" นาที,เวลาที่ล็อกแอป "+time_for_lock.getText()+" นาที",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
