package com.qf.rwxchina.xiaochefudriver.Order;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.qf.rwxchina.xiaochefudriver.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *商务下单
 */
public class BusinessFragment extends Fragment {
    private TextView price1;
    private TextView price2;
    private TextView price3;
    private String uid;
    private LinearLayout mTime1;
    private LinearLayout mTime2;
    private LinearLayout mTime3;
    private List<TimeDuan> datas;
    private TimeDuan data;
    private TextView t1;
    private TextView t2;
    private TextView t3;
    private TextView wait;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business, container, false);
        init(view);
        SharedPreferences sp = getActivity().getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        uid = sp.getString("uid","");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        NetData();
    }

    //获取数据
    private void NetData() {
        datas = new ArrayList<>();
        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(this)
                .doPostAsync(
                        HttpInfo.Builder()
                                .setUrl(HttpPath.GETDRIVERPRICE)
                                .addParam("uid",uid)
                                .build(),
                        new CallbackOk() {
                            @Override
                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()) {
                                    //获取到数据
                                    String result = info.getRetDetail();
                                    Log.e("kunlun","商务下单="+result);
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

                                                datas.add(data);
                                            }

                                            Log.e("kunlun","=="+datas.size());
                                            if (datas.size() == 1){
                                                mTime2.setVisibility(View.GONE);
                                                mTime3.setVisibility(View.GONE);
                                                t1.setText(datas.get(0).getBegintime()+"-"+datas.get(0).getEndtime());
                                                wait.setText("等待时间满"+datas.get(0).getWaittimeduan()+"分钟开始收费"+datas.get(0).getWaitmoney()+"每分钟，不满30分钟不收费");
                                                price1.setText("￥"+datas.get(0).getMoney());
                                            }else if (datas.size() == 2){
                                                mTime3.setVisibility(View.GONE);
                                                t1.setText(datas.get(0).getBegintime()+"-"+datas.get(0).getEndtime());
                                                wait.setText("等待时间满"+datas.get(0).getWaittimeduan()+"分钟开始收费"+datas.get(0).getWaitmoney()+"每分钟，不满30分钟不收费");
                                                price1.setText("￥"+datas.get(0).getMoney());

                                                t2.setText(datas.get(1).getBegintime()+"-"+datas.get(1).getEndtime());
                                                price2.setText("￥"+datas.get(1).getMoney());
                                            }else if (datas.size() == 3){
                                                t1.setText(datas.get(0).getBegintime()+"-"+datas.get(0).getEndtime());
                                                wait.setText("等待时间满"+datas.get(0).getWaittimeduan()+"分钟开始收费"+datas.get(0).getWaitmoney()+"每分钟，不满30分钟不收费");
                                                price1.setText("￥"+datas.get(0).getMoney());

                                                t2.setText(datas.get(1).getBegintime()+"-"+datas.get(1).getEndtime());
                                                price2.setText("￥"+datas.get(1).getMoney());

                                                t3.setText(datas.get(2).getBegintime()+"-"+datas.get(2).getEndtime());
                                                price3.setText("￥"+datas.get(2).getMoney());
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
        price1 = (TextView) view.findViewById(R.id.fragment_business_price1);
        price2 = (TextView) view.findViewById(R.id.fragment_business_price2);
        price3 = (TextView) view.findViewById(R.id.fragment_business_price3);
        mTime1 = (LinearLayout) view.findViewById(R.id.fragment_business_time1);
        mTime2 = (LinearLayout) view.findViewById(R.id.fragment_business_time2);
        mTime3 = (LinearLayout) view.findViewById(R.id.fragment_business_time3);
        t1 = (TextView) view.findViewById(R.id.fragment_business_t1);
        t2 = (TextView) view.findViewById(R.id.fragment_business_t2);
        t3 = (TextView) view.findViewById(R.id.fragment_business_t3);
        wait = (TextView) view.findViewById(R.id.fragment_business_wait);
    }

}
