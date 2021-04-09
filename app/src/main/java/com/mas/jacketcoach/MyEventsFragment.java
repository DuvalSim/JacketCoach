package com.mas.jacketcoach;

import com.mas.jacketcoach.model.Event;

import java.util.ArrayList;

public class MyEventsFragment extends BaseEventFragment {
    @Override
    public void populateEventArray() {
        eventList.add(new Event(1, "Orinize", "Name", "Sport", "Date",123,123,new ArrayList<String>()));
    }
}
