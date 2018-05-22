package com.example.aj.commenton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Fragment loginFragment = new LoginFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, loginFragment)
                .commit();
    }
}
