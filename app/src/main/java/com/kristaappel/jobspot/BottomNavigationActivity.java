package com.kristaappel.jobspot;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kristaappel.jobspot.fragments.AppliedJobsFragment;
import com.kristaappel.jobspot.fragments.MapFragment;
import com.kristaappel.jobspot.fragments.ProfileFragment;
import com.kristaappel.jobspot.fragments.SavedJobsFragment;
import com.kristaappel.jobspot.fragments.SavedSearchListFragment;
import com.kristaappel.jobspot.fragments.SearchBoxFragment;
import com.kristaappel.jobspot.fragments.SearchResultListFragment;
import com.kristaappel.jobspot.fragments.SearchScreenFragment;
import com.kristaappel.jobspot.fragments.SortFilterFragment;
import com.kristaappel.jobspot.objects.FileUtil;
import com.kristaappel.jobspot.objects.Job;
import com.kristaappel.jobspot.objects.LocationHelper;
import com.kristaappel.jobspot.objects.NetworkMonitor;
import com.kristaappel.jobspot.objects.NotificationBroadcastReceiver;
import com.kristaappel.jobspot.objects.SavedSearch;
import com.kristaappel.jobspot.objects.VolleySingleton;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class BottomNavigationActivity extends AppCompatActivity implements SearchBoxFragment.OnSearchBoxFragmentInteractionListener, SortFilterFragment.OnSortFilterInteractionListener, SavedSearchListFragment.OnSavedSearchInteractionListener {

    private ActionBar actionBar;
    private static String keywords;
    private static String location;
    private String radius = "20";
    private String posted = "30";
    private static String liPictureURL = "";
    private static String liName = "";
    private static String liHeadline = "";
    private static String liLocation = "";
    private static String liIndustry = "";
    private static String liSummary = "";
    private static Double jobLat;
    private static Double jobLng;
    private static final ArrayList<Job> jobList = new ArrayList<>();
    public static boolean mapIsShowing = true;
    public static boolean listIsShowing = false;
    public static String sortBy = "accquisitiondate";
    private static SavedSearch savedSearch;


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

        // Start notifications if they are enabled:
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.kristaappel.jobspot.preferences", Context.MODE_PRIVATE);
        String notificationPreference = sharedPreferences.getString("notifications", "on");
        if (notificationPreference.equals("on")){
            NotificationBroadcastReceiver notificationReceiver = new NotificationBroadcastReceiver();
            notificationReceiver.setAlarm(this);
        }

    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            EditText et_keywords = (EditText) findViewById(R.id.et_keywords);
            EditText et_location = (EditText) findViewById(R.id.et_location);
            // Get user's input data & make sure it's not empty:
            if (et_location != null && et_keywords != null){
                if (et_location.getText().toString().length() >0){
                    location = et_location.getText().toString();
                }
                if (et_keywords.getText().toString().length() >0){
                    keywords = et_keywords.getText().toString();
                }
            }

            switch (item.getItemId()) {
                case R.id.navigation_search:
                    // Create and display a SearchScreenFragment:
                    SearchScreenFragment searchScreenFrag;
                    if (jobList != null){
                        searchScreenFrag = SearchScreenFragment.newInstance(jobList);
                    }else if (location != null && keywords != null) {
                        searchScreenFrag = SearchScreenFragment.newInstance(location, keywords);
                    }else{
                        searchScreenFrag = new SearchScreenFragment();
                    }
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
                    ProfileFragment profileFrag;
                    if (!liPictureURL.equals("")){
                        profileFrag = ProfileFragment.newInstance(liName, liPictureURL, liHeadline, liLocation, liIndustry, liSummary);
                    }else{
                        profileFrag = new ProfileFragment();
                    }
                    getFragmentManager().beginTransaction().replace(R.id.content, profileFrag).commit();
                    // Show actionbar:
                    if (actionBar != null){
                        actionBar.show();
                        actionBar.setTitle("Profile");
                    }
                    return true;
            }
            return false;
        }

    };



    @Override
    public void onSearchBoxFragmentInteraction(int id) {
        Button mapButton = (Button) findViewById(R.id.mapFragToggle1);
        Button listButton = (Button) findViewById(R.id.mapFragToggle2);
        EditText et_keywords = (EditText) findViewById(R.id.et_keywords);
        EditText et_location = (EditText) findViewById(R.id.et_location);

        switch (id){
            case R.id.mapFragToggle1:
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0x01001);
                }else{
                    // Create and display a MapFragment in bottom container:
                    MapFragment mapFrag;
                    if (jobList != null){
                        mapFrag = MapFragment.newInstance(jobList);
                    }else{
                        mapFrag = new MapFragment();
                    }
                    getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, mapFrag).commit();
                    mapIsShowing = true;
                    listIsShowing = false;
                    // Set buttons to appropriate colors:
                    mapButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mapButton.setTextColor(getResources().getColor(R.color.colorWhite));
                    listButton.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
                    listButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                break;
            case R.id.mapFragToggle2:
                // Create and display a SearchResultListFragment in bottom container:
                SearchResultListFragment searchResultListFrag = SearchResultListFragment.newInstance(jobList);
                getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, searchResultListFrag).commit();
                mapIsShowing = false;
                listIsShowing = true;
                // Set buttons to appropriate colors:
                mapButton.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
                mapButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                listButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                listButton.setTextColor(getResources().getColor(R.color.colorWhite));
                break;
            case R.id.locationButton:
                // Get the user's current location and enter it into the location box:
                Location currentLocation = LocationHelper.getCurrentLocation(getApplicationContext(), new MapFragment());
                if (currentLocation != null){
                    Address currentAddress = LocationHelper.getCurrentAddressFromLocation(currentLocation, getApplicationContext());
                    et_location.setText(currentAddress.getLocality() + ", " + currentAddress.getAdminArea()); // This shows city, state
                }else{
                    Toast.makeText(this, "Location not found.  Please make sure location services are enabled.", Toast.LENGTH_SHORT).show();
                }
                
                break;
            case R.id.filterButton:
                //Show a list of filter & sort options, use chosen options in job search
                SortFilterFragment sortFilterFragment = new SortFilterFragment();
                getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, sortFilterFragment).commit();
                InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(et_keywords.getWindowToken(), 0);
                break;
            case R.id.recentButton:
                // Show recent searches and let user pick one to search
                SavedSearchListFragment savedSearchListFragment = new SavedSearchListFragment();
                getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, savedSearchListFragment).commit();
                break;
            case R.id.searchButton:
                jobList.clear();
                // Set appropriate button colors:
                if (mapIsShowing){
                    mapButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mapButton.setTextColor(getResources().getColor(R.color.colorWhite));
                    listButton.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
                    listButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                }else{
                    mapButton.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
                    mapButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    listButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    listButton.setTextColor(getResources().getColor(R.color.colorWhite));
                }
                // Get user's input data & make sure it's not empty:
                if (et_location.getText().toString().length() >0){
                    location = et_location.getText().toString();
                }
                if (et_keywords.getText().toString().length() >0){
                    keywords = et_keywords.getText().toString();
                }

                if (location==null || location.equals("")){
                    Toast.makeText(this, "Enter a location", Toast.LENGTH_SHORT).show();
                }else if  (keywords==null || (keywords.equals(""))){
                    Toast.makeText(this, "Enter keywords", Toast.LENGTH_SHORT).show();
                }else{
                    searchForJobs(keywords, location);
                }
                InputMethodManager inputMethodManager2 = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager2.hideSoftInputFromWindow(et_keywords.getWindowToken(), 0);
                break;
        }
    }


    public static void getCoordsForCompany(final Activity activity, String response, Job foundJob){
        try {
            // Parse JSON results from company look-up to get coordinates:
            JSONObject responseObject = new JSONObject(response);
            JSONArray resultArray = responseObject.getJSONArray("results");
            if (resultArray.length()>0){
                JSONObject resultObj = resultArray.getJSONObject(0);
                JSONObject geometryObject = resultObj.getJSONObject("geometry");
                JSONObject locationObject = geometryObject.getJSONObject("location");
                jobLat = locationObject.getDouble("lat");
                jobLng = locationObject.getDouble("lng");
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        // Use coords with job data that was already retrieved to create a Job object:
        Job newJob = null;
        if (foundJob != null && jobLat != null && jobLng != null) {
            newJob = new Job(foundJob.getJobID(), foundJob.getJobTitle(), foundJob.getCompanyName(), foundJob.getDatePosted(), foundJob.getJobURL(), foundJob.getJobCityState(), jobLat, jobLng);
        }else{
            Log.i("BottomNav:321", "foundjob is nul!!!!!!!!!!");
        }
        // Add new job to the list of jobs:
        if (newJob != null){
            jobList.add(newJob);
        }
        Log.i("BottomNavActivity:327", "joblistcount:" + jobList.size());

        // Sort jobs appropriately:
        if (sortBy.equals("location") || sortBy.equals("accquisitiondate")){
            Collections.sort(jobList, new Comparator<Job>() {
                @Override
                public int compare(Job job1, Job job2) {
                    if (sortBy.equals("location")){
                        // Sort by distance:
                        return job1.getDistance(activity, job1).compareTo(job2.getDistance(activity, job2));
                    }else if (sortBy.equals("accquisitiondate")){
                        // Sort by date:
                        return job2.getDatePosted().compareToIgnoreCase(job1.getDatePosted());
                    }
                    return 0;
                }
            });
        }

        if (sortBy.equals("accquisitiondate")){
            Collections.sort(jobList, new Comparator<Job>() {
                final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
                @Override
                public int compare(Job job1, Job job2) {
                    try{
                        return dateTimeFormat.parse(job2.getDatePosted()).compareTo(dateTimeFormat.parse(job1.getDatePosted()));
                    }catch (ParseException e){
                        throw new IllegalArgumentException(e);
                    }
                }
            });
        }

        if (mapIsShowing){
            // Create and display a MapFragment in bottom container:
            MapFragment mapFrag;
            if (jobList != null){
                mapFrag = MapFragment.newInstance(jobList);
            }else{
                if (savedSearch != null){
                    mapFrag = MapFragment.newInstance(savedSearch.getLocation(), savedSearch.getKeywords());
                }else{
                    mapFrag = new MapFragment();
                }
            }
            activity.getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, mapFrag).commit();
        }else if (listIsShowing){
            // Create and display a SearchResultListFragment in bottom container:
            SearchResultListFragment listfrag;
            if (jobList != null){
                listfrag = SearchResultListFragment.newInstance(jobList);
            }else{
                listfrag = new SearchResultListFragment();
            }
            activity.getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, listfrag).commit();
        }

    }


    private void searchForJobs(String _keywords, String _location){
        if (!NetworkMonitor.deviceIsConnected(this)){
            Toast.makeText(this, "No connection.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Save the search:
        String time = String.valueOf(System.currentTimeMillis());
        SavedSearch newSavedSearch = new SavedSearch(_keywords, radius, _location, posted, time);
        saveTheSearch(newSavedSearch);
        // Show progress bar:
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        //Get the search radius, sort type, & number of days from user and put it into the job search url:
        String url = "https://api.careeronestop.org/v1/jobsearch/TZ1zgEyKTNm69nF/"+_keywords+"/"+_location+"/"+radius+"/"+sortBy+"/desc/0/120/"+posted;
        // Get jobs from API:
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("BottomNavActvty:405", "response: " + response);
                makeJobList(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("BottomNav:411", "That didn't work!!!!!!!");
                if (error.networkResponse.statusCode == 404){
                    Toast.makeText(BottomNavigationActivity.this, "No jobs available.", Toast.LENGTH_SHORT).show();
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

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void saveTheSearch(SavedSearch search){
        // Get saved searches from device:
        ArrayList<SavedSearch> savedSearches = FileUtil.readSavedSearches(this);
        // Find out if the search is already saved:
        boolean foundMatch = false;
        for (int i=0; i<savedSearches.size(); i++){
            if (savedSearches.get(i).equals(search)){
                foundMatch = true;
            }
        }
        // If the search isn't saved, then save it:
        if (!foundMatch){
            savedSearches.add(search);
            // Save savedSearch to device:
            FileUtil.writeSavedSearch(this, savedSearches);

            // Save the search to Firebase:
            Firebase firebase = new Firebase("https://jobspot-a0171.firebaseio.com/");
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if (firebaseUser != null) {
                firebase.child("users").child(firebaseUser.getUid()).child("savedsearches").child(search.getDateTime()).setValue(search);
            }
        }
    }

    private void makeJobList(String searchResponse){
        // Parse JSON to make a list of Job objects:
        try {
            JSONObject responseObj = new JSONObject(searchResponse);
            JSONArray jobsArray = responseObj.getJSONArray("Jobs");
            for (int i = 0; i<jobsArray.length(); i++){
                JSONObject jobObj = jobsArray.getJSONObject(i);
                String jobid = jobObj.getString("JvId");
                String jobtitle = jobObj.getString("JobTitle");
                String companyname = jobObj.getString("Company");
                String dateposted = jobObj.getString("AccquisitionDate");
                String joburl = jobObj.getString("URL");
                String jobcitystate = jobObj.getString("Location");
                Job foundJob = new Job(jobid, jobtitle, companyname, dateposted, joburl, jobcitystate, 0, 0);
                // Get the coordinates:
                LocationHelper.lookUpCompany(this, foundJob);
            }
            Log.i("BottomNavActivity:473", "jobs count: " + jobsArray.length());
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

    @Override
    public void onSortFilterInteraction(String _radius, String _posted, String _sortBy) {
        // Get user inputs from radio buttons and apply them to search:
        radius = _radius;
        posted = _posted;
        sortBy = _sortBy;

        onSearchBoxFragmentInteraction(R.id.searchButton);
    }

    @Override
    public void onsavedSearchInteraction(SavedSearch savedSearch) {
        BottomNavigationActivity.savedSearch = savedSearch;

        keywords = savedSearch.getKeywords();
        radius = savedSearch.getRadius();
        location = savedSearch.getLocation();
        posted = savedSearch.getDays();

        EditText et_loc = (EditText) findViewById(R.id.et_location);
        EditText et_kw = (EditText) findViewById(R.id.et_keywords);

        et_loc.setText(location);
        et_kw.setText(keywords);

        onSearchBoxFragmentInteraction(R.id.searchButton);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // App returns here after LinkedIn connection is made.  Get data from LinkedIn API:
        Log.i("LINKEDIN", "onActivityResult from activity");

        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        Log.i("LINKEDIN", "NOW it's a success");

        String url = "https://api.linkedin.com/v1/people/~:(formatted-name,email-address,headline,location,industry,summary,picture-url)?format=json"; //:(email-address,formatted-name, phone-numbers, picture-urls::(original))";
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retrieving Data...");
        progressDialog.show();

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                String liEmail = "";
                liIndustry = "";

                Log.i("LINKEDIN", "response: " + apiResponse);
                JSONObject responseObject = apiResponse.getResponseDataAsJson();
                Log.i("LINKEDIN", "response as json: " + responseObject);
                try {
                    liName = responseObject.getString("formattedName");
                    liEmail = responseObject.getString("emailAddress");
                    liHeadline = responseObject.getString("headline");
                    liIndustry = responseObject.getString("industry");
                    JSONObject liLocationObject = responseObject.getJSONObject("location");
                    liLocation = liLocationObject.getString("name");
                    liPictureURL = responseObject.getString("pictureUrl");
                    liSummary = responseObject.getString("summary");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BottomNavigationActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                }
                Log.i("LINKEDIN", "name: " + liName);
                Log.i("LINKEDIN", "email: " + liEmail);
                Log.i("LINKEDIN", "headline: " + liHeadline);
                Log.i("LINKEDIN", "industry: " + liIndustry);
                Log.i("LINKEDIN", "location: " + liLocation);
                Log.i("LINKEDIN", "picture url: " + liPictureURL);
                Log.i("LINKEDIN", "summary: " + liSummary);

                ProfileFragment.displayLinkedInData(BottomNavigationActivity.this, liPictureURL, liHeadline, liLocation, liIndustry, liSummary);

            }

            @Override
            public void onApiError(LIApiError error) {
                Log.i("LINKEDIN", "onApiError");
                error.printStackTrace();
            }
        });
        progressDialog.dismiss();

    }

}
