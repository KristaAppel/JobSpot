package com.kristaappel.jobspot.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.kristaappel.jobspot.LoginActivity;
import com.kristaappel.jobspot.R;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.R.attr.name;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class ProfileFragment extends android.app.Fragment implements View.OnClickListener {

    private Firebase firebase;
    AccessToken linkedInAccessToken;
    public static final String PACKAGE = "com.kristaappel.jobspot";

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

        //
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
        Log.i("loginToLinkedIn", "method called");
//        if (linkedInAccessToken != null) {
//            LISessionManager.getInstance(getActivity().getApplicationContext()).init(linkedInAccessToken);
//        }
        LISessionManager.getInstance(getActivity().getApplicationContext()).init(getActivity(), buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
//                Log.i("LINKEDIN", "Success" + linkedInAccessToken.toString());
                if (linkedInAccessToken == null) {
                    linkedInAccessToken = LISessionManager.getInstance(getActivity().getApplicationContext()).getSession().getAccessToken();
                    //              Toast.makeText(getActivity(), "Success" + accessToken, Toast.LENGTH_SHORT).show();
                }else{
                    LISessionManager.getInstance(getActivity().getApplicationContext()).init(linkedInAccessToken);
                }


            }

            @Override
            public void onAuthError(LIAuthError error) {
 //               Toast.makeText(getActivity(), "Error" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("LINKEDIN", "Error: " + error.toString());
            }
        }, true);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.i("LIKEDIN", "onActivityResult from fragment");
//        LISessionManager.getInstance(getActivity().getApplicationContext()).onActivityResult(getActivity(), requestCode, resultCode, data);
// //       Toast.makeText(getActivity(), "NOW it's success", Toast.LENGTH_SHORT).show();
//        Log.i("LINKEDIN", "NOW it's success");
//
//        String url = "https://api.linkedin.com/v1/people/~"; //:(email-address,formatted-name, phone-numbers, picture-urls::(original))";
//        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Retrieving Data...");
//        progressDialog.show();
//
//        APIHelper apiHelper = APIHelper.getInstance(getActivity().getApplicationContext());
//        apiHelper.getRequest(getActivity(), url, new ApiListener() {
//            @Override
//            public void onApiSuccess(ApiResponse apiResponse) {
//                Log.i("LINKEDIN", "response: " + apiResponse);
//                JSONObject responseObject = apiResponse.getResponseDataAsJson();
//                try {
//                    String email = responseObject.getString("emailAddress");
//                    String name = responseObject.getString("formattedName");
//                    Log.i("LINKEDIN", "name: " + name);
//                    Log.i("LINKEDIN", "email: " + email);
//                    progressDialog.dismiss();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onApiError(LIApiError error) {
//                Log.i("LINKEDIN", "onApiError");
//                error.printStackTrace();
//            }
//        });
//    }
}
