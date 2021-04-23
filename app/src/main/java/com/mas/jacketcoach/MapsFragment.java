    package com.mas.jacketcoach;

    import android.Manifest;
    import android.content.Context;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.location.Address;
    import android.location.Geocoder;
    import android.location.Location;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.MenuItem;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.widget.Toolbar;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import androidx.fragment.app.Fragment;

    import com.google.android.gms.common.api.Status;
    import com.google.android.gms.location.FusedLocationProviderClient;
    import com.google.android.gms.location.LocationServices;
    import com.google.android.gms.maps.CameraUpdate;
    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.OnMapReadyCallback;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.BitmapDescriptorFactory;
    import com.google.android.gms.maps.model.CameraPosition;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.Marker;
    import com.google.android.gms.maps.model.MarkerOptions;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.android.libraries.places.api.Places;
    import com.google.android.libraries.places.api.model.Place;
    import com.google.android.libraries.places.api.net.PlacesClient;
    import com.google.android.libraries.places.widget.Autocomplete;
    import com.google.android.libraries.places.widget.AutocompleteActivity;
    import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.mas.jacketcoach.helper.MapStateManager;
    import com.mas.jacketcoach.model.Event;

    import java.io.IOException;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Locale;
    import java.util.Map;


public class MapsFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, Toolbar.OnMenuItemClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mGoogleMap;
    private DatabaseReference mDatabase;
    private ArrayList<Event> events = new ArrayList<>();
    private Map<Marker, Event> mMarkerMap = new HashMap<>();
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static int SEARCH_CALLED_AUTOCOMPLETE = 0;
    private static int ADD_CALLED_AUTOCOMPLETE = 1;
    private int autocompleteCaller;

    // The current logged in user handle
    private FirebaseAuth mAuth;

    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnownLocation;
    private boolean locationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 911;
    //10 for city, 15 for streets
    protected static int DEFAULT_ZOOM = 13;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //want to receive menu related callbacks
        setHasOptionsMenu(true);
        String apiKey = getString(R.string.google_maps_key);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), apiKey);
        }

        PlacesClient placesClient = Places.createClient(getContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        this.getEventsFirebase();
        updateLocationUI();

        mGoogleMap.setOnMapLongClickListener(MapsFragment.this);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        MapStateManager mapStateManager = new MapStateManager(this.getContext());

        if (mapStateManager.mapStateIsOutdated()) {
            Log.d("NAVIGATION", "Map outdated - getting current location");
            setCameraOnDeviceLocation();
        } else {
            setCameraOnSavedState();
        }


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getLocationPermission();
        Toolbar myToolbar = (Toolbar) getView().findViewById(R.id.main_toolbar);
        myToolbar.inflateMenu(R.menu.toolbar_menu);
        myToolbar.setOnMenuItemClickListener(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        FloatingActionButton fab = view.findViewById(R.id.add_event_circle);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autocompleteCaller = ADD_CALLED_AUTOCOMPLETE;
                Log.d("NAVIGATION", "searching");
                onSearchCalled();
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("NAVIGATION", "Fragment stopped, saving state");
        MapStateManager mapStateManager = new MapStateManager(this.getContext());
        mapStateManager.saveMapState(mGoogleMap);

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Event markerInfo = mMarkerMap.get(marker);
        EventWindowMap event = new EventWindowMap(markerInfo, mGoogleMap);
        event.show(getParentFragmentManager(), "eventMap");
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Log.d("NAVIGATION", "setting");
                return true;
            case R.id.action_search:
                autocompleteCaller = SEARCH_CALLED_AUTOCOMPLETE;
                onSearchCalled();
                return true;

            case R.id.action_centerOnLocation:
                setCameraOnDeviceLocation(false);
                return true;


            case R.id.action_signout:
                signOutUser();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

    }

    private void signOutUser() {
        // Distinguish between DEBUG and actual user mode
        // TODO: OnCreate of main, check if no user is logged in and crash (you know what I mean) cause security fam
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(getContext(), "Goodbye " + mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "DEBUG MODE - Signed Out", Toast.LENGTH_SHORT).show();
        }

        // Sign Out the handle regardless
        mAuth.signOut();

        // Start a fresh app instance
        // TODO: In a production Android app, this should be done by a robust receiver instead
        Intent loginUserActivity = new Intent(getContext(), LoginActivity.class);
        loginUserActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginUserActivity);
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
                if (autocompleteCaller == SEARCH_CALLED_AUTOCOMPLETE) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    Log.i("Autocomplete : ", "Got place : " + place.getName() + " " + place.getLatLng());
                    CameraUpdate camUpdate = CameraUpdateFactory.newLatLng(place.getLatLng());
                    mGoogleMap.moveCamera(camUpdate);
                } else if (autocompleteCaller == ADD_CALLED_AUTOCOMPLETE) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("LAT_LNG", place.getLatLng());
                    bundle.putString("LOCATION_NAME", place.getName());
                    Intent intent = new Intent(getActivity(), AddEventActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
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
        bundle.putString("LOCATION_NAME", getAddress(this.getContext(), latLng.latitude, latLng.longitude));
        Intent intent = new Intent(getActivity(), AddEventActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public String getAddress(Context ctx, double lat, double lng){
        String fullAdd=null;
        try{
            Geocoder geocoder= new Geocoder(ctx, Locale.getDefault());
            List<android.location.Address> addresses = geocoder.getFromLocation(lat,lng,1);
            if(addresses.size()>0){
                Address address = addresses.get(0);
                fullAdd = address.getAddressLine(0);

                // if you want only city or pin code use following code //
           /* String Location = address.getLocality();
            String zip = address.getPostalCode();
            String Country = address.getCountryName(); */
            }


        }catch(IOException ex){
            ex.printStackTrace();
        }
        return fullAdd;
    }

    //region Map Location

    //---------------------MAP POSITION-----------------

    private void setCameraOnSavedState() {

        Log.d("NAVIGATION", "Getting last saved map");
        MapStateManager mapStateManager = new MapStateManager(this.getContext());
        CameraPosition cameraPosition = mapStateManager.getSavedCameraPosition();
        if (cameraPosition != null) {
            Log.d("NAVIGATION", "Setting map position with saved map found");
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mGoogleMap.moveCamera(update);
        } else {
            Log.e("NAVIGATION", "No saved map was found, setting on default position");
            setCameraOnDefaultPosition();
        }

    }

    private void setCameraOnDefaultPosition() {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(33.753746, -84.386330), DEFAULT_ZOOM));
    }


    //---------------------USER LOCATION-----------------


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        Log.d("NAVIGATION", "Getting permission");
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        Log.d("NAVIGATION", "permi result");
        switch (requestCode) {

            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    Log.i("NAVIGATION", "location permission was approved");
                    updateLocationUI();
                    setCameraOnDeviceLocation();
                }
            }
        }
    }

    private void updateLocationUI() {
        Log.d("NAVIGATION", "UPDATE UI");

        try {
            if (locationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
            }
        } catch (SecurityException e) {
            Log.e("NAVIGATION", e.getMessage());
        }
    }
    //Set Camera on Device location if location is available
    private void setCameraOnDeviceLocation(Boolean setDefaultIfUnavailable){
        if (setDefaultIfUnavailable){
            setCameraOnDeviceLocation();
        }
        getLocationPermission();
        try {
            if (locationPermissionGranted) {
                Log.d("NAVIGATION", "permission granted");
                Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.isSuccessful()) {
                            
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        }
                    }
                });
            }

        } catch (SecurityException e)  {
            Log.d("NAVIGATION", "security pbm");
            Log.e("NAVIGATION", e.getMessage(), e);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this.getContext(), "Could not get device location", Toast.LENGTH_LONG);
            toast.show();
        }
    }
    //Set Camera on Device Location or on last saved state if the device location cannot be retrieved
    private void setCameraOnDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Log.d("NAVIGATION", "permission granted");
                Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                Log.d("NAVIGATION", "Current location is null. Using last saved state.");
                                setCameraOnSavedState();
                            }
                        }
                    }
                });
            } else {
                Log.d("NAVIGATION", "Using last saved state not granted");
                setCameraOnSavedState();
            }

        } catch (SecurityException e) {
            Log.d("NAVIGATION", "security pbm");
            Log.e("NAVIGATION", e.getMessage(), e);
        }
    }

    //endregion

    public void getEventsFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("events");
        Log.d("NAVIGATION", "getting events");
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d("NAVIGATION", "error",task.getException());
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    for (DataSnapshot event : task.getResult().getChildren()) {
                        events.add(Event.fromDataSnapshot(event));
                    }
                    Log.d("NAVIGATION", "got events");
                    if (events != null && events.size() > 0) {
                        Log.d("Got events :", events.get(0).toString(), task.getException());
                    }
                    addEventsOnMap();
                }
            }
        });

    }

    public void addEventsOnMap(){
        for (int i = 0; i < events.size(); i++) {
            LatLng eventLocation = new LatLng(events.get(i).getLatitude(), events.get(i).getLongitude());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(events.get(i).getDate());
                Log.d("Date Test", date.toString());
                // TODO: Change the next line so events created today will show up
                // To be confirmed : work on my end
                if (date.after(new Date())) {
                    int sportIcon = R.drawable.ic_default;
                    switch(events.get(i).getSport()) {
                        case "Basketball":
                            sportIcon = R.drawable.ic_basketball;
                            break;
                        case "Volleyball":
                            sportIcon = R.drawable.ic_volleyball;
                            break;
                        case "Soccer":
                            sportIcon = R.drawable.ic_soccer;
                            break;
                        case "Tennis":
                            sportIcon = R.drawable.ic_tennis;
                            break;
                        case "Football":
                            sportIcon = R.drawable.ic_football;
                            break;
                        case "Rugby":
                            sportIcon = R.drawable.ic_rugby;
                            break;
                        case "Ultimate":
                            sportIcon = R.drawable.ic_frisbee;
                            break;
                        case "Handball":
                            sportIcon = R.drawable.ic_handball;
                            break;
                        default:
                            sportIcon = R.drawable.ic_default;
                    }
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(eventLocation)
                            .title(events.get(i).getName())
                            .icon(BitmapDescriptorFactory.fromResource(sportIcon)));
                    mMarkerMap.put(marker, events.get((i)));
                    mGoogleMap.setOnInfoWindowClickListener(MapsFragment.this);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}