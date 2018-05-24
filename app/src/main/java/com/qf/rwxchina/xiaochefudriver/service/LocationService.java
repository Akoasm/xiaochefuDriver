package com.qf.rwxchina.xiaochefudriver.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.orhanobut.logger.Logger;
import com.qf.rwxchina.xiaochefudriver.Bean.DistanceInfo;
import com.qf.rwxchina.xiaochefudriver.Bean.GpsLocation;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.db.DistanceInfoDao;

import java.util.ArrayList;
import java.util.List;

/**
 * 司机计算行驶距离服务
 */
public class LocationService extends Service {
    private LocationClient mLocationClient = null;
    private BDLocationListener mLocationListener = new MyLocationListener();
    private DistanceInfoDao mDistanceInfoDao;
    private volatile int discard = 1;
    private Object lock = new Object();
    private volatile GpsLocation currentGpsLocation = new GpsLocation();
    private volatile GpsLocation prevGpsLocation = new GpsLocation();       //定位数据
    double x = 30.667522;
    double y = 104.078115;
    private boolean isFirst = true;
    public static BDLocation bd;
    public static int num = 0;
    BDLocation bdLocation,currbdLocation;
    int time = 1;
    private boolean isFirst02 = true;
    List<DistanceInfo> list = new ArrayList<DistanceInfo>();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDistanceInfoDao = new DistanceInfoDao(getApplicationContext());
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mLocationListener);
        initLocation();
        mLocationClient.start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        Intent localIntent = new Intent();
        localIntent.setClass(this, LocationService.class); // 销毁时重新启动Service
        this.startService(localIntent);
        startService(new Intent(this,LocationService.class));
    }

    /**
     * 开始计算距离
     *
     * @param location
     */
    private String startCompute(BDLocation location) {
        synchronized (lock) {
            if (checkProperLocation(location) == true) {
            } else {
                return null;
            }

            if (MyApplication.orderDealInfoId != -1) {
                DistanceInfo mDistanceInfo = mDistanceInfoDao.getById(MyApplication.orderDealInfoId);
                mDistanceInfo.setId(MyApplication.orderDealInfoId);
                if (mDistanceInfo != null) {
                    currbdLocation=location;
                    /** 计算距离  */
                    float distance = 0.0f;
                    LatLng latLng1 = new LatLng(mDistanceInfo.getLatitude(), mDistanceInfo.getLongitude());
                    LatLng latLng2 = new LatLng(currbdLocation.getLatitude(),currbdLocation.getLongitude());
                    distance = (float) (DistanceUtil.getDistance(latLng1, latLng2) / 1000);
                    Logger.t("LocationService").e("location=" + location.getLocType()+"精度"+location.getLongitude() +
                            "维度："+location.getLatitude()+
                            "数据库精度" + mDistanceInfo.getLatitude() +
                            "数据库纬度" +  mDistanceInfo.getLongitude()+
                            "distance=" + distance*1000+"米");

                    if (currbdLocation.getLocType()==61){//gps定位，路口好，信号好几本都是gps定位
                        if (!noMove(distance)) {//是否在移动
                            if (checkProperMove(distance)) {//合理移动
                                float drivedDistance = mDistanceInfo.getDistance();
                                mDistanceInfo.setDistance(distance + drivedDistance);
                                mDistanceInfo.setLongitude(currbdLocation.getLongitude());
                                mDistanceInfo.setLatitude(currbdLocation.getLatitude());
                                mDistanceInfoDao.updateDistance(mDistanceInfo);
                                discard = 1;
                                time = 1;
                            }
                        }
                    }else if (currbdLocation.getLocType()==161){//网络定位，在隧道，或者桥下几本都是网络定位，但是有的定位不准备吗，所以要舍大舍小
//distance > 0.002
                        if (distance < 0.035 && distance > 0.002){//舍大舍下
                            float drivedDistance = mDistanceInfo.getDistance();
                            mDistanceInfo.setDistance(distance + drivedDistance);
                            mDistanceInfo.setLongitude(currbdLocation.getLongitude());
                            mDistanceInfo.setLatitude(currbdLocation.getLatitude());
                            mDistanceInfoDao.updateDistance(mDistanceInfo);
                        }else {
                            return null;
                        }
                    }
                    list = mDistanceInfoDao.quereall();
                    Logger.t("LocationService").e("list="+list.toString());

                }

            }

        }
        return null;
    }

    /**
     * 检测是否在原地不动
     *
     * @param distance
     * @returnl
     */
    private boolean noMove(float distance) {
        if (distance <0.0005) {
            return true;
        } else {

            return false;
        }
    }

    /**
     * 检测是否在正确的移动
     *
     * @param distance
     * @return
     */
    private boolean checkProperMove(float distance) {
        //原始的
        if (distance >0.0006 && distance < 15) {
//            Log.i("limming","是正确移动distance"+distance*1000+"米");
            return true;
        } else {
//            Log.i("limming02","非正确移动distance"+
// distance*1000+"米");

            return false;
        }
    }

    /**
     * 检测获取的数据是否是正常的
     *
     * @param location
     * @return
     */
    private boolean checkProperLocation(BDLocation location) {
        if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {
            return true;
        }
        return false;
    }

    //获取定位
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
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
        option.setPriority(LocationClientOption.GpsFirst);//gps
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
//            Log.i("limming", "定位1：X=" + location.getLatitude() + "  y=" + location.getLongitude()+"定位类型："+location.getLocType());
//            startCompute(location);
            bdLocation = location;
            startCompute(bdLocation);
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            MyApplication.lat = location.getLatitude();
            MyApplication.lng = location.getLongitude();
            sb.append("\nlontitude : ");
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

//            MyLocationData locData = new MyLocationData.Builder()
//                    .accuracy(location.getRadius())
//                    // 此处设置开发者获取到的方向信息，顺时针0-360dhuiya
//                    .direction(100).latitude(location.getLatitude())
//                    .longitude(location.getLongitude()).build();

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, START_STICKY, startId);
    }
}
