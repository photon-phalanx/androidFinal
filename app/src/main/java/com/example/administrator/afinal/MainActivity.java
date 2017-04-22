package com.example.administrator.afinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private TextView historyCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Copy.copyFileFromAssets(this,"NotesList.sqlite3");
        SharedPreferences preferences = getSharedPreferences("historyCount", Context.MODE_PRIVATE);
        int count = preferences.getInt("historyCount", 0);
        historyCount = (TextView) findViewById(R.id.historyCount);
        historyCount.setText(Integer.toString(count));
        button = (Button) findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });
    }
}
