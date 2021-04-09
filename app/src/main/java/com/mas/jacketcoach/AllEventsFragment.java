package com.mas.jacketcoach;

import com.mas.jacketcoach.model.Event;

import java.util.ArrayList;

public class AllEventsFragment extends BaseEventFragment {

    @Override
    public void populateEventArray() {
        eventList.add(new Event(1, "Orinize", "Name All", "Sport All", "Date",123,123,new ArrayList<String>()));
        eventList.add(new Event(1, "Orinize", "Name 2", "Sport 2", "Date",123,123,new ArrayList<String>()));
    }
}
