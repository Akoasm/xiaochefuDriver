package com.qf.rwxchina.xiaochefudriver.MapHome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.qf.rwxchina.xiaochefudriver.R;

/**
 * 接单
 */
public class AcceptOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_accept_order);
    }
}
