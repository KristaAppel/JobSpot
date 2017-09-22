package com.kristaappel.jobspot;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        Button loginButton = (Button) findViewById(R.id.button_Login);
        loginButton.setOnClickListener(this);
        Button createAccountLink = (Button) findViewById(R.id.button_CreateAccountLink);
        createAccountLink.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_Login){
            // TODO: LOGIN
        }else if (v.getId() == R.id.button_CreateAccountLink){
            Intent createAccountLinkIntent = new Intent(this, CreateAccountActivity.class);
            startActivity(createAccountLinkIntent);
        }
    }
}
