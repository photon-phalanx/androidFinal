package com.example.administrator.afinal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {
    private TextView leftTime;
    private Timer timer = new Timer();
    private SQLiteDatabase db;
    private int count = 15;
    private Cursor cursor;
    private String word,newWord;
    private HashSet<String> set;
    private EditText wordEditText;
    private TextView textView1, textView2, textView3;
    private Button submitButton;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            leftTime.setText(Integer.toString(msg.arg1));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        leftTime = (TextView) findViewById(R.id.leftTime);
        wordEditText = (EditText) findViewById(R.id.editField);
        textView1 = (TextView) findViewById(R.id.word1);
        textView2 = (TextView) findViewById(R.id.word2);
        textView3 = (TextView) findViewById(R.id.word3);
        submitButton = (Button) findViewById(R.id.submitButton);
        db = SQLiteDatabase.openOrCreateDatabase("data/data/" + this.getPackageName() + "/databases/NotesList.sqlite3", null);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == 0) return;
                else {
                    cursor = db.query("Note", null, "words=?", new String[]{wordEditText.getText().toString()}, null, null, null);
                    if (cursor.moveToFirst()) {
                        // cursor.move(0);
                        word = cursor.getString(0);
                        cursor.close();
                        if (!set.contains(word)) {
                            Boolean flag = false;
                            timer.cancel();
                            set.add(word);
                            cursor = db.rawQuery("select * from Note where words like '"+word.charAt(word.length()-1)+"%'",null);
                            while (cursor.moveToNext()) {
                                newWord = cursor.getString(0);
                                if (!set.contains(newWord)) {
                                    set.add(newWord);
                                    textView1.setText(textView2.getText().toString());
                                    textView2.setText(word);
                                    textView3.setText(newWord);
                                    flag = true;
                                    break;
                                }
                            }
                            if (flag ){
                                count = 15;
                                timer = new Timer();
                                doTimer();
                            } else {
                                // TODO 这里应该是赢了
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "重复啦~",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        this.gameStart();
    }

    private void gameStart() {
        cursor = db.query("Note", null, null, null, null, null, null);
        set = new HashSet();
        if (cursor.moveToFirst()) {
            cursor.move((int) (Math.random() * 15329));
            word = cursor.getString(0);
            set.add(word);
            textView3.setText(word);
            cursor.close();
            this.doTimer();
        }
    }

    private void doTimer() {
        timer.schedule(new TimerTask() {
            public void run() {
                Message msg = Message.obtain();
                msg.arg1 = count;
                count--;
                if (count > 0) {
                    handler.sendMessage(msg);

                } else {
                    // TODO 这当然逻辑上就是完全不正确的，待写
                    timer.cancel();
                    timer = new Timer();
                    handler.sendMessage(msg);
                    count = 15;
                    // doTimer();
                }
            }
        }, 1000, 1000);
    }
}
