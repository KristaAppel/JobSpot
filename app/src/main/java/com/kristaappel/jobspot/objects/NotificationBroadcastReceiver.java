package com.kristaappel.jobspot.objects;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kristaappel.jobspot.BottomNavigationActivity;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.R.attr.radius;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.kristaappel.jobspot.BottomNavigationActivity.sortBy;
import static com.kristaappel.jobspot.objects.FileUtil.readMostRecentSearch;

public class NotificationBroadcastReceiver extends WakefulBroadcastReceiver {

    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Receiver", "onReceive");
        setAlarm(context);
        getNewJobs(context);
//        showNotification(context);
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

    public void getNewJobs(final Context context){
        Log.i("Receiver", "getNewJobs");

        if (!NetworkMonitor.deviceIsConnected(context)){
            return;
        }
        final ArrayList<Job> newJobs = new ArrayList<>();
        // Get the most recent job search:
        final SavedSearch recentSearch = FileUtil.readMostRecentSearch(context);
        if (recentSearch != null){
            Log.i("Receiver", "most recent search: " + recentSearch.getKeywords() + " - " + recentSearch.getLocation());
        }else{
            Log.i("Receiver", "most recent search is null");
        }
        // Get the most recent job from the most recent search:
        final Job mostRecentJob = FileUtil.readMostRecentJob(context);
        if (mostRecentJob != null) {
            Log.i("Receiver", "most recent job: " + mostRecentJob.getJobTitle() + " - " + mostRecentJob.getDatePosted());
        }else{
            Log.i("Receiver", "most recent job is null");
        }
        if (recentSearch != null){
            // Create url string from recent search criteria:
            String maxJobsString = String.valueOf(BottomNavigationActivity.maxJobs);
            String url = "https://api.careeronestop.org/v1/jobsearch/TZ1zgEyKTNm69nF/" + recentSearch.getKeywords() + "/"+recentSearch.getLocation()+"/" + recentSearch.getRadius() + "/" + "accquisitiondate" + "/desc/0/"+maxJobsString+"/" + "30";
            final ArrayList<Job> jobs = new ArrayList<>();
            final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
            // Run the job search:
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("Receiver", "response: " + response);
                    // Parse JSON to make a list of Job objects:
                    try {
                        JSONObject responseObj = new JSONObject(response);
                        JSONArray jobsArray = responseObj.getJSONArray("Jobs");
                        for (int i = 0; i<jobsArray.length(); i++){
                            JSONObject jobObj = jobsArray.getJSONObject(i);
                            String jobid = jobObj.getString("JvId");
                            String jobtitle = jobObj.getString("JobTitle");
                            String companyname = jobObj.getString("Company");
                            String dateposted = jobObj.getString("AccquisitionDate");
                            String joburl = jobObj.getString("URL");
                            String jobcitystate = jobObj.getString("Location");
                            Job foundJob = new Job(jobid, jobtitle, companyname, dateposted, joburl, jobcitystate, 0, 0, "");

                            jobs.add(foundJob);
                        }
                        for (Job job : jobs){
                            try{
                                // Find out if each job is newer than the most recent job:
                                if (mostRecentJob != null && dateTimeFormat.parse(mostRecentJob.getDatePosted()).compareTo(dateTimeFormat.parse(job.getDatePosted())) < 0) {
                                    // If a job is new, add it to the newJobs arraylist:
                                    newJobs.add(job);
                                    Log.i("Receiver", "new jobs: " + newJobs.size());
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // If there are new jobs, show a notification:
                    if (newJobs.size() > 0){
                        showNotification(context, newJobs.size(), recentSearch);
                    }else{
                        Log.i("Receiver", "New Jobs: " + newJobs.size());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Receiver", "That didn't work!!!!!!!");
                    if (error.networkResponse.statusCode == 404){
                        Log.i("Receiver", "No jobs available");
                    }
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "Bearer imXBBrutJKGqrj6NHkLNPA41F8H/dbvQDiYjpaLrQWmYzJb+PNAZ7dg8D6Gv7onpkZl1mccgSRygH+xiE7AZrQ==");
                    return params;
                }
            };

            VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
        }

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

    private void showNotification(Context context, int numberOfNewJobs, SavedSearch search){
            // Create an expanded notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.drawable.jobspot_small_notification_icon);
            builder.setContentTitle("New " + search.getKeywords() + " Jobs");
            builder.setContentText("There are " + numberOfNewJobs + " new " + search.getKeywords() + " jobs available in " + search.getLocation());
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
