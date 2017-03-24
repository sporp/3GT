package website.comas.cbtapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        String cons = b.getString("consecutiveDays");
        Intent service = new Intent(context, AlertService.class);
        context.startService(service);
    }
}