package com.qf.rwxchina.xiaochefudriver.Order;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.Bean.OrderInfo;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *抢单列表
 */
public class OrderListActivity extends AppCompatActivity {
    private ImageView mBack;
    private ListView mListview;

    private double lng;
    private double lat;
    private Intent intent;
    private Bundle bundle;
    private List<OrderInfo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        MyApplication.getInstance().addActivity(this);
        init();
        getIntentInfo();
        HttpGetOrder();
    }

    //获取传递过来的信息
    private void getIntentInfo() {
        intent = getIntent();
        bundle = intent.getExtras();
        lng = bundle.getDouble("lng");
        lat = bundle.getDouble("lat");
    }

    //获取订单信息
    private void HttpGetOrder() {
        list = new ArrayList<>();
        Log.e("kunlun",HttpPath.GETORDER_PATH+"?lng="+lng+"&lat="+lat);
        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(this)
                .doPostAsync(
                        HttpInfo.Builder().setUrl(HttpPath.GETORDER_PATH)
                                .addParam("lng",lng+"")
                                .addParam("lat",lat+"")
                                .build(),
                        new CallbackOk() {
                            @Override
                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()) {
                                    //获取到数据
                                    String result = info.getRetDetail();
                                    if (result != null) {
                                        Log.e("kunlun","result="+result);
                                        try {
                                            JSONObject object = new JSONObject(result);
                                            JSONArray arr = new JSONArray(object.optString("data"));
                                            for (int i = 0;i<arr.length();i++){
                                                JSONObject obj = arr.getJSONObject(i);
                                                OrderInfo order = new OrderInfo();
                                                order.setId(obj.optInt("id"));
                                                order.setOut_trade_no(obj.optString("out_trade_no"));
                                                order.setOrderson(obj.optString("orderson"));
                                                order.setOrdertype(obj.optInt("ordertype"));
                                                order.setUid(obj.optInt("uid"));
                                                order.setDrivercount(obj.optInt("drivercount"));
                                                order.setSaddress(obj.optString("saddress"));
                                                order.setSlat(obj.optDouble("slat"));
                                                order.setSlng(obj.optDouble("slng"));
                                                order.setBespeaktime(obj.optString("bespeaktime"));
                                                order.setCreatetime(obj.optString("createtime"));
                                                order.setPhones(obj.optString("phones"));
                                                order.setOaddress(obj.optString("oaddress"));
                                                order.setOlng(obj.optDouble("olng"));
                                                order.setOlat(obj.optDouble("olat"));
                                                order.setIndentdistance(obj.optString("indentdistance"));

                                                list.add(order);
                                            }
                                            if (list.size() == 0){
                                                Toast.makeText(getApplicationContext(),"没有订单",Toast.LENGTH_SHORT).show();
                                            }else {
                                                setListView();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        setListView();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
    }

    private void setListView() {
        mListview.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object getItem(int position) {
                return list.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                ViewHolder viewHoler = null;
                if (convertView == null){
                    viewHoler = new ViewHolder();
                    convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.listview_item_order,null);
                    viewHoler.saddress = (TextView) convertView.findViewById(R.id.listview_item_order_start);
                    viewHoler.oaddress = (TextView) convertView.findViewById(R.id.listview_item_order_destination);
                    viewHoler.distance = (TextView) convertView.findViewById(R.id.listview_item_order_distance);
                    viewHoler.order = (Button) convertView.findViewById(R.id.listview_item_order_ordering);

                    convertView.setTag(viewHoler);
                }else {
                    viewHoler = (ViewHolder) convertView.getTag();
                }
                viewHoler.saddress.setText(list.get(position).getSaddress());
                viewHoler.oaddress.setText(list.get(position).getOaddress());
                viewHoler.distance.setText(list.get(position).getIndentdistance());
                viewHoler.order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(getApplicationContext(),OrderdetailsActivity.class);
                        bundle.putString("orderson",list.get(position).getOrderson());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                return convertView;
            }
        });
    }

    private class ViewHolder{
        TextView saddress;
        TextView oaddress;
        TextView distance;
        Button order;
    }

    private void init() {
        mBack = (ImageView) findViewById(R.id.activity_order_list_back);
        mListview = (ListView) findViewById(R.id.actvity_order_list_listview);
    }

    public void back(View view) {
        finish();
    }
}
