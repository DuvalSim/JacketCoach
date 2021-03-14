package com.mas.jacketcoach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mas.jacketcoach.model.Event;
import com.mas.jacketcoach.model.MarkerInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapsFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, Toolbar.OnMenuItemClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mGoogleMap;
    private FirebaseEvents eventsData;
    private Map<Marker, MarkerInfo> mMarkerMap = new HashMap<>();
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //want to receive menu related callbacks
        setHasOptionsMenu(true);
        String apiKey = getString(R.string.google_maps_key);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), apiKey);
        }

        PlacesClient placesClient = Places.createClient(getContext());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.setOnMapLongClickListener(MapsFragment.this);
        MapStateManager mapStateManager = new MapStateManager(this.getContext());
        CameraPosition cameraPosition = mapStateManager.getSavedCameraPosition();
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        if(cameraPosition != null){
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mGoogleMap.moveCamera(update);
        }
        ArrayList<Event> events = new ArrayList<>();
        eventsData = new FirebaseEvents();
        events = eventsData.getEventsFirebase();

        for(int i=0; i<events.size();i++){
            LatLng eventLocation = new LatLng(events.get(i).getLatitude(),events.get(i).getLongitude());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
               Date date = format.parse(events.get(i).getDate());
                if (date.after(new Date())) {
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(eventLocation)
                            .title(events.get(i).getName()));
                    MarkerInfo markerInfo = new MarkerInfo(events.get(i).getName(), events.get(i).getSport(), events.get(i).getDate());
                    mMarkerMap.put(marker, markerInfo);
                    mGoogleMap.setOnInfoWindowClickListener(MapsFragment.this);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("NAVIGATION", "HEY");
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar myToolbar = (Toolbar) getView().findViewById(R.id.main_toolbar);
        myToolbar.inflateMenu(R.menu.toolbar_menu);
        myToolbar.setOnMenuItemClickListener(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("NAVIGATION", "on Pause");
//        MapStateManager mapStateManager = new MapStateManager(this.getContext());
//        mapStateManager.saveMapState(mGoogleMap);
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d("NAVIGATION", "on STOP");
        MapStateManager mapStateManager = new MapStateManager(this.getContext());
        mapStateManager.saveMapState(mGoogleMap);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        MarkerInfo markerInfo = mMarkerMap.get(marker);
        EventWindowMap event = new EventWindowMap(markerInfo);
        event.show(getParentFragmentManager(),"eventMap");
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Log.d("NAVIGATION","setting");
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;
            case R.id.action_search:
                onSearchCalled();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

    }

    public void onSearchCalled() {
        // Set the fields to specify which types of place data to return.
//        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)//.setCountry("NG") //NIGERIA
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Status status = Autocomplete.getStatusFromIntent(data);
        Log.i("Autocomplete : ", "status returned : " + status);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            Log.i("Autocomplete : ", "onActivityResults");
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("Autocomplete : ", "Got place : " + place.getName() + " " + place.getLatLng());

                CameraUpdate camUpdate = CameraUpdateFactory.newLatLng(place.getLatLng());
                mGoogleMap.moveCamera(camUpdate);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
//                Status status = Autocomplete.getStatusFromIntent(data);
                Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getContext(), "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.e("Autocomplete", status.getStatusMessage());
            } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                Log.i("Autocomplete : ", "Operation canceled");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void onMapLongClick(LatLng latLng) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("LAT_LNG", latLng);
        Intent intent = new Intent(getActivity(), AddEventActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}