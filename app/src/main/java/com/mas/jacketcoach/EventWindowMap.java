package com.mas.jacketcoach;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.mas.jacketcoach.model.Event;

public class EventWindowMap extends BottomSheetDialogFragment {
    private MaterialButton buttonParticipate;
    private MaterialButton buttonContact;
    private MaterialButton buttonShare;
    private MaterialButton buttonCancelEvent;
    private TextView textEventName;
    private TextView textEventSport;
    private TextView textEventDate;
    private Event markerInfos;

    private FirebaseAuth mAuth;

    public EventWindowMap(Event markerInfos) {
        this.markerInfos = markerInfos;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_windowmap, container, false);

        buttonParticipate = view.findViewById(R.id.button_participate);
        buttonContact = view.findViewById(R.id.button_contact);
        buttonShare = view.findViewById(R.id.button_share);
        buttonCancelEvent = view.findViewById(R.id.button_cancel_event);
        textEventName = view.findViewById(R.id.text_event_name);
        textEventSport = view.findViewById(R.id.text_event_sport);
        textEventDate = view.findViewById(R.id.text_event_date);

        textEventName.setText(markerInfos.getName());
        textEventSport.setText(markerInfos.getSport());
        textEventDate.setText(markerInfos.getDate());

        // Initialize Firebase handles
        mAuth = FirebaseAuth.getInstance();

        // Bullet-proof
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getActivity(), "Error: No associated user found. Login with a valid user.", Toast.LENGTH_LONG).show();

            // TODO: In a production Android app, this should be done by a robust receiver instead
            Intent loginUserActivity = new Intent(getActivity(), LoginActivity.class);
            loginUserActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginUserActivity);
        }

        // If user is the host, hide the participate button
        if (mAuth.getCurrentUser().getUid().equals(markerInfos.getIdOrganizer())) {
            buttonParticipate.setVisibility(View.GONE);
            buttonContact.setVisibility(View.GONE);

            buttonCancelEvent.setVisibility(View.VISIBLE);
        } else {
            buttonCancelEvent.setVisibility(View.GONE);
        }

        buttonParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This check is just added in case the check before
                if (mAuth.getCurrentUser().getUid().equals(markerInfos.getIdOrganizer())) {
                    Toast.makeText(getActivity(), "Development Error: Host should not select participate button.", Toast.LENGTH_SHORT).show();
                    return;
                }

                buttonParticipate.setText("Opt-out");
            }
        });

        buttonContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser().getUid().equals(markerInfos.getIdOrganizer())) {
                    Toast.makeText(getActivity(), "Development Error: Host should not select contact organizer button.", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        return view;
    }
}
