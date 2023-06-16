package com.example.emptyreminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.provider.AlarmClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReminderView extends LinearLayout {
    private TextView nameTextView;
    private TextView infoTextView;
    private TextView completedTextView;
    private TextView timeTextView;
    private TextView dateTextView;
    private ImageButton deleteButton;
    private ImageButton completeButton;

    private ImageButton remindAdd;



    public ReminderView(Context context) {
        super(context);
        init(context);
    }

    public ReminderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ReminderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("MissingInflatedId")
    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.reminder_item, this);

        nameTextView = view.findViewById(R.id.name_text_view);
        infoTextView = view.findViewById(R.id.info_text_view);
        completedTextView = view.findViewById(R.id.completed_text_view);
        timeTextView = view.findViewById(R.id.time_text_view);
        dateTextView = view.findViewById(R.id.date_text_view);
        deleteButton = view.findViewById(R.id.delete_button);
        remindAdd = view.findViewById(R.id.remindAdd);
        completeButton = view.findViewById(R.id.complete_button);
    }

    public void setReminder(Reminder reminder) {
        nameTextView.setText(reminder.getName());
        infoTextView.setText(reminder.getInfo());
        timeTextView.setText(reminder.getTime());
        dateTextView.setText(reminder.getDate());
        Calendar calendar = Calendar.getInstance();
        calendar.set(reminder.getYear(), reminder.getMonth(), reminder.getDay(), reminder.getHour(), reminder.getMinute());
        boolean isOutOfDate = calendar.getTimeInMillis() < System.currentTimeMillis();

        setBackgroundResource(isOutOfDate ? R.drawable.reminder_background_out_of_date : R.drawable.reminder_background);

        if (reminder.isCompleted()) {
            completedTextView.setText("Принято");
            completedTextView.setTextColor(Color.parseColor("#4ea739"));
            setBackgroundResource(R.drawable.reminder_background);
        } else {
            completedTextView.setText("Не принято");
            completedTextView.setTextColor(Color.parseColor("#474448"));
        }
    }

    public void setOnDeleteClickListener(View.OnClickListener onClickListener) {
        deleteButton.setOnClickListener(onClickListener);
    }

    public void setOnCompleteClickListener(View.OnClickListener onClickListener) {
        completeButton.setOnClickListener(onClickListener);
    }
    public void setOnEddClickListener(View.OnClickListener onClickListener) {
        remindAdd.setOnClickListener(onClickListener);
    }
}

