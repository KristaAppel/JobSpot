package com.kristaappel.jobspot.objects;


import android.Manifest;
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

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class LocationHelper {
    static LocationManager locationManager;


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


    public static Address getCurrentAddress(Location location, Context context){
        Geocoder geocoder = new Geocoder(context);
        Address theAddress = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            theAddress = addresses.get(0);
            Log.i("LocationHelper", "Current Location: " + theAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return theAddress;
    }


    public static void requestLocationUpdates(Context context, LocationListener locationListener) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        // Check for permissions:
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10.0f, locationListener);
    }


    public static void stopRequestingLocationUpdates(Context context, LocationListener locationListener){
        LocationManager locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
    }


}
