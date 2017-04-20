package com.example.administrator.afinal;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

public class StartActivity extends AppCompatActivity {
    private TextView leftTime;
    private int count = 5;
    private Timer timer = new Timer();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            leftTime.setText(new Integer(msg.arg1).toString());
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        leftTime = (TextView)findViewById(R.id.leftTime);
        DoTimer();

    }
    private void DoTimer () {
        timer.schedule(new TimerTask() {
            public void run() {
                Message msg = Message.obtain();
                msg.arg1 = count;
                count--;
                if(count >0) {
                    handler.sendMessage(msg);

                } else {
                    timer.cancel();
                    timer = new Timer();
                    handler.sendMessage(msg);
                    count = 5;
                    DoTimer();
                }
            }
        }, 1000, 1000);
    }

}
