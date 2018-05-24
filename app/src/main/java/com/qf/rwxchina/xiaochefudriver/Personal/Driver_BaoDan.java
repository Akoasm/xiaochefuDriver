package com.qf.rwxchina.xiaochefudriver.Personal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.qf.rwxchina.xiaochefudriver.Adapter.DriverBaoDanAdapter;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/8.
 * 司机保单代驾服务通知
 */
public class Driver_BaoDan extends AppCompatActivity{
    private SharedPreferences sp;
    String  uid;
    ListView gonggao_listview;
    ImageView gong_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gonggao_listview);
        MyApplication.getInstance().addActivity(this);
        sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        //用户id
        uid = sp.getString("uid", "");
        init();
        report_message();
    }

    public void init()
    {
        gonggao_listview= (ListView) findViewById(R.id.gonggao_listview);
        gong_back= (ImageView) findViewById(R.id.gong_back);
        gong_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void report_message()
    {
        if (Network.HttpTest(Driver_BaoDan.this)) {
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.report_message).addParam("driverid",uid).build(),
                            new CallbackOk() {

                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();
                                        Log.e("wh", ">>>"+result);
                                        if (result != null) {
                                            //将得到的json数据返回给HashMap
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> state = AnalyticalJSON.getHashMap(map.get("state"));
                                            ArrayList<HashMap<String, String>> data_list = AnalyticalJSON.getList_zj(map.get("data"));
                                            if(state.get("code").equals("0"))
                                            {
                                                DriverBaoDanAdapter driverBaoDanAdapter=new DriverBaoDanAdapter(data_list,Driver_BaoDan.this);
                                                gonggao_listview.setAdapter(driverBaoDanAdapter);


                                            }else
                                            {
                                                Toast.makeText(Driver_BaoDan.this,state.get("msg"), Toast.LENGTH_SHORT).show();
                                            }



                                        } else {

                                            Toast.makeText(Driver_BaoDan.this,"服务器连接失败", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                }
                            });
        }
    }
}
