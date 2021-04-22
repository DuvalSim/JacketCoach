package com.mas.jacketcoach;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mas.jacketcoach.model.Event;

import java.util.ArrayList;
import java.util.Collection;

public abstract class BaseEventFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    protected FirebaseAuth mAuth;
    protected DatabaseReference mDatabase;


    protected EventsAdapter mListViewAdapter;


    /**
     * Return the firebase query that should be used to get the events to be displayed in the fragment
     * The attribute mDatabase should be used to return the query.
     * @return      The firebase Query object or null
     */
    protected abstract Query getEventQuery();

    protected Collection<Event> handleDataSnapshot(Iterable<DataSnapshot> events) {
        ArrayList<Event> eventList = new ArrayList<>();
        for (DataSnapshot event : events) {
            eventList.add(Event.fromDataSnapshot(event));
        }
        return eventList;
    }

    public BaseEventFragment() {
        // Required empty public constructor
    }

    private void populateEventArray() {
        Query query = getEventQuery();
        if (query == null){
            Log.d("NAVIGATION", "query is null");
            return;
        }
        query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Log.d("NAVIGATION", "onComplete");
                if (!task.isSuccessful()) {

                    Log.e("Firebase", "Error getting data in populate event array", task.getException());
                } else {
                    Collection<Event> eventList = handleDataSnapshot(task.getResult().getChildren());

                    mListViewAdapter.addAll(eventList);
                }
            }
        });


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("NAVIGATION", "createView");
        View view = inflater.inflate(R.layout.fragment_event, container, false);


        mListViewAdapter = new EventsAdapter(getContext(), new ArrayList<Event>());
        ListView listView = (ListView) view.findViewById(R.id.listViewEvent);

        listView.setAdapter(mListViewAdapter);
        Log.d("NAVIGATION", "populate event array");
        populateEventArray();
        // Inflate the layout for this fragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("startActivityDebug", "onItemClick");
                Event event = (Event) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), EventMonitor.class);
                intent.putExtra(getString(R.string.extra_eventToMonitor), event);
                startActivity(intent);
            }
        });
        return view;
    }

    public class EventsAdapter extends ArrayAdapter<Event> {
        public EventsAdapter(Context context, ArrayList<Event> events){
            super(context,0,events);
        }

        private class ViewHolder {
            TextView eventName;
            TextView eventDate;
            TextView eventSport;
            ImageView sportLogo;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Event event = getItem(position);
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
                viewHolder.eventName = (TextView) convertView.findViewById(R.id.event_name);
                viewHolder.eventDate = (TextView) convertView.findViewById(R.id.event_date);
                viewHolder.eventSport = (TextView) convertView.findViewById(R.id.event_sport);
                viewHolder.sportLogo = (ImageView) convertView.findViewById(R.id.event_sport_logo);

                // Cache the viewHolder
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.eventName.setText(event.getName());
            viewHolder.eventName.setTypeface(Typeface.DEFAULT_BOLD);

            viewHolder.eventDate.setText(event.getDate());

            viewHolder.eventSport.setText(event.getSport());
            viewHolder.eventSport.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));

            int sportIcon;
            switch(event.getSport()) {
                case "Basketball":
                    sportIcon = R.drawable.ic_basketball;
                    break;
                case "Volleyball":
                    sportIcon = R.drawable.ic_volleyball;
                    break;
                case "Soccer":
                    sportIcon = R.drawable.ic_soccer;
                    break;
                case "Tennis":
                    sportIcon = R.drawable.ic_tennis;
                    break;
                case "Football":
                    sportIcon = R.drawable.ic_football;
                    break;
                case "Rugby":
                    sportIcon = R.drawable.ic_rugby;
                    break;
                case "Ultimate":
                    sportIcon = R.drawable.ic_frisbee;
                    break;
                case "Handball":
                    sportIcon = R.drawable.ic_handball;
                    break;
                default:
                    sportIcon = R.drawable.ic_default;
            }

            viewHolder.sportLogo.setBackgroundResource(sportIcon);

            return convertView;
        }
    }

}
