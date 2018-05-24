package com.qf.rwxchina.xiaochefudriver.Return;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
 * 结伴返程Dialog_1
 */
public class ReturnDialogActivity1 extends Activity implements OnClickListener{
    private ImageView mCancel;
    private CircleImageView mImg;
    private TextView mName;
    private RatingBar mRatingBar;
    private TextView mYear;
    private TextView mNum;
    private Button mComeBack;

    String img,name,driverid,mudi_address,address;
    int rating,year,agentsum,id;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_return_dialog1);
        MyApplication.getInstance().addActivity(this);
        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        //司机ID
        driverid = sp.getString("uid", "");
        init();
        setDriverInfo();
    }

    /**
     * 获取司机信息
     */
    private void setDriverInfo() {

        img= getIntent().getStringExtra("img");
        name= getIntent().getStringExtra("name");
        rating= getIntent().getIntExtra("rating",0);
        year= getIntent().getIntExtra("year",0);
        agentsum= getIntent().getIntExtra("agentsum",0);
        id=getIntent().getIntExtra("driverId",0);
        address= getIntent().getStringExtra("address");
        mudi_address= getIntent().getStringExtra("mudi_address");

        Log.e("wh",address+">>>"+mudi_address);


        //头像
        if("".equals(img)){
            mImg.setImageResource(R.drawable.icon_account);
        }else{
            Picasso.with(ReturnDialogActivity1.this).load(img).error(R.drawable.icon_account).into(mImg);
        }

        mName.setText(name);
        mRatingBar.setRating(rating);
        mYear.setText(year+"");
        mNum.setText(agentsum+"");


    }

    private void init() {
        mCancel = (ImageView) findViewById(R.id.activity_return_dialong1_cancel);
        mImg = (CircleImageView) findViewById(R.id.activity_return_dialong1_img);
        mName = (TextView) findViewById(R.id.activity_return_dialong1_name);
        mRatingBar = (RatingBar) findViewById(R.id.activity_return_dialong1_ratingBar);
        mYear = (TextView) findViewById(R.id.activity_return_dialong1_year);
        mNum = (TextView) findViewById(R.id.activity_return_dialong1_num);
        mComeBack = (Button) findViewById(R.id.activity_return_dialong1_comeback);
        mCancel.setOnClickListener(this);
        mComeBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_return_dialong1_cancel:
                finish();
                break;
            case R.id.activity_return_dialong1_comeback:
                if(address.equals("")||mudi_address.equals(""))
                {
                    Toast.makeText(ReturnDialogActivity1.this,"请你报点", Toast.LENGTH_SHORT).show();
                }else
                {
                    togowith();
                }
                break;
        }
    }

    /**
     * 结伴返程
     */
    public void togowith()
    {
        if (Network.HttpTest(ReturnDialogActivity1.this)) {

            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.togowith).addParam("to_driverid",driverid).addParam("get_driverid",id+"").addParam("from_address",address).addParam("to_address",mudi_address).build(),
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
                                                Toast.makeText(ReturnDialogActivity1.this,"发起成功", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }else
                                            {
                                                Toast.makeText(ReturnDialogActivity1.this,list.get("msg"), Toast.LENGTH_SHORT).show();
                                            }

                                        } else {

                                            Toast.makeText(ReturnDialogActivity1.this,"服务器连接失败", Toast.LENGTH_SHORT).show();


                                        }

                                    }
                                }
                            });
        }

    }

}
