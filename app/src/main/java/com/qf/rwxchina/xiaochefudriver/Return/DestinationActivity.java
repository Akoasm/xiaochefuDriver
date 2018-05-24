package com.qf.rwxchina.xiaochefudriver.Return;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
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
import com.qf.rwxchina.xiaochefudriver.Bean.Address;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.LocationXY;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/17.
 * 输入目的地
 */
public class DestinationActivity extends AppCompatActivity{

    EditText changyong_ed;
    ImageView changyong_back;
    ListView changyong_listview;
    //检索周边地址集合
    private List<Address> lists =new ArrayList<Address>();
    private MyAdapter adapter;
    private double lng;
    private double lat;
    private String sAddress;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_destination);
        MyApplication.getInstance().addActivity(this);
        init();
    }

    public  void  init()
    {
        //获取当前经纬度
        Location myLocation = new LocationXY().init(getApplicationContext());
        lng = myLocation.getLongitude();
        lat = myLocation.getLatitude();
        changyong_ed= (EditText) findViewById(R.id.changyong_ed);
        changyong_back= (ImageView) findViewById(R.id.changyong_back);
        changyong_listview= (ListView) findViewById(R.id.changyong_listview);

        //输入框监听事件
        changyong_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    lists.clear();
                    sAddress = s.toString();
                    poiAddress();
                } else {
                    poiAddress();
                    lists.clear();
                    for (int i = 0; i < lists.size(); i++) {
                        lists.remove(i);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        changyong_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(DestinationActivity.this, ComeBackActivity.class);

                intent.putExtra("address", lists.get(i).getAddress());
                intent.putExtra("detailedAddress", lists.get(i).getDetailedAddress());
                intent.putExtra("lng", lists.get(i).getLng());
                intent.putExtra("lat", lists.get(i).getLat());
                setResult(1, intent);
                finish();

            }
        });

        changyong_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }




    //输入地址检索周边
    private void poiAddress() {
        //创建检索实例
        final PoiSearch mPoiSearch = PoiSearch.newInstance();
        //创建检索的一些条件
        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.location(new LatLng(lat, lng));
        option.keyword(sAddress);
        option.radius(100000);
        option.pageNum(1);

        mPoiSearch.searchNearby(option);
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {//检索结果
                List<PoiInfo> allPoi = poiResult.getAllPoi();
                if (allPoi != null) {
                    for (int i = 0; i < allPoi.size(); i++) {
                        Address address = new Address();
                        address.setAddress(allPoi.get(i).name);
                        address.setDetailedAddress(allPoi.get(i).address);
                        address.setLat(allPoi.get(i).location.latitude);
                        address.setLng(allPoi.get(i).location.longitude);

                        Log.e("wh", "地址=" + allPoi.get(i).name);
                        Log.e("wh", "详细地址=" + allPoi.get(i).address);

                        lists.add(address);
                    }

                    if (lists.size() > 0) {
                        adapter = new MyAdapter();
                        changyong_listview.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "没有搜索到信息", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    Toast.makeText(getApplicationContext(), "没有搜索到信息", Toast.LENGTH_SHORT).show();
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
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.listview_item_address, null);

                viewHolder.add = (TextView) convertView.findViewById(R.id.listview_item_adddress_add);
                viewHolder.delAdd = (TextView) convertView.findViewById(R.id.listview_item_adddress_deladd);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.add.setText(lists.get(position).getAddress());
            viewHolder.delAdd.setText(lists.get(position).getDetailedAddress());

            return convertView;
        }
    }

    private class ViewHolder {
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
