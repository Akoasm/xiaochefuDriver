package com.qf.rwxchina.xiaochefudriver.Personal;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/26.
 */
public class TiXingXiangQingActivity extends Activity implements View.OnClickListener{

    private SharedPreferences sp;
    String driverid,n="",mon;
    EditText tx_zhanghao,tx_name,tx_yinhang;
    Button tx_queren,tx_quxiao;
    String zhanghu,name,info="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tixianxiangqing);
        MyApplication.getInstance().addActivity(this);
        sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        //司机ID
        driverid = sp.getString("uid", "");
        init();
    }
    public void  init()
    {

        n=getIntent().getStringExtra("type");
        mon=getIntent().getStringExtra("mon");


        tx_zhanghao= (EditText) findViewById(R.id.tx_zhanghao);
        tx_name= (EditText) findViewById(R.id.tx_name);
        tx_yinhang= (EditText) findViewById(R.id.tx_yinhang);
        tx_queren= (Button) findViewById(R.id.tx_queren);
        tx_quxiao= (Button) findViewById(R.id.tx_quxiao);
        tx_queren.setOnClickListener(this);
        tx_quxiao.setOnClickListener(this);

        if(n.equals("1"))
        {
            tx_yinhang.setVisibility(View.GONE);
            info="支付宝";
        }else  if(n.equals("2"))
        {
            tx_yinhang.setVisibility(View.GONE);
            info="微信";
        }

        else
        {
            tx_yinhang.setVisibility(View.VISIBLE);
        }

    }




    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case  R.id.tx_queren:
                zhanghu=tx_zhanghao.getText().toString();
                name=tx_name.getText().toString();
               if(n.equals("1")||n.equals("2")) {
                   if(zhanghu.equals("")||name.equals("")) {
                       Toast.makeText(TiXingXiangQingActivity.this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                   }else {
                       driverdrawal();
                   }
               }else {
                   info=tx_yinhang.getText().toString();
                   if(zhanghu.equals("")||name.equals("")||info.equals("")) {
                       Toast.makeText(TiXingXiangQingActivity.this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                   }else {
                       driverdrawal();
                   }
               }
                break;
            case  R.id.tx_quxiao:
                finish();
                break;
        }
    }



    public void driverdrawal() {

        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(this)
                .doPostAsync(
                        HttpInfo.Builder().setUrl(HttpPath.driverdrawal).addParam("driverid", driverid).addParam("account",zhanghu).addParam("price",mon).addParam("info",info).addParam("name",name).build(),
                        new CallbackOk() {

                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()) {
                                    //获取到数据
                                    String result = info.getRetDetail();
                                    Log.e("wh", result);
                                    if (result != null) {
                                        //将得到的json数据返回给HashMap
                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                        HashMap<String, String> list = AnalyticalJSON.getHashMap(map.get("state"));
                                        Log.e("hrr","code="+list.get("code"));
                                        if (list.get("code").equals("0")) {
                                            finish();
                                        } else {
                                            Toast.makeText(TiXingXiangQingActivity.this, list.get("msg"), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(TiXingXiangQingActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
    }
}
