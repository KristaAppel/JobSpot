package com.kristaappel.jobspot;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kristaappel.jobspot.objects.NetworkMonitor;

import static com.kristaappel.jobspot.R.id.et_keywords;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText_username;
    private EditText editText_password;
    private String username;
    private String password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_username = (EditText) findViewById(R.id.editText_login_email);
        editText_password = (EditText) findViewById(R.id.editText_login_password);

        Log.i("BottomNavActivity", "istablet: " + getResources().getBoolean(R.bool.is_tablet));
        if (getResources().getBoolean(R.bool.is_tablet)){
            // It's a tablet.  Show in landscape:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        }else{
            // It's a phone.  Show in portrait:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Firebase.setAndroidContext(this);

        // Hide actionbar:
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("LoginActivity", "onAuthStateChanged:signed_in:" + user.getUid());
                    // The user has signed in.  Go to the map:
                Intent mapIntent = new Intent(getApplicationContext(), BottomNavigationActivity.class);
                startActivity(mapIntent);
                } else {
                    // User is signed out
                    Log.d("LoginActivity", "onAuthStateChanged:signed_out");
                }
            }
        };

        // Listen for button clicks:
        Button loginButton = (Button) findViewById(R.id.button_Login);
        loginButton.setOnClickListener(this);
        Button createAccountLink = (Button) findViewById(R.id.button_CreateAccountLink);
        createAccountLink.setOnClickListener(this);

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent callingIntent = getIntent();
        if (callingIntent.hasExtra("LogoutExtra")) {
            mAuth.signOut();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_Login){
            // Check for connection:
            if (!NetworkMonitor.deviceIsConnected(getApplicationContext())){
                Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
            }else{
                username = editText_username.getText().toString();
                password = editText_password.getText().toString();
                // Make sure fields are not blank:
                if (username.length() < 1 || password.length() < 1){
                    Toast.makeText(getApplicationContext(), "Enter Email and Password", Toast.LENGTH_SHORT).show();
                    // Make sure the email is valid:
                }else if (!username.contains(".") || !username.contains("@")) {
                    Toast.makeText(getApplicationContext(), "Enter a Valid Email Address", Toast.LENGTH_SHORT).show();
                }else{
                    login();
                }
            }
        }else if (v.getId() == R.id.button_CreateAccountLink){
            Intent createAccountLinkIntent = new Intent(this, CreateAccountActivity.class);
            startActivity(createAccountLinkIntent);
        }
    }


    private void login(){
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // Login was not successful.  Tell the user why:
                            Log.i("LoginActivity", "signInWithEmail:failed", task.getException());
                            try{
                                throw task.getException();
                            } catch(Exception e){
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.i("CreateAccountActivity", "exception: " + e.getMessage());
                            }
                        }
                    }

                });
    }


}
