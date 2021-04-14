package com.mas.jacketcoach;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mas.jacketcoach.model.Event;

public class EventWindowMap extends BottomSheetDialogFragment {
    private MaterialButton buttonParticipate;
    private MaterialButton buttonContact;
    private MaterialButton buttonShare;
    private MaterialButton buttonCancelEvent;
    private TextView textEventName;
    private TextView textEventSport;
    private TextView textEventDate;
    private Event eventInfo;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public EventWindowMap(Event eventInfo) {
        this.eventInfo = eventInfo;
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

        textEventName.setText(eventInfo.getName());
        textEventSport.setText(eventInfo.getSport());
        textEventDate.setText(eventInfo.getDate());

        // Initialize Firebase handles
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        // Bullet-proof
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getActivity(), "Error: No associated user found. Login with a valid user.", Toast.LENGTH_LONG).show();

            // TODO: In a production Android app, this should be done by a robust receiver instead
            Intent loginUserActivity = new Intent(getActivity(), LoginActivity.class);
            loginUserActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginUserActivity);
        }

        // If user is the host, hide the participate button
        if (mAuth.getCurrentUser().getUid().equals(eventInfo.getIdOrganizer())) {
            buttonParticipate.setVisibility(View.GONE);
            buttonContact.setVisibility(View.GONE);

            buttonCancelEvent.setVisibility(View.VISIBLE);
        } else {
            buttonCancelEvent.setVisibility(View.GONE);
        }

        // Check if current user is already a participant of this event
        if (eventInfo.getPlayers().contains(mAuth.getCurrentUser().getUid())) {
            buttonParticipate.setText(R.string.opt_out);
        }

        buttonParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Future-dev bullet-proofing
                if (mAuth.getCurrentUser().getUid().equals(eventInfo.getIdOrganizer())) {
                    Toast.makeText(getActivity(), "Development Error: Host should not select participate button.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (eventInfo.getPlayers().contains(mAuth.getCurrentUser().getUid())) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Event Opt-out")
                            .setMessage("Are you sure you want to opt-out of this event?")

                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    optOutUserFromEvent();
                                    buttonParticipate.setText(R.string.participate);
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    // New participant
                    eventInfo.getPlayers().add(mAuth.getCurrentUser().getUid());
                    updateEventsDB();
                    buttonParticipate.setText(R.string.opt_out);
                }

            }
        });

        buttonContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser().getUid().equals(eventInfo.getIdOrganizer())) {
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


        buttonCancelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Future-dev bullet-proofing
                if (!mAuth.getCurrentUser().getUid().equals(eventInfo.getIdOrganizer())) {
                    Toast.makeText(getActivity(), "Development Error: Only hosts can cancel their created events", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        return view;
    }

    // Helper method for opting-out a user from participating in an event
    private void optOutUserFromEvent() {
        // delete the user from the players list of this event
        eventInfo.getPlayers().remove(mAuth.getCurrentUser().getUid());
        updateEventsDB();
    }

    private void updateEventsDB() {
//        DatabaseReference newRef = mDatabase.child("events").push();
//        newRef.setValue(eventInfo);
    }
}
