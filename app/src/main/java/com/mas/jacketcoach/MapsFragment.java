package com.mas.jacketcoach;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;

public class MapsFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    private FirebaseEvents eventsData;
    private GoogleMap mGoogleMap;

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
        events = eventsData.getEvents();

        for(int i=0; i<events.size();i++){
            LatLng eventLocation = new LatLng(events.get(i).getLatitude(),events.get(i).getLongitude());
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(eventLocation)
                    .title(events.get(i).getNom())
                    .snippet(events.get(i).getSport() + events.get(i).getDate()));
            mGoogleMap.setOnInfoWindowClickListener(MapsFragment.this);
        }
//        LatLng sydney = new LatLng(-34, 151);
//        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(marker.getSnippet())
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}