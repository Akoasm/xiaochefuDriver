package com.qf.rwxchina.xiaochefudriver.MapHome;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
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
 * 手动定位
 */
public class ManualLocationActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView mBack;
    private EditText mSearch;
    private ImageView mNavigation;
    private ListView mListview;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient = null;
    private BDLocationListener mLocationListener = new MyLocationListener();
    boolean isFirstLoc = true;// 是否首次定位
    private BDLocation mLocation;

    private List<Address> lists = new ArrayList<>();
    private boolean isLocation = true;

    private double lng;
    private double lat;
    private BitmapDescriptor bitmap;
    private Marker marker;
    private MyAdapter adapter;
    private String sAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_location);
        MyApplication.getInstance().addActivity(this);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mLocationListener);

        init();
        initLocation();
        setOnClock();
    }

    private void setOnClock() {
        mBack.setOnClickListener(this);
        mNavigation.setOnClickListener(this);
        //输入框监听事件
        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (lists != null){
                        lists.clear();
                    }
                    sAddress = s.toString();
                    poiAddress();
                } else {
                    lists.clear();
                    for (int i = 0; i < lists.size(); i++) {
                        lists.remove(i);
                    }
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ManualLocationActivity.this, MapFragment.class);
//                Log.e("wh", "点击得到的经纬度" + lists.get(i).getLng() + ">>" + lists.get(i).getLat());
                intent.putExtra("address", lists.get(i).getAddress());
                intent.putExtra("detailedAddress", lists.get(i).getDetailedAddress());
                intent.putExtra("lng", lists.get(i).getLng());
                intent.putExtra("lat", lists.get(i).getLat());
                setResult(111, intent);
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
        option.location(new LatLng(lat,lng));
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

//                        Log.e("kunlun", "地址=" + allPoi.get(i).name);
//                        Log.e("kunlun", "详细地址=" + allPoi.get(i).address);
//                        Log.e("kunlun", "经度=" + allPoi.get(i).location.longitude + "");
//                        Log.e("kunlun", "纬度=" + allPoi.get(i).location.latitude + "");
                        lists.add(address);
                    }

                    if (lists.size() > 0) {
                        adapter = new MyAdapter();
                        mListview.setAdapter(adapter);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_manual_location_back:
                finish();
                break;
            case R.id.activity_manual_location_navigation:
                LatLng ll = new LatLng(mLocation.getLatitude(),
                        mLocation.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 14);	//设置地图中心点以及缩放级别
//				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
                break;
        }
    }


    //初始化
    private void init() {
        mBack = (ImageView) findViewById(R.id.activity_manual_location_back);
        mSearch = (EditText) findViewById(R.id.activity_manual_location_search);
        mNavigation = (ImageView) findViewById(R.id.activity_manual_location_navigation);
        mListview = (ListView) findViewById(R.id.activity_manual_location_listview);
        mMapView = (MapView) findViewById(R.id.activity_manual_location_baiduMap);

        Location location = new LocationXY().init(getApplicationContext());
        lng = location.getLongitude();
        lat = location.getLatitude();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        int span = 3000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);

        mBaiduMap = mMapView.getMap();
        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient.start();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            mLocation = location;
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            lat = location.getLatitude();
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            lng = location.getLongitude();
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有  人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息

//            List<Poi> list = location.getPoiList();// POI数据
//            if (list != null) {
//                sb.append("\npoilist size = : ");
//                sb.append(list.size());
//                for (Poi p : list) {
//                    sb.append("\npoi= : ");
//                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
//                    Log.e("kunlun",p.getName());
//                }
//            }

//            Log.e("kunlun","地址="+location.getAddrStr());

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360dhuiya
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);	//设置定位数据

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 13);	//设置地图中心点以及缩放级别
//				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }
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

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
