package com.mas.jacketcoach;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mas.jacketcoach.helper.Validator;

public class RegisterActivity extends AppCompatActivity {

    private EditText nickNameEditText;
    private EditText firstNameEditText;
    private EditText registerEmailAddressEditText;
    private EditText registerPasswordEditText;

    private FirebaseAuth mAuth;

    private final String TAG = "RegisterActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        nickNameEditText = (EditText) findViewById(R.id.nickNameEditText);
        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        registerEmailAddressEditText = (EditText) findViewById(R.id.registerEmailAddressEditText);
        registerPasswordEditText = (EditText) findViewById(R.id.registerPasswordEditText);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            // reload();
        }
    }

    // Register button onClick handler
    public void registerUser(View view) {
        String enteredNickname = nickNameEditText.getText().toString();
        String enteredFirstName = firstNameEditText.getText().toString();

        if (!Validator.isValidText(enteredNickname)) {
            nickNameEditText.setError("Invalid Nickname Entered");
            return;
        }

        if (!Validator.isValidText(enteredFirstName)) {
            firstNameEditText.setError("Invalid Name Entered");
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

        // Sign a new user up with their email and password
        mAuth.createUserWithEmailAndPassword(enteredEmail, enteredPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            Toast.makeText(RegisterActivity.this, "Welcome " + enteredFirstName, Toast.LENGTH_SHORT).show();

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
                            errorBuilder.setMessage(task.getException().getMessage());
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
}