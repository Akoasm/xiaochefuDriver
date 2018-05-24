package com.qf.rwxchina.xiaochefudriver.Order;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.CircleImageView;

/**
 * 当前订单
 */

public class CurrentActivity extends AppCompatActivity {
    private ImageView mBack;
    private CircleImageView mImg;
    private TextView mName;
    private TextView mNumber;
    private TextView mPhone;
    private TextView mStart;
    private TextView mDestination;
    private TextView mTime;
    private TextView mSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);
        MyApplication.getInstance().addActivity(this);
        init();
    }

    private void init() {
        mBack = (ImageView) findViewById(R.id.activity_current_back);
        mImg = (CircleImageView) findViewById(R.id.activity_current_img);
        mName = (TextView) findViewById(R.id.activity_current_name);
        mNumber = (TextView) findViewById(R.id.activity_current_number);
        mPhone = (TextView) findViewById(R.id.activity_current_phone);
        mStart = (TextView) findViewById(R.id.activity_current_start);
        mDestination = (TextView) findViewById(R.id.activity_current_destination);
        mTime = (TextView) findViewById(R.id.activity_current_time);
        mSource = (TextView) findViewById(R.id.activity_current_source);
    }
}
