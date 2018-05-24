package com.qf.rwxchina.xiaochefudriver.Personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;

/**
 * Created by Administrator on 2016/10/21.
 */
public class MaessageActivity extends AppCompatActivity {
    ImageView activity_messge_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        MyApplication.getInstance().addActivity(this);
        activity_messge_back= (ImageView) findViewById(R.id.activity_messge_back);
        activity_messge_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
