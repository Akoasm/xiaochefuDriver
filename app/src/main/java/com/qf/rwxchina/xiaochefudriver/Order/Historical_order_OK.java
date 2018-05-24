package com.qf.rwxchina.xiaochefudriver.Order;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.State.StateActivity;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;
import com.qf.rwxchina.xiaochefudriver.Utils.logutils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/14.
 * 历史订单已完成
 */
public class Historical_order_OK  extends Fragment{

    ListView historical_list;
    private SharedPreferences sp;
    String  uid;
    HistoricalBaseAdaPter historicalBaseAdaPter;
    ArrayList<HashMap<String, String>> list_data=new ArrayList<HashMap<String, String>>();
    //判断编辑或删除
    boolean istrue = true;
    //用于存放删除的ID
    ArrayList<String> list_id = new ArrayList<String>();
    //用于存放是否选中
    HashMap<String, Boolean>   isc = new HashMap<String, Boolean>();
    StateActivity stateActivity = new  StateActivity();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.historical_order_listview, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sp = getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        uid = sp.getString("uid", "");
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        Historcal();
    }

    /*
            *  初始化控件
             */
    public void init() {
        historical_list= (ListView) getView().findViewById(R.id.historical_list);
        //绑定数据源
        historicalBaseAdaPter=new HistoricalBaseAdaPter(list_data,getContext());
        historical_list.setAdapter(historicalBaseAdaPter);

        historical_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if ("1".equals(list_data.get(i).get("from"))){
                    Toast.makeText(getActivity(),"自报单，不能查看",Toast.LENGTH_SHORT).show();
                }else {
                    if (istrue == false) {
                        historicalBaseAdaPter.setisc(i);
                    } else {
                        Log.e("hrr","list_data="+list_data.get(i).toString());
                        Intent intent=new Intent(getActivity(),DetilsMapActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("starttime",list_data.get(i).get("overwait_time"));
                        bundle.putString("endtime", list_data.get(i).get("endtime"));
                        bundle.putString("createtime", list_data.get(i).get("createtime"));
                        bundle.putString("distance", list_data.get(i).get("distance"));
                        bundle.putString("saddress", list_data.get(i).get("saddress"));
                        bundle.putString("oaddress", list_data.get(i).get("oaddress"));
                        bundle.putString("is_report", list_data.get(i).get("is_report"));
                        bundle.putString("type_id", list_data.get(i).get("type_id"));
                        bundle.putString("username", list_data.get(i).get("username"));
                        bundle.putString("alltime", list_data.get(i).get("_alltime"));
                        bundle.putString("original_totalmoney", list_data.get(i).get("original_totalmoney"));

                        bundle.putString("ordertype", list_data.get(i).get("ordertype"));

                        bundle.putString("platenumber", list_data.get(i).get("platenumber"));
                        bundle.putString("beginwait_time", list_data.get(i).get("beginwait_time"));
                        bundle.putString("overwait_time", list_data.get(i).get("overwait_time"));
                        bundle.putString("endtime", list_data.get(i).get("endtime"));
                        bundle.putString("waittime", list_data.get(i).get("waittime"));
                        bundle.putString("waitmoney", list_data.get(i).get("waitmoney"));
                        bundle.putString("distancemoney", list_data.get(i).get("distancemoney"));
                        bundle.putString("_fanli", list_data.get(i).get("_fanli"));
                        intent.putExtras(bundle);
                        startActivity(intent);
                        Log.e("wh","你点的是跳转");

                    }
                }

            }
        });

    }


    /**
     * 历史订单已完成
     */
    public void Historcal() {
        if (Network.HttpTest(getActivity())) {
            stateActivity.showDialog(getActivity(),"订单加载中..");
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.getorder)
                                    .addParam("driverid",uid)
                                    .addParam("status","1")
                                    .build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();
                                        LogUtil.e("已完成="+result);

                                        if (result != null) {
                                            //将得到的json数据返回给HashMap
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> list_state = AnalyticalJSON.getHashMap(map.get("state"));
                                            list_data = AnalyticalJSON.getList_zj(map.get("data"));
                                            if (list_state.get("code").equals("0")) {

                                                historicalBaseAdaPter.setlist(list_data);

                                            } else {
                                                Toast.makeText(getActivity(), list_state.get("msg"), Toast.LENGTH_SHORT).show();
                                            }

                                        } else {

                                            Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();

                                        }

                                    }else {
                                        Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
        }
        stateActivity.dismissDialog();

    }

    /**
     * Created by Administrator on 2016/9/22.
     * 历史订单适配
     */
    public class HistoricalBaseAdaPter extends BaseAdapter {

        ArrayList<HashMap<String, String>> list;
        Context context;

        public HistoricalBaseAdaPter(ArrayList<HashMap<String, String>> list, Context context) {
            this.context = context;
            this.list = list;

        }
        public void setlist(ArrayList<HashMap<String, String>> list) {
            this.list = list;
            this.notifyDataSetChanged();

        }
        //点击item更改删除的小红点
        public void setisc(int i) {
            String key = list.get(i).get("id");
            Log.e("wh", "点击item的ID》》" + key);
            if (isc.containsKey(key) && isc.get(key)) {
                isc.put(key, false);
                list_id.remove(key);
            } else {
                isc.put(key, true);
                list_id.add(key);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHoder viewHoder;
            if(view==null)
            {
                viewHoder=new ViewHoder();

                view=LayoutInflater.from(context).inflate(R.layout.historical_order_context,null);
                viewHoder.or_time= (TextView) view.findViewById(R.id.or_time);
                viewHoder.distance= (TextView) view.findViewById(R.id.distance);
                viewHoder.or_saddress= (TextView) view.findViewById(R.id.or_saddress);
                viewHoder.or_oaddress= (TextView) view.findViewById(R.id.or_oaddress);
                viewHoder.or_name= (TextView) view.findViewById(R.id.or_name);
                viewHoder.img_true= (ImageView) view.findViewById(R.id.img_true);
                viewHoder.or_weibaodan= (TextView) view.findViewById(R.id.or_weibaodan);

                view.setTag(viewHoder);
            }else
            {
                viewHoder= (ViewHoder) view.getTag();
            }

            viewHoder.or_weibaodan.setVisibility(View.VISIBLE);

            //更改删除图标
            if (isc.containsKey(list.get(i).get("id")) && isc.get(list.get(i).get("id"))) {
                viewHoder.img_true.setImageResource(R.drawable.icon_choice_ture);
            } else {
                viewHoder.img_true.setImageResource(R.drawable.icon_choice_false);
            }


            //是否隐藏删除小红点
            if (istrue == false) {
                viewHoder.img_true.setVisibility(View.VISIBLE);
            } else {
                viewHoder.img_true.setVisibility(View.GONE);
            }
            viewHoder.or_weibaodan.setVisibility(View.VISIBLE);

            //订单创建时间
            viewHoder.or_time.setText(list.get(i).get("createtime"));
            //总公里数
            viewHoder.distance.setText(list.get(i).get("distance")+"公里");
            //出发地址
            viewHoder.or_saddress.setText(list.get(i).get("saddress"));
            //目的地
            viewHoder.or_oaddress.setText(list.get(i).get("oaddress"));
            //是否报单
            if(list.get(i).get("is_report").equals("0"))
            {
                viewHoder.or_weibaodan.setText("未 报 单");
                viewHoder.or_weibaodan.setBackgroundResource(R.drawable.background_linear);
            }else if(list.get(i).get("is_report").equals("1"))
            {
                viewHoder.or_weibaodan.setText("已 报 单");
                viewHoder.or_weibaodan.setBackgroundResource(R.drawable.background_huise);
            }else if(list.get(i).get("is_report").equals("2"))
            {
                viewHoder.or_weibaodan.setText("待 报 单");
                viewHoder.or_weibaodan.setBackgroundResource(R.drawable.background_huise);
            }else
            {
                viewHoder.or_weibaodan.setText("司机投诉");
                viewHoder.or_weibaodan.setBackgroundResource(R.drawable.background_huise);
            }
            //客户名称
            if(list.get(i).get("type_id").equals("1"))
            {
                viewHoder.or_name.setText("用户"+list.get(i).get("username"));
            }else
            {
                viewHoder.or_name.setText("商户"+list.get(i).get("username"));
            }



//          //点击未保单
//            if(list.get(i).get("is_report").equals("0")&&istrue==true){
//                viewHoder.or_weibaodan.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        Intent intent=new Intent(context,TableActivity.class);
//                        startActivity(intent);
//                    }
//                });
//
//
//
//            }

            return view;
        }


        public class ViewHoder {
            TextView or_time, distance, or_saddress, or_oaddress,or_name,or_weibaodan;
            ImageView img_true;
        }
    }

    /**
     * 删除历史订单
     */
    public void Historcal_del(final String iid) {
        if (Network.HttpTest(getActivity())) {
            stateActivity.showDialog(getActivity(),"订单正在删除..");
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.cancelorder).addParam("driverID",uid).addParam("id",iid).build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();
                                        Log.e("wh", result);

                                        if (result != null) {
                                            //将得到的json数据返回给HashMap
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> list_state = AnalyticalJSON.getHashMap(map.get("state"));

                                            if (list_state.get("code").equals("0")) {

                                            } else {
                                                Toast.makeText(getActivity(), list_state.get("msg"), Toast.LENGTH_SHORT).show();
                                            }

                                        } else {

                                            Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();

                                        }

                                    }else {

                                        Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
        }

        stateActivity.dismissDialog();

    }





    /**
     * 显示删除的红点
     */
    public void fr_true() {
        istrue = false;
        Log.e("wh", "显示小红点");
        list_id.clear();
        isc.clear();
        historicalBaseAdaPter.setlist(list_data);
    }

    /**
     * 隐藏删除的红点
     */
    public void fr_fal() {
        istrue = true;
        Log.e("wh", "隐藏小红点");

        //循环删除
        for (int i = 0; i < list_id.size(); i++) {
            Log.e("wh", "删除的ID》》" + list_id.get(i));
            Historcal_del(list_id.get(i));


            //循环所有数据 ， 将ID跟删除ID进行对比，然后移除
            for (int j = 0; j < list_data.size(); j++) {
                if (list_data.get(j).get("id").equals(list_id.get(i))) {
                    list_data.remove(j);
                }
            }


        }

        historicalBaseAdaPter.setlist(list_data);

    }



}
