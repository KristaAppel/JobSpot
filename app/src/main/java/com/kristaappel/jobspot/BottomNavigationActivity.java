package com.kristaappel.jobspot;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
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
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.kristaappel.jobspot.R.string.search;
import static com.kristaappel.jobspot.fragments.ProfileFragment.linkedInAccessToken;
import static com.kristaappel.jobspot.fragments.ProfileFragment.linkedInError;


public class BottomNavigationActivity extends AppCompatActivity implements SearchBoxFragment.OnSearchBoxFragmentInteractionListener, SearchScreenFragment.OnSearchMenuInteractionListener, SortFilterFragment.OnSortFilterInteractionListener, SavedSearchListFragment.OnSavedSearchInteractionListener {

    private ActionBar actionBar;
    public static String keywords;
    public static String location;
    public static String radius = "20";
    public static String posted = "30";
    private static String liPictureURL = "";
    private static String liName = "";
    private static String liEmail = "";
    private static String liHeadline = "";
    private static String liLocation = "";
    private static String liSummary = "";
    private static Double jobLat;
    private static Double jobLng;
    private static final ArrayList<Job> jobList = new ArrayList<>();
    public static boolean mapIsShowing = true;
    public static boolean listIsShowing = false;
    public static String sortBy = "accquisitiondate";
    private static SavedSearch savedSearch;
    private AdView adView;
    private static boolean isTablet = false;
    SortFilterFragment sortFilterFragment;
    public static int maxJobs = 30;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottomnavigation);

        Log.i("BottomNavActivity", "istablet: " + getResources().getBoolean(R.bool.is_tablet));
        if (getResources().getBoolean(R.bool.is_tablet)){
            // It's a tablet.  Show in landscape:
            isTablet = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }else{
            // It's a phone.  Show in portrait:
            isTablet = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        actionBar = getSupportActionBar();

        if (actionBar != null){
            actionBar.show();
        }

        MobileAds.initialize(this, "ca-app-pub-3536941884869320~4191396929");

        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("08940364D47932550C2D91C5209F820D")
                .build();
        adView.loadAd(adRequest);
        adView.setVisibility(View.GONE);

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
                    // Show actionbar:
                    if (actionBar!= null){
                        actionBar.show();
                        actionBar.setTitle("Job Search");
                    }
                    adView.setVisibility(View.GONE);
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
                    adView.setVisibility(View.VISIBLE);
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
                    adView.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_account:
                    // Create and display a ProfileFragment:
                    ProfileFragment profileFrag;
                    if (!liName.equals("")){
           //         if (!liPictureURL.equals("")){
                        profileFrag = ProfileFragment.newInstance(liName, liEmail, liPictureURL, liHeadline, liLocation, liSummary);
                    }else{
                        profileFrag = new ProfileFragment();
                    }
                    getFragmentManager().beginTransaction().replace(R.id.content, profileFrag).commit();
                    // Show actionbar:
                    if (actionBar != null){
                        actionBar.show();
                        actionBar.setTitle("Profile");
                    }
                    adView.setVisibility(View.GONE);
                    return true;
            }
            return false;
        }

    };


    @Override
    public void onSearchBoxFragmentInteraction(int id) {
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
                    toggleButtonColors();
                    adView.setVisibility(View.GONE);
                }
                break;
            case R.id.mapFragToggle2:
                // Create and display a SearchResultListFragment in bottom container:
                SearchResultListFragment searchResultListFrag = SearchResultListFragment.newInstance(jobList);
                getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, searchResultListFrag).commit();
                mapIsShowing = false;
                listIsShowing = true;
                toggleButtonColors();
                adView.setVisibility(View.VISIBLE);
                break;
            case R.id.locationButton:
                // Get the user's current location and enter it into the location box:
                Location currentLocation = LocationHelper.getCurrentLocation(getApplicationContext(), new MapFragment());
                if (currentLocation != null){
                    Address currentAddress = LocationHelper.getCurrentAddressFromLocation(currentLocation, getApplicationContext());
                    et_location.setText(currentAddress.getPostalCode() ); // This shows current zip code
                }else{
                    Toast.makeText(this, "Location not found.  Please make sure location services are enabled.", Toast.LENGTH_SHORT).show();
                }
                
                break;
            case R.id.filterButton:
                jobList.clear();
                //Show a list of filter & sort options, use chosen options in job search
                sortFilterFragment = new SortFilterFragment();
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
                toggleButtonColors();
                // Get user's input data & make sure it's not empty:
                if (et_location.getText().toString().length() >0){
                    location = et_location.getText().toString().trim();
                }
                if (et_keywords.getText().toString().length() >0){
                    keywords = et_keywords.getText().toString().trim();
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

    private void toggleButtonColors(){
        // Show appropriate colors for the map/list buttons:
        Button mapButton = (Button) findViewById(R.id.mapFragToggle1);
        Button listButton = (Button) findViewById(R.id.mapFragToggle2);
        if (mapIsShowing){
            mapButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mapButton.setTextColor(getResources().getColor(R.color.colorWhite));
            listButton.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
            listButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else if (listIsShowing){
            mapButton.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
            mapButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            listButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            listButton.setTextColor(getResources().getColor(R.color.colorWhite));
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
            newJob = new Job(foundJob.getJobID(), foundJob.getJobTitle(), foundJob.getCompanyName(), foundJob.getDatePosted(), foundJob.getJobURL(), foundJob.getJobCityState(), jobLat, jobLng, "");
        }else{
            if (foundJob == null){
                Log.i("BottomNav:354", "foundjob is nul!!!!!!!!!!");
            }else if (foundJob.getJobLat() == 0 || foundJob.getJobLng() == 0){
                Log.i("BottomNav:357", "lat or lng is null!!!!!!!!!!");
                newJob = new Job(foundJob.getJobID(), foundJob.getJobTitle(), foundJob.getCompanyName(), foundJob.getDatePosted(), foundJob.getJobURL(), foundJob.getJobCityState(), 0, 0, "");
                Toast.makeText(activity, "Could not retrieve job locations.  API limit reached.", Toast.LENGTH_SHORT).show();
            }

        }
        // Add new job to the list of jobs:
        if (newJob != null && !jobList.contains(newJob)){
            jobList.add(newJob);
        }
        Log.i("BottomNavActivity:323", "joblistcount:" + jobList.size());

        sortJobs(activity, jobList);

        showJobs(activity, jobList);

    }

    private static void sortJobs(final Activity activity, ArrayList<Job> jobs){
        // Sort jobs appropriately:
        if (sortBy.equals("location") || sortBy.equals("accquisitiondate")){
            Collections.sort(jobs, new Comparator<Job>() {
                @Override
                public int compare(Job job1, Job job2) {
                    if (sortBy.equals("location") && job1.getDistance(activity, job1) != null && job2.getDistance(activity, job2) != null){
                        // Sort by distance:
                        return job1.getDistance(activity, job1).compareTo(job2.getDistance(activity, job2));
                    }else if (sortBy.equals("accquisitiondate") && job2.getDatePosted() != null && job1.getDatePosted() != null){
                        // Sort by date:
                        return job2.getDatePosted().compareToIgnoreCase(job1.getDatePosted());
                    }
                    return 0;
                }
            });
        }

        if (sortBy.equals("accquisitiondate")){
            Collections.sort(jobs, new Comparator<Job>() {
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

        // Get the most recent job and save it to the device:
        ArrayList<Job> recentJobs = new ArrayList<>();
        recentJobs.addAll(jobs);
        final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
        Collections.sort(recentJobs, new Comparator<Job>() {
            @Override
            public int compare(Job job1, Job job2) {
                try {
                    return dateTimeFormat.parse(job2.getDatePosted()).compareTo(dateTimeFormat.parse(job1.getDatePosted()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        if (recentJobs.size() > 0){
            Job mostRecentJob = recentJobs.get(0);
            FileUtil.writeMostRecentJob(activity, mostRecentJob);
        }

    }

    private static void showJobs(final Activity activity, ArrayList<Job> jobs){
        if (isTablet){
            // Create and display a MapFragment in bottom-left container:
            MapFragment mapFrag;
            if (jobs != null){
                mapFrag = MapFragment.newInstance(jobs);
            }else{
                if (savedSearch != null){
                    mapFrag = MapFragment.newInstance(savedSearch.getLocation(), savedSearch.getKeywords());
                }else{
                    mapFrag = new MapFragment();
                }
            }
            activity.getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, mapFrag).commit();
            // Create and display a SearchResultListFragment in bottom-right container:
            SearchResultListFragment listfrag;
            if (jobs != null){
                listfrag = SearchResultListFragment.newInstance(jobs);
            }else{
                listfrag = new SearchResultListFragment();
            }
            activity.getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer2, listfrag).commit();
        }else{
            if (mapIsShowing){
                // Create and display a MapFragment in bottom container:
                MapFragment mapFrag;
                if (jobs != null){
                    mapFrag = MapFragment.newInstance(jobs);
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
                if (jobs != null){
                    listfrag = SearchResultListFragment.newInstance(jobs);
                }else{
                    listfrag = new SearchResultListFragment();
                }
                activity.getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, listfrag).commit();
            }
        }

    }


    private void searchForJobs(String _keywords, String _location){
        if (!NetworkMonitor.deviceIsConnected(this)){
            Toast.makeText(this, "No connection.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Show progress bar:
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        jobList.clear();
        // Save the search:
        String time = String.valueOf(System.currentTimeMillis());
        SavedSearch newSavedSearch = new SavedSearch(_keywords, radius, _location, posted, time);
        saveTheSearch(newSavedSearch);
        //Get the search radius, sort type, & number of days from user and put it into the job search url:
        String maxJobsString = String.valueOf(maxJobs);
        String url = "https://api.careeronestop.org/v1/jobsearch/TZ1zgEyKTNm69nF/"+_keywords+"/"+_location+"/"+radius+"/"+sortBy+"/desc/0/"+maxJobsString+"/"+posted;
        Log.i("radius", radius);
        // Get jobs from API:
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("BottomNavActvty:499", "response: " + response);
                makeJobList(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("BottomNav:505", "That didn't work!!!!!!!");
                if (error.networkResponse.statusCode == 404){
                    Toast.makeText(BottomNavigationActivity.this, "No jobs available.", Toast.LENGTH_SHORT).show();
                    ArrayList<Job> emptyJobs = new ArrayList<>();
                    showJobs(BottomNavigationActivity.this,emptyJobs);
                    ProgressBar progressBar = (ProgressBar)BottomNavigationActivity.this.findViewById(R.id.progressBar);
                    if (progressBar != null){
                        progressBar.setVisibility(View.INVISIBLE);
                    }
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
            if (savedSearches.get(i).getKeywords().equals(search.getKeywords()) &&
                    savedSearches.get(i).getLocation().equals(search.getLocation()) &&
                    savedSearches.get(i).getRadius().equals(search.getRadius()) &&
                    savedSearches.get(i).getDays().equals(search.getDays())){
                Log.i("match", savedSearches.get(i).getKeywords() + " " + search.getKeywords() + "\n" + savedSearches.get(i).getLocation() + " " + search.getLocation());
                foundMatch = true;
            }
        }
        // If the search isn't saved, then save it:
        if (!foundMatch){
            Log.i("match", "no matches");
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
        // Save filters:
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.kristaappel.jobspot.preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("radius", search.getRadius());
        editor.putString("posted", search.getDays());
        editor.putString("sortBy", sortBy);
        editor.apply();

        FileUtil.writeMostRecentSearch(this, search);
    }

    private void makeJobList(String searchResponse){
        // Parse JSON to make a list of Job objects:
        try {
            JSONObject responseObj = new JSONObject(searchResponse);
            JSONArray jobsArray = responseObj.getJSONArray("Jobs");
            for (int i = 0; i<maxJobs; i++){
                JSONObject jobObj = jobsArray.getJSONObject(i);
                String jobid = jobObj.getString("JvId");
                String jobtitle = jobObj.getString("JobTitle");
                String companyname = jobObj.getString("Company");
                String dateposted = jobObj.getString("AccquisitionDate");
                String joburl = jobObj.getString("URL");
                String jobcitystate = jobObj.getString("Location");
                Job foundJob = new Job(jobid, jobtitle, companyname, dateposted, joburl, jobcitystate, 0, 0, "");
                // Get the coordinates:
                LocationHelper.lookUpCompany(this, foundJob);
            }
            Log.i("BottomNavActivity:477", "jobs count: " + jobsArray.length());
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
        jobList.clear();
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

    public static void linkedInClicked(Activity activity){
        loginToLinkedIn(activity);
    }

    // Set permissions to retrieve info from LinkedIn:
    private static Scope buildScope(){
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    public static void checkLinkedInToken(Activity activity){
        if (ProfileFragment.linkedInAccessToken != null) {
            Log.i("LINKEDINprofile124", "access token not null");
            LISessionManager.getInstance(activity.getApplicationContext()).init(linkedInAccessToken);
            if (!ProfileFragment.linkedInError){
                Log.i("LINKEDINprofile127", "no error & access token not null");
                ProfileFragment.displayLinkedInData(activity, liName, liEmail, liPictureURL, liHeadline, liLocation, liSummary);
            }
        }
    }

    private static void loginToLinkedIn(final Activity activity){
        LISessionManager.getInstance(activity).init(activity, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                Log.i("LINKEDINprofile185", "onAuthSuccess");
                linkedInError = false;
                if (ProfileFragment.linkedInAccessToken == null) {
                    ProfileFragment.linkedInAccessToken = LISessionManager.getInstance(activity).getSession().getAccessToken();
                }else{
                    Log.i("LINKEDINprofile190", "access token not null");
                    LISessionManager.getInstance(activity).init(ProfileFragment.linkedInAccessToken);
                }
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Log.i("LINKEDINprofile197", "onAuthError: " + error.toString());
                //           LISessionManager.getInstance(getActivity().getApplicationContext()).clearSession();
                linkedInError = true;
            }
        }, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            Toast.makeText(this, "Could not retrieve LinkedIn data.  Please allow permission.", Toast.LENGTH_SHORT).show();
            linkedInError = true;
            Log.i("LINKEDIN657", "requestCode: " + requestCode);
            Log.i("LINKEDIN658", "resultCode: " + resultCode);
            Log.i("LINKEDIN659", "data: " + data.getData());
            Log.i("LINKEDIN660", "returning from onActivityResult - result not OK");
            return;
        }
        Log.i("LINKEDIN663", "resultCode: " + resultCode);
        // App returns here after LinkedIn connection is made.  Get data from LinkedIn API:
        Log.i("LINKEDIN665", "onActivityResult from BottomNavigationActivity");

        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        Log.i("LINKEDIN668", "NOW it's a success");

        String url = "https://api.linkedin.com/v1/people/~:(formatted-name,email-address,headline,location,summary,picture-url)?format=json"; //:(email-address,formatted-name, phone-numbers, picture-urls::(original))";
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retrieving Data...");
        progressDialog.show();

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {

                Log.i("LINKEDIN684", "response: " + apiResponse);
                JSONObject responseObject = apiResponse.getResponseDataAsJson();
                Log.i("LINKEDIN686", "response as json: " + responseObject);
                try {
                    if (responseObject.has("formattedName")){
                        liName = responseObject.getString("formattedName");
                    }

                    if (responseObject.has("emailAddress")){
                        liEmail = responseObject.getString("emailAddress");
                    }

                    if (responseObject.has("headline")){
                        liHeadline = responseObject.getString("headline");
                    }

                    JSONObject liLocationObject = null;
                    if (responseObject.has("location")){
                        liLocationObject = responseObject.getJSONObject("location");
                    }

                    if (liLocationObject != null && liLocationObject.has("name")){
                        liLocation = liLocationObject.getString("name");
                    }

                    if (responseObject.has("pictureUrl")){
                        liPictureURL = responseObject.getString("pictureUrl");
                    }

                    if (responseObject.has("summary")){
                        liSummary = responseObject.getString("summary");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("LINKEDIN719", "JSONException in onApiSuccess: " + e.toString());
                    Toast.makeText(BottomNavigationActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                }
                Log.i("LINKEDIN", "name: " + liName);
                Log.i("LINKEDIN", "email: " + liEmail);
                Log.i("LINKEDIN", "headline: " + liHeadline);
                Log.i("LINKEDIN", "location: " + liLocation);
                Log.i("LINKEDIN", "picture url: " + liPictureURL);
                Log.i("LINKEDIN", "summary: " + liSummary);

                //TODO: save profile data to firebase
//                Firebase firebase = new Firebase("https://jobspot-a0171.firebaseio.com/");
//                FirebaseAuth mAuth = FirebaseAuth.getInstance();
//                FirebaseUser firebaseUser = mAuth.getCurrentUser();
//
//                if (firebaseUser != null) {
//                    Map<String, Object> childUpdates = new HashMap<>();
//                    String key = firebase.child("users").child(firebaseUser.getUid()).child("userProfile").push().getKey();
//                    Log.i("linkedin", "key: " + key);
//                    firebase.child("users").child(firebaseUser.getUid()).child("userProfile").push().child("fullName").setValue(liName);
//                }

                ProfileFragment.displayLinkedInData(BottomNavigationActivity.this, liName, liEmail, liPictureURL, liHeadline, liLocation, liSummary);

            }

            @Override
            public void onApiError(LIApiError error) {
                Log.i("LINKEDIN735", "onApiError");
                error.printStackTrace();
            }
        });
        progressDialog.dismiss();

    }

    @Override
    public void onSearchMenuInteraction() {
        onSearchBoxFragmentInteraction(R.id.recentButton);
    }
}
