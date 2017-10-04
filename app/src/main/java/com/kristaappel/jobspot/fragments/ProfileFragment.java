package com.kristaappel.jobspot.fragments;

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

import com.firebase.client.Firebase;
import com.kristaappel.jobspot.LoginActivity;
import com.kristaappel.jobspot.R;

public class ProfileFragment extends android.app.Fragment implements View.OnClickListener {

    private Firebase firebase;

    public ProfileFragment() {
        // Required empty public constructor
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
        }
    }

}
