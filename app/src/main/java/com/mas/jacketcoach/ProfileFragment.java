package com.mas.jacketcoach;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mas.jacketcoach.helper.Validator;
import com.mas.jacketcoach.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Fragment UI elements as initialized in onViewCreated
    private CircleImageView userImageDisplay;
    private EditText nickNameDisplay;
    private EditText fullNameDisplay;
    private TextView emailDisplay;
    private MaterialButton updateUserInfoButton;


    // Firebase Auth and DB handles
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private DatabaseReference userDBRef; // user specific

    private final String TAG = "Profile-LOG";

    // Current local user handle
    User current;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Initialize Firebase handles
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userImageDisplay = (CircleImageView) getView().findViewById(R.id.userImageDisplay);
        userImageDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "User image update has not been implemented yet!", Toast.LENGTH_SHORT).show();
            }
        });

        nickNameDisplay = (EditText) getView().findViewById(R.id.nickNameDisplay);
        fullNameDisplay = (EditText) getView().findViewById(R.id.fullNameDisplay);
        emailDisplay = (TextView) getView().findViewById(R.id.emailDisplay);

        // Register a handler for user information change
        if (mAuth.getCurrentUser() != null) {
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get User object and use the values to update the UI
                    current = dataSnapshot.getValue(User.class);

                    nickNameDisplay.setText(current.getPlayNickname());
                    fullNameDisplay.setText(current.getFullName());
                    emailDisplay.setText(current.getEmail());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Error reading registered user data.", Toast.LENGTH_SHORT).show();
                    // Getting user failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            userDBRef = mRef.child("Users").child(mAuth.getCurrentUser().getUid());
            userDBRef.addValueEventListener(postListener);
        }

        updateUserInfoButton = (MaterialButton) getView().findViewById(R.id.updateUserInfoButton);
        updateUserInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                updateUserInfo();
            }
        });
    }

    // Button event handler for updating user info
    private void updateUserInfo() {
        // Making sure user handle is valid (e.g. not in DEBUG mode)
        if (current == null) {
            Toast.makeText(getContext(), "Error: Can't update null user info.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate inputs as they are all processed at once
        if (!Validator.isValidText(nickNameDisplay.getText().toString())) {
            nickNameDisplay.setError("Invalid Nickname Entered");
            return;
        }

        if (!Validator.isValidText(fullNameDisplay.getText().toString())) {
            fullNameDisplay.setError("Invalid Name Entered");
            return;
        }

        // Update local handle to be pushed to database
        current.setPlayNickname(nickNameDisplay.getText().toString());
        current.setFullName(fullNameDisplay.getText().toString());

        // Update DB
        DatabaseReference currentUserRef = mRef.child("Users").child(mAuth.getCurrentUser().getUid());
        currentUserRef.setValue(current);

        Toast.makeText(getContext(), "User info updated!", Toast.LENGTH_SHORT).show();
    }
}