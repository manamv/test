package com.arhiser.alarmc;

import android.annotation.SuppressLint;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AlarmActivity extends AppCompatActivity {
    Ringtone ringtone;
    Button dismissButton;
    TextView alarmTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, notificationUri);

        if (ringtone == null) {
            notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(this, notificationUri);
        }

        if (ringtone != null) {
            ringtone.play();
        }

        // Получаем ссылку на кнопку "Отклонить"
        dismissButton = findViewById(R.id.dismiss_button);

        // Получаем ссылку на текст
        alarmTextView = findViewById(R.id.alarm_text);

        // Получаем текст будильника из Intent
        String alarmText = getIntent().getStringExtra("alarm_text");
        alarmTextView.setText(alarmText);

        // Устанавливаем обработчик нажатия на кнопку "Отклонить"
        dismissButton.setOnClickListener(v -> {
            // Останавливаем воспроизведение звука будильника
            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
            }

            // Закрываем активность
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
        super.onDestroy();
    }
}
