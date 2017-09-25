package com.kristaappel.jobspot;

import android.Manifest;
import android.content.pm.PackageManager;
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

import com.kristaappel.jobspot.fragments.AppliedJobsFragment;
import com.kristaappel.jobspot.fragments.MapFragment;
import com.kristaappel.jobspot.fragments.ProfileFragment;
import com.kristaappel.jobspot.fragments.SavedJobsFragment;
import com.kristaappel.jobspot.fragments.SearchBoxFragment;
import com.kristaappel.jobspot.fragments.SearchResultListFragment;
import com.kristaappel.jobspot.fragments.SearchScreenFragment;


public class BottomNavigationActivity extends AppCompatActivity implements SearchBoxFragment.OnSearchBoxFragmentInteractionListener {

    ActionBar actionBar;
    Button mapButton;
    Button listButton;
    EditText et_location;
    EditText et_keywords;


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
                // TODO: get current location and find nearby jobs, show them on map.  display location in top search bar
                Log.i("TAG", "get current location");
                break;
            case R.id.filterButton:
                // TODO: show a list of filter & sort options, use chosen options in job search
                Log.i("TAG", "show filter/sort options");
                break;
            case R.id.searchButton:
                // TODO: search for & display jobs
                String location = "";
                String keywords = "";
                if (et_location.getText().toString().length() >0){
                    location = et_location.getText().toString();
                }
                if (et_keywords.getText().toString().length() >0){
                    keywords = et_keywords.getText().toString();
                }
                Log.i("BottomNavActivity", "Entered location: " + location + " Entered keywords: " + keywords);
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
