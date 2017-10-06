package com.kristaappel.jobspot.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.kristaappel.jobspot.BottomNavigationActivity;
import com.kristaappel.jobspot.LoginActivity;
import com.kristaappel.jobspot.R;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.squareup.picasso.Picasso;

import static android.R.attr.fragment;


public class ProfileFragment extends android.app.Fragment implements View.OnClickListener {

    private Firebase firebase;
    AccessToken linkedInAccessToken;
    static String liPictureUrl = "";
    static String liName = "";



    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String pictureUrl, String name) {
        Bundle args = new Bundle();

        liPictureUrl = pictureUrl;
        liName = name;

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
        Button logoutButton = (Button) view.findViewById(R.id.button_logout);
        ImageButton linkedInSignInButton = (ImageButton) view.findViewById(R.id.linkedin_signin_button);
        linkedInSignInButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        firebase = new Firebase("https://jobspot-a0171.firebaseio.com/");

      BottomNavigationActivity.displayLinkedInData(getActivity());

        if (linkedInAccessToken != null) {
            Log.i("LINKEDIN", "token is not null");
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
            // Logout and go to Login screen:
            firebase.unauth();
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            loginIntent.putExtra("LogoutExtra", "Logout");
            startActivity(loginIntent);
        }else if (v.getId() == R.id.linkedin_signin_button){
            Log.i("ProfileFragment", "Sign in with linkedIn");

            loginToLinkedIn();
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


}
