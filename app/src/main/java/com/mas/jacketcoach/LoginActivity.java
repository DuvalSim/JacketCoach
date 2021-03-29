package com.mas.jacketcoach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mas.jacketcoach.helper.Validator;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText emailEditText;
    private EditText passwordEditText;

    private final String TAG = "LoginActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize the email and password boxes
        emailEditText = (EditText) findViewById(R.id.editTextEmailAddress);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            // Start the main activity if user is authenticated (making sure activity stack is cleared)
            Intent mainUserActivity = new Intent(LoginActivity.this, MainActivity.class);
            mainUserActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainUserActivity);
        }
    }


    // Button event listener for the login button
    // Uses Firebase authentication framework to login a registered user using email and password
    public void loginUser(View view) {
        Log.d(TAG, emailEditText.getText().toString());
        Log.d(TAG, passwordEditText.getText().toString());

        String enteredEmail = emailEditText.getText().toString();
        String enteredPassword = passwordEditText.getText().toString();

        if (!Validator.isValidEmail(enteredEmail)) {
            emailEditText.setError("Invalid Email Entered");
            return;
        }

        if (!Validator.isValidPassword(enteredPassword)) {
            passwordEditText.setError("Invalid Password Entered");
            return;
        }

        // Try to log user in
        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                            // Successfully signed in
                            // No need to pass the user to the other intent, this is a singleton object that can be accessed everywhere like above

                            // Start the main activity if user is authenticated (making sure activity stack is cleared)
                            Intent mainUserActivity = new Intent(LoginActivity.this, MainActivity.class);
                            mainUserActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainUserActivity);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            // TODO: Farzam
                        }
                    }
                });
    }

    // Register button onClick handler
    public void registerUserOnLogin(View view) {
        Intent registerUserActivity = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(registerUserActivity, 0);
    }

    // Debug button onClick handler (to be removed)
    public void debugOnClick(View view) {
        // TODO: Farzam: IMPLEMENT REGISTER PAGE AND REMOVE BELOW TRANSITION. FOR NOW WE JUST TRANSITION TO MAIN PAGE FOR EASIER DEVELOPMENT THERE
        Intent mainUserActivity = new Intent(LoginActivity.this, MainActivity.class);
        mainUserActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainUserActivity);
    }
}