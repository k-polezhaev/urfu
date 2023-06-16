package com.example.emptyreminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderAlarmReceiver extends BroadcastReceiver {
    public static final String REMINDER_TITLE = "REMINDER_TITLE";
    public static final String REMINDER_TEXT = "REMINDER_TEXT";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra(REMINDER_TITLE);
        String text = intent.getStringExtra(REMINDER_TEXT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder_channel")
                .setSmallIcon(R.drawable.ic_arrow_back)
                .setContentTitle("Вы ничего не забыли?")
                .setContentText("Время принять")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }
}
