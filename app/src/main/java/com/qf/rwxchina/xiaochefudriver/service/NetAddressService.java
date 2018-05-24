package com.qf.rwxchina.xiaochefudriver.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;

import android.os.Message;

import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.qf.rwxchina.xiaochefudriver.Bean.GpsLocation;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkHttpUtil;
import com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkStringCallBack;
import com.qf.rwxchina.xiaochefudriver.db.DistanceInfoDao;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.service.AlarmReceiver;
import okhttp3.Call;
import okmanager.OkManagerCallback;

/**
 * 服务，每隔3s上传一次司机位置
 */
public class NetAddressService extends Service {
    private double lat;
    private double lng;
    private String driverId;
    private Location location;
    private SharedPreferences sp;
    private LocationClient mLocationClient = null;
    private BDLocationListener mLocationListener = new MyLocationListener();
    private DistanceInfoDao mDistanceInfoDao;
    private volatile int discard = 1;
    private Object lock = new Object();
    private volatile GpsLocation currentGpsLocation = new GpsLocation();
    private volatile GpsLocation prevGpsLocation = new GpsLocation();       //定位数据
    private static int workstate = -1;//司机状态 0下班,1空闲，2服务中

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mDistanceInfoDao = new DistanceInfoDao(getApplicationContext());
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mLocationListener);
        initLocation();
        Thread thread = new Thread(runnable);
        thread.start();
        Log.e("kunlun", "service start!");
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            msg.obj = "100";
            driverId = sp.getString("uid", "");
            workstate = sp.getInt("work_status", 0);
            if (workstate == 2 || workstate == 1) {
//                Log.i("lkymsg", "1111111111");
//                NetLocation();
                checkNet();
            }
            handler.sendMessage(msg);
        }
    };

    // 更新界面

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.obj.equals("100")) {
                handler.postDelayed(runnable, 5000);
            }

        }
    };

    public void checkNet(){
        if(isNetworkConnected(this)){

            NetLocation();
        }else{


        }
    }
    //检查网络状态
    public boolean isNetworkConnected(Context context){
        if(context!=null){
            ConnectivityManager mConnectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo=mConnectivityManager.getActiveNetworkInfo();
            if(mNetworkInfo!=null){
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //上传司机位置
    private void NetLocation() {
        Log.i("hrr", "上传司机位置示例=" + HttpPath.UPDATEGPS + "?driverID=" + driverId + "&lng=" + lng + "&lat=" + lat + "&address=" + MyApplication.locationAddress);
        Map<String,String> params=new HashMap<String,String>();
        params.put("driverID", driverId);
        params.put("lng", lng + "");
        params.put("lat", lat + "");
        params.put("address", MyApplication.locationAddress + "");

        OkHttpUtil
                .post(HttpPath.UPDATEGPS, params, this, new OkStringCallBack() {
                    @Override
                    public void myError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void myResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject state = jsonObject.getJSONObject("state");
                            int code = state.getInt("code");
                            if (code == 0) {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("ssss", e.toString());
                        }
                    }
                });
//        OkHttpUtils.get().url(HttpPath.UPDATEGPS)
//                .addParams("driverID", driverId)
//                .addParams("lng", lng + "")
//                .addParams("lat", lat + "")
//                .addParams("address", MyApplication.locationAddress + "")
//                .build().execute(new OkManagerCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                super.onError(call, e, id);
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                super.onResponse(response, id);
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//
//                    JSONObject state = jsonObject.getJSONObject("state");
//                    int code = state.getInt("code");
//                    if (code == 0) {
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.i("ssss", e.toString());
//                }
//
//            }
//        });
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
        mLocationClient.start();
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
//            Log.i("zk02","经纬度："+location.getLongitude()+"    "+location.getLatitude());
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    for (int i = 0;i<Integer.MAX_VALUE;i++){
//                        lat = location.getLatitude();
//                        lng = location.getLongitude();
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();

            lat = location.getLatitude();
            lng = location.getLongitude();
            //  Log.e("address", "uid=" + driverId + "\nlng=" + lng + "\nlat=" + lat);

            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            MyApplication.lat = location.getLatitude();
            sb.append("\nlontitude : ");
            MyApplication.lng = location.getLongitude();
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
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

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
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

//            Log.e("kunlun","lat="+location.getLatitude()+"\nlng="+location.getLongitude());

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360dhuiya
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("kunlun", "service destroy!");
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.cancel(pi);
//        Intent localIntent = new Intent();
//        localIntent.setClass(this, NetAddressService.class); // 销毁时重新启动Service
//        this.startService(localIntent);
    }


    public boolean HttpTest(Context context) {
        boolean http = true;
        if (context != null) {
            ConnectivityManager con = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = con.getActiveNetworkInfo();
            if (networkinfo == null || !networkinfo.isAvailable()) {
                // 无网络
                Log.i("hrr", "无网络");
                http = false;
            }
            boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting();
            if (!wifi) {
                // WIFI 不可用
                Log.i("hrr", "WIFI 不可用");
//                isWifiAvailable(getApplicationContext());
                http = false;
            } else {
//                WifiManager manager= (WifiManager) getSystemService(Context.WIFI_SERVICE);
//                WifiManager.WifiLock wifiLock=manager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF,"com.qf.rwxchina.xiaochefudriver");
//                wifiLock.acquire();
            }
        }
        return http;
    }

    /**
     * 判断wifi连接状态
     *
     * @param ctx
     * @return
     */
    public boolean isWifiAvailable(Context ctx) {
        ConnectivityManager conMan = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        if (NetworkInfo.State.CONNECTED == wifi) {
            Log.i("hrr", "wifi连接状态");
            return true;
        } else {
            Log.i("hrr", "wifi未连接状态");
            return false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Notification notification = new Notification(R.drawable.icon_about,
//                getString(R.string.app_name), System.currentTimeMillis());
//
//        PendingIntent pendingintent = PendingIntent.getActivity(this, 0,
//                new Intent(this, AppMain.class), 0);
//        notification.setLatestEventInfo(this, "uploadservice", "请保持程序在后台运行",
//                pendingintent);
        return super.onStartCommand(intent, START_STICKY, startId);
    }
}
