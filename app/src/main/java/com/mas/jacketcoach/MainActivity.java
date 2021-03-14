package com.mas.jacketcoach;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* NAVIGATION */

        //Initialize Bottom Navigation View
        BottomNavigationView bottomNavView = findViewById(R.id.bottomNav_view);

        //Initialize top bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = this.getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(false);
//        Initialize navigation controller

        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

//        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration);

        NavigationUI.setupWithNavController(bottomNavView,navController);

        /* NAVIGATION END */
    }

//    private SearchView.OnQueryTextListener textListener = new SearchView.OnQueryTextListener() {
//        @Override
//        public boolean onQueryTextSubmit(String query) {
//            Log.d("NAVIGATION", "quesry is :" + query);
//            return true;
//        }
//
//        @Override
//        public boolean onQueryTextChange(String newText) {
//            Log.d("NAVIGATION", "newText is :" + newText);
//            return true;
//        }
//    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        Log.d("NAVIGATION", "onCreateMenu");
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) searchItem.getActionView();

//        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("NAVIGATION", "query is :" + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("NAVIGATION", "newText is :" + newText);
                return false;
            }
        });
        return true;
    }

    // ToolBar actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Log.d("NAVIGATION","setting");
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}