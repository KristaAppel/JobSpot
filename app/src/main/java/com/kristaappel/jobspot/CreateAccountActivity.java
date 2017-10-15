package com.kristaappel.jobspot;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editText_username;
    private EditText editText_password;
    private Firebase firebase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        Button createAccountButton = (Button) findViewById(R.id.button_CreateAccount);
        createAccountButton.setOnClickListener(this);
        Button loginLink = (Button) findViewById(R.id.button_LoginLink);
        loginLink.setOnClickListener(this);

        editText_username = (EditText) findViewById(R.id.editText_create_email);
        editText_password = (EditText) findViewById(R.id.editText_create_password);

        Log.i("BottomNavActivity", "istablet: " + getResources().getBoolean(R.bool.is_tablet));
        if (getResources().getBoolean(R.bool.is_tablet)){
            //TODO: it's a tablet.  do tablet stuff
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        }else{
            //TODO: it's a phone.  do phone stuff.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        firebase = new Firebase("https://jobspot-a0171.firebaseio.com/");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.i("CreateAccountActivity", "onAuthStateChanged:signed_in:" + user.getUid());
                    firebaseUser = user;
                    // The user has signed in.  Go to the map:
               Intent mapIntent = new Intent(getApplicationContext(), BottomNavigationActivity.class);
               startActivity(mapIntent);
                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
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
        if (v.getId() == R.id.button_CreateAccount){
            // Check for connection:
            if (!NetworkMonitor.deviceIsConnected(getApplicationContext())){
                Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
            }else{
                final String email = editText_username.getText().toString();
                String password = editText_password.getText().toString();

                // Make sure fields are not blank:
                if (email.length() < 1 || password.length() < 1) {
                    Toast.makeText(getApplicationContext(), "Enter Email and Password", Toast.LENGTH_SHORT).show();
                    // Make sure the email is valid:
                }else if(!email.contains(".") || !email.contains("@")) {
                    Toast.makeText(getApplicationContext(), "Enter a Valid Email Address", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.i("CreateAccountActivity", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                    if (!task.isSuccessful()) {
                                        // Account creation was not successful.  Tell the user why:
                                        try{
                                            throw task.getException();
                                        } catch(Exception e){
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            Log.i("CreateAccountActivity", "exception: " + e.getMessage());
                                        }
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();

                                        // Save the user's uid and email to the database:
                                        firebaseUser = mAuth.getCurrentUser();
                                        firebase.child("users").child(firebaseUser.getUid()).child("email").setValue(email);
                                    }
                                }
                            });
                }
            }
        }else if (v.getId() == R.id.button_LoginLink){
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }
    }
}
