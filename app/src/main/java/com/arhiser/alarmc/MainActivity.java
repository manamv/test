package com.arhiser.alarmc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Button setAlarm;
    EditText alarmTextInput;
    TextView nextAlarmText, nextAlarmTime;
    Button cancelAlarm;
    AlarmManager alarmManager;
    PendingIntent alarmPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAlarm = findViewById(R.id.alarm_button);
        alarmTextInput = findViewById(R.id.alarm_text);
        nextAlarmText = findViewById(R.id.next_alarm_text);
        nextAlarmTime = findViewById(R.id.next_alarm_time);
        cancelAlarm = findViewById(R.id.cancel_alarm);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        setAlarm.setOnClickListener(v -> {
            showTimePicker();
        });

        cancelAlarm.setOnClickListener(v -> {
            cancelAlarm();
        });

        updateNextAlarmUI();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }

    private void showTimePicker() {
        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Выберите время для будильника")
                .build();

        materialTimePicker.addOnPositiveButtonClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.MINUTE, materialTimePicker.getMinute());
            calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.getHour());

            setAlarm(calendar.getTimeInMillis(), alarmTextInput.getText().toString());
            updateNextAlarmUI();
        });

        materialTimePicker.show(getSupportFragmentManager(), "tag_picker");
    }

    private void setAlarm(long triggerAtMillis, String alarmText) {
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.putExtra("alarm_text", alarmText);
        alarmPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(triggerAtMillis, alarmPendingIntent), alarmPendingIntent);

        Toast.makeText(this, "Будильник установлен на " + DateFormat.format("HH:mm", triggerAtMillis), Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm() {
        if (alarmPendingIntent != null) {
            alarmManager.cancel(alarmPendingIntent);
            Toast.makeText(this, "Будильник отменен", Toast.LENGTH_SHORT).show();
            updateNextAlarmUI();
        }
    }

    private void updateNextAlarmUI() {
        AlarmManager.AlarmClockInfo nextAlarm = alarmManager.getNextAlarmClock();
        if (nextAlarm != null) {
            nextAlarmText.setVisibility(TextView.VISIBLE);
            nextAlarmTime.setVisibility(TextView.VISIBLE);
            cancelAlarm.setVisibility(Button.VISIBLE);
            nextAlarmTime.setText(DateFormat.format("HH:mm", nextAlarm.getTriggerTime()));
        } else {
            nextAlarmText.setVisibility(TextView.GONE);
            nextAlarmTime.setVisibility(TextView.GONE);
            cancelAlarm.setVisibility(Button.GONE);
        }
    }
}
