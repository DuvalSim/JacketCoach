package com.mas.jacketcoach;

import com.google.firebase.database.Query;
import com.mas.jacketcoach.model.Event;

import java.util.ArrayList;

public class SignedInEventsFragment extends BaseEventFragment {

    @Override
    protected Query getEventQuery() {
        return null;
//        mAuth.getCurrentUser().getUid()
    }
}
