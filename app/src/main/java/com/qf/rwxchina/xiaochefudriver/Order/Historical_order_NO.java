package com.qf.rwxchina.xiaochefudriver.Order;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/14.
 * 历史订单未完成
 */
public class Historical_order_NO extends Fragment{


    ListView historical_list;
    private SharedPreferences sp;
    String uid;
    HistoricalBaseAdaPter historicalBaseAdaPter;
    ArrayList<HashMap<String, String>> list_data=new ArrayList<HashMap<String, String>>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.historical_order_listview, container, false);
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
        historical_list = (ListView) getView().findViewById(R.id.historical_list);
        historicalBaseAdaPter = new HistoricalBaseAdaPter(list_data, getContext());
        historical_list.setAdapter(historicalBaseAdaPter);
    }


    /**
     * 历史订单未完成
     */
    public void Historcal() {
        if (Network.HttpTest(getActivity())) {
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.getorder).addParam("driverid", uid).addParam("status", "2").build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();
                                        Log.e("wh", "未完成="+result);
                                        if (result != null) {
                                            //将得到的json数据返回给HashMap
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> list_state = AnalyticalJSON.getHashMap(map.get("state"));
                                             list_data = AnalyticalJSON.getList_zj(map.get("data"));
                                            if (list_state.get("code").equals("0")) {
                                                Log.e("wh","list_data="+list_data.toString());
                                                historicalBaseAdaPter.setlist(list_data);
                                            } else {
                                                Toast.makeText(getActivity(), list_state.get("msg"), Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
        }


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
            if (view == null) {
                viewHoder = new ViewHoder();
                view = LayoutInflater.from(context).inflate(R.layout.historical_order_context, null);
                viewHoder.or_time = (TextView) view.findViewById(R.id.or_time);
                viewHoder.distance = (TextView) view.findViewById(R.id.distance);
                viewHoder.or_saddress = (TextView) view.findViewById(R.id.or_saddress);
                viewHoder.or_oaddress = (TextView) view.findViewById(R.id.or_oaddress);
                viewHoder.or_name = (TextView) view.findViewById(R.id.or_name);
                viewHoder.img_true = (ImageView) view.findViewById(R.id.img_true);

                view.setTag(viewHoder);
            } else {
                viewHoder = (ViewHoder) view.getTag();
            }
            Log.e("wh","list.i="+list.get(i).toString());
            //订单创建时间
            viewHoder.or_time.setText(list.get(i).get("createtime"));
            //总公里数
            viewHoder.distance.setText(list.get(i).get("distance") + "公里");
            //出发地址
            viewHoder.or_saddress.setText(list.get(i).get("saddress"));
            //目的地
            viewHoder.or_oaddress.setText(list.get(i).get("oaddress"));
            //客户名称

            //客户名称
            if(list.get(i).get("type_id").equals("1"))
            {
                viewHoder.or_name.setText("用户");
            }else
            {
                viewHoder.or_name.setText("商户");
            }
            return view;
        }


        public class ViewHoder {
            TextView or_time, distance, or_saddress, or_oaddress, or_name;
            ImageView img_true;
        }
    }
}
