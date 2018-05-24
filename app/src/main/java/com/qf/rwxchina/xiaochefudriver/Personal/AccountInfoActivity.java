package com.qf.rwxchina.xiaochefudriver.Personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.orhanobut.logger.Logger;
import com.qf.rwxchina.xiaochefudriver.Adapter.AccountInfoAdapter;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/7/7.
 */

public class AccountInfoActivity extends AppCompatActivity implements View.OnClickListener{
    private ListView mListview;
    private ImageView accback;
    ArrayList<HashMap<String, String>> list;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountinfo_listview);
        initView();
        getData();
        setView();
    }
    void initView(){
        mListview= (ListView) findViewById(R.id.accountinfo_lv);
        accback = (ImageView) findViewById(R.id.activity_accountinfo_back);
    }
    void setView(){
        accback.setOnClickListener(this);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(AccountInfoActivity.this,XiangQingLieBiaoActivity.class);





                intent.putExtra("qibujia1",list.get(i).get("price"));//起步价
                intent.putExtra("qibugongli1",list.get(i).get("maxnumber"));//起步公里
                intent.putExtra("cc_qibugonglishu1",list.get(i).get("maxnumber_exc"));//超出起步公里数
                intent.putExtra("cc_qibugonglifeiyong1",list.get(i).get("kil_price"));//超出起步公里费用
                intent.putExtra("dengdaishijian1",list.get(i).get("waittimeduan"));//等待时间
                intent.putExtra("cc_dengdaishijian1",list.get(i).get("waittimeduan_exc"));//超出等待时间
                intent.putExtra("cc_dengdaishijianfeiyong1",list.get(i).get("wait_price"));//超出等待时间费用

                        if(  list.get(i).get("fantype").equals("0"))
                        {
                            intent.putExtra("type","0");//费率标识符%
                            intent.putExtra("fantype",list.get(i).get("fan1"));//费率标识符%
                        }else
                        {
                            intent.putExtra("type","1");//费率标识符%
                            intent.putExtra("fantype",list.get(i).get("fan2"));//费率标识符 元
                        }




                startActivity(intent);
            }
        });
    }
   void getData(){


       OkHttpUtil.Builder()
               .setCacheLevel(CacheLevel.FIRST_LEVEL)
               .setConnectTimeout(25).build(this)
               .doPostAsync(
                       HttpInfo.Builder().setUrl(HttpPath.GETPOSTAGE)

                               .build(),
                       new CallbackOk() {
                           @Override
                           public void onResponse(HttpInfo info) throws IOException {
                               if (info.isSuccessful()) {
                                   //获取到数据
                                   String result = info.getRetDetail();
                                 // Log.e("wh",result);
                                   Logger.e(result);
                                   if (result != null) {
                                       HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                       HashMap<String, String> map1 = AnalyticalJSON.getHashMap(map.get("state"));
                                       if(map1.get("code").equals("0"))
                                       {
                                            list=  AnalyticalJSON.getList_zj(map.get("data"));
                                           AccountInfoAdapter accountInfoAdapter=new AccountInfoAdapter(AccountInfoActivity.this,list);
                                           mListview.setAdapter(accountInfoAdapter);
                                       }else
                                       {
                                           Toast.makeText(AccountInfoActivity.this, map1.get("msg"),Toast.LENGTH_SHORT).show();
                                       }
                                   } else {
                                       Toast.makeText(AccountInfoActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           }
                       });


   }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_accountinfo_back:
                finish();
                break;
        }
    }
}
