package website.comas.cbtapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nick on 5/9/16.
 */
public class Notifier extends Service {

    private DBHelper mydb;
    private String currDate;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(){
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager nMg = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent intenter = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this,0,intenter,0);

        mydb = new DBHelper(this);
        currDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date());

        if(mydb.entryToday(currDate) == false) {
            Notification mNotifier = new Notification.Builder(this)
                    .setContentTitle("Log your day")
                    .setContentText("Make sure you log your day and write 3 good things that happened")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent)
                    .build();

            nMg.notify(1, mNotifier);
        }
        else{
            Notification mNotifier = new Notification.Builder(this)
                    .setContentTitle("You've logged today!")
                    .setContentText("Good Job!")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent)
                    .build();

            nMg.notify(1, mNotifier);
        }
    }
}
