package com.mas.jacketcoach;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mas.jacketcoach.helper.MapStateManager;
import com.mas.jacketcoach.helper.Validator;
import com.mas.jacketcoach.model.Event;
import com.mas.jacketcoach.model.User;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.IntStream;


public class AddEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static String OTHER_SPORT = "Other";
    private static String[] GEAR = {"a basketball", "a volleyball", "a soccer ball", "a tennis ball and a racket", "a football", "a rugby ball", "a frisbee", "a handball"};
    private TextView addressText;
    private EditText date_field;
    private EditText time_field;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private Spinner maxplayersSpinner;
    private int maxplayers;
    private Spinner weatherSpinner;
    private boolean rainorshine;
    private Spinner sportSpinner;
    private EditText sportEditText;
    private String sport;
    private TextView gearText;
    private LatLng latlng;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String event_location_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Log.d("NAVIGATION", "Add event create");
        //Initialize top bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        //Retrieve values from MapsFragment
        latlng = getIntent().getExtras().getParcelable("LAT_LNG");
        //TODO : Store it in DB ?

        // Initialize UI elements
        event_location_name = getIntent().getExtras().getString("LOCATION_NAME","");
        addressText = (TextView) findViewById(R.id.address) ;
        addressText.setText(event_location_name);
        date_field = (EditText)findViewById(R.id.date);
        date_field.setFocusable(false); // disable editing of this field
        date_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                chooseDate();
            }
        });
        time_field = (EditText)findViewById(R.id.time);
        time_field.setFocusable(false);
        time_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE - Calendar.MINUTE%10);
                // time picker dialog
                TimePickerDialog picker = new TimePickerDialog(AddEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                showTime(sHour, sMinute);
                            }
                        }, hour, minutes, false);
                picker.show();
            }
        });
        nameEditText = (EditText) findViewById(R.id.name);
        descriptionEditText = (EditText) findViewById(R.id.description);
        maxplayersSpinner = (Spinner) findViewById(R.id.maxplayers);
        maxplayersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                maxplayers = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
        Integer[] numbers = new Integer[30];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i + 1;
        }
        ArrayAdapter<Integer> adapterInt = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, numbers);
        maxplayersSpinner.setAdapter(adapterInt);
        sportEditText = (EditText) findViewById(R.id.other_sport);
        sportSpinner = (Spinner) findViewById(R.id.sport_spinner);
        ArrayAdapter<CharSequence> adapterString = ArrayAdapter.createFromResource(this, R.array.sports_dropdown, android.R.layout.simple_spinner_item);
        adapterString.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSpinner.setAdapter(adapterString);
        sportSpinner.setOnItemSelectedListener(this);
        weatherSpinner = (Spinner) findViewById(R.id.weatherSpinner);
        ArrayAdapter<CharSequence> weatherAdapter = ArrayAdapter.createFromResource(this, R.array.weather_dropdown,android.R.layout.simple_spinner_item);
        weatherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weatherSpinner.setAdapter(weatherAdapter);
        weatherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    rainorshine = true;
                } else {
                    rainorshine = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        gearText = (TextView) findViewById(R.id.gearText);
        //Firebase setup
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sport = parent.getItemAtPosition(position).toString();
        if (sport.equals(OTHER_SPORT)) {
            sportEditText.setVisibility(View.VISIBLE);
            gearText.setText("Remember to bring gear!");
        } else {
            sportEditText.setVisibility(View.INVISIBLE);
            sportEditText.setText("");
            InputMethodManager inputManager = (InputMethodManager) AddEventActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(sportEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            gearText.setText("Remember to bring " + GEAR[position] + "!");
        }
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
        String enteredDescription = descriptionEditText.getText().toString();
        String enteredSport;
        if (sport.equals(OTHER_SPORT)) {
            enteredSport = sportEditText.getText().toString();
        } else {
            enteredSport = sport;
        }
        String enteredDate = date_field.getText().toString() + " " + time_field.getText().toString();

        //Validating input
        if (!Validator.isValidText(enteredName)) {
            nameEditText.setError("Invalid Name Entered");
            return;
        }
        if (!Validator.isValidText(enteredDescription)) {
            descriptionEditText.setError("Invalid Description Entered");
            return;
        }
        if (!Validator.isValidText(enteredSport)) {
            sportEditText.setError("Invalid Sport Entered");
            return;
        }
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setLenient(false);
        try { sdf.parse(enteredDate); }
        catch ( Exception e ) { date_field.setError("Invalid Date or Time"); }

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

        Event event = new Event(eventFirebasePushId, user.getUid(), enteredName, enteredSport, enteredDate, latlng.latitude, latlng.longitude, players, enteredDescription, maxplayers, rainorshine);
        mDatabase.child("events").child(eventFirebasePushId).setValue(event);

        // Update the User table
        DatabaseReference userRef = mDatabase.child(getString(R.string.users_table_key)).child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Add this event id to the user assigned event list --> NOTE: Host vs Participant will be based on eventId vs organizerId
                User participant = snapshot.getValue(User.class);
                participant.getUserEvents().add(eventFirebasePushId);
                userRef.setValue(participant);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error getting host info: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

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

    public void showTime(int hour, int min) {
        String format;
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        if (min < 10) {
            time_field.setText(new StringBuilder().append(hour).append(" : 0").append(min).append(" ").append(format));
        } else {
            time_field.setText(new StringBuilder().append(hour).append(" : ").append(min).append(" ").append(format));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }
}