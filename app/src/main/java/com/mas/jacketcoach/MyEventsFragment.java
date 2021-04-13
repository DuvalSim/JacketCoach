package com.mas.jacketcoach;

import android.util.Log;

import com.google.firebase.database.Query;
import com.mas.jacketcoach.model.Event;

import java.util.ArrayList;

public class MyEventsFragment extends BaseEventFragment {


    @Override
    protected Query getEventQuery() {
        Log.d("NAVIGATION", mAuth.getCurrentUser().getUid());
        if (mAuth.getCurrentUser() != null){
            Log.d("NAVIGATION","connected");
            return mDatabase.child("events").orderByChild("idOrganizer").equalTo(mAuth.getCurrentUser().getUid());
        } else {
            return null;
        }
    }
}
