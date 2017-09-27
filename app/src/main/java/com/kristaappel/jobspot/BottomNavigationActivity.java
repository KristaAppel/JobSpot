package com.kristaappel.jobspot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.kristaappel.jobspot.fragments.AppliedJobsFragment;
import com.kristaappel.jobspot.fragments.MapFragment;
import com.kristaappel.jobspot.fragments.ProfileFragment;
import com.kristaappel.jobspot.fragments.SavedJobsFragment;
import com.kristaappel.jobspot.fragments.SearchBoxFragment;
import com.kristaappel.jobspot.fragments.SearchResultListFragment;
import com.kristaappel.jobspot.fragments.SearchScreenFragment;
import com.kristaappel.jobspot.objects.Job;
import com.kristaappel.jobspot.objects.LocationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class BottomNavigationActivity extends AppCompatActivity implements SearchBoxFragment.OnSearchBoxFragmentInteractionListener {

    ActionBar actionBar;
    Button mapButton;
    Button listButton;
    EditText et_location;
    EditText et_keywords;
    String keywords;
    String location;
    static Double jobLat;
    static Double jobLng;
    static String jobid;
    static String jobtitle;
    static String companyname;
    static String dateposted;
    static String joburl;
    static String jobcitystate;
    static LatLng joblatLng;
    static ArrayList<Job> jobList = new ArrayList<>();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    // Create and display a SearchScreenFragment:
                    SearchScreenFragment searchScreenFrag = new SearchScreenFragment();
                    getFragmentManager().beginTransaction().replace(R.id.content, searchScreenFrag).commit();
                    // Hide actionbar:
                    if (actionBar != null){
                        actionBar.hide();
                    }
                    return true;
                case R.id.navigation_saved:
                    // Create and display a SavedJobsFragment:
                    SavedJobsFragment savedFrag = new SavedJobsFragment();
                    getFragmentManager().beginTransaction().replace(R.id.content, savedFrag).commit();
                    // Show actionbar:
                    if (actionBar != null){
                        actionBar.show();
                        actionBar.setTitle("Saved Jobs");
                    }
                    return true;
                case R.id.navigation_applied:
                    // Create and display an AppliedJobsFragment:
                    AppliedJobsFragment appliedFrag = new AppliedJobsFragment();
                    getFragmentManager().beginTransaction().replace(R.id.content, appliedFrag).commit();
                    // Show actionbar:
                    if (actionBar != null){
                        actionBar.show();
                        actionBar.setTitle("Jobs You've Applied For");
                    }
                    return true;
                case R.id.navigation_account:
                    // Create and display a ProfileFragment:
                    ProfileFragment profileFrag = new ProfileFragment();
                    getFragmentManager().beginTransaction().replace(R.id.content, profileFrag).commit();
                    // Show actionbar:
                    if (actionBar != null){
                        actionBar.show();
                        actionBar.setTitle("Preferences");
                    }
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottomnavigation);

        actionBar = getSupportActionBar();

        // Hide actionbar:
        if (actionBar != null){
            actionBar.hide();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Create and display a SearchScreenFragment:
        SearchScreenFragment searchScreenFrag = new SearchScreenFragment();
        getFragmentManager().beginTransaction().replace(R.id.content, searchScreenFrag).commit();

    }


    @Override
    public void onSearchBoxFragmentInteraction(int id) {
        mapButton = (Button) findViewById(R.id.mapFragToggle1);
        listButton = (Button) findViewById(R.id.mapFragToggle2);
        et_keywords = (EditText) findViewById(R.id.et_keywords);
        et_location= (EditText) findViewById(R.id.et_location);

        switch (id){
            case R.id.mapFragToggle1:
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0x01001);
                }else{
                    // Create and display a MapFragment in bottom container:
                    MapFragment mapFrag = new MapFragment();
                    getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, mapFrag).commit();
                    // Set buttons to appropriate colors:
                    mapButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mapButton.setTextColor(getResources().getColor(R.color.colorWhite));
                    listButton.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
                    listButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                break;
            case R.id.mapFragToggle2:
                // Create and display a ResultsListFragment in bottom container:
                SearchResultListFragment searchResultListFrag = new SearchResultListFragment();
                getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, searchResultListFrag).commit();
                // Set buttons to appropriate colors:
                mapButton.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
                mapButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                listButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                listButton.setTextColor(getResources().getColor(R.color.colorWhite));
                break;
            case R.id.locationButton:
                // TODO: find nearby jobs for the location, show them on map/list.
                Location currentLocation = LocationHelper.getCurrentLocation(getApplicationContext(), new MapFragment());
                Address currentAddress = LocationHelper.getCurrentAddressFromLocation(currentLocation, getApplicationContext());
                et_location.setText(currentAddress.getLocality() + ", " + currentAddress.getAdminArea()); // This shows city, state
                break;
            case R.id.filterButton:
                // TODO: show a list of filter & sort options, use chosen options in job search
                Log.i("TAG", "show filter/sort options");
                break;
            case R.id.searchButton:
                // Get user's input data & make sure it's not empty:
                if (et_location.getText().toString().length() >0){
                    location = et_location.getText().toString();
                }
                if (et_keywords.getText().toString().length() >0){
                    keywords = et_keywords.getText().toString();
                }
                Log.i("BottomNavActivity", "Entered location: " + location + " Entered keywords: " + keywords);
                if (location==null || location.equals("")){
                    Toast.makeText(this, "Enter a location", Toast.LENGTH_SHORT).show();
                }else if  (keywords==null || (keywords.equals(""))){
                    Toast.makeText(this, "Enter keywords", Toast.LENGTH_SHORT).show();
                }else{
                    searchForJobs(keywords, location);
                    //TODO: display jobs on map & list
                }

        }

    }

    public static LatLng getCoordsForCompany(String response){
        LatLng coords = null;
        try {
            // Parse JSON results from company look-up to get coordinates:
            JSONObject responseObject = new JSONObject(response);
            JSONArray resultArray = responseObject.getJSONArray("results");
            JSONObject resultObj = resultArray.getJSONObject(0);
            JSONObject geometryObject = resultObj.getJSONObject("geometry");
            JSONObject locationObject = geometryObject.getJSONObject("location");
            jobLat = locationObject.getDouble("lat");
            jobLng = locationObject.getDouble("lng");
            coords = new LatLng(jobLat, jobLng);
            Log.i("BottomNavigActivity:220", "coords: " + coords);
        }catch (JSONException e){
            e.printStackTrace();
        }
        joblatLng = coords;

        // Use coords with job data that was already retrieved to create a Job object:
        Job newJob = new Job(jobid, jobtitle, companyname, dateposted, joburl, jobcitystate, joblatLng);
        jobList.add(newJob);
        Log.i("BottomNavActivity:229", "joblistcount:" + jobList.size());
        return coords;
    }


    private void searchForJobs(String keywords, String location){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //TODO: get the distance, sort type, etc from user and put it into the url
        String url = "https://api.careeronestop.org/v1/jobsearch/TZ1zgEyKTNm69nF/"+keywords+"/"+location+"/25/accquisitiondate/desc/0/10/30";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("BottomNavigationActvty", "response: " + response);
                makeJobList(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("BottomNavigationActvty", "That didn't work!!!!!!!");
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
        requestQueue.add(stringRequest);
    }

    public void makeJobList(String searchResponse){
        // Parse JSON to make a list of Job objects:
        try {
            JSONObject responseObj = new JSONObject(searchResponse);
            JSONArray jobsArray = responseObj.getJSONArray("Jobs");
            for (int i = 0; i<jobsArray.length(); i++){
                JSONObject jobObj = jobsArray.getJSONObject(i);
                jobid = jobObj.getString("JvId");
                jobtitle = jobObj.getString("JobTitle");
                companyname = jobObj.getString("Company");
                dateposted = jobObj.getString("AccquisitionDate");
                joburl = jobObj.getString("URL");
                jobcitystate = jobObj.getString("Location");
                // Get the coordinates:
                LocationHelper.lookUpCompany(this, companyname, jobcitystate);
            }
            Log.i("BottomNavActivity:278", "jobs count: " + jobsArray.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0x01001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Create and display a MapFragment in bottom container:
            MapFragment mapFrag = new MapFragment();
            getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, mapFrag).commit();

        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This application requires access to your location.")
                    .setPositiveButton("OK", null)
                    .show();
        }

    }
}
