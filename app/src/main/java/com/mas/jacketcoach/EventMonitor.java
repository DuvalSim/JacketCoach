package com.mas.jacketcoach;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Layout;
import android.util.Log;
import android.util.Size;
import android.view.CollapsibleActionView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mas.jacketcoach.model.Event;
import com.mas.jacketcoach.model.User;

import org.w3c.dom.Text;

import java.util.Collection;

public class EventMonitor extends AppCompatActivity {

    private Event mEvent;
    private TextView eventName_textView;
    private TextView eventSport_textView;
    private LinearLayout players_layout;
    private DatabaseReference mDatabase;
//    private TextView

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_eventmonitor);
        mEvent = (Event) getIntent().getSerializableExtra("EVENT_MONITORED");
        DatabaseReference eventRef = mDatabase.child(getString(R.string.events_table_key)).child(mEvent.getId());
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Add this event id to the user assigned event list
                mEvent = snapshot.getValue(Event.class);
                Log.d("EventMonitor", mEvent.getPlayers().toString());
                updateUI();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error getting participant info: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void updateUI(){

        eventName_textView = (TextView) findViewById(R.id.event_name);
        eventSport_textView = (TextView) findViewById(R.id.event_sport);

        players_layout = (LinearLayout) findViewById(R.id.layout_players);
        players_layout.removeViews(1, players_layout.getChildCount() - 2);
        eventName_textView.setText(mEvent.getName());
        eventSport_textView.setText(mEvent.getSport());

        // Organizer :
        TextView eventOrganizer_textView = (TextView) findViewById(R.id.event_organizer);
        eventOrganizer_textView.setText(mEvent.getIdOrganizer());
        int index = 0;
        Collection<String> participatingUsers = (Collection<String>) mEvent.getPlayers().clone();
        participatingUsers.remove(mEvent.getIdOrganizer());
        for (String player : participatingUsers){
//            LinearLayout playerLayout = new LinearLayout(this);
//            ViewGroup playerLayout = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.textview_monitor_event, null,false );
//
//            Log.d("EventMonitor", "COME ON");
//            TextView playerName = (TextView) playerLayout.getChildAt(0);
//            playerName.setText(player);

//            ImageButton removePlayer = (ImageButton) playerLayout.findViewById(R.id.remove_player_button);
//            removePlayer.setBackground(null);
            LinearLayout playerLayout = new LinearLayout(this);
//            playerLayout.setOrientation(LinearLayout.HORIZONTAL);
            playerLayout.setId(index);
            playerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            TextView playerName = new TextView(this);
            playerName.setText(player);
            playerName.setTextSize(18);

            playerLayout.addView(playerName);
            playerName.setLayoutParams(eventName_textView.getLayoutParams());
            ImageButton removePlayer = new ImageButton(this);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.(2);
            params.weight = 1.0f;
            params.gravity = Gravity.CENTER_HORIZONTAL;
            removePlayer.setLayoutParams(params);
            removePlayer.setImageResource(R.drawable.ic_remove_player);
            removePlayer.setPadding(50,0,0,7);
            removePlayer.setBackground(null);
            removePlayer.setTag(R.string.TAG_PLAYER,player);
            removePlayer.setTag(R.string.TAG_LAYOUT, playerLayout.getId());
            removePlayer.setLayoutParams(params);
            removePlayer.setOnClickListener(removePlayerClicked);
            playerLayout.addView(removePlayer);

            players_layout.addView(playerLayout, ++index);

        }
        if(index == 0){
            TextView noPlayerTextView = new TextView(this);
            noPlayerTextView.setText("No player registered yet");
            players_layout.addView(noPlayerTextView, 1);
        }
    }

    private View.OnClickListener removePlayerClicked = new View.OnClickListener() {
        public void onClick(View v) {
            String playerId = (String) v.getTag(R.string.TAG_PLAYER);
            int layoutId = (int) v.getTag(R.string.TAG_LAYOUT);
            Toast toast = Toast.makeText(v.getContext(),"remove player ".concat(playerId), Toast.LENGTH_SHORT);
            toast.show();

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
            Log.d("EventMonitor", "removing Plater");
            // Remove users from event

            mEvent.getPlayers().remove(playerId);
            mDatabase.child("events").child(mEvent.getId()).child(getString(R.string.firebase_event_players_key)).setValue(mEvent.getPlayers());

//            View layoutToDelete =  findViewById(layoutId);
//            players_layout.removeView(layoutToDelete);
//            if(mEvent.getPlayers().size() == 1){
//                TextView noPlayerTextView = new TextView(getApplicationContext());
//                noPlayerTextView.setText("No player registered yet");
//                players_layout.addView(noPlayerTextView, 1);
//            }
        }
    };


}
