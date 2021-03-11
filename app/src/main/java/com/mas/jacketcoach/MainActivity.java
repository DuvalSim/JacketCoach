package com.mas.jacketcoach;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* NAVIGATION */

        //Initialize Bottom Navigation View
        BottomNavigationView bottomNavView = findViewById(R.id.bottomNav_view);

        //Initialize top bar
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.map_fragment,R.id.chat_fragment,R.id.profile_fragment)
                .build();

        //Initialize navigation controller

        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration);

        NavigationUI.setupWithNavController(bottomNavView,navController);

        /* NAVIGATION END */
    }

}