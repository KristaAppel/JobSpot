package com.kristaappel.jobspot.objects;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.provider.Settings;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Date;

public class NotificationBroadcastReceiver extends WakefulBroadcastReceiver {

    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Receiver", "onReceive");
        setAlarm(context);
        //TODO: make/send notification now
    }

    public void setAlarm(Context context){
        Log.i("Receiver", "set alarm");
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(java.util.Calendar.SECOND, 10); //TODO: change this to 24 hours
        Date date = calendar.getTime();

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarms(Context context){
        Log.i("Receiver", "cancel alarms");
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(pendingIntent);

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }


}
