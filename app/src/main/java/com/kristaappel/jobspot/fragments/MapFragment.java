package com.kristaappel.jobspot.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

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

/**
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MapFragment extends com.google.android.gms.maps.MapFragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnInfoWindowClickListener{

    private GoogleMap googleMap;
    private Geocoder geocoder;
    private Address currentAddress;
    private Location currentLocation;
    private ArrayList<Job> jobs;

    private static final String ARG_PARAM1 = "param1";

    private OnFragmentInteractionListener mListener;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(ArrayList<Job> joblist) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, joblist);
        fragment.setArguments(args);
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

        geocoder = new Geocoder(getContext());
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnSearchBoxFragmentInteractionListener");
        }
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
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnInfoWindowClickListener(this);

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

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
            if (et_location.getText().toString().equals("") || et_location.getText()==null){
                if (jobs==null || jobs.size()<1){
                    // If there are no searched jobs, show the current location in the location box:
                    et_location.setText(currentAddress.getLocality() + ", " + currentAddress.getAdminArea()); // This shows city, state
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


    private void zoomInCamera(){
        if (googleMap != null){
            LatLng zoomToLatLong = null;
            String enteredLocation = "";
            EditText et_location = (EditText) getActivity().findViewById(R.id.et_location);
            enteredLocation = et_location.getText().toString();
            if (!enteredLocation.equals("")){
                // Zoom to entered location:
                Geocoder gc = new Geocoder(getContext());
                try {
                    List<Address> address = gc.getFromLocationName(enteredLocation, 1);
                    Address place = address.get(0);
                    Log.i("MapFragment", "the place's address is: " + place.toString());
                    Log.i("MapFragment", "the place's latitude is: " + place.getLatitude());
                    Log.i("MapFragment", "the place's longtiide is: " + place.getLongitude());
                    zoomToLatLong = new LatLng(place.getLatitude(), place.getLongitude());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                // Zoom to current location:
                zoomToLatLong = new LatLng(currentAddress.getLatitude(), currentAddress.getLongitude());
            }

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(zoomToLatLong, 13);
            googleMap.animateCamera(cameraUpdate);
        }

    }

    private void addMapMarkers(){
        if (googleMap != null){
//            MarkerOptions markerOptions = new MarkerOptions();
            // Add a marker for each job in the area:
            if (jobs != null){
                for (Job job : jobs){
                    Log.i("MapFragment", "coords in mapfrag: " + job.getJobLat() + ", " + job.getJobLng());
                    if (job.getJobLat() != 0 && job.getJobLng() != 0 && job.getCompanyName() != null && job.getJobTitle() != null){
                        LatLng jobPosition = new LatLng(job.getJobLat(), job.getJobLng());
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title(job.getJobTitle());
                        markerOptions.snippet(job.getCompanyName());
                        markerOptions.position(jobPosition);
                        googleMap.addMarker(markerOptions);
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(View v);
    }
}
