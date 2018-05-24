package com.qf.rwxchina.xiaochefudriver.Personal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.qf.rwxchina.xiaochefudriver.Adapter.DriverDetailsAdapter;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/18.
 * 司机详情
 */
public class Driver_details extends AppCompatActivity {

    ImageView sj_back;
    ListView driverdetails_listview;
    View contview;
    ImageView dr_img;
    TextView dr_name, dr_year, dr_type, dr_num;
    RatingBar myRatingBar;
    private SharedPreferences sp;
    String  uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_details);
        MyApplication.getInstance().addActivity(this);
        sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        //用户id
        uid = sp.getString("uid", "");
        sj_back = (ImageView) findViewById(R.id.sj_back);
        sj_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        init();
        driverinfo();
        drivercomment();
    }

    public void init() {
        // 拦截头部布局文件
        contview = LayoutInflater.from(this).inflate(
                R.layout.driver_head, null);
        driverdetails_listview = (ListView) findViewById(R.id.driverdetails_listview);
        dr_img = (ImageView) contview.findViewById(R.id.dr_img);
        dr_name = (TextView) contview.findViewById(R.id.dr_name);
        dr_year = (TextView) contview.findViewById(R.id.dr_year);
        dr_type = (TextView) contview.findViewById(R.id.dr_type);
        dr_num = (TextView) contview.findViewById(R.id.dr_num);
        myRatingBar = (RatingBar) contview.findViewById(R.id.dr_bar);
        driverdetails_listview.addHeaderView(contview);
    }


    /**
     * 司机详情
     */
    public void driverinfo() {
        if (Network.HttpTest(Driver_details.this)) {
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.driverinfo).addParam("driverid",uid).build(),
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
                                            HashMap<String, String> data = AnalyticalJSON.getHashMap(map.get("data"));
                                            if(state.get("code").equals("0"))
                                            {
                                                 //头像
                                                if(data.get("head_image").equals(""))
                                                {
                                                    dr_img.setImageResource(R.drawable.icon_account);
                                                }else{
                                                    Picasso.with(Driver_details.this).load(data.get("head_image")).error(R.drawable.icon_account).into(dr_img);
                                                }
                                                //名称
                                                dr_name.setText(data.get("name"));
                                                //驾龄
                                                dr_year.setText(data.get("driving_years")+"年");
                                                //状态
                                                if(data.get("work_status").equals("0"))
                                                {
                                                    dr_type.setText("未上线");
                                                }else if(data.get("work_status").equals("1"))
                                                {
                                                    dr_type.setText("空闲");

                                                }else
                                                {
                                                    dr_type.setText("服务中");
                                                }

                                                //代驾次数
                                                dr_num.setText(data.get("agentsum")+"次");
                                               //评星
                                                myRatingBar.setRating(Float.parseFloat(data.get("avglevel")));

                                            }else
                                            {
                                                Toast.makeText(Driver_details.this,state.get("msg"), Toast.LENGTH_SHORT).show();
                                            }



                                        } else {

                                            Toast.makeText(Driver_details.this,"服务器连接失败", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                }
                            });
        }
    }

    /**
     * 用户评论
     */
    public void drivercomment()
    {
        if (Network.HttpTest(Driver_details.this)) {
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.drivercomment).addParam("driverid",uid).build(),
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


                                                    DriverDetailsAdapter driverDetailsAdapter = new DriverDetailsAdapter(data_list, Driver_details.this);
                                                    driverdetails_listview.setAdapter(driverDetailsAdapter);



                                            }else
                                            {
                                                Toast.makeText(Driver_details.this,state.get("msg"), Toast.LENGTH_SHORT).show();
                                            }



                                        } else {

                                            Toast.makeText(Driver_details.this,"服务器连接失败", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                }
                            });
        }
    }





}
