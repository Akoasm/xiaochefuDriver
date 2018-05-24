package com.qf.rwxchina.xiaochefudriver.Order;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.orhanobut.logger.Logger;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.CircleImageView;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

/**
 * 当前订单
 */

public class CurrentOrderActivity extends AppCompatActivity {

    CircleImageView activity_current_img;
    TextView activity_current_name,activity_current_number,activity_current_phone,activity_current_start,activity_current_destination,activity_current_time,activity_current_source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);
        MyApplication.getInstance().addActivity(this);
        init();
        orderdata();
    }

    public void init(){
        activity_current_img= (CircleImageView) findViewById(R.id.activity_current_img);
        activity_current_name= (TextView) findViewById(R.id.activity_current_name);
        activity_current_number= (TextView) findViewById(R.id.activity_current_number);
        activity_current_phone= (TextView) findViewById(R.id.activity_current_phone);
        activity_current_start= (TextView) findViewById(R.id.activity_current_start);
        activity_current_destination= (TextView) findViewById(R.id.activity_current_destination);
        activity_current_time= (TextView) findViewById(R.id.activity_current_time);
        activity_current_source= (TextView) findViewById(R.id.activity_current_source);
    }

    public void back(View view) {
        finish();
    }

    private void orderdata() {
        if (Network.HttpTest(CurrentOrderActivity.this)){
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.orderdata).addParam("orderson",MyApplication.orderson).build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();
                                        Logger.t("CurrentOrderActivity").e("result="+result);
                                        if (result != null) {
                                            //将得到的json数据返回给HashMap
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> list= AnalyticalJSON.getHashMap(map.get("state"));
                                            if(list.get("code").equals("0")){
                                                HashMap<String, String> list_data= AnalyticalJSON.getHashMap(map.get("data"));
                                                HashMap<String, String> list_userInfo= AnalyticalJSON.getHashMap(list_data.get("userInfo"));
                                                if(list_userInfo.get("userurl").equals("")){
                                                    activity_current_img.setImageResource(R.drawable.icon_account);
                                                }else{
                                                    Picasso.with(CurrentOrderActivity.this).load(list_userInfo.get("userurl")).error(R.drawable.icon_account).into(activity_current_img);
                                                }

                                                activity_current_name.setText(list_userInfo.get("name"));
                                                activity_current_number.setText(list_data.get("drivercount")+"名");
                                                activity_current_phone.setText(list_data.get("phones"));
                                                activity_current_start.setText(list_data.get("saddress"));
                                                activity_current_destination.setText(list_data.get("oaddress"));
                                                activity_current_time.setText(list_data.get("bespeaktime"));
                                                if(list_data.get("type").equals("0")){
                                                    activity_current_source.setText("系统下单");
                                                }else{
                                                    activity_current_source.setText("人工下单");
                                                }

                                            }else{

                                                Toast.makeText(CurrentOrderActivity.this,list.get("msg"),Toast.LENGTH_SHORT).show();
                                            }

                                        } else {
                                            Toast.makeText(CurrentOrderActivity.this,"服务器连接失败", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                }
                            });
        }

    }
}
