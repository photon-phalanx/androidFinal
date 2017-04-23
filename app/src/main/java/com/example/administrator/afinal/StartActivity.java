package com.example.administrator.afinal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {
    private Timer timer = new Timer();
    private SQLiteDatabase db;
    private int count = 15, mark = 0;
    private Cursor cursor;
    private String word, newWord;
    private HashSet<String> set;
    private EditText wordEditText;
    private TextView textView1, textView2, textView3, grade, leftTime;
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
        grade = (TextView) findViewById(R.id.grade);
        wordEditText = (EditText) findViewById(R.id.editField);
        textView1 = (TextView) findViewById(R.id.word1);
        textView2 = (TextView) findViewById(R.id.word2);
        textView3 = (TextView) findViewById(R.id.word3);
        submitButton = (Button) findViewById(R.id.submitButton);
        db = SQLiteDatabase.openOrCreateDatabase("data/data/" + this.getPackageName() + "/databases/NotesList.sqlite3", null);
        wordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    wordEditText.setText(newWord.charAt(newWord.length() - 1));
                } else if (s.charAt(0) != newWord.charAt(newWord.length() - 1)) {
                    String wholeText = newWord.substring(0, 1) + s;
                    wordEditText.setText(wholeText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == 0) return;
                else {
                    wordEditText.setSelection(wordEditText.getText().length());
                    cursor = db.query("Note", null, "words=?", new String[]{wordEditText.getText().toString()}, null, null, null);
                    if (cursor.moveToFirst()) {
                        // cursor.move(0);
                        word = cursor.getString(0); // 这里其实没有必要再做一遍，不过写了就懒得改了
                        cursor.close();
                        if (!set.contains(word)) {
                            Boolean flag = false;
                            timer.cancel();
                            set.add(word);
                            cursor = db.rawQuery("select * from Note where words like '" + word.charAt(word.length() - 1) + "%'", null);
                            while (cursor.moveToNext()) {
                                newWord = cursor.getString(0);
                                if (!set.contains(newWord)) {
                                    set.add(newWord);
                                    textView1.setText(textView2.getText().toString());
                                    textView2.setText(word);
                                    textView3.setText(newWord);
                                    gradeIncrease();
                                    wordEditText.setText(newWord.charAt(newWord.length() - 1));
                                    wordEditText.setSelection(1);
                                    flag = true;
                                    break;
                                }
                            }
                            if (flag) {
                                count = 15;
                                timer = new Timer();
                                doTimer();
                            } else {
                                // TODO 这里应该是赢了
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "重复啦~",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "拼错了哦~",
                                Toast.LENGTH_SHORT).show();
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
            newWord = cursor.getString(0);
            set.add(newWord);
            textView3.setText(newWord);
            cursor.close();
            wordEditText.setText(newWord.charAt(newWord.length() - 1));
            wordEditText.setSelection(1);
            this.doTimer();
        }
    }

    private void gradeIncrease() {
        mark++;
        grade.setText(Integer.toString(mark));
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
