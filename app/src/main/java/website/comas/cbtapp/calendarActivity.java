package website.comas.cbtapp;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class calendarActivity extends AppCompatActivity{
    Button info;
    private DBHelper mydb;
    CalendarView calendar;
    private String currDate;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        info = (Button)findViewById(R.id.infoButton);
        mydb = new DBHelper(this);
        initCalendar();
        viewAll();
    }

    public void initCalendar(){
        currDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        calendar = (CalendarView) findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy");
                month = month+1;
                String smonth = "" + month;
                String sday = "" + dayOfMonth;
                if(month < 10){
                    smonth = "0" + smonth;
                }
                if(dayOfMonth < 10){
                    sday = "0" + sday;
                }
                currDate = smonth+"-"+sday+"-"+year;
                //s.format(new Date(calendar.getDate()));
                Toast.makeText(calendarActivity.this, currDate, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void viewAll(){
        info.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = mydb.getInfoByDate(currDate);
                        if(res.getCount() == 0){
                            showMessage("Error","No data found");
                            return;
                        }
                        StringBuffer buffer = new StringBuffer();
                        while(res.moveToNext()){
                            buffer.append("Date: "+res.getString(0)+"\n");
                            buffer.append("Rating: "+res.getString(1)+"\n");
                            buffer.append("Thing 1: "+res.getString(2)+"\n");
                            buffer.append("Thing 2: "+res.getString(3)+"\n");
                            buffer.append("Thing 3: "+res.getString(4)+"\n");
                        }

                        showMessage("Selected Date's Info",buffer.toString());
                    }
                }
        );
    }

    public void showMessage(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
