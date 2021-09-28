package org.esei.dm.clockalarm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Timer refreshTimeTimer = new Timer();
    private int hourAlarm = 0;
    private int minuteAlarm = 0;
    private boolean enabledAlarm = false;
    private boolean alarmActive = false;


    final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            showCurrentTime();
            return false;
        }
    });

    private void checkAlarm(int hour, int minute) {
        if (enabledAlarm && !alarmActive){
            if (hour == hourAlarm && minute == minuteAlarm)
                showAlarmDialog();
        }
    }

    class RefreshTimeTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button  = findViewById(R.id.buttonAlarmVertical);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hourAlarm = hourOfDay;
                        minuteAlarm = minute;
                        enabledAlarm = true;
                        button.setText("ALARMA "+ hourAlarm + ":" + minuteAlarm);
                    }
                };
                TimePickerDialog dialog = new TimePickerDialog(MainActivity.this, listener, 10, 10, true);
                dialog.show();
            }
        });
        refreshTimeTimer.scheduleAtFixedRate(new RefreshTimeTimerTask(), 0, 1000);
    }

    private void showCurrentTime() {
        Date now =new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        TextView textView = findViewById(R.id.textViewHour);
        textView.setText(""+hour);
        textView = findViewById(R.id.textViewMinutes);
        int minute = cal.get(Calendar.MINUTE);
        textView.setText(""+minute);
        checkAlarm(hour, minute);
    }

    private void showAlarmDialog() {
        alarmActive = true;
        Button button =  findViewById(R.id.buttonAlarmVertical);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("ALARMA "+ hourAlarm +":"+minuteAlarm);
        builder.setPositiveButton("Apagar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enabledAlarm  = false;
                hourAlarm = 0;
                minuteAlarm = 0;
                button.setText("ALARMA");
            }
        });
        builder.setNegativeButton("Posponer 1 min", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                minuteAlarm = minuteAlarm + 1;
                button.setText("ALARMA "+ hourAlarm +":"+minuteAlarm);
                alarmActive = false;
            }
        });
        builder.create().show();
    }
}