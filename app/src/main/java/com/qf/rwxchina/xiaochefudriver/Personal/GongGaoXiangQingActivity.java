package com.qf.rwxchina.xiaochefudriver.Personal;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.qf.rwxchina.xiaochefudriver.Adapter.GongGaoAdapter;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.State.StateActivity;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/27.
 * 公告列表
 */
public class GongGaoXiangQingActivity extends AppCompatActivity implements View.OnClickListener{


    ImageView gonggaoxq_back;
    ListView gonggaoxq_listview;
    String driverid;
    private SharedPreferences sp;
    ArrayList<HashMap<String, String>> list_data;
    Intent intent;
    StateActivity stateActivity=new StateActivity();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gonggaoxq_listview);
        MyApplication.getInstance().addActivity(this);
        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        //司机ID
        driverid = sp.getString("uid", "");
        init();


        getMessageNew();
        getMessageList();

    }

    public void init()
    {

        gonggaoxq_back= (ImageView) findViewById(R.id.gonggaoxg_black);
        gonggaoxq_listview= (ListView) findViewById(R.id.gonggaoxg_listview);
        gonggaoxq_back.setOnClickListener(this);


        gonggaoxq_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                // 写入
//                SharedPreferences.Editor editor = sp.edit();
//                editor.putInt("msgid",Integer.parseInt(list_data.get(i).get("msgid")));
//                //提交
//                editor.commit();

                intent=new Intent(GongGaoXiangQingActivity.this,MaessageActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.gonggaoxg_black:
                finish();
                break;
        }
    }




    //获取新消息
    public void getMessageNew()
    {
        if (Network.HttpTest(GongGaoXiangQingActivity.this)) {
            stateActivity.showDialog(GongGaoXiangQingActivity.this,"加载中...");
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.getMessageNew).addParam("driverID",driverid).build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();


                                        if (result != null) {
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> list = AnalyticalJSON.getHashMap(map.get("state"));
                                            list_data = AnalyticalJSON.getList_zj(map.get("data"));

                                            if(list.get("code").equals("0"))
                                            {
                                                GongGaoAdapter gongGaoAdapter=new GongGaoAdapter(GongGaoXiangQingActivity.this,list_data);
                                                gonggaoxq_listview.setAdapter(gongGaoAdapter);

                                            }else
                                            {
                                                Toast.makeText(GongGaoXiangQingActivity.this,list.get("msg"), Toast.LENGTH_SHORT).show();
                                            }

                                        } else {

                                            Toast.makeText(GongGaoXiangQingActivity.this,"服务器连接失败", Toast.LENGTH_SHORT).show();

                                        }

                                    } else {
                                        Toast.makeText(GongGaoXiangQingActivity.this,"服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
        }
        stateActivity.dismissDialog();
    }


    /**
     * 获取历史消息
     */
    public void getMessageList()
    {
        if (Network.HttpTest(GongGaoXiangQingActivity.this)) {
            stateActivity.showDialog(GongGaoXiangQingActivity.this,"加载中...");
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.getMessageList).addParam("driverID",driverid).build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();


                                        if (result != null) {
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> list = AnalyticalJSON.getHashMap(map.get("state"));
                                            ArrayList<HashMap<String, String>> list_data = AnalyticalJSON.getList_zj(map.get("data"));

                                            if(list.get("code").equals("0"))
                                            {
                                                GongGaoAdapter gongGaoAdapter=new GongGaoAdapter(GongGaoXiangQingActivity.this,list_data);
                                                gonggaoxq_listview.setAdapter(gongGaoAdapter);
                                            }else
                                            {
                                                Toast.makeText(GongGaoXiangQingActivity.this,list.get("msg"), Toast.LENGTH_SHORT).show();
                                            }

                                        } else {

                                            Toast.makeText(GongGaoXiangQingActivity.this,"服务器连接失败", Toast.LENGTH_SHORT).show();


                                        }

                                    }else {
                                        Toast.makeText(GongGaoXiangQingActivity.this,"服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
        }
        stateActivity.dismissDialog();

    }

}
