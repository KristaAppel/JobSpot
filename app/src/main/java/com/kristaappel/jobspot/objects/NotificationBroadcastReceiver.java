package com.kristaappel.jobspot.objects;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.SplashActivity;

import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationBroadcastReceiver extends WakefulBroadcastReceiver {

    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Receiver", "onReceive");
        setAlarm(context);
        showNotification(context);
    }

    public void setAlarm(Context context){
        Log.i("Receiver", "set alarm");
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(java.util.Calendar.SECOND, 24); //TODO: change this to 24 hours
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

    private void showNotification(Context context){
            // Create an expanded notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.drawable.jobspot_small_notification_icon);
            builder.setContentTitle("New Jobs");
            builder.setContentText("There are new jobs waiting for you!");
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.jobspot_large_notification_icon);
            builder.setLargeIcon(bitmap);


            // Create PendingIntent to open the app:
            Intent intent = new Intent(context, SplashActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Tell the notification to call the PendingIntent when the notification is clicked:
            builder.setContentIntent(pendingIntent);
            // Tell the notification to cancel when it is clicked:
            builder.setAutoCancel(true);

            Notification notification = builder.build();

            // Show the notification:
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0x0003, notification);
    }


}
