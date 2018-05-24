package com.qf.rwxchina.xiaochefudriver.Order;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/11.
 * 订单取消
 */
public class OrderQuxiao extends AppCompatActivity{
    ImageView qu_img;
    TextView qu_name,qu_num,remark,cancel_reason;
    Button qu_ok;
    String orderson;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userquxiao);
        MyApplication.getInstance().addActivity(this);
        init();
        mediaPlayer.start();
        Intent intent= getIntent();
        orderson= MyApplication.orderson;
        Log.e("wh","取消订单orderson》》"+orderson);
        agreeIndent();
    }

    public  void  init()
    {
        mediaPlayer = MediaPlayer.create(OrderQuxiao.this, R.raw.quxiao02);
        qu_img= (ImageView) findViewById(R.id.qu_img);
        qu_name= (TextView) findViewById(R.id.qu_name);
        qu_num= (TextView) findViewById(R.id.qu_num);
        qu_ok= (Button) findViewById(R.id.qu_ok);
        remark= (TextView) findViewById(R.id.remark);
        cancel_reason= (TextView) findViewById(R.id.cancel_reason);
        qu_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                MyApplication.orderState=0;
                SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
                // 写入
                SharedPreferences.Editor editor = sp.edit();
                //更改上班状态
                editor.putInt("work_status",1);
                //提交
                editor.commit();
                Intent intent = new Intent(OrderQuxiao.this,MainActivity.class);
                startActivity(intent);

            }
        });
    }


    private void agreeIndent() {
        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(this)
                .doPostAsync(
                        HttpInfo.Builder().setUrl(HttpPath.ORDER_DETAILS_PATH)
                                .addParam("orderson",orderson)
                                .build(),
                        new CallbackOk() {
                            @Override
                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()) {
                                    //获取到数据
                                    String result = info.getRetDetail();
                                    Log.e("wh",result+"<<<");
                                    if (result != null) {
                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                        HashMap<String, String> list_msg= AnalyticalJSON.getHashMap(map.get("state"));
                                        HashMap<String, String> list_date= AnalyticalJSON.getHashMap(map.get("data"));
                                        HashMap<String, String> list_userInfo= AnalyticalJSON.getHashMap(list_date.get("userInfo"));
                                        Log.e("wh","集合》》"+list_userInfo);

                                        if(list_msg.get("code").equals("0")) {
                                            //名称
                                            if(list_userInfo.get("name").equals(""))
                                            {
                                                qu_name.setText("小车夫");
                                            }else
                                            {
                                                qu_name.setText(list_userInfo.get("name"));
                                            }

                                            //电话号码
                                            qu_num.setText(list_date.get("phones"));
                                            //头像
                                            if(list_userInfo.get("userurl").equals(""))
                                            {
                                                qu_img.setImageResource(R.drawable.icon_account);
                                            }else{
                                                Picasso.with(getApplicationContext()).load(list_userInfo.get("userurl")).error(R.drawable.icon_account).into(qu_img);
                                            }
                                            remark.setText(list_date.get("remark"));
                                            cancel_reason.setText(list_date.get("cancel_reason"));


                                        }else
                                        {
                                            Intent  intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(OrderQuxiao.this,list_msg.get("msg"), Toast.LENGTH_SHORT).show();
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
            MyApplication.orderState=0;
            SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
            // 写入
            SharedPreferences.Editor editor = sp.edit();
            //更改上班状态
            editor.putInt("work_status",1);
            //提交
            editor.commit();
            Intent intent = new Intent(OrderQuxiao.this,MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
