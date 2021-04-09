package com.mas.jacketcoach;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mas.jacketcoach.model.Event;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEventFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    protected ArrayList<Event> eventList;
    public abstract void populateEventArray();

    public BaseEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        eventList = new ArrayList<Event>();
        populateEventArray();
        EventsAdapter listViewAdapter = new EventsAdapter(getContext(), eventList);
        ListView listView = (ListView) view.findViewById(R.id.listViewEvent);

        listView.setAdapter(listViewAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    public class EventsAdapter extends ArrayAdapter<Event> {
        public EventsAdapter(Context context, ArrayList<Event> events){
            super(context,0,events);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Event event = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
            }
            TextView eventName = (TextView) convertView.findViewById(R.id.event_name);
            TextView eventSport = (TextView) convertView.findViewById(R.id.event_sport);
            eventName.setText(event.getName());
            eventSport.setText(event.getSport());

            return convertView;
        }
    }


}
