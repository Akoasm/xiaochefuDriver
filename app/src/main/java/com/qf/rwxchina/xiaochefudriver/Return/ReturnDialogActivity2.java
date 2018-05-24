package com.qf.rwxchina.xiaochefudriver.Return;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
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
 * 结伴返程Dialog_2
 */
public class ReturnDialogActivity2 extends Activity implements View.OnClickListener{
    private ImageView mClose;
    private CircleImageView mImg;
    private TextView mName;
    private RatingBar mRatingBar;
    private TextView mYear;
    private TextView mNum;
    private Button mSure;
    private Button mRefuse;
    private SharedPreferences sp;
    String driverid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_return_dialog2);
        MyApplication.getInstance().addActivity(this);
        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        //司机ID
        driverid = sp.getString("uid", "");
        init();
        driverinfo();
    }

    private void init() {
        mClose = (ImageView) findViewById(R.id.activity_return_dialong2_close);
        mImg = (CircleImageView) findViewById(R.id.activity_return_dialong2_img);
        mName = (TextView) findViewById(R.id.activity_return_dialong2_name);
        mRatingBar = (RatingBar) findViewById(R.id.activity_return_dialong2_RatingBar);
        mYear = (TextView) findViewById(R.id.activity_return_dialong2_year);
        mNum = (TextView) findViewById(R.id.activity_return_dialong2_num);
        mSure = (Button) findViewById(R.id.activity_return_dialong2_sure);
        mRefuse = (Button) findViewById(R.id.activity_return_dialong2_refuse);
        mClose.setOnClickListener(this);
        mSure.setOnClickListener(this);
        mRefuse.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_return_dialong2_close:
                finish();
                break;
            case R.id.activity_return_dialong2_sure:
                isgo_ok();
                break;
            case R.id.activity_return_dialong2_refuse:
                isgo_no();
                break;
        }
    }

    /**
     * 同意结伴返程
     */
    public void isgo_ok()
    {
        if (Network.HttpTest(ReturnDialogActivity2.this)) {

            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.isgo).addParam("to_driverid", MyApplication.driverid).addParam("get_driverid",driverid).addParam("type","1").build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();
                                        Log.e("wh", result+">>>>");

                                        if (result != null) {
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> list = AnalyticalJSON.getHashMap(map.get("state"));
                                            HashMap<String, String> list_data = AnalyticalJSON.getHashMap(map.get("data"));
                                            if(list.get("code").equals("0"))
                                            {
                                                Toast.makeText(ReturnDialogActivity2.this,"等待司机呼叫你", Toast.LENGTH_SHORT).show();
                                                finish();

                                            }else
                                            {
                                                Toast.makeText(ReturnDialogActivity2.this,list.get("msg"), Toast.LENGTH_SHORT).show();
                                            }

                                        } else {

                                            Toast.makeText(ReturnDialogActivity2.this,"服务器连接失败", Toast.LENGTH_SHORT).show();


                                        }

                                    }
                                }
                            });
        }

    }



    /**
     * 拒绝结伴返程
     */
    public void isgo_no()
    {
        if (Network.HttpTest(ReturnDialogActivity2.this)) {

            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.isgo).addParam("to_driverid",driverid).addParam("get_driverid",MyApplication.driverid).addParam("type","2").build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();
                                        Log.e("wh", result+">>>>");

                                        if (result != null) {
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> list = AnalyticalJSON.getHashMap(map.get("state"));
                                            HashMap<String, String> list_data = AnalyticalJSON.getHashMap(map.get("data"));
                                            if(list.get("code").equals("0"))
                                            {

                                                finish();
                                            }else
                                            {
                                                Toast.makeText(ReturnDialogActivity2.this,list.get("msg"), Toast.LENGTH_SHORT).show();
                                            }

                                        } else {

                                            Toast.makeText(ReturnDialogActivity2.this,"服务器连接失败", Toast.LENGTH_SHORT).show();


                                        }

                                    }
                                }
                            });
        }

    }


    /**
     * 司机详情
     */
    public void driverinfo() {
        if (Network.HttpTest(ReturnDialogActivity2.this)) {
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.driverinfo).addParam("driverid",MyApplication.driverid).build(),
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
                                                    mImg.setImageResource(R.drawable.icon_account);
                                                }else{
                                                    Picasso.with(ReturnDialogActivity2.this).load(data.get("head_image")).error(R.drawable.icon_account).into(mImg);
                                                }
                                                //名称
                                                mName.setText(data.get("name"));
                                                //驾龄
                                                mYear.setText(data.get("driving_years")+"年");


                                                //代驾次数
                                                mNum.setText(data.get("agentsum")+"次");
                                                //评星
                                                mRatingBar.setRating(Float.parseFloat(data.get("avglevel")));

                                            }else
                                            {
                                                Toast.makeText(ReturnDialogActivity2.this,state.get("msg"), Toast.LENGTH_SHORT).show();
                                            }



                                        } else {

                                            Toast.makeText(ReturnDialogActivity2.this,"服务器连接失败", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                }
                            });
        }
    }


}
