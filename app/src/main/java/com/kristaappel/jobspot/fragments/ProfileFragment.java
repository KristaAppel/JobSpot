package com.kristaappel.jobspot.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kristaappel.jobspot.BottomNavigationActivity;
import com.kristaappel.jobspot.LoginActivity;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.NetworkMonitor;
import com.kristaappel.jobspot.objects.NotificationBroadcastReceiver;
import com.linkedin.platform.AccessToken;
import com.squareup.picasso.Picasso;

import java.sql.Struct;
import java.util.HashMap;

import static com.kristaappel.jobspot.BottomNavigationActivity.firebase;


public class ProfileFragment extends android.app.Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static AccessToken linkedInAccessToken;
    private static String liPictureUrl = "";
    private static String liName = "";
    private static String liEmail = "";
    private static String liHeadline = "";
    private static String liLocation = "";
    private static String liSummary = "";
    private SharedPreferences sharedPreferences;
    public static boolean linkedInError = false;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String lname, String lemail, String pictureURL, String headline, String location, String summary) {
        Bundle args = new Bundle();

        liName = lname;
        liEmail = lemail;
        liPictureUrl = pictureURL;
        liHeadline = headline;
        liLocation = location;
        liSummary = summary;

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set button click listeners:
        ImageButton linkedInSignInButton = (ImageButton) view.findViewById(R.id.linkedin_signin_button);
        linkedInSignInButton.setOnClickListener(this);
        Switch notificationSwitch = (Switch) view.findViewById(R.id.switch_notifications);
        notificationSwitch.setOnCheckedChangeListener(this);


//        // Display LinkedIn profile data, if available:
//        if (!linkedInError){
//            Log.i("LINKEDINprofile109", "no error");
//            displayProfileData(getActivity(), liName, liEmail, liPictureUrl, liHeadline, liLocation, liSummary);
//        }

        loadProfileFromFirebase();

        sharedPreferences = getActivity().getSharedPreferences("com.kristaappel.jobspot.preferences", Context.MODE_PRIVATE);

        String notificationPreference = sharedPreferences.getString("notifications", "on");
        if (notificationPreference.equals("on")){
            notificationSwitch.setChecked(true);
        }else{
            notificationSwitch.setChecked(false);
        }

        BottomNavigationActivity.checkLinkedInToken(getActivity()); ////////////////////////////////////////////////////////
//        if (linkedInAccessToken != null) {
//            Log.i("LINKEDINprofile124", "access token not null");
//            LISessionManager.getInstance(getActivity().getApplicationContext()).init(linkedInAccessToken);
//            if (!linkedInError){
//                Log.i("LINKEDINprofile127", "no error & access token not null");
//                displayProfileData(getActivity(), liName, liEmail, liPictureUrl, liHeadline, liLocation, liSummary);
//            }
//        }
    }

    private void getDataFromSnapshot(DataSnapshot dataSnapshot){
        HashMap snapshotHashMap = (HashMap) dataSnapshot.getValue();
        liName = snapshotHashMap.get("fullName").toString();
        liEmail = snapshotHashMap.get("email").toString();
        liPictureUrl = snapshotHashMap.get("picture").toString();
        liHeadline = snapshotHashMap.get("headline").toString();
        liLocation = snapshotHashMap.get("location").toString();
        liSummary = snapshotHashMap.get("summary").toString();
    }

    private void loadProfileFromFirebase(){
        // Load profile data from Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        BottomNavigationActivity.firebase.child("users").child(firebaseUser.getUid()).child("userProfile").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getDataFromSnapshot(dataSnapshot);
                displayProfileData(getActivity(), liName, liEmail, liPictureUrl, liHeadline, liLocation, liSummary);
                Log.i("PROFILEonChildADded", "snapshot: " + dataSnapshot.getKey() + " " + dataSnapshot.getValue());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getDataFromSnapshot(dataSnapshot);
                displayProfileData(getActivity(), liName, liEmail, liPictureUrl, liHeadline, liLocation, liSummary);
                Log.i("PROFILEonChildChanged", "snapshot: " + dataSnapshot.getKey() + " " + dataSnapshot.getValue());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getDataFromSnapshot(dataSnapshot);
                displayProfileData(getActivity(), liName, liEmail, liPictureUrl, liHeadline, liLocation, liSummary);
                Log.i("PROFILEonChildRemoved", "snapshot: " + dataSnapshot.getKey() + " " + dataSnapshot.getValue());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getDataFromSnapshot(dataSnapshot);
                displayProfileData(getActivity(), liName, liEmail, liPictureUrl, liHeadline, liLocation, liSummary);
                Log.i("PROFILEonChildMoved", "snapshot: " + dataSnapshot.getKey() + " " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i("PROFILEonCancelled", "Failed to read value:" + firebaseError.getMessage().toString());
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.profile_menu_logout){
            // Logout:
            BottomNavigationActivity.firebase.unauth();
            linkedInError = true;
            // Go to login screen:
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            loginIntent.putExtra("LogoutExtra", "Logout");
            startActivity(loginIntent);
        }
        if (item.getItemId() == R.id.profile_menu_edit){
            //TODO: allow user to edit profile
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.linkedin_signin_button){
            linkedInError = false;
            if (NetworkMonitor.deviceIsConnected(getActivity())){
                BottomNavigationActivity.linkedInClicked(getActivity());//////////////////////////////////////////////////////
//                loginToLinkedIn();
            }else {
                Toast.makeText(getActivity(), "No connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }
//    // Set permissions to retrieve info from LinkedIn:
//    private static Scope buildScope(){
//        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
//    }
//
//
//    private void loginToLinkedIn(){
//        LISessionManager.getInstance(getActivity().getApplicationContext()).init(getActivity(), buildScope(), new AuthListener() {
//            @Override
//            public void onAuthSuccess() {
//                Log.i("LINKEDINprofile185", "onAuthSuccess");
//                linkedInError = false;
//                if (linkedInAccessToken == null) {
//                    linkedInAccessToken = LISessionManager.getInstance(getActivity().getApplicationContext()).getSession().getAccessToken();
//                }else{
//                    Log.i("LINKEDINprofile190", "access token not null");
//                    LISessionManager.getInstance(getActivity().getApplicationContext()).init(linkedInAccessToken);
//                }
//            }
//
//            @Override
//            public void onAuthError(LIAuthError error) {
//                Log.i("LINKEDINprofile197", "onAuthError: " + error.toString());
//                //           LISessionManager.getInstance(getActivity().getApplicationContext()).clearSession();
//                linkedInError = true;
//            }
//        }, true);
//    }

    public static void displayProfileData(Activity activity, String _liName, String lEmail, String liPictureURL, String liHeadline, String liLocation, String liSummary) {
        liName = _liName;
        liEmail = lEmail;

        // Get views:
        ImageButton linkedInButton = (ImageButton) activity.findViewById(R.id.linkedin_signin_button);
        TextView textViewExplanation = (TextView) activity.findViewById(R.id.textView_profile_explanation);
        ImageView profileImageView = (ImageView) activity.findViewById(R.id.imageView_profile);
        TextView textViewName = (TextView) activity.findViewById(R.id.textView_profile_name);
        TextView textViewEmail = (TextView) activity.findViewById(R.id.textView_profile_email);
        TextView textViewHeadline = (TextView) activity.findViewById(R.id.textView_profile_headline);
        TextView textViewLocation = (TextView) activity.findViewById(R.id.textView_profile_location);
        TextView textViewSummary = (TextView) activity.findViewById(R.id.textView_profile_summary);
        // If we have data from LinkedIn, hide the LinkedIn button and show the data:
        if (!liPictureURL.trim().equals("") && profileImageView != null) {
            // display profile image:
                profileImageView = (ImageView) activity.findViewById(R.id.imageView_profile);
                Picasso.with(activity).load(liPictureURL).into(profileImageView);
        }
        // display text:
        textViewName.setText(liName);
        textViewEmail.setText(liEmail);
        textViewHeadline.setText(liHeadline);
        textViewLocation.setText(liLocation);
        textViewSummary.setText(liSummary);

        // Hide 'Sign in with LinkedIn' button:
//        linkedInButton.setVisibility(View.INVISIBLE);
//        textViewExplanation.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        NotificationBroadcastReceiver notificationReceiver = new NotificationBroadcastReceiver();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (isChecked){
            // Start notifications:
            notificationReceiver.setAlarm(getActivity());
            editor.putString("notifications", "on");
        }else{
            // Stop notifications:
            notificationReceiver.cancelAlarms(getActivity());
            editor.putString("notifications", "off");
        }
        editor.apply();
    }


}
