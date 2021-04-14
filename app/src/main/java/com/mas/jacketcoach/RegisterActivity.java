package com.mas.jacketcoach;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mas.jacketcoach.helper.Validator;
import com.mas.jacketcoach.model.User;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText nickNameEditText;
    private EditText fullNameEditText;
    private EditText registerEmailAddressEditText;
    private EditText registerPasswordEditText;
    private EditText registerPhoneNumberEditText;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;

    private final String TAG = "RegisterActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase DB
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        nickNameEditText = (EditText) findViewById(R.id.nickNameEditText);
        fullNameEditText = (EditText) findViewById(R.id.fullNameEditText);
        registerEmailAddressEditText = (EditText) findViewById(R.id.registerEmailAddressEditText);
        registerPasswordEditText = (EditText) findViewById(R.id.registerPasswordEditText);
        registerPhoneNumberEditText = (EditText) findViewById(R.id.registerPhoneNumberEditText);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mUser = mAuth.getCurrentUser();
        if (mUser != null){
            // reload();
        }
    }

    // Register button onClick handler
    public void registerUser(View view) {
        String enteredNickname = nickNameEditText.getText().toString();
        String enteredFirstName = fullNameEditText.getText().toString();

        if (!Validator.isValidText(enteredNickname)) {
            nickNameEditText.setError("Invalid Nickname Entered");
            return;
        }

        if (!Validator.isValidText(enteredFirstName)) {
            fullNameEditText.setError("Invalid Name Entered");
            return;
        }

        String enteredEmail = registerEmailAddressEditText.getText().toString();
        String enteredPassword = registerPasswordEditText.getText().toString();

        if (!Validator.isValidEmail(enteredEmail)) {
            registerEmailAddressEditText.setError("Invalid Email Entered");
            return;
        }

        if (!Validator.isValidPassword(enteredPassword)) {
            registerPasswordEditText.setError("Invalid Password Entered");
            return;
        }

        String enteredPhoneNumber = registerPhoneNumberEditText.getText().toString();

        if (!Validator.isValidPhoneNumber(enteredPhoneNumber)) {
            registerPhoneNumberEditText.setError("Invalid Phone Number Entered");
            return;
        }

        // Sign a new user up with their email and password
        mAuth.createUserWithEmailAndPassword(enteredEmail, enteredPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            // Get the current user and update their Firebase profile name
                            mUser = Objects.requireNonNull(task.getResult()).getUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(enteredFirstName)
                                    .build();
                            mUser.updateProfile(profileUpdates);

                            // Update the firebase user DB with the user info
                            User currentUser = new User(mUser.getUid(), enteredNickname, enteredFirstName, enteredEmail, enteredPhoneNumber);
                            updateUsersDatabase(currentUser);

                            // Create new user session
                            Intent mainUserActivity = new Intent(RegisterActivity.this, MainActivity.class);
                            mainUserActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainUserActivity);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            // Display error reason to the user
                            AlertDialog.Builder errorBuilder = new AlertDialog.Builder(RegisterActivity.this);
                            errorBuilder.setTitle("Account Creation Failed");
                            errorBuilder.setMessage(Objects.requireNonNull(task.getException()).getMessage());
                            errorBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });

                            errorBuilder.create().show();
                        }
                    }
                });

    }

    private void updateUsersDatabase(User currentUser) {
        // Creating a user reference based on the unique UID generated by Firebase auth
        // Note: UID will both be a parent and a children
        DatabaseReference currentUserRef = mDatabase.child("Users").child(currentUser.getUid());
        currentUserRef.setValue(currentUser);
    }
}