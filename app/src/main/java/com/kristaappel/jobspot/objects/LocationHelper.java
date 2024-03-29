package com.kristaappel.jobspot.objects;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kristaappel.jobspot.BottomNavigationActivity;
import java.io.IOException;
import java.util.List;
import static android.content.Context.LOCATION_SERVICE;

public class LocationHelper {

    private static LocationManager locationManager;


    public static Location getCurrentLocation(Context context, LocationListener locationListener) {
        Location currentLocation = null;

        // Check for permission
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get last known location:
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (currentLocation == null) {
                // No last known location.  Begin requesting location updates:
                requestLocationUpdates(context, locationListener);
            }
        }
        return currentLocation;
    }


    public static Address getCurrentAddressFromLocation(Location location, Context context){
        Geocoder geocoder = new Geocoder(context);
        Address theAddress = null;
        try {
            // Get Address object from Location object:
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            theAddress = addresses.get(0);
            Log.i("LocationHelper", "Current Location: " + theAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return theAddress;
    }

    public static void lookUpCompany(final Activity activity, final Job foundJob){
        // This will look up a company by name, city, & state and get additional information about it.
        // This will be used (in the BottomNavigationActivity) to get the company's coordinates so it can be pinned on the map.

        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="+foundJob.getCompanyName()+foundJob.getJobCityState()+"&key=AIzaSyC7q_VhmcurOkyz4wwIc0UkK7L0o1bUK-0\n";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Send the response back to the activity to be parsed & used:
                BottomNavigationActivity.getCoordsForCompany(activity, response, foundJob);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("LocationHelper", "That didn't work!!!!!!!");
                Log.i("LocationHelper", error.toString());
            }
        })
        {

        };
        VolleySingleton.getInstance(activity.getApplicationContext()).addToRequestQueue(stringRequest);
    }


    public static void requestLocationUpdates(Context context, LocationListener locationListener) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        // Check for permissions:
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Start requesting location updates:
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10.0f, locationListener);
    }


    public static void stopRequestingLocationUpdates(Context context, LocationListener locationListener){
        LocationManager locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
    }


}
