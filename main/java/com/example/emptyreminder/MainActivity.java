package com.example.emptyreminder;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private AlarmManager alarmManager;
    private Calendar calendar;
    private MainViewModel viewModel;
    private PendingIntent pendingIntent;


    public MainActivity() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getReminders().observe(this, this::updateReminders);

        Button button = findViewById(R.id.btn_add_reminder);
        button.setOnClickListener(v -> openReminderAddActivity());
        createNoticChannel();
    }

    private void setAlarm(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(this, "Уведомление установлено на " + sdf.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
    }

    private void createNoticChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "reminder_channel";
            String description = "Alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("reminder_channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void updateReminders(List<Reminder> reminders) {
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.removeAllViews();

        TextView noRemindersTextView = findViewById(R.id.no_reminders_text_view);
        noRemindersTextView.setVisibility(reminders.isEmpty() ? View.VISIBLE : View.GONE);

        ImageView noRemindersImage = findViewById(R.id.no_reminders_text_view1);
        noRemindersImage.setVisibility(reminders.isEmpty() ? View.VISIBLE : View.GONE);


        for (Reminder reminder : reminders) {
            ReminderView reminderView = new ReminderView(this);
            reminderView.setReminder(reminder);
            reminderView.setOnEddClickListener(view -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(reminder.getYear(), reminder.getMonth(), reminder.getDay(), reminder.getHour(), reminder.getMinute());
                setAlarm(calendar);
            });
            reminderView.setOnDeleteClickListener(v -> {
                viewModel.deleteReminder(reminder);
                Toast.makeText(this, "Напоминание удалено", Toast.LENGTH_SHORT).show();
            });

            reminderView.setOnCompleteClickListener(v -> {
                viewModel.toggleCompleted(reminder);
                Toast.makeText(this, "Напоминание обновлено", Toast.LENGTH_SHORT).show();
                reminderView.setReminder(reminder);
            });

            linearLayout.addView(reminderView);

            View space = new View(this);
            space.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    16
            ));
            space.setBackgroundColor(Color.TRANSPARENT);
            linearLayout.addView(space);
        }
    }


    public void openReminderAddActivity() {
        Intent intent = new Intent(this, AddReminder.class);
        startActivity(intent);
    }
}

