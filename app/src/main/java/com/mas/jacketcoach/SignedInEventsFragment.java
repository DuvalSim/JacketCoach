package com.mas.jacketcoach;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.mas.jacketcoach.model.Event;

import java.util.ArrayList;
import java.util.Collection;

public class SignedInEventsFragment extends BaseEventFragment {
    //TODO : CHANGE BACKEND OR QUERY TO GET EVENTS AS THE WAY TO GET EVENTS IS FOR NOW VERY INEFFICIENT
    @Override
    protected Query getEventQuery() {
        if(mAuth.getCurrentUser() == null){
            return null;
        } else {
            return mDatabase.child("events");
        }

//        mAuth.getCurrentUser().getUid()
    }

    @Override
    protected Collection<Event> handleDataSnapshot(Iterable<DataSnapshot> events) {
        ArrayList<Event> eventList = new ArrayList<Event>();
        for(DataSnapshot event : events){
            for ( DataSnapshot player : event.child("players").getChildren()) {
                if(player.getValue().toString().equals(mAuth.getCurrentUser().getUid())){
                    eventList.add(Event.fromDataSnapshot(event));
                    break;
                }
                Log.d("EventTab", player.getValue().toString());
            }
        }
        return eventList;
    }
}
