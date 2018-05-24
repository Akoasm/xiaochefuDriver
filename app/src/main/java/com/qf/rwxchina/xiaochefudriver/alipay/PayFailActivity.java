package com.qf.rwxchina.xiaochefudriver.alipay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;


/**
 * Created by Administrator on 2017/4/19 0019.
 */
public class PayFailActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView back,again;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_fail);
        MyApplication.getInstance().addActivity(this);
        init();
    }

    private void init(){
        back= (ImageView) findViewById(R.id.pay_fail_back);
        again= (ImageView) findViewById(R.id.pay_again);
        back.setOnClickListener(this);
        again.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pay_fail_back:
            case R.id.pay_again:
                finish();
                break;

        }
    }
}
