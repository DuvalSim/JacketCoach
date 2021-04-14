package com.mas.jacketcoach;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mas.jacketcoach.model.Event;
import com.mas.jacketcoach.model.User;

public class EventWindowMap extends BottomSheetDialogFragment {
    final String TAG = "EventWindowMap-log-tag";
    private View view;

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

    private GoogleMap mGoogleMap;

    public EventWindowMap(Event eventInfo, GoogleMap mGoogleMap) {
        this.eventInfo = eventInfo;
        this.mGoogleMap = mGoogleMap;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.event_windowmap, container, false);

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
                                    // delete the user from the players list of this event
                                    eventInfo.getPlayers().remove(mAuth.getCurrentUser().getUid());
                                    updateEventsPlayerInDB();
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
                    updateEventsPlayerInDB();
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

                // Get host information
                // Note: Host = Organizer

                DatabaseReference userRef = mDatabase.child(getString(R.string.users_table_key)).child(eventInfo.getIdOrganizer());
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User organizer = snapshot.getValue(User.class);
                        String hostFullName = organizer.getFullName();
                        String hostNickName = organizer.getPlayNickname();
                        String hostEmail = organizer.getEmail();
                        String hostPhoneNumber = organizer.getPhoneNumber();

                        new AlertDialog.Builder(getContext())
                                .setTitle("Organizer Info")
                                .setMessage(Html.fromHtml("<b>Name:</b> " + hostFullName + "<br>" +
                                        "<b>Play Nickname:</b> " + hostNickName + "<br>" +
                                        "<b>Email:</b> " + hostEmail + "<br>" +
                                        "<b>Phone number:</b> " + hostPhoneNumber))

                                .setPositiveButton("Call Organizer", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Uri number = Uri.parse("tel:"+hostPhoneNumber);
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                                        startActivity(callIntent);
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_menu_info_details)
                                .show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Error getting organizer info: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "To-Implement: Share via facebook, calendar invite, etc!", Toast.LENGTH_SHORT).show();
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

                new AlertDialog.Builder(getContext())
                        .setTitle("Event Cancellation")
                        .setMessage("Are you sure you want to cancel this event? This action is irreversible.")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                cancelEventFromDB();

                                // Remove this marker from MapsFragment
                                reloadFragment();

                                Toast.makeText(getActivity(), "Event cancelled", Toast.LENGTH_SHORT).show();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        return view;
    }

    // Helper method for reloading the fragment so marker information get updated
    private void reloadFragment() {


//        LatLng eventLocation = new LatLng(eventInfo.getLatitude(), eventInfo.getLongitude());
//        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
//                .position(eventLocation)
//                .title(eventInfo.getName()));
//        marker.remove();
//        mGoogleMap.setOnInfoWindowClickListener();

        // Close this window
        dismiss();

//        Fragment currentFragment =  getActivity().getSupportFragmentManager().findFragmentById(R.id.mapFragmentConstraintContainer);
//        if (currentFragment != null) {
//            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.detach(currentFragment);
//            fragmentTransaction.attach(currentFragment);
//            fragmentTransaction.commit();
//        }
    }

    // Helper method for removing an event node from the DB
    private void cancelEventFromDB() {
        DatabaseReference eventRef = mDatabase.child(getString(R.string.events_table_key)).child(eventInfo.getId());
        eventRef.removeValue();
    }

    // Helper method for updating the Players node for adding/removing participants
    private void updateEventsPlayerInDB() {
        DatabaseReference eventRef = mDatabase.child(getString(R.string.events_table_key)).child(eventInfo.getId()).child(getString(R.string.events_players_key));
        eventRef.setValue(eventInfo.getPlayers());
    }
}