package com.mas.jacketcoach;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mas.jacketcoach.model.Event;

import java.util.ArrayList;

public class AllEventsFragment extends BaseEventFragment {

    @Override
    protected Query getEventQuery() {
        return mDatabase.child("events");
    }
}
