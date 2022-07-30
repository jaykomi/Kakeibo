package com.example.kakeibo;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;


//Created By Prabhat Dwivedi

public class NotificationHandler extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Test", "Finished");
        Intent intentStart = new Intent(context, NotificationIntent.class);

        // een andere volgorde van callen van de context zorgt voor instant crashes. irrelevant of de notification triggert of niet.
        context.startForegroundService(intentStart);
       context.startService(intentStart);
        context.stopService(intentStart);

    }
}
