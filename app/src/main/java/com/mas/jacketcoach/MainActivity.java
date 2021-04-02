package com.mas.jacketcoach;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.mas.jacketcoach.helper.MapStateManager;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* NAVIGATION */

        //Initialize Bottom Navigation View
        BottomNavigationView bottomNavView = findViewById(R.id.bottomNav_view);

        //Initialize top bar
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
//        setSupportActionBar(myToolbar);

//        ActionBar actionBar = this.getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(false);
//        Initialize navigation controller

        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNavView,navController);

        // Overwrite the status color on the MainActivity to match colorPrimaryDark
        // Done programmatically here since other toolbar stuff might be overwriting it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.GT_Color_Navy));
        }

        //------------- MAP IS RESET -----------------

        //TODO RESET THE MAP USING A LOGIN VARIABLE ??
        MapStateManager mapStateManager = new MapStateManager(this.getApplicationContext());
        mapStateManager.onCreateMainActivity();


        // DEBUG
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(this, "Signed in ID: " + mAuth.getCurrentUser().getDisplayName() + " !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "DEBUG MODE - Signed In with no actual user", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("NAVIGATION", "TEST");
    }



}