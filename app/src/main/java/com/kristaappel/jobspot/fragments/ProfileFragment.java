package com.kristaappel.jobspot.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.kristaappel.jobspot.LoginActivity;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.NetworkMonitor;
import com.kristaappel.jobspot.objects.NotificationBroadcastReceiver;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends android.app.Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Firebase firebase;
    AccessToken linkedInAccessToken;
    static String liPictureUrl = "";
    static String liName = "";
    static String liHeadline = "";
    static String liLocation = "";
    static String liIndustry = "";
    static String liSummary = "";
    Switch notificationSwitch;
    SharedPreferences sharedPreferences;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String lname, String pictureURL, String headline, String location, String industry, String summary) {
        Bundle args = new Bundle();

        liName = lname;
        liPictureUrl = pictureURL;
        liHeadline = headline;
        liLocation = location;
        liIndustry = industry;
        liSummary = summary;

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Button logoutButton = (Button) view.findViewById(R.id.button_logout);
        ImageButton linkedInSignInButton = (ImageButton) view.findViewById(R.id.linkedin_signin_button);
        linkedInSignInButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        notificationSwitch = (Switch) view.findViewById(R.id.switch_notifications);
        notificationSwitch.setOnCheckedChangeListener(this);

        firebase = new Firebase("https://jobspot-a0171.firebaseio.com/");

        displayLinkedInData(getActivity(), liPictureUrl, liHeadline, liLocation, liIndustry, liSummary);

        sharedPreferences = getActivity().getSharedPreferences("com.kristaappel.jobspot.preferences", Context.MODE_PRIVATE);

        String notificationPreference = sharedPreferences.getString("notifications", "on");
        if (notificationPreference.equals("on")){
            notificationSwitch.setChecked(true);
        }else{
            notificationSwitch.setChecked(false);
        }

        if (linkedInAccessToken != null) {
            LISessionManager.getInstance(getActivity().getApplicationContext()).init(linkedInAccessToken);
        }

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
        if (v.getId() == R.id.button_logout){
            // Logout:
            firebase.unauth();
            // Go to login screen:
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            loginIntent.putExtra("LogoutExtra", "Logout");
            startActivity(loginIntent);
        }else if (v.getId() == R.id.linkedin_signin_button){
            if (NetworkMonitor.deviceIsConnected(getActivity())){
                loginToLinkedIn();
            }else {
                Toast.makeText(getActivity(), "No connection.", Toast.LENGTH_SHORT).show();
            }
            
        }

    }
    // Set permissions to retrieve info from LinkedIn:
    private static Scope buildScope(){
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }


    public void loginToLinkedIn(){
        LISessionManager.getInstance(getActivity().getApplicationContext()).init(getActivity(), buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                if (linkedInAccessToken == null) {
                    linkedInAccessToken = LISessionManager.getInstance(getActivity().getApplicationContext()).getSession().getAccessToken();
                }else{
                    Log.i("LINKEDIN", "access token not null");
                    LISessionManager.getInstance(getActivity().getApplicationContext()).init(linkedInAccessToken);
                }
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Toast.makeText(getActivity(), "Error" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("LINKEDIN", "Error: " + error.toString());
            }
        }, true);
    }

    public static void displayLinkedInData(Activity activity, String liPictureURL, String liHeadline, String liLocation, String liIndustry, String liSummary) {
        // Get views:
        ImageButton linkedInButton = (ImageButton) activity.findViewById(R.id.linkedin_signin_button);
        TextView textViewExplanation = (TextView) activity.findViewById(R.id.textView_profile_explanation);
        ImageView profileImageView = (ImageView) activity.findViewById(R.id.imageView_profile);
        TextView textViewName = (TextView) activity.findViewById(R.id.textView_profile_name);
        TextView textViewHeadline = (TextView) activity.findViewById(R.id.textView_profile_headline);
        TextView textViewLocation = (TextView) activity.findViewById(R.id.textView_profile_location);
        TextView textViewIndustry = (TextView) activity.findViewById(R.id.textView_profile_industry);
        TextView textViewSummary = (TextView) activity.findViewById(R.id.textView_profile_summary);
        // If we have data from LinkedIn, hide the LinkedIn button and show the data:
        if (!liPictureURL.equals("") && profileImageView != null) {
            // display profile image:
            profileImageView = (ImageView) activity.findViewById(R.id.imageView_profile);
            Picasso.with(activity).load(liPictureURL).into(profileImageView);
            // display text:
            textViewName.setText(liName);
            textViewHeadline.setText(liHeadline);
            textViewLocation.setText(liLocation);
            textViewIndustry.setText(liIndustry);
            textViewSummary.setText(liSummary);
            // Hide 'Sign in with LinkedIn' button:
            linkedInButton.setVisibility(View.INVISIBLE);
            textViewExplanation.setVisibility(View.INVISIBLE);
        }
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
