package com.qf.rwxchina.xiaochefudriver.Personal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/20.
 * 详细页面
 */

public class MinuteActivity extends Activity {
    ListView listView;
    MinuteAdapter minuteAdapter;
    List<MinuteInfo> minuteInfos;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minute);
        MyApplication.getInstance().addActivity(this);
        init();
        getData();
    }

    public void init(){
        listView= (ListView) findViewById(R.id.activity_minute_listview);
        back= (ImageView) findViewById(R.id.activity_minute_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        minuteAdapter=new MinuteAdapter(this);
        minuteInfos=new ArrayList<MinuteInfo>();
    }
    private void getData(){
        if (Network.HttpTest(this)){
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(HttpInfo.Builder().setUrl(HttpPath.getMinute).build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()){
                                        String requst=info.getRetDetail();
                                        try {
                                            JSONObject object=new JSONObject(requst);
                                            JSONArray jsonArray=object.getJSONArray("data");
                                            for (int i=0;i<jsonArray.length();i++){
                                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                                MinuteInfo minuteInfo=new MinuteInfo();
                                                minuteInfo.setTitle(i+jsonObject.getString("title"));
                                                minuteInfo.setContent(jsonObject.getString("content"));
                                                minuteInfos.add(minuteInfo);
                                            }
                                            setContent();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            });
        }
    }

    private void setContent(){
        listView.setAdapter(minuteAdapter);
        minuteAdapter.setMinuteInfos(minuteInfos);
    }
}
