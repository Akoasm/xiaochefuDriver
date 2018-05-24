package com.qf.rwxchina.xiaochefudriver.Personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;

/**
 * Created by Administrator on 2016/9/8.
 * 关于小车夫
 */
public class About_xiaochefu extends AppCompatActivity{

    ImageView guanyu_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_xiaochefu);
        MyApplication.getInstance().addActivity(this);
        guanyu_back= (ImageView) findViewById(R.id.guanyu_back);
        guanyu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }
}
