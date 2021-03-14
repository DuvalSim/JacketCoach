package com.mas.jacketcoach;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mas.jacketcoach.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FirebaseEvents {

    private DatabaseReference mDatabase;
    private ArrayList<Event> events = new ArrayList<>();

    public ArrayList<Event> getEventsFirebase(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("events");
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    for (DataSnapshot event : task.getResult().getChildren()) {
                        int id = Integer.parseInt(event.child("id").getValue().toString());
                        String idOrganizer = event.child("idOrganizer").getValue().toString();
                        String name = event.child("name").getValue().toString();
                        String sport = event.child("sport").getValue().toString();
                        String date = event.child("date").getValue().toString();
                        double latitude = Double.parseDouble(event.child("latitude").getValue().toString());
                        double longitude = Double.parseDouble(event.child("longitude").getValue().toString());
                        ArrayList<String> players = new ArrayList<>();
                        for (DataSnapshot player : event.getChildren()) {
                            players.add(player.getValue().toString());
                        }
                        events.add(new Event(id, idOrganizer, name, sport,date,latitude,longitude, players));
                    }
                    Log.d("EVENTS BEFORE", String.valueOf(events));
                }
            }
        });

        //TODO : ASYNCHRONOUS CALL TO FIREBASE
        // MOCK UP EVENTS FOR NOW
        events.add(new Event(1, "2","Best Basket Event", "Basketball","2021-05-06",33.753746,-84.38633, new ArrayList<>()));
        events.add(new Event(1, "4","Go foot", "Football","2021-03-19",33.8,-84.4, new ArrayList<>()));
        return events;
    }
}
