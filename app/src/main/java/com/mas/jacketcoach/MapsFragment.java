package com.mas.jacketcoach;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mas.jacketcoach.model.Event;
import com.mas.jacketcoach.model.MarkerInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapsFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private FirebaseEvents eventsData;
    private Map<Marker, MarkerInfo> mMarkerMap = new HashMap<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("NAVIGATION", "activity created");
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
    public void onResume() {
        super.onResume();
        Log.d("NAVIGATION", "on Resume");
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
}