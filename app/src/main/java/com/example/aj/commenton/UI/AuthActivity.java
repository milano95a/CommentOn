package com.example.aj.commenton.UI;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.aj.commenton.R;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        navigateToLogin();
    }

    private void navigateToLogin(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new LoginFragment())
                .commit();
    }
}
