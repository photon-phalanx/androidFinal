package com.example.administrator.afinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FinishActivity extends AppCompatActivity {
    private TextView reasonText,countText;
    private Button go_back_button,retry_button;
    private int count;
    private String reason,str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        reasonText = (TextView)findViewById(R.id.reasonText);
        countText = (TextView)findViewById(R.id.countText);
        go_back_button = (Button)findViewById(R.id.go_back_button);
        retry_button = (Button)findViewById(R.id.retry_button);
        Bundle bundle = this.getIntent().getExtras();
        count = bundle.getInt("mark");
        reason = bundle.getString("reason");
        str = reasonText.getText().toString();
        str = str + reason;
        reasonText.setText(str);
        countText.setText(Integer.toString(count));
        go_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinishActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        retry_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinishActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });
    }
}
