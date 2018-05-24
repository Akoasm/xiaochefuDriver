package com.qf.rwxchina.xiaochefudriver.Order;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.Home.MainActivity;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.CircleImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

/**
 * 取消订单
 */
public class CancelOrderActivity extends AppCompatActivity implements OnClickListener {
    private ImageView mBack;
    private CircleImageView mImg;
    private TextView mName;
    private CheckBox mBox1;
    private CheckBox mBox2;
    private CheckBox mBox3;
    private CheckBox mBox4;
    private CheckBox mBox5;
    private EditText mOther;
    private Button mCancel;
    String m1 = "", m2 = "", m3 = "", m4 = "", m5 = "";
    private SharedPreferences sp;
    String driverid;
    private String reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_order);
        MyApplication.getInstance().addActivity(this);
        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        MyApplication.getInstance().addActivity(this);
        //司机ID
        driverid = sp.getString("uid", "");
        init();

        agreeIndent();
    }

    private void init() {

//        mBack = (ImageView) findViewById(R.id.activity_cancel_order_back);
        mImg = (CircleImageView) findViewById(R.id.activity_cancel_order_img);
        mName = (TextView) findViewById(R.id.activity_cancel_order_name);
        mBox1 = (CheckBox) findViewById(R.id.activity_cancel_order_checkbox1);
        mBox2 = (CheckBox) findViewById(R.id.activity_cancel_order_checkbox2);
        mBox3 = (CheckBox) findViewById(R.id.activity_cancel_order_checkbox3);
        mBox4 = (CheckBox) findViewById(R.id.activity_cancel_order_checkbox4);
        mBox5 = (CheckBox) findViewById(R.id.activity_cancel_order_checkbox5);
        mOther = (EditText) findViewById(R.id.activity_cancel_order_other);
        mCancel = (Button) findViewById(R.id.activity_cancel_order_cancel);
        //  mBack.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        mBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    m1 = compoundButton.getText().toString();
                } else {
                    m1 = "";
                }

            }
        });


        mBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    m2 = compoundButton.getText().toString();
                } else {
                    m2 = "";
                }

            }
        });

        mBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    m3 = compoundButton.getText().toString();
                } else {
                    m3 = "";
                }

            }
        });


        mBox4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    m4 = compoundButton.getText().toString();
                } else {
                    m4 = "";
                }

            }
        });

        mBox5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    m5 = mOther.getText().toString();
                } else {
                    m5 = "";
                }

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.activity_cancel_order_back:
//                finish();
//                break;
            case R.id.activity_cancel_order_cancel://取消订单
                if (!mBox1.isChecked() && !mBox2.isChecked() && !mBox3.isChecked() && !mBox4.isChecked() && !mBox5.isChecked()) {
                    Toast.makeText(getApplicationContext(), "请选择原因", Toast.LENGTH_SHORT).show();
                } else {
                    if (mBox5.isChecked()) {
                        m5 = mOther.getText().toString();
                        if (TextUtils.isEmpty(m5)) {
                            Toast.makeText(getApplicationContext(), "您勾选了其他原因，请填写其他原因", Toast.LENGTH_SHORT).show();
                        } else {
                            reason = m1 + m2 + m3 + m4 + m5;
                            cancel();
                        }
                    } else {
                        reason = m1 + m2 + m3 + m4;
                        cancel();
                    }
                }

                break;
        }
    }


    private void agreeIndent() {
        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(this)
                .doPostAsync(
                        HttpInfo.Builder().setUrl(HttpPath.ORDER_DETAILS_PATH)
                                .addParam("orderson", MyApplication.orderson)
                                .build(),
                        new CallbackOk() {
                            @Override
                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()) {
                                    //获取到数据
                                    String result = info.getRetDetail();

                                    if (result != null) {
                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                        HashMap<String, String> list_msg = AnalyticalJSON.getHashMap(map.get("state"));
                                        HashMap<String, String> list_date = AnalyticalJSON.getHashMap(map.get("data"));
                                        HashMap<String, String> list_userInfo = AnalyticalJSON.getHashMap(list_date.get("userInfo"));
                                        if (list_msg.get("code").equals("0")) {
                                            //名称
                                            if (list_userInfo.get("name").equals("")) {
                                                mName.setText("小车夫");
                                            } else {
                                                mName.setText(list_userInfo.get("name"));
                                            }

                                            //头像
                                            if (list_userInfo.get("userurl").equals("")) {
                                                mImg.setImageResource(R.drawable.icon_account);
                                            } else {
                                                Picasso.with(getApplicationContext()).load(list_userInfo.get("userurl")).error(R.drawable.icon_account).into(mImg);
                                            }
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(CancelOrderActivity.this, list_msg.get("msg"), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
    }


    private void cancel() {
        Log.e("wh", "reason=" + reason);
        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(this)
                .doPostAsync(
                        HttpInfo.Builder().setUrl(HttpPath.order_cancle)
                                .addParam("order", MyApplication.orderson)
                                .addParam("reason", reason)
                                .build(),
                        new CallbackOk() {
                            @Override
                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()) {
                                    //获取到数据
                                    String result = info.getRetDetail();
                                    Log.e("wh", result + "<<<");
                                    if (result != null) {
                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                        HashMap<String, String> list_msg = AnalyticalJSON.getHashMap(map.get("state"));
                                        if (list_msg.get("code").equals("0")) {
                                            MyApplication.orderState = 0;
                                            SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                                            // 写入
                                            SharedPreferences.Editor editor = sp.edit();
                                            //更改上班状态
                                            editor.putInt("work_status", 1);
                                            //提交
                                            editor.commit();
                                            Intent intent = new Intent(CancelOrderActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(CancelOrderActivity.this, list_msg.get("msg"), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

}
