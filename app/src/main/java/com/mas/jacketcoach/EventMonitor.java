package com.mas.jacketcoach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mas.jacketcoach.helper.MapStateManager;
import com.mas.jacketcoach.model.Event;
import com.mas.jacketcoach.model.User;

import java.util.ArrayList;
import java.util.Collection;

public class EventMonitor extends AppCompatActivity {

    private Event mEvent;
    // Views
    private TextView eventName_textView;
    private TextView eventDescription_textView;
    private TextView maxPlayers_textView;
    private TextView eventSport_textView;
    private TextView eventDate_textView;
    private TextView eventOrganizer_textView;
    private LinearLayout players_layout;
    private MaterialButton viewOnMap_button;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ValueEventListener listener;
    private DatabaseReference eventRef;
    private ArrayList<User> users;

    private boolean isCurrentUserOrganizer;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("startActivityDebug", "onStart event monitor");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("NAVIGATION", "event monitor");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_eventmonitor);
        // Get event to be displayed (before DB update)
        users = new ArrayList<>();
        mEvent = (Event) getIntent().getSerializableExtra("EVENT_MONITORED");
        isCurrentUserOrganizer = mAuth.getCurrentUser().getUid().equals(mEvent.getIdOrganizer());
        eventName_textView = (TextView) findViewById(R.id.event_name);
        eventDescription_textView = (TextView) findViewById(R.id.event_description);
        maxPlayers_textView = (TextView) findViewById(R.id.maxplayers);
        eventSport_textView = (TextView) findViewById(R.id.event_sport);
        eventDate_textView = (TextView) findViewById(R.id.event_date);
        players_layout = (LinearLayout) findViewById(R.id.layout_players);
        eventOrganizer_textView = (TextView) findViewById(R.id.event_organizer);
        viewOnMap_button = (MaterialButton) findViewById(R.id.event_view_on_map);
        viewOnMap_button.setOnClickListener(viewOnMapClicked);
        // Getting event reference in DB
        eventRef = mDatabase.child(getString(R.string.events_table_key)).child(mEvent.getId());
        listener = eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Add this event id to the user assigned event list
                Log.d("EventMonitor", mEvent.getId());
                mEvent = snapshot.getValue(Event.class);
                getUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error getting participant info: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        Toolbar mToolbar = (Toolbar) findViewById(R.id.monitoring_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void updateUI(){
        // Remove layouts of participants to prevent adding users already in place
        players_layout.removeViews(1, players_layout.getChildCount() - 2);

        eventName_textView.setText(mEvent.getName());
        eventDescription_textView.setText(mEvent.getDescription());
        maxPlayers_textView.setText(String.valueOf(mEvent.getMaxplayers()));
        eventSport_textView.setText(mEvent.getSport());
        eventDate_textView.setText(mEvent.getDate());

        String nameOrganizer = searchUserWithUID(mEvent.getIdOrganizer());
        eventOrganizer_textView.setText(nameOrganizer);

        int index = 0;
        Collection<String> participatingUsers = (Collection<String>) mEvent.getPlayers().clone();
        participatingUsers.remove(mEvent.getIdOrganizer());
        for (String player : participatingUsers){

            LinearLayout playerLayout = new LinearLayout(this);
            playerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            TextView playerName = new TextView(this);
            String name = searchUserWithUID(player);
            playerName.setText(name);
            playerName.setTextSize(18);
            playerName.setLayoutParams(eventName_textView.getLayoutParams());
            playerLayout.addView(playerName);

            if(isCurrentUserOrganizer) {
                ImageButton removePlayer = new ImageButton(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.weight = 1.0f;
                params.gravity = Gravity.CENTER_HORIZONTAL;
                removePlayer.setLayoutParams(params);
                removePlayer.setImageResource(R.drawable.ic_remove_player);
                removePlayer.setPadding(50, 0, 0, 7);
                removePlayer.setBackground(null);
                removePlayer.setTag(R.string.TAG_PLAYER, player);
                removePlayer.setLayoutParams(params);
                removePlayer.setOnClickListener(removePlayerClicked);
                playerLayout.addView(removePlayer);
            }
            players_layout.addView(playerLayout, ++index);

        }
        if(index == 0){
            TextView noPlayerTextView = new TextView(this);
            noPlayerTextView.setText(R.string.monitoring_noPlayerRegistered);
            noPlayerTextView.setTextSize(18);
            players_layout.addView(noPlayerTextView, 1);
        }
    }
    private View.OnClickListener viewOnMapClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MapStateManager mapStateManager = new MapStateManager(getApplicationContext());
            mapStateManager.centerOnEvent((float) mEvent.getLatitude(), (float) mEvent.getLongitude());
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    };
    private View.OnClickListener removePlayerClicked = new View.OnClickListener() {
        public void onClick(View v) {
            String playerId = (String) v.getTag(R.string.TAG_PLAYER);

            // Remove event from user list
            DatabaseReference userRef = mDatabase.child(getString(R.string.users_table_key)).child(playerId);
            userRef.get().addOnCompleteListener( new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("EventMonitor", "Error getting user reference", task.getException());
                    } else {
                        User participant = task.getResult().getValue(User.class);
                        Log.d("EventMonitor", "Got result");
                        Log.d("EventMonitor", participant.toString());
//                        User participant = task.getResult().getValue(User.class);
                        participant.getUserEvents().remove(mEvent.getId());
                        Log.d("EventMonitor", participant.getUserEvents().toString());
                        Log.d("EventMonitor", mEvent.getId());
                        userRef.setValue(participant);
                    }
                }
            });
            Log.d("EventMonitor", "removing Player");
            // Remove users from event

            mEvent.getPlayers().remove(playerId);
            mDatabase.child("events").child(mEvent.getId()).child(getString(R.string.firebase_event_players_key)).setValue(mEvent.getPlayers());

            Toast toast = Toast.makeText(v.getContext(),"Player has been removed :".concat(playerId), Toast.LENGTH_SHORT);
            toast.show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(eventRef != null && listener != null){
            Log.d("EventMonitor", "remove listener");
            eventRef.removeEventListener(listener);
        }

    }

    protected Collection<User> handleDataSnapshot(Iterable<DataSnapshot> users) {
        ArrayList<User> userList = new ArrayList<>();
        for (DataSnapshot user : users) {
            userList.add(User.fromDataSnapshot(user));
        }
        return userList;
    }

    private void getUsers(){
        mDatabase.child(getString(R.string.users_table_key)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Log.d("NAME_UID", "onComplete");
                if (!task.isSuccessful()) {
                    Log.d("NAME_UID", "Does not work");
                    Log.e("Firebase", "Error getting user in getName", task.getException());
                } else {
                    Collection<User> userList = handleDataSnapshot(task.getResult().getChildren());
                    Log.d("NAME_UID",String.valueOf(task.getResult().getChildren()));
                    Log.d("NAME_UID", "Got user !", task.getException());
                    users.addAll(userList);
                    updateUI();
                }
            }
        });
    }

    private String searchUserWithUID(String uid) {
        for (User user : users) {
            if (user.getUid().equals(uid)) {
                return user.getPlayNickname();
            }
        }
        return "Nickname not found";
    }

}
