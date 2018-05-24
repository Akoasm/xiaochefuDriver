package com.qf.rwxchina.xiaochefudriver.Return;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.qf.rwxchina.xiaochefudriver.Bean.DriverInfo;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.CircleImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 结伴返程
 */
public class ComeBackActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView mBack;
    private ImageView mNacigation;
    private LinearLayout mAdd;
    private LinearLayout mMude;
    private TextView mAddress;
    private TextView mMudedi;

    private BaiduMap mBaiduMap = null;
    private MapView mMapView = null;
    private LocationClient mLocationClient = null;
    private BDLocationListener mLocationListener = new MyLocationListener();
    boolean isFirstLoc = true;// 是否首次定位

    private ArrayList<DriverInfo> driver = new ArrayList<>();

    String address="",detailedAddresst="";
    String mudi_address="";
    //目的地的经纬度
    private double oLng;
    private double oLat;
    //起始地经纬度
    private double lat;
    private double lng;

    private BDLocation mLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_come_back);
        MyApplication.getInstance().addActivity(this);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mLocationListener);
        init();
        initLocation();

       getDriver();


    }

    /**
     * 获取附近司机
     */
    private void getDriver() {
        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(this)
                .doPostAsync(
                        HttpInfo.Builder()
                                .setUrl(HttpPath.GETDRIVER)
                                .addParam("lat",lat+"")
                                .addParam("lng",lng+"")
                                .build(),
                        new CallbackOk() {
                            @Override
                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()) {
                                    //获取到数据
                                    String result = info.getRetDetail();
                                    Log.e("wh","附近司机》"+result);
                                    if (result != null) {
                                        try {
                                            JSONObject obj1 = new JSONObject(result);
                                            JSONArray arr = new JSONArray(obj1.optString("data"));
                                            for (int i = 0;i<arr.length();i++){
                                                JSONObject obj2 = arr.getJSONObject(i);
                                                DriverInfo dinfo = new DriverInfo();
                                                dinfo.setDriverid(obj2.optInt("driverid"));
                                                dinfo.setHead_image(obj2.optString("head_image"));
                                                dinfo.setName(obj2.optString("name"));
                                                dinfo.setAvglevel(obj2.optInt("avglevel"));
                                                dinfo.setDriving_years(obj2.optInt("driving_years"));
                                                dinfo.setWork_status(obj2.optInt("work_status"));
                                                dinfo.setAgentsum(obj2.optInt("agentsum"));
                                                dinfo.setLng(obj2.optDouble("lng"));
                                                dinfo.setLat(obj2.optDouble("lat"));
                                                dinfo.setDistance(obj2.optString("distance"));

                                                driver.add(dinfo);
                                            }
                                            if (driver.size() != 0){
                                                handler.sendEmptyMessage(100);
                                            }else {
                                                Toast.makeText(getApplicationContext(), "附近没有司机", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 100:
                    for (int i = 0;i<driver.size();i++){
                        test(driver.get(i).getLat(),driver.get(i).getLng(),i);
                    }

                    mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Intent intent = new Intent(getApplicationContext(), ReturnDialogActivity1.class);

                            int index = Integer.parseInt(marker.getTitle());

                            intent.putExtra("driverId",driver.get(index).getDriverid());
                            intent.putExtra("img",driver.get(index).getHead_image());
                            intent.putExtra("name",driver.get(index).getName());
                            intent.putExtra("rating",driver.get(index).getAvglevel());
                            intent.putExtra("year",driver.get(index).getDriving_years());
                            intent.putExtra("agentsum",driver.get(index).getAgentsum());
                            intent.putExtra("address",address);
                            intent.putExtra("mudi_address",mudi_address);

                            startActivity(intent);

                            return false;
                        }
                    });
                    break;
            }
        }
    };

    private void test(Double dLat,Double dLng,int index) {
        View myView = LayoutInflater.from(this).inflate(R.layout.layout_out_driver, null);
        CircleImageView mImg = (CircleImageView) myView.findViewById(R.id.layout_out_driver_img);

            if(driver.get(index).getHead_image().equals(""))
            {
                mImg.setImageResource(R.drawable.icon_account);

                Log.e("wh","11111111");
            }else{
                Picasso.with(getApplicationContext()).load(driver.get(index).getHead_image()).into(mImg);

                Log.e("wh","22222222");
            }



        TextView mName = (TextView) myView.findViewById(R.id.layout_out_driver_name);
        mName.setText(driver.get(index).getName());
        RatingBar mRatingBar = (RatingBar) myView.findViewById(R.id.layout_out_driver_ratingbar);
        mRatingBar.setRating(driver.get(index).getAvglevel());

        myView.setBackgroundResource(R.drawable.icon_driver_background);
        BitmapDescriptor bd = BitmapDescriptorFactory.fromView(myView);
        LatLng pt = new LatLng(dLat, dLng);
        OverlayOptions options = new MarkerOptions().position(pt).icon(bd).title(index+"");
        mBaiduMap.addOverlay(options);
    }

    private void init() {
        mMapView = (MapView) findViewById(R.id.activity_come_back_mapView);
        mBack = (ImageView) findViewById(R.id.activity_come_back_back);
        mNacigation = (ImageView) findViewById(R.id.activity_come_back_nacigation);
        mAdd = (LinearLayout) findViewById(R.id.activity_come_back_add);
        mMude = (LinearLayout) findViewById(R.id.activity_come_back_mude);
        mAddress = (TextView) findViewById(R.id.activity_come_back_address);
        mMudedi = (TextView) findViewById(R.id.activity_come_back_tv_mude);
        mMude.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mAdd.setOnClickListener(this);
        mNacigation.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_come_back_back:
                finish();
                break;
            //当前位置
            case R.id.activity_come_back_add:
                Intent   inte=new Intent(ComeBackActivity.this,DestinationActivity.class);
                startActivityForResult(inte,1);
                break;
            //目的地
            case R.id.activity_come_back_mude:
                Intent   intent=new Intent(ComeBackActivity.this,DestinationActivity.class);
                startActivityForResult(intent,0);
                break;

            case R.id.activity_come_back_nacigation:
                navigation();
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0 & resultCode == 1)
        { Bundle bundle = data.getExtras();
            mudi_address= bundle.getString("address");
            detailedAddresst=bundle.getString("detailedAddress");
            oLng=bundle.getDouble("lng");
            oLat=bundle.getDouble("lat");
            mMudedi.setText(mudi_address);



        }else  if (requestCode == 1 & resultCode == 1)
        { Bundle bundle = data.getExtras();
            address= bundle.getString("address");
            detailedAddresst=bundle.getString("detailedAddress");
            lng=bundle.getDouble("lng");
            lat=bundle.getDouble("lat");
            mAddress.setText(address);
            getDriver();


        }
    }

    //获取定位
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


    public class MyLocationListener implements BDLocationListener{

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
            lat = location.getLatitude();
            lng = location.getLongitude();
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
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
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息

            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());

                }
            }

            lat=location.getLatitude();
            lng=location.getLongitude();








            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360dhuiya
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);	//设置定位数据




            if (location.getLocType()==61||location.getLocType()==161)
            {
                mLocationClient.stop();
            }

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,16);	//设置地图中心点以及缩放级别
//				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);

                address=location.getAddrStr();
                mAddress.setText(location.getAddrStr());
            }
        }
    }


    /**
     * 定位到自己的位置
     */
    private void navigation() {
        LatLng ll = new LatLng(mLocation.getLatitude(),
                mLocation.getLongitude());
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);    //设置地图中心点以及缩放级别
//				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mLocationClient.stop();
    }

}
