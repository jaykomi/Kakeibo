package com.example.kakeibo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class NotificationIntent extends IntentService {
    private static final int NOTIFICATION_ID = 3;

    public NotificationIntent() {
        super("NotificationIntent");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // maak de notificatiemanager aan
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // controleer op build versie. Andere versies vereisen andere startforeground services.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("Code",
                    "Start",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Notification!");
            notificationManager.createNotificationChannel(notificationChannel);

        }

        //bouw de notificatie builder en stuur de inhoud zoals title text en icon mee. Zet de pending intent in de builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "Code")
                .setSmallIcon(R.drawable.wallet_icon)
                .setContentTitle("Kakeibo")
                .setContentText("Don't forget to add your daily expenses!")
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_SOUND );
        Intent intentOuter = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, intentOuter, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(0, builder.build());

        stopForeground(true);
        stopSelf();

    }
}