package com.mas.jacketcoach;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mas.jacketcoach.model.Event;

import org.w3c.dom.Text;

import java.util.Collection;

public class EventMonitor extends AppCompatActivity {

    private Event mEvent;
    private TextView eventName_textView;
    private TextView eventSport_textView;
    private LinearLayout players_layout;
//    private TextView

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventmonitor);
        Event mEvent = (Event) getIntent().getSerializableExtra("EVENT_MONITORED");
        eventName_textView = (TextView) findViewById(R.id.event_name);
        eventSport_textView = (TextView) findViewById(R.id.event_sport);

        players_layout = (LinearLayout) findViewById(R.id.layout_players);
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
            playerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            TextView playerName = new TextView(this);
            playerName.setText(player);
            playerName.setTextSize(18);
            playerName.setId(index);
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
            removePlayer.setTag(player);
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
            String playerId = (String) v.getTag();
            Toast toast = Toast.makeText(v.getContext(),"remove player ".concat(playerId), Toast.LENGTH_SHORT);
            toast.show();
        }
    };


}
