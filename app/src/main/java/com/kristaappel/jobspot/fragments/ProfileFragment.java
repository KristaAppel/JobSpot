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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kristaappel.jobspot.BottomNavigationActivity;
import com.kristaappel.jobspot.LoginActivity;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.NetworkMonitor;
import com.kristaappel.jobspot.objects.NotificationBroadcastReceiver;
import com.linkedin.platform.AccessToken;
import com.squareup.picasso.Picasso;
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
    EditText etName;
    EditText etNEmail;
    EditText etHeadline;
    EditText etLocation;
    EditText etSummary;
    private static TextView tvName;
    private static TextView tvEmail;
    private static TextView tvHeadline;
    private static TextView tvLocation;
    private static TextView tvSummary;
    private static ImageView profileImageView;
    Button saveButton;
    Button cancelButton;
    ImageButton linkedInButton;
    TextView textViewExplanation;


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

     //   BottomNavigationActivity.checkLinkedInToken(getActivity()); ////////////////////////////////////////////////////////
//        if (linkedInAccessToken != null) {
//            Log.i("LINKEDINprofile124", "access token not null");
//            LISessionManager.getInstance(getActivity().getApplicationContext()).init(linkedInAccessToken);
//            if (!linkedInError){
//                Log.i("LINKEDINprofile127", "no error & access token not null");
//                displayProfileData(getActivity(), liName, liEmail, liPictureUrl, liHeadline, liLocation, liSummary);
//            }
//        }

        profileImageView = (ImageView) getActivity().findViewById(R.id.imageView_profile);

        etName = (EditText) getActivity().findViewById(R.id.et_profile_name);
        etNEmail = (EditText) getActivity().findViewById(R.id.et_profile_email);
        etHeadline = (EditText) getActivity().findViewById(R.id.et_profile_headline);
        etLocation = (EditText) getActivity().findViewById(R.id.et_profile_location);
        etSummary = (EditText) getActivity().findViewById(R.id.et_profile_summary);

        tvName = (TextView) getActivity().findViewById(R.id.textView_profile_name);
        tvEmail = (TextView) getActivity().findViewById(R.id.textView_profile_email);
        tvHeadline = (TextView) getActivity().findViewById(R.id.textView_profile_headline);
        tvLocation = (TextView) getActivity().findViewById(R.id.textView_profile_location);
        tvSummary = (TextView) getActivity().findViewById(R.id.textView_profile_summary);

        saveButton = (Button) getActivity().findViewById(R.id.button_profile_save);
        saveButton.setOnClickListener(this);

        cancelButton = (Button) getActivity().findViewById(R.id.button_profile_cancel);
        cancelButton.setOnClickListener(this);

        linkedInButton = (ImageButton) getActivity().findViewById(R.id.linkedin_signin_button);
        textViewExplanation = (TextView) getActivity().findViewById(R.id.textView_profile_explanation);

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
        if (firebaseUser != null) {
            firebase.child("users").child(firebaseUser.getUid()).child("userProfile").addChildEventListener(new ChildEventListener() {
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
                    Log.i("PROFILEonCancelled", "Failed to read value:" + firebaseError.getMessage());
                }
            });
        }

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
            firebase.unauth();
            linkedInError = true;
            // Go to login screen:
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            loginIntent.putExtra("LogoutExtra", "Logout");
            startActivity(loginIntent);
        }
        if (item.getItemId() == R.id.profile_menu_edit){
            startEditMode();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startEditMode(){
        // Show save & cancel buttons:
        saveButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

        // Show hidden edittexts:
        etName.setVisibility(View.VISIBLE);
        etNEmail.setVisibility(View.VISIBLE);
        etHeadline.setVisibility(View.VISIBLE);
        etLocation.setVisibility(View.VISIBLE);
        etSummary.setVisibility(View.VISIBLE);

        // Populate edittext text:
        etName.setText(tvName.getText().toString());
        etNEmail.setText(tvEmail.getText().toString());
        etHeadline.setText(tvHeadline.getText().toString());
        etLocation.setText(tvLocation.getText().toString());
        etSummary.setText(tvSummary.getText().toString());

        // Hide textviews:
        tvName.setVisibility(View.INVISIBLE);
        tvEmail.setVisibility(View.INVISIBLE);
        tvHeadline.setVisibility(View.INVISIBLE);
        tvLocation.setVisibility(View.INVISIBLE);
        tvSummary.setVisibility(View.INVISIBLE);

        // Hide LinkedIn Login Button & Text:
        if (linkedInButton != null){
            linkedInButton.setVisibility(View.INVISIBLE);
        }
        if (textViewExplanation != null){
            textViewExplanation.setVisibility(View.INVISIBLE);
        }
    }

    private void endEditMode(){
        // Hide keyboard:
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null){
            manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }

        // Hide save & cancel buttons:
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);

        // Hide edittexts:
        etName.setVisibility(View.INVISIBLE);
        etNEmail.setVisibility(View.INVISIBLE);
        etHeadline.setVisibility(View.INVISIBLE);
        etLocation.setVisibility(View.INVISIBLE);
        etSummary.setVisibility(View.INVISIBLE);

        // Show textviews:
        tvName.setVisibility(View.VISIBLE);
        tvEmail.setVisibility(View.VISIBLE);
        tvHeadline.setVisibility(View.VISIBLE);
        tvLocation.setVisibility(View.VISIBLE);
        tvSummary.setVisibility(View.VISIBLE);

        // Show LinkedIn Login Button & Text:
        if (linkedInButton != null){
            linkedInButton.setVisibility(View.VISIBLE);
        }
        if (textViewExplanation != null){
            textViewExplanation.setVisibility(View.VISIBLE);
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
        switch (v.getId()){
            case R.id.linkedin_signin_button:
                linkedInError = false;
                if (NetworkMonitor.deviceIsConnected(getActivity())){
                    BottomNavigationActivity.linkedInClicked(getActivity());//////////////////////////////////////////////////////
//                loginToLinkedIn();
                }else {
                    Toast.makeText(getActivity(), "No connection.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_profile_save:
                endEditMode();
                getInputText();
                saveProfileToFirebase();
                break;
            case R.id.button_profile_cancel:
                endEditMode();
                break;
        }
    }

    private void getInputText(){
        liName = etName.getText().toString();
        liEmail = etNEmail.getText().toString();
        liHeadline = etHeadline.getText().toString();
        liLocation = etLocation.getText().toString();
        //TODO: get a new pictureURL ??
        liSummary = etSummary.getText().toString();
    }

    private void saveProfileToFirebase(){
        // Save profile data to firebase:
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebase.child("users").child(firebaseUser.getUid()).child("userProfile").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()){
                        String childKey = "";
                        // Get the child key:
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            childKey = snapshot.getKey();
                        }

                        Log.i("childKey: ", childKey);

                        // Save the profile data under the child key:
                        firebase.child("users").child(firebaseUser.getUid()).child("userProfile").child(childKey).child("fullName").setValue(liName);
                        firebase.child("users").child(firebaseUser.getUid()).child("userProfile").child(childKey).child("email").setValue(liEmail);
                        firebase.child("users").child(firebaseUser.getUid()).child("userProfile").child(childKey).child("headline").setValue(liHeadline);
                        firebase.child("users").child(firebaseUser.getUid()).child("userProfile").child(childKey).child("location").setValue(liLocation);
                        firebase.child("users").child(firebaseUser.getUid()).child("userProfile").child(childKey).child("picture").setValue(liPictureUrl);
                        firebase.child("users").child(firebaseUser.getUid()).child("userProfile").child(childKey).child("summary").setValue(liSummary);
                    }else{

                        // Create profile hashmap:
                        HashMap<String, String> profileHashmap = new HashMap<>();
                        profileHashmap.put("fullName", liName);
                        profileHashmap.put("email", liEmail);
                        profileHashmap.put("headline", liHeadline);
                        profileHashmap.put("location", liLocation);
                        profileHashmap.put("picture", liPictureUrl);
                        profileHashmap.put("summary", liSummary);

                        // Push profile hashmap under a new key:
                        firebase.child("users").child(firebaseUser.getUid()).child("userProfile").push().setValue(profileHashmap);
                    }
                    Toast.makeText(getActivity(), "Profile has been updated.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.i("FirebaseError: ", firebaseError.toString());
                }
            });
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

    public static void displayProfileData(Activity activity, String _lName, String lEmail, String lipictureURL, String liheadline, String lilocation, String lisummary) {
        liName = _lName;
        liEmail = lEmail;
        liPictureUrl = lipictureURL;
        liHeadline = liheadline;
        liLocation = lilocation;
        liSummary = lisummary;

        // Display profile image if we have one:
        if (!liPictureUrl.trim().equals("") && profileImageView != null) {
            // display profile image:
                profileImageView = (ImageView) activity.findViewById(R.id.imageView_profile);
                Picasso.with(activity).load(liPictureUrl).into(profileImageView);
        }

        // display text:
        tvName.setText(liName);
        tvEmail.setText(liEmail);
        tvHeadline.setText(liHeadline);
        tvLocation.setText(liLocation);
        tvSummary.setText(liSummary);
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
