package com.mas.jacketcoach.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapStateManager {

    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String ZOOM = "zoom";
    private static final String BEARING = "bearing";
    private static final String TILT = "tilt";
    private static final String MAPTYPE = "MAPTYPE";

    private static final String PREFS_NAME ="mapState";
    private static boolean mapHasalreadyBeenCreated = false;
    private static boolean centeredOnEvent = false;

    private SharedPreferences mapStatePrefs;

    public MapStateManager(Context context) {
        mapStatePrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveMapState(GoogleMap mapMie) {
        SharedPreferences.Editor editor = mapStatePrefs.edit();

        CameraPosition position = mapMie.getCameraPosition();

        editor.putFloat(LATITUDE, (float) position.target.latitude);
        editor.putFloat(LONGITUDE, (float) position.target.longitude);
        editor.putFloat(ZOOM, position.zoom);
        editor.putFloat(TILT, position.tilt);
        editor.putFloat(BEARING, position.bearing);
        editor.putInt(MAPTYPE, mapMie.getMapType());
        editor.commit();
        mapHasalreadyBeenCreated = true;
        centeredOnEvent = false;
    }
    public boolean mapStateIsOutdated() {
        //outdated if application just started or no map available
        double latitude = mapStatePrefs.getFloat(LATITUDE, 0);
        Log.d("NAVIGATION", "Centered on event is :");
        if(centeredOnEvent){
            Log.d("NAVIGATION", "true");
        }

        Boolean outdated = !centeredOnEvent && ((! mapHasalreadyBeenCreated) || (latitude == 0));
        return outdated ;
    };

    public void onCreateMainActivity() {mapHasalreadyBeenCreated = false; };
    public CameraPosition getSavedCameraPosition() {
        double latitude = mapStatePrefs.getFloat(LATITUDE, 0);
        if (latitude == 0) {
            return null;
        }
        double longitude = mapStatePrefs.getFloat(LONGITUDE, 0);
        LatLng target = new LatLng(latitude, longitude);

        float zoom = mapStatePrefs.getFloat(ZOOM, 0);
        float bearing = mapStatePrefs.getFloat(BEARING, 0);
        float tilt = mapStatePrefs.getFloat(TILT, 0);

        CameraPosition position = new CameraPosition(target, zoom, tilt, bearing);
        return position;
    }

    public void centerOnEvent(float latitude, float longitude) {
        SharedPreferences.Editor editor = mapStatePrefs.edit();
        editor.putFloat(LATITUDE, latitude);
        editor.putFloat(LONGITUDE,longitude);
        editor.commit();
        centeredOnEvent = true;

    }

    public int getSavedMapType() {
        return mapStatePrefs.getInt(MAPTYPE, GoogleMap.MAP_TYPE_NORMAL);
    }

}
