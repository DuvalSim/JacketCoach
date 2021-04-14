package com.mas.jacketcoach;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.mas.jacketcoach.model.MarkerInfo;

public class EventWindowMap extends BottomSheetDialogFragment {
    private MaterialButton buttonParticipate;
    private MaterialButton buttonContact;
    private MaterialButton buttonShare;
    private TextView textEventNom;
    private TextView textEventSport;
    private TextView textEventDate;
    private MarkerInfo markerInfos;

    public EventWindowMap(MarkerInfo markerInfos) {
        this.markerInfos = markerInfos;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_windowmap, container, false);

        buttonParticipate = view.findViewById(R.id.button_participate);
        buttonContact = view.findViewById(R.id.button_contact);
        buttonShare = view.findViewById(R.id.button_share);
        textEventNom = view.findViewById(R.id.text_event_nom);
        textEventSport = view.findViewById(R.id.text_event_sport);
        textEventDate = view.findViewById(R.id.text_event_date);

        textEventNom.setText(markerInfos.getNom());
        textEventSport.setText(markerInfos.getSport());
        textEventDate.setText(markerInfos.getDate().toString());

        buttonParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // IMPLEMENT FOR PARTICIPATE BUTTON
                Log.d("Test", "Test1");
            }
        });

        buttonContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // IMPLEMENT FOR CONTACT ORGANIZER BUTTON
                Log.d("Test", "Test2");

            }
        });

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // IMPLEMENT FOR SHARE BUTTON
                Log.d("Test", "Test3");

            }
        });

        return view;
    }
}
