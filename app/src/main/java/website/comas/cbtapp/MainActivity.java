package website.comas.cbtapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
This app will once a day ask how you are doing. You will be able to rate it from a 1-5 scale, then write down
three positive things that have happened during the week. It will try to implement this CBT method
in order to help the owner of the app try to gain a more positive mindset and look for the positive
in any situation. You will be able to update this at any time until the next day
 */

//TODO: Have a daily notification to remind user to input daily rating

//TODO: Add settings tab to change when you get notified/when the app resets to the next day

public class MainActivity extends AppCompatActivity {

    private EditText positive1,positive2,positive3;
    private TextView entryBoolView;
    private RatingBar dailyRating;
    private boolean dailyuseflag, firstTime;
    private DBHelper mydb;
    private String currDate;
    private int currRating, id;
    private String pos1, pos2, pos3;
    private Button calStart;
    SharedPreferences sharedPreferences;

    PendingIntent pi;
    BroadcastReceiver br;
    AlarmManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.button);
        calStart = (Button)findViewById(R.id.button2);

        positive1 = (EditText)findViewById(R.id.editText);
        positive2 = (EditText)findViewById(R.id.editText2);
        positive3 = (EditText)findViewById(R.id.editText3);
        dailyRating = (RatingBar)findViewById(R.id.ratingBar);
        entryBoolView = (TextView)findViewById(R.id.textViewEntryBool);

        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if(!(sharedPreferences.contains("firstTime"))){
            SharedPreferences.Editor ed = sharedPreferences.edit();
            ed.putBoolean("firstTime", false);
            ed.commit();
            firstSetup();
        }
        else {
            setup();
        }

        mydb = new DBHelper(this); //Make a da database

        setDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        //listener to changes on the rating bar
        dailyRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                currRating = (int) dailyRating.getRating();
            }
        });

        //push the text fields on button click to SQL database
        button.setOnClickListener(new View.OnClickListener() { //Updates database with daily rating and 3 good things
            @Override
            public void onClick(View v) {
                pos1 = positive1.getText().toString();
                pos2 = positive2.getText().toString();
                pos3 = positive3.getText().toString();

                if (mydb.entryToday(currDate) == false) {
                    mydb.insertDailyRating(currDate, currRating, pos1, pos2, pos3);
                    Toast.makeText(MainActivity.this, "Daily Mood Rating Added!", Toast.LENGTH_SHORT).show();
                    entryBoolView.setText("Daily Mood Rating Added!");
                    dailyuseflag = true;
                } else { //get ID
                    id = mydb.getIDfromDate(currDate);
                    mydb.updateDailyRating(currDate, currRating, pos1, pos2, pos3);
                    Toast.makeText(MainActivity.this, "Daily Mood Rating Updated!", Toast.LENGTH_SHORT).show();
                    entryBoolView.setText("Daily Mood Rating Updated!");
                }
            }
        });

        //change activity to the calendar
        calStart.setOnClickListener(new View.OnClickListener() { //Updates database with daily rating and 3 good things
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, calendarActivity.class);
                startActivity(intent);
            }
        });

        //hide keyboards when you tap anywhere else on the screen than the keyboard
        positive1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {if (!hasFocus) {hideKeyboard(v);}
            }
        });
        positive2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {if (!hasFocus) {hideKeyboard(v);}
            }
        });
        positive3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {if (!hasFocus) {hideKeyboard(v);}
            }
        });
    }

    protected void onPause(Bundle savedInstanceState){
        //close the database connection when app is no longer being used
        mydb.close();
    }

    protected void onStop(Bundle savedInstanceState){ //save stuff into shared preferences here too?

    }

    public void run(){
        Bundle extras = getIntent().getExtras();
        if(extras !=null){

        }
    }

    private void firstSetup(){
        dailyuseflag = false;
        currDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        showMessage("Welcome to 3 Good Things!", "This is an app designed to help people. " +
                "Write down 3 positive things that happened to you today, and you " +
                "can check what you've written at any time! " +
                "This app uses the idea of cognitive behavioral therapy to help reprogram your " +
                "brain to have more positive thoughts. If you stick with it, better thoughts " +
                "might just stick with you!");
    }

    private void setup() {
        //setup each time app is launched
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        dailyuseflag = sharedPreferences.getBoolean("duseflag", true); //set value of saved daily use flag

        currDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        String oldDate = sharedPreferences.getString("savedDate", "No string found");

        if(!(oldDate.equals(currDate))){ //if dates are not equal
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("savedDate", currDate); //commit new date
            dailyuseflag = false;
            editor.putBoolean("duseflag",dailyuseflag); //reset dailyuseflag so new entries may be added
            editor.commit();
        }

        alarmMethod();
        return;
    }

    public void showMessage(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private void setDate(){
        //set fields based on whether an input has been made today
        if(mydb.entryToday(currDate)){
            currRating = mydb.getRating(currDate);
            String rating = currRating + "";
            if(currRating > 0 && currRating<=5)
            dailyRating.setRating(currRating);
            String t1 = mydb.getThings(currDate, "thing1");
            if(!(t1.equals("Nothing written")))
                positive1.setText(t1);
            String t2 = mydb.getThings(currDate, "thing2");
            if(!(t2.equals("Nothing written")))
                positive2.setText(t2);
            String t3 = mydb.getThings(currDate, "thing3");
            if(!(t3.equals("Nothing written")))
                positive3.setText(t3);
            entryBoolView.setText("You have logged an entry today, but feel free to update it!");
        }
        else{
            entryBoolView.setText("You have not logged how today was");
        }
    }

    private void alarmMethod(){
        //This needs to be fixed
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent amIntent = new Intent(MainActivity.this, Notifier.class);
        pi = PendingIntent.getService(this,0,amIntent,0);

        Calendar alarmStarter = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        alarmStarter.set(Calendar.HOUR_OF_DAY, 10);
        alarmStarter.set(Calendar.MINUTE, 00);
        alarmStarter.set(Calendar.SECOND, 0);
        alarmStarter.set(Calendar.AM_PM, Calendar.PM);
        if (now.after(alarmStarter)) {
            Log.d("Notice", "Day increased by one");
            alarmStarter.add(Calendar.DATE, 1);
        }
        am.setRepeating(AlarmManager.RTC_WAKEUP, alarmStarter.getTimeInMillis(), 1000*60*60*24 , pi);
        return;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
