package com.llm.myapplication.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.llm.myapplication.R;


public class AboutActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        textView = (TextView) findViewById(R.id.textView2);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setTextSize(24);
        textView.setText("闲的蛋疼！");
    }
}
