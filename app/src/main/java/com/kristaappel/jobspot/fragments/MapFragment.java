package com.kristaappel.jobspot.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.Job;
import com.kristaappel.jobspot.objects.LocationHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends com.google.android.gms.maps.MapFragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnInfoWindowClickListener{

    private GoogleMap googleMap;
    private Address currentAddress;
    private Location currentLocation;
    private ArrayList<Job> jobs;
    private static String displayLocation;
    private static String displayKeywords;
    private static final String ARG_PARAM1 = "param1";


    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */

    public static MapFragment newInstance(ArrayList<Job> joblist) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, joblist);
        fragment.setArguments(args);
        return fragment;
    }

    public static MapFragment newInstance(String _location, String _keywords){
        MapFragment fragment = new MapFragment();
        displayLocation = _location;
        displayKeywords = _keywords;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobs = getArguments().getParcelableArrayList(ARG_PARAM1);
        }
    }


    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0x01001);

        }
        getMapAsync(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (googleMap != null){
            // Reload the map markers:
            googleMap.clear();
            addMapMarkers();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getArguments() != null) {
            jobs = getArguments().getParcelableArrayList(ARG_PARAM1);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        LocationHelper.stopRequestingLocationUpdates(getContext(), this);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (googleMap == null){
            googleMap = map;
        }

        setAdapter();

        googleMap.setOnInfoWindowClickListener(this);

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        if (displayLocation != null){
            // Display the location and keywords from the search in the editTexts:
            EditText et_loc = (EditText) getActivity().findViewById(R.id.et_location);
            EditText et_kw = (EditText) getActivity().findViewById(R.id.et_keywords);
            et_loc.setText(displayLocation);
            et_kw.setText(displayKeywords);

            // Update map:
            zoomInCamera();
            googleMap.clear();
            addMapMarkers();
        }else{
            currentLocation = LocationHelper.getCurrentLocation(getContext(), this);
            if (currentLocation == null){
                // No last known location.  Begin requesting location updates:
                LocationHelper.requestLocationUpdates(getContext(), this);
            }else{
                // Get current address:
                currentAddress = LocationHelper.getCurrentAddressFromLocation(currentLocation, getContext());
                // Display current address in the location box:
                EditText et_location = (EditText) getActivity().findViewById(R.id.et_location);
                //et_location.setText(currentAddress.getAddressLine(0)); // This shows full address
                if (et_location.getText().toString().length()<1){
                    if (jobs==null || jobs.size()<1){
                        // If there are no searched jobs, show the current location in the location box:
                        et_location.setText(currentAddress.getPostalCode()); // This shows zip code
                    }else{
                        // If there are job search results, show the location of the searched jobs in the location box:
                        if (jobs.size()>1){
                            et_location.setText(jobs.get(0).getJobCityState());
                        }
                    }
                }
                // Update map:
                zoomInCamera();
                googleMap.clear();
                addMapMarkers();
            }
        }
        ProgressBar progressBar = (ProgressBar)getActivity().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }


    private void setAdapter(){
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Create custom layout for Info Window so we can have a multiline snippet:
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getContext());
                title.setTextColor(getResources().getColor(R.color.colorBlack));
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setTextSize(18);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setGravity(Gravity.CENTER);
                snippet.setText(marker.getSnippet());

                linearLayout.addView(title);
                linearLayout.addView(snippet);
                return linearLayout;
            }
        });
    }


    private void zoomInCamera(){
        if (googleMap != null){
            LatLng zoomToLatLong = null;
            String enteredLocation;
            EditText et_location = (EditText) getActivity().findViewById(R.id.et_location);
            enteredLocation = et_location.getText().toString();
            if (!enteredLocation.equals("")){
                // Get entered location:
                Geocoder gc = new Geocoder(getContext());
                try {
                    List<Address> address = gc.getFromLocationName(enteredLocation, 1);
                    Address place = address.get(0);
                    zoomToLatLong = new LatLng(place.getLatitude(), place.getLongitude());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                // Get current location:
                zoomToLatLong = new LatLng(currentAddress.getLatitude(), currentAddress.getLongitude());
            }

            // Zoom:
            if (zoomToLatLong != null){
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(zoomToLatLong, 10);
             //   googleMap.animateCamera(cameraUpdate);
                googleMap.moveCamera(cameraUpdate);
            }
        }

    }

    private void addMapMarkers(){
        if (googleMap != null){
            // Add a marker for each job in the area:
            if (jobs != null){
                for (Job job : jobs){
                    if (job.getJobLat() != 0 && job.getJobLng() != 0 && job.getCompanyName() != null && job.getJobTitle() != null){
                        LatLng jobPosition = new LatLng(job.getJobLat(), job.getJobLng());
                        googleMap.addMarker(new MarkerOptions().position(jobPosition).title(job.getJobTitle()).snippet(job.getCompanyName() + "\n" + job.getDistance(getContext(), job) + " miles away\n" + "Posted on: " + job.getDatePosted()));
                    }
                }
    }

        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.i("MapFragment", "Clicked on " + marker.getTitle());

        // Find the selected job in the jobs list:
        Job selectedJob = null;
        for (Job job : jobs){
            if (job.getJobTitle().equals(marker.getTitle())){
                selectedJob = job;
            }
        }

        if (selectedJob != null){
            //Create and display a JobInfoFragment for the selected job:
            JobInfoFragment jobInfoFragment = JobInfoFragment.newInstance(selectedJob);
            getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, jobInfoFragment).commit();
        }
    }


    // 4 LocationListener Callbacks:

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        currentAddress = LocationHelper.getCurrentAddressFromLocation(location, getContext());
        Log.i("MapFragment", "onLocationChanged: " + currentAddress);
        // Display current address in the location box:
        EditText et_location = (EditText) getActivity().findViewById(R.id.et_location);
        et_location.setText(currentAddress.getAddressLine(0)); // This shows full address
        //et_location.setText(currentAddress.getLocality() + ", " + currentAddress.getAdminArea()); // This shows city, state

        zoomInCamera();
        LocationHelper.stopRequestingLocationUpdates(getContext(), this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
