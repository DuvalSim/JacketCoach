package com.mas.jacketcoach;

import com.mas.jacketcoach.model.Event;

import java.util.ArrayList;

public class FirebaseEvents {

    public ArrayList<Event> getEvents(){
        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event(1,"First event", "Basket", "14 March", 33.753746, -84.386330));
        events.add(new Event(2,"Second event", "Foot", "17 March", 33.8, -84.5));
        return events;
    }
}
