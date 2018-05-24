package com.qf.rwxchina.xiaochefudriver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TextView textView = (TextView) findViewById(R.id.activity_test_text);
        Intent i = getIntent();
        String s = i.getExtras().getString("text");
        textView.setText(s);
    }
}
