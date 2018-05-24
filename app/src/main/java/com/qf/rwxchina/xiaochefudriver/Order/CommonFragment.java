package com.qf.rwxchina.xiaochefudriver.Order;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;

import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.Bean.TimeDuan;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 普通代驾
 */
public class CommonFragment extends Fragment {
    private TextView price1;
    private TextView price2;
    private TextView price3;
    private TextView price4;
    private TextView jifei;
    private TextView wait;
    private LinearLayout mTime1;
    private LinearLayout mTime2;
    private LinearLayout mTime3;

    private List<TimeDuan> datas;
    private TimeDuan data;

    private TextView t1;
    private TextView t2;
    private TextView t3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_common, container, false);
        init(view);
        netData();
        return view;
    }

    /**
     * 网络获取数据
     */
    private void netData() {
        datas = new ArrayList<>();
        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(this)
                .doPostAsync(
                        HttpInfo.Builder()
                                .setUrl(HttpPath.GETDRIVERPRICE)
                                .addParam("cityid", MyApplication.cityId+"")
                                .build(),
                        new CallbackOk() {
                            @Override
                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()) {
                                    //获取到数据
                                    String result = info.getRetDetail();
                                    Log.e("hrr","result="+result.toString());
                                    if (result != null) {
                                        try {
                                            JSONObject object = new JSONObject(result);
                                            JSONArray arr = new JSONArray(object.optString("data"));
                                            for (int i=0;i<arr.length();i++){
                                                JSONObject obj = arr.getJSONObject(i);
                                                data = new TimeDuan();
                                                data.setMaxnumber(obj.optString("maxnumber"));
                                                data.setMoney(obj.optString("money"));
                                                data.setAddmoney(obj.optString("addmoney"));
                                                data.setWaittimeduan(obj.optString("waittimeduan"));
                                                data.setWaitmoney(obj.optString("waitmoney"));
                                                data.setBegintime(obj.optString("begintime"));
                                                data.setEndtime(obj.optString("endtime"));
                                                data.setMaxnumber_exc(obj.optString("maxnumber_exc"));
                                                data.setWaittimeduan_exc(obj.optString("waittimeduan_exc"));
                                                datas.add(data);
                                            }
//                                            超出起步公里数，每5公里加收20元，不足5公里按5公里计算。\n计费时间以司机开始代驾时间计算。
//                                            前30分钟免费，超过30分钟后每30分钟加收等待费20元，不足30分钟按30分钟计算。
                                            switch (datas.size()){
                                                case 1:
                                                    mTime2.setVisibility(View.GONE);
                                                    mTime3.setVisibility(View.GONE);
                                                    t1.setText(datas.get(0).getBegintime()+"-"+datas.get(0).getEndtime());
                                                    String waittimeduan=datas.get(0).getWaittimeduan_exc();
                                                    wait.setText("前"+waittimeduan+"分钟免费，超过"+waittimeduan
                                                            +"分钟后每"+waittimeduan+"分钟加收等待费"+datas.get(0).getWaitmoney()
                                                            +"元，不足"+waittimeduan+"分钟按"+waittimeduan+"分钟计算。");


                                                    String gongli=datas.get(0).getMaxnumber_exc();
                                                    jifei.setText("超出起步公里数，每"+gongli+"公里加收"+datas.get(0).getAddmoney()
                                                            +"元，不足"+gongli+"公里按"+gongli+"公里计算。计费时间以司机开始代驾时间计算。");
//                                                wait.setText("等待时间满"+datas.get(0).getWaittimeduan()+"分钟开始收费"+datas.get(0).getWaitmoney()+"每分钟，不满30分钟不收费");
                                                    price1.setText("￥"+datas.get(0).getMoney());
                                                    break;
                                                case 2:
                                                    mTime3.setVisibility(View.GONE);
                                                    t1.setText(datas.get(0).getBegintime()+"-"+datas.get(0).getEndtime());
                                                    String waittimeduan2=datas.get(0).getWaittimeduan_exc();
                                                    wait.setText("前"+waittimeduan2+"分钟免费，超过"+waittimeduan2
                                                            +"分钟后每"+waittimeduan2+"分钟加收等待费"+datas.get(0).getWaitmoney()
                                                            +"元，不足"+waittimeduan2+"分钟按"+waittimeduan2+"分钟计算。");

                                                    String gongli2=datas.get(0).getMaxnumber_exc();
                                                    jifei.setText("超出起步公里数，每"+gongli2+"公里加收"+datas.get(0).getAddmoney()
                                                            +"元，不足"+gongli2+"公里按"+gongli2+"公里计算。计费时间以司机开始代驾时间计算。");
//                                                wait.setText("等待时间满"+datas.get(0).getWaittimeduan()+"分钟开始收费"+datas.get(0).getWaitmoney()+"每分钟，不满30分钟不收费");
                                                    price1.setText("￥"+datas.get(0).getMoney());

                                                    t2.setText(datas.get(1).getBegintime()+"-"+datas.get(1).getEndtime());
                                                    price2.setText("￥"+datas.get(1).getMoney());
                                                    break;
                                                case 3:
                                                    t1.setText(datas.get(0).getBegintime()+"-"+datas.get(0).getEndtime());
                                                    String waittimeduan3=datas.get(0).getWaittimeduan_exc();
                                                    wait.setText("前"+waittimeduan3+"分钟免费，超过"+waittimeduan3
                                                            +"分钟后每"+waittimeduan3+"分钟加收等待费"+datas.get(0).getWaitmoney()
                                                            +"元，不足"+waittimeduan3+"分钟按"+waittimeduan3+"分钟计算。");


                                                    String gongli3=datas.get(0).getMaxnumber_exc();
                                                    jifei.setText("超出起步公里数，每"+gongli3+"公里加收"+datas.get(0).getAddmoney()
                                                            +"元，不足"+gongli3+"公里按"+gongli3+"公里计算。计费时间以司机开始代驾时间计算。");
//                                                wait.setText("等待时间满"+datas.get(0).getWaittimeduan()+"分钟开始收费"+datas.get(0).getWaitmoney()+"每分钟，不满30分钟不收费");
                                                    price1.setText("￥"+datas.get(0).getMoney());

                                                    t2.setText(datas.get(1).getBegintime()+"-"+datas.get(1).getEndtime());
                                                    price2.setText("￥"+datas.get(1).getMoney());

                                                    t3.setText(datas.get(2).getBegintime()+"-"+datas.get(2).getEndtime());
                                                    price3.setText("￥"+datas.get(2).getMoney());
                                                    break;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
    }


    private void init(View view) {
        price1 = (TextView) view.findViewById(R.id.fragment_common_price1);
        price2 = (TextView) view.findViewById(R.id.fragment_common_price2);
        price3 = (TextView) view.findViewById(R.id.fragment_common_price3);
        price4 = (TextView) view.findViewById(R.id.fragment_common_price4);
        wait = (TextView) view.findViewById(R.id.fragment_common_wait);
        mTime1 = (LinearLayout) view.findViewById(R.id.fragment_common_time1);
        mTime2 = (LinearLayout) view.findViewById(R.id.fragment_common_time2);
        mTime3 = (LinearLayout) view.findViewById(R.id.fragment_common_time3);
        t1 = (TextView) view.findViewById(R.id.fragment_common_t1);
        t2 = (TextView) view.findViewById(R.id.fragment_common_t2);
        t3 = (TextView) view.findViewById(R.id.fragment_common_t3);
        jifei= (TextView) view.findViewById(R.id.fragment_common_jifei);
    }

}
