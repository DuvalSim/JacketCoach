package com.mas.jacketcoach;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mas.jacketcoach.model.MarkerInfo;

public class EventWindowMap extends BottomSheetDialogFragment {
    private Button buttonParticipate;
    private Button buttonContact;
    private Button buttonShare;
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
            }
        });

        buttonContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // IMPLEMENT FOR CONTACT ORGANIZER BUTTON
            }
        });

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // IMPLEMENT FOR SHARE BUTTON
            }
        });

        return view;
    }
}
