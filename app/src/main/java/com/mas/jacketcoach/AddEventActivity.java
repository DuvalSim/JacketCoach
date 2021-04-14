package com.mas.jacketcoach;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mas.jacketcoach.helper.MapStateManager;
import com.mas.jacketcoach.helper.Validator;
import com.mas.jacketcoach.model.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AddEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static String OTHER_SPORT = "Other";
    private EditText nameEditText;
    private EditText sportEditText;
    private EditText date_field;
    private String sport;
    private Spinner sportSpinner;
    private LatLng latlng;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        //Initialize top bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        //Retrieve values from MapsFragment
        latlng = getIntent().getExtras().getParcelable("LAT_LNG");

        // Initialize UI elements
        nameEditText = (EditText) findViewById(R.id.name);
        sportEditText = (EditText) findViewById(R.id.other_sport);
        date_field = (EditText)findViewById(R.id.date);
        date_field.setFocusable(false); // disable editing of this field
        date_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                chooseDate();
            }
        });
        sportSpinner = (Spinner) findViewById(R.id.sport_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sports_dropdown, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSpinner.setAdapter(adapter);
        sportSpinner.setOnItemSelectedListener(this);

        //Firebase setup
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    // ToolBar actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Log.d("NAVIGATION","setting");
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            // reload();
        }
    }

    public void addEvent(View view) {
        //Parsing input
        String enteredName = nameEditText.getText().toString();
        String enteredSport;
        if (sport.equals(OTHER_SPORT)) {
            enteredSport = sportEditText.getText().toString();
        } else {
            enteredSport = sport;
        }
        String enteredDate = date_field.getText().toString();

        //Validating input
        if (!Validator.isValidText(enteredName)) {
            nameEditText.setError("Invalid Name Entered");
            return;
        }
        if (!Validator.isValidText(enteredSport)) {
            sportEditText.setError("Invalid Sport Entered");
            return;
        }
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try { sdf.parse(enteredDate); }
        catch ( Exception e ) { date_field.setError("Invalid Date"); }

        // Pushing input
        // firebase pushId needs to be saved locally so we can grab event information later on
        String eventFirebasePushId = mDatabase.child("events").push().getKey();
        Date currentTime = Calendar.getInstance().getTime();
        //I don't think we need an id field because the event key generated by push() identifies the event. So its a constant 1.
        ArrayList<String> players = new ArrayList<String>();
        if (user == null) {
            Toast.makeText(this, "ERROR: Error: No associated user found. Login with a valid user.", Toast.LENGTH_LONG).show();

            // TODO: In a production Android app, this should be done by a robust receiver instead
            Intent loginUserActivity = new Intent(this, LoginActivity.class);
            loginUserActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginUserActivity);
        } else {
            players.add(user.getUid());
        }

        Event event = new Event(eventFirebasePushId, user.getUid(), enteredName, enteredSport, enteredDate, latlng.latitude, latlng.longitude, players);
        mDatabase.child("events").child(eventFirebasePushId).setValue(event);

        Toast.makeText(this, "Event Created!", Toast.LENGTH_SHORT);
        MapStateManager mapStateManager = new MapStateManager(this.getApplicationContext());
        mapStateManager.centerOnEvent((float) latlng.latitude,(float) latlng.longitude);
        Intent intent = new Intent(AddEventActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void chooseDate() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker =
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(final DatePicker view, final int year, final int month,
                                          final int dayOfMonth) {

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        calendar.set(year, month, dayOfMonth);
                        String dateString = sdf.format(calendar.getTime());

                        date_field.setText(dateString); // set the date
                    }
                }, year, month, day); // set date picker to current date

        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Added by Farzam to disable past dates
        datePicker.show();

        datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sport = parent.getItemAtPosition(position).toString();
        if (sport.equals(OTHER_SPORT)) {
            sportEditText.setVisibility(View.VISIBLE);
        } else {
            sportEditText.setVisibility(View.INVISIBLE);
            sportEditText.setText("");
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(sportEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }
}