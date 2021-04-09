package com.mas.jacketcoach;

import com.mas.jacketcoach.model.Event;

import java.util.ArrayList;

public class SignedInEventsFragment extends BaseEventFragment {
    @Override
    public void populateEventArray() {
        eventList.add(new Event(1, "Orinize", "Name Signed in", "Sport signed in", "Date",123,123,new ArrayList<String>()));
    }
}
