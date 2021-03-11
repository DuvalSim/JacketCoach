package com.example.jacketcoach;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback  {

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
//        LatLng sydney = new LatLng(-34, 151);
//        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    //    private OnMapReadyCallback callback = new OnMapReadyCallback() {
//
//        /**
//         * Manipulates the map once available.
//         * This callback is triggered when the map is ready to be used.
//         * This is where we can add markers or lines, add listeners or move the camera.
//         * In this case, we just add a marker near Sydney, Australia.
//         * If Google Play services is not installed on the device, the user will be prompted to
//         * install it inside the SupportMapFragment. This method will only be triggered once the
//         * user has installed Google Play services and returned to the app.
//         */
//        @Override
//        public void onMapReady(GoogleMap googleMap) {
//            mGoogleMap = googleMap;
//            LatLng sydney = new LatLng(-34, 151);
//            mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        }
//    };

//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera.
//     * In this case, we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to
//     * install it inside the SupportMapFragment. This method will only be triggered once the
//     * user has installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
////        mGoogleMap = googleMap;
////        CameraPosition cameraPosition = mapStateManager.getSavedCameraPosition();
////        if(cameraPosition != null){
////            CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
////
////            mGoogleMap.moveCamera(update);
////        }
//        LatLng sydney = new LatLng(-34, 151);
//        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }

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
}

