package com.example.emptyreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private NotificationHelper notificationHelper;
    private AlarmHelper alarmHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getReminders().observe(this, this::updateReminders);

        Button button = findViewById(R.id.btn_add_reminder);
        button.setOnClickListener(v -> openReminderAddActivity());

    }

    private void updateReminders(List<Reminder> reminders) {
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.removeAllViews();

        TextView noRemindersTextView = findViewById(R.id.no_reminders_text_view);
        noRemindersTextView.setVisibility(reminders.isEmpty() ? View.VISIBLE : View.GONE);

        ImageView noRemindersImage = findViewById(R.id.no_reminders_text_view1);
        noRemindersImage.setVisibility(reminders.isEmpty() ? View.VISIBLE : View.GONE);


        for (Reminder reminder : reminders) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            ReminderView reminderView = new ReminderView(this);
            reminderView.setReminder(reminder);
            reminderView.setOnEddClickListener(view -> {
                TimePicker timePicker = findViewById(R.id.time_new_reminder);
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                DatePicker datePicker = findViewById(R.id.date_new_reminder);
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                Calendar calendar = Calendar.getInstance();
                calendar.set(hour, minute);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), getAlarmInfoPendingIntent());

                alarmManager.setAlarmClock(alarmClockInfo, getAlarmActionPendingIntent());
                Toast.makeText(this, "Будильник установлен на " + sdf.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
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

    private boolean isOutOfDate(Reminder reminder) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(reminder.getYear(), reminder.getMonth(), reminder.getDay(), reminder.getHour(), reminder.getMinute());

        return calendar.getTimeInMillis() > System.currentTimeMillis();
    }

    public void openReminderAddActivity() {
        Intent intent = new Intent(this, AddReminder.class);
        startActivity(intent);
    }
    private PendingIntent getAlarmInfoPendingIntent() {
        Intent alarmInfoIntent = new Intent(this, MainActivity.class);
        alarmInfoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(this, 0, alarmInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getAlarmActionPendingIntent() {
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

