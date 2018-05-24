package com.qf.rwxchina.xiaochefudriver.Return;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.qf.rwxchina.xiaochefudriver.Bean.BaoDanAddress;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hrr
 * on 2017/2/13.
 * 报单目的地
 */
public class BaoDanDestinationActivity extends AppCompatActivity {
    private EditText mOpinion;
    private TextView myAddress;
    private String sAddress;
    private ListView mListView;
    private TextView mHistoryAdd;
    private TextView mHistoryAddress;
    private LinearLayout mHistory;

    private String historyAdd;
    private String historyAddress;
    private Double hlat;
    private Double hLng;

    private MyAdapter adapter;
    private List<BaoDanAddress> lists = null;
    private double lng;
    private double lat;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String s;
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        MyApplication.getInstance().addActivity(this);
        getIntentData();
        init();
        sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        editor = sp.edit();
        lng = MyApplication.lng;
        lat = MyApplication.lat;
        setListView();
        getHistory();
    }

    /**
     * 获取历史信息
     */
    private void getHistory() {
        if (!TextUtils.isEmpty(sp.getString("historyAdd",""))){
            mHistory.setVisibility(View.VISIBLE);

            historyAdd = sp.getString("historyAdd","");
            historyAddress = sp.getString("historyAddress","");
            hLng = Double.parseDouble(sp.getString("lng",""));
            hlat = Double.parseDouble(sp.getString("lat",""));

            mHistoryAdd.setText(historyAdd);
            mHistoryAddress.setText(historyAddress);
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();
        s=intent.getExtras().getString("address");
        type=intent.getExtras().getString("type");
    }

    //检索周边
    private void poiAddress() {
        lists = new ArrayList<>();
        //创建检索实例
        final PoiSearch mPoiSearch = PoiSearch.newInstance();
        //创建检索的一些条件
        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.location(new LatLng(lat,lng));
        option.keyword(sAddress);
        option.radius(10000000);
        option.pageNum(1);

        mPoiSearch.searchNearby(option);
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {//检索结果
                List<PoiInfo> allPoi = poiResult.getAllPoi();
                if(allPoi != null){
                    for (int i = 0;i<allPoi.size();i++){
                        BaoDanAddress address = new BaoDanAddress();
                        address.setAddress(allPoi.get(i).name);
                        address.setDetailedAddress(allPoi.get(i).address);
                        address.setLng(allPoi.get(i).location.longitude);
                        address.setLat(allPoi.get(i).location.latitude);
                        Log.e("kunlun","地址="+allPoi.get(i).name);
                        Log.e("kunlun","详细地址="+allPoi.get(i).address);
                        Log.e("kunlun","lng="+allPoi.get(i).location.longitude+"\nlat="+allPoi.get(i).location.latitude);
                        lists.add(address);
                    }
                    if (lists.size() > 0){
                        adapter = new MyAdapter();
                        mListView.setAdapter(adapter);
                    }else {
                        Toast.makeText(getApplicationContext(),"没有搜索到信息",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {//详细检索的回调方法

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
    }


    private void init() {
        mOpinion = (EditText) findViewById(R.id.activity_distaination_opinion);
        myAddress = (TextView) findViewById(R.id.activity_distaination_address);
        mListView = (ListView) findViewById(R.id.activity_distaination_listview);
        mHistory = (LinearLayout) findViewById(R.id.activity_distaination_history);
        mHistory.setVisibility(View.GONE);
        mHistoryAdd = (TextView) findViewById(R.id.activity_distaination_history_add);
        mHistoryAddress = (TextView) findViewById(R.id.activity_distaination_history_address);
        myAddress.setText(s);
        switch (type){
            case "start":
                mHistory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent data=new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("position",historyAddress);
                        bundle.putDouble("lng",hLng);
                        bundle.putDouble("lat",hlat);
                        data.putExtras(bundle);
                        setResult(202, data);
                        finish();
                    }
                });
                break;
            case "destination":
                mHistory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent data=new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("position",historyAddress);
                        bundle.putDouble("lng",hLng);
                        bundle.putDouble("lat",hlat);
                        data.putExtras(bundle);
                        setResult(201, data);
                        finish();
                    }
                });
                break;
        }


        mOpinion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)){
                    sAddress = s.toString();
                    poiAddress();
                }else {
                    lists.clear();
//                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setListView(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editor.putString("historyAdd",lists.get(position).getAddress());
                editor.putString("historyAddress",lists.get(position).getDetailedAddress());
                editor.putString("lng",lists.get(position).getLng()+"");
                editor.putString("lat",lists.get(position).getLat()+"");
                editor.commit();

                Intent data=new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("position",lists.get(position).getDetailedAddress());
                bundle.putDouble("lng",lists.get(position).getLng());
                bundle.putDouble("lat",lists.get(position).getLat());
                data.putExtras(bundle);
                setResult(201, data);
                finish();
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.listview_item_address,null);

                viewHolder.add = (TextView) convertView.findViewById(R.id.listview_item_adddress_add);
                viewHolder.delAdd = (TextView) convertView.findViewById(R.id.listview_item_adddress_deladd);

                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.add.setText(lists.get(position).getAddress());
            viewHolder.delAdd.setText(lists.get(position).getDetailedAddress());

            return convertView;
        }
    }

    private class ViewHolder{
        TextView add;
        TextView delAdd;
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
