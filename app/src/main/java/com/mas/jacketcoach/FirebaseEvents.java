package com.mas.jacketcoach;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
                    for (int i=0;i<task.getResult().getChildrenCount();i++) {
                        int id = Integer.parseInt(task.getResult().child((String.valueOf(i))).child("id").getValue().toString());
                        int idOrganizer = Integer.parseInt(task.getResult().child((String.valueOf(i))).child("idOrganizer").getValue().toString());
                        String name = task.getResult().child((String.valueOf(i))).child("name").getValue().toString();
                        String sport = task.getResult().child((String.valueOf(i))).child("sport").getValue().toString();
                        String date = task.getResult().child((String.valueOf(i))).child("date").getValue().toString();
                        double latitude = Double.parseDouble(task.getResult().child((String.valueOf(i))).child("latitude").getValue().toString());
                        double longitude = Double.parseDouble(task.getResult().child((String.valueOf(i))).child("longitude").getValue().toString());
                        ArrayList<String> players = new ArrayList<>();
                            for (int j=0;j<task.getResult().child("players").getChildrenCount();j++) {
                                players.add(task.getResult().child("players").child((String.valueOf(j))).getValue().toString());
                            }
                        events.add(new Event(id, idOrganizer, name, sport,date,latitude,longitude, players));
                    }
                    Log.d("EVENTS BEFORE", String.valueOf(events));
                }
            }
        });
        //TODO : ASYNCHRONOUS CALL TO FIREBASE
        // MOCK UP EVENTS FOR NOW
        events.add(new Event(1, 2,"Best Basket Event", "Basketball","2021-05-06",33.753746,-84.38633, new ArrayList<>()));
        events.add(new Event(1, 4,"Go foot", "Football","2021-03-19",33.8,-84.4, new ArrayList<>()));
        return events;
    }
}
