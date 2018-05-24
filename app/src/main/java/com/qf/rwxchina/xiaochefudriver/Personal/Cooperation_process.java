package com.qf.rwxchina.xiaochefudriver.Personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;

/**
 * Created by Administrator on 2016/9/18.
 * 合作流程
 */
public class Cooperation_process extends AppCompatActivity{

    ImageView liucheng_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ooperation_process);
        MyApplication.getInstance().addActivity(this);
        init();
    }

    public void init()
    {
        liucheng_back= (ImageView) findViewById(R.id.liucheng_back);
        liucheng_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
