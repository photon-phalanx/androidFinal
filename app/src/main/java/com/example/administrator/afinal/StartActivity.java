package com.example.administrator.afinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
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
    private Button submitButton, musicButton;
    private int historyCount;
    private MediaPlayer mediaPlayer;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 != 0) {
                leftTime.setText(Integer.toString(msg.arg1));
            } else {
                if (historyCount < mark) {
                    editor = preferences.edit();
                    editor.putInt("historyCount", mark);
                    editor.commit();
                }
                Intent intent = new Intent(StartActivity.this, FinishActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("mark", mark);
                bundle.putString("reason", "暂时想不到单词了吧~");
                intent.putExtras(bundle);
                mediaPlayer.stop();
                finish();
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        preferences = getSharedPreferences("historyCount", Context.MODE_PRIVATE);
        leftTime = (TextView) findViewById(R.id.leftTime);
        grade = (TextView) findViewById(R.id.grade);
        wordEditText = (EditText) findViewById(R.id.editField);
        textView1 = (TextView) findViewById(R.id.word1);
        textView2 = (TextView) findViewById(R.id.word2);
        textView3 = (TextView) findViewById(R.id.word3);
        submitButton = (Button) findViewById(R.id.submitButton);
        musicButton = (Button) findViewById(R.id.musicButton);
        mediaPlayer = MediaPlayer.create(this, R.raw.magnet);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        musicButton.setText("停止");
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = musicButton.getText().toString();
                if (text.equals("停止")) {
                    mediaPlayer.pause();
                    musicButton.setText("播放");
                } else {
                    mediaPlayer.start();
                    musicButton.setText("停止");
                }
            }
        });
        historyCount = preferences.getInt("historyCount", 0);
        db = SQLiteDatabase.openOrCreateDatabase("data/data/" + this.getPackageName() + "/databases/NotesList.sqlite3", null);
        wordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    wordEditText.setText(newWord.substring(newWord.length() - 1));
                    wordEditText.setSelection(1);
                } else if (s.charAt(0) != newWord.charAt(newWord.length() - 1)) {
                    String wholeText = newWord.substring(newWord.length() - 1) + s;
                    wordEditText.setText(wholeText);
                    wordEditText.setSelection(1);
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
                    cursor = db.rawQuery("select * from Note where words= '" + wordEditText.getText().toString() + "' COLLATE NOCASE", null);
                    if (cursor.moveToFirst()) {
                        word = cursor.getString(0); // 这里其实没有必要再做一遍，不过写了就懒得改了
                        cursor.close();
                        if (!set.contains(word)) {
                            Boolean flag = false;
                            timer.cancel();
                            set.add(word);
                            cursor = db.rawQuery("select * from Note where words like '" + word.charAt(word.length() - 1) + "%' COLLATE NOCASE", null);
                            int tmpCount = cursor.getCount();
                            int tmpRandom = (int) (Math.random() * tmpCount);
                            cursor.moveToNext();
                            cursor.move(tmpRandom - 1);
                            for (int i = tmpRandom; i < tmpCount ; i++) {
                                newWord = cursor.getString(0);
                                if (!set.contains(newWord)) {
                                    set.add(newWord);
                                    textView1.setText(textView3.getText().toString());
                                    textView2.setText(word);
                                    textView3.setText(newWord);
                                    gradeIncrease();
                                    wordEditText.setText(newWord.substring(newWord.length() - 1));
                                    wordEditText.setSelection(1);
                                    flag = true;
                                    break;
                                }
                                cursor.moveToNext();
                            }
                            if (!flag) {
                                cursor.moveToFirst();
                                for (int i = 0; i < tmpRandom; i++) {
                                    newWord = cursor.getString(0);
                                    if (!set.contains(newWord)) {
                                        set.add(newWord);
                                        textView1.setText(textView2.getText().toString());
                                        textView2.setText(word);
                                        textView3.setText(newWord);
                                        gradeIncrease();
                                        wordEditText.setText(newWord.substring(newWord.length() - 1));
                                        wordEditText.setSelection(1);
                                        flag = true;
                                        break;
                                    }
                                    cursor.moveToNext();
                                }
                            }
                            if (flag) {
                                count = 15;
                                timer = new Timer();
                                doTimer();
                            } else {
                                // 这里应该是赢了
                                timer.cancel();
                                if (historyCount < mark) {
                                    editor = preferences.edit();
                                    editor.putInt("historyCount", mark);
                                    editor.commit();
                                }
                                Intent intent = new Intent(StartActivity.this, FinishActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("mark", mark);
                                bundle.putString("reason", "哇好厉害！ai接不出下一个词了哦~~");
                                intent.putExtras(bundle);
                                mediaPlayer.stop();
                                finish();
                                startActivity(intent);
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
            wordEditText.setText(newWord.substring(newWord.length() - 1));
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
                count--;
                msg.arg1 = count;
                if (count > 0) {
                    handler.sendMessage(msg);

                } else {
                    timer.cancel();
                    handler.sendMessage(msg);
                }
            }
        }, 1000, 1000);
    }
}
