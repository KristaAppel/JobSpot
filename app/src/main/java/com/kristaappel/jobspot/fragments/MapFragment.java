package com.kristaappel.jobspot.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kristaappel.jobspot.Manifest;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.LocationHelper;

import java.io.IOException;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

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
    private final double jobLat = 28.590647; //TODO: change to job location's latitude
    private final double jobLong = -81.304510; //TODO: change to job location's longitude

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(View v) {
        if (mListener != null) {
            mListener.onFragmentInteraction(v);
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

        currentLocation = LocationHelper.getCurrentLocation(getContext(), this);
        if (currentLocation == null){
            // No last known location.  Begin requesting location updates:
            LocationHelper.requestLocationUpdates(getContext(), this);
        }else{
            // Get current address:
            currentAddress = LocationHelper.getCurrentAddress(currentLocation, getContext());
            // Display current address in the location box:
            EditText et_location = (EditText) getActivity().findViewById(R.id.et_location);
            et_location.setText(currentAddress.getAddressLine(0)); // This shows full address
            //et_location.setText(currentAddress.getLocality() + ", " + currentAddress.getAdminArea()); // This shows city, state

            // Update map:
            zoomInCamera();
            googleMap.clear();
            addMapMarkers();
        }
    }


    private void zoomInCamera(){
        if (googleMap != null){
            LatLng zoomToLatLong = new LatLng(currentAddress.getLatitude(), currentAddress.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(zoomToLatLong, 14);
            googleMap.animateCamera(cameraUpdate);
        }

    }

    private void addMapMarkers(){
        if (googleMap != null){
            // TODO: add a marker for each job in the area:
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title("Company Name");
            markerOptions.snippet("Job Title");
            LatLng jobLocation = new LatLng(jobLat, jobLong); //TODO: change to the job's location
            markerOptions.position(jobLocation);
            googleMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //TODO: Go  to the job detail page for the clicked job
        Log.i("MapFragment", "Clicked on " + marker.getTitle());
    }


    // 4 LocationListener Callbacks:

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        currentAddress = LocationHelper.getCurrentAddress(location, getContext());
        Log.i("MapFragment", "onLocationChanged: " + currentAddress);
        // Display current address in the location box:
        EditText et_location = (EditText) getActivity().findViewById(R.id.et_location);
        et_location.setText(currentAddress.getAddressLine(0)); // This shows full address
        //et_location.setText(currentAddress.getLocality() + ", " + currentAddress.getAdminArea()); // This shows city, state

//        chosenLocationListener.handleChosenLocation(currentLocation.getLatitude(), currentLocation.getLongitude());

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
