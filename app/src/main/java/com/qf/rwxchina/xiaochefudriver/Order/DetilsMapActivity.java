package com.qf.rwxchina.xiaochefudriver.Order;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.trace.OnTrackListener;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.State.GsonService;
import com.qf.rwxchina.xiaochefudriver.State.HistoryTrackData;
import com.qf.rwxchina.xiaochefudriver.Utils.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by longkeyou on 2017/4/7.
 */
public class DetilsMapActivity extends AppCompatActivity {
    private BaiduMap mBaiduMap = null;
    private MapView mMapView = null;

    MyApplication trackApp;

    // 起点图标
    private static BitmapDescriptor bmStart;
    // 终点图标
    private static BitmapDescriptor bmEnd;

    // 起点图标覆盖物
    private static MarkerOptions startMarker = null;
    // 终点图标覆盖物
    private static MarkerOptions endMarker = null;
    // 路线覆盖物
    private static MarkerOptions markerOptions = null;

    private MapStatusUpdate msUpdate = null;

    /**
     * Track监听器
     */
    protected static OnTrackListener trackListener = null;
    public static PolylineOptions polyline = null;

    private int startTime = 0;
    private int endTime = 0;

    private Polyline mPolyline;
    private Marker mMoveMarker;
    private Handler mHandler;
    // 通过设置间隔时间和距离可以控制速度和图标移动的距离
    private static final int TIME_INTERVAL = 80;
    private static final double DISTANCE = 0.001;
    List<LatLng> polylines = new ArrayList<>();
    //    private BDLocation mLocation;
    boolean isopen;//是否展开订单详情
    LinearLayout open, detilll, detil_all;
    //    LinearLayout.LayoutParams params;
    ImageView detil_imgopen, detil_back;
    int height, width;
    TextView detil_money, detil_tvdistance, detil_tvtime, detil_startaddress, detil_endaddress, detil_ordertype, detil_platenumber, detil_createtime, detil_beginwait_time, detil_overwait_time, detil_endtime, detil_waittimeduan,
            detil_waitmoney, detil_distance, detil_distancemoney, detil_fanli, detil_guize;
    private String pop_detil_money, pop_detil_tvdistance, pop_detil_tvtime, pop_detil_startaddress, pop_detil_endaddress, pop_detil_ordertype,
            pop_detil_platenumber, pop_detil_createtime, pop_detil_beginwait_time, pop_detil_overwait_time, pop_detil_endtime, pop_detil_waittimeduan,
            pop_detil_waitmoney, pop_detil_distance, pop_detil_distancemoney, pop_detil_fanli, pop_detil_guize, alltime, waittime, detil_fanli02, ordertype;

    private PopupWindow window;
    int fromYDelta;
    private boolean isPopWindowShowing = false;
    private RelativeLayout mTopLine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setContentView(R.layout.activity_detilsmap);
        MyApplication.getInstance().addActivity(this);
        init();

    }

    private void init() {
        findview();
        setonclick();
        isopen = false;

        //开启鹰眼服务
        trackApp = MyApplication.getInstance();
        trackApp.createTrace(this);
        //触发鹰眼监听
        initOnTrackListener();
        //地图构造
        MapStatus.Builder builder = new MapStatus.Builder();
        //初始经纬度
        builder.target(new LatLng(30.665636, 104.063752));
        //缩放等级
        builder.zoom(19.0f);
        //设置地图构造
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        //赋值
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            String stime=bundle.getString("starttime");
            if ("".equals(stime)||stime==null){
                stime="0";
            }
            startTime = Integer.parseInt(stime);
            endTime = Integer.parseInt(bundle.getString("endtime"));

            pop_detil_money = bundle.getString("original_totalmoney");
            pop_detil_tvdistance = bundle.getString("distance") + "km";
            alltime = bundle.getString("alltime");
            pop_detil_startaddress = bundle.getString("saddress");
            pop_detil_endaddress = bundle.getString("oaddress");
            pop_detil_ordertype = bundle.getString("ordertype");
            pop_detil_platenumber = bundle.getString("platenumber");
            pop_detil_createtime = bundle.getString("createtime");
            pop_detil_beginwait_time = DateUtil.getStrTime(bundle.getString("beginwait_time"));
            pop_detil_overwait_time = DateUtil.getStrTime(bundle.getString("overwait_time"));
            pop_detil_endtime = DateUtil.getStrTime(bundle.getString("endtime"));
            pop_detil_waitmoney = bundle.getString("waitmoney") + "元";
            pop_detil_distance = bundle.getString("distance") + "(公里)";
            pop_detil_distancemoney = bundle.getString("distancemoney") + "元";
            waittime = bundle.getString("waittime");
            ordertype = bundle.getString("ordertype");
            detil_fanli02 = bundle.getString("_fanli");

            detil_money.setText(pop_detil_money);
            detil_tvdistance.setText(pop_detil_tvdistance);
            detil_startaddress.setText(pop_detil_startaddress);
            detil_endaddress.setText(pop_detil_endaddress);
            detil_tvtime.setText(alltime + "分钟");
//
//
//            detil_tvtime.setText(alltime+"分钟");
//            if (waittime.equals("")||TextUtils.isEmpty(waittime)){
//                waittime="0";
//            }
//            detil_fanli02=bundle.getString("_fanli");
//            if (detil_fanli02.equals("")||TextUtils.isEmpty(detil_fanli02)){
//                detil_fanli02="0";
//            }
//
//
//            if (alltime.equals("")|| TextUtils.isEmpty(alltime)){
//                alltime="0";
//            }
//
//            switch (bundle.getString("ordertype")){
//                case "0":
//                    detil_ordertype.setText("普通叫车");
//
//                    break;
//                case "1":
//                    detil_ordertype.setText("商户叫车");
//
//                    break;
//                case "2":
//                    detil_ordertype.setText("司机自建订单");
//
//                    break;
//            }


//            detil_money.setText(bundle.getString("original_totalmoney"));
//            detil_tvdistance.setText(bundle.getString("distance")+"km");
//            detil_createtime.setText(bundle.getString("createtime"));
//            detil_beginwait_time.setText(DateUtil.getStrTime(bundle.getString("beginwait_time")));
//            detil_overwait_time.setText(DateUtil.getStrTime(bundle.getString("overwait_time")));
//            detil_endtime.setText(DateUtil.getStrTime(bundle.getString("endtime")));
//            detil_waittimeduan.setText(waittime+"(分钟)");
//            detil_waitmoney.setText(bundle.getString("waitmoney")+"元");
//            detil_distance.setText(bundle.getString("distance")+"(公里)");
//            detil_startaddress.setText(bundle.getString("saddress"));
//            detil_endaddress.setText(bundle.getString("oaddress"));
//            detil_platenumber.setText(bundle.getString("platenumber"));
//            detil_distancemoney.setText(bundle.getString("distancemoney")+"元");
//            detil_fanli.setText(detil_fanli02+"元");


        }

        //判断地图是否加载完成
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                queryHistoryTrack(1, "need_denoise=1,need_vacuate=1,need_mapmatch=0");
            }
        });
    }

    private void findview() {
        open = (LinearLayout) findViewById(R.id.detil_openll);
        detil_back = (ImageView) findViewById(R.id.detil_back);
        detilll = (LinearLayout) findViewById(R.id.detil_detil_ll);
        detil_imgopen = (ImageView) findViewById(R.id.detil_imgopen);
        mMapView = (MapView) findViewById(R.id.detilsmap);
        mMapView.getHeight();
        Log.i("lkymsg", " mMapView.getHeight();" + mMapView.getHeight());
        mBaiduMap = mMapView.getMap();

        detil_money = (TextView) findViewById(R.id.detil_money);
        detil_tvdistance = (TextView) findViewById(R.id.detil_tvdistance);
        detil_tvtime = (TextView) findViewById(R.id.detil_tvtime);
        detil_startaddress = (TextView) findViewById(R.id.detil_startaddress);
        detil_endaddress = (TextView) findViewById(R.id.detil_endaddress);
//        detil_ordertype= (TextView) findViewById(R.id.detil_ordertype);
//        detil_platenumber= (TextView) findViewById(R.id.detil_platenumber);
//        detil_beginwait_time= (TextView) findViewById(R.id.detil_beginwait_time);
//        detil_overwait_time= (TextView) findViewById(R.id.detil_overwait_time);
//        detil_endtime= (TextView) findViewById(R.id.detil_endtime);
//        detil_createtime= (TextView) findViewById(R.id.detil_createtime);
//        detil_waittimeduan= (TextView) findViewById(R.id.detil_waittimeduan);
//        detil_waitmoney= (TextView) findViewById(R.id.detil_waitmoney);
//        detil_distance= (TextView) findViewById(R.id.detil_distance);
//        detil_distancemoney= (TextView) findViewById(R.id.detil_distancemoney);
//        detil_fanli= (TextView) findViewById(R.id.detil_fanli);
//        detil_all= (LinearLayout) findViewById(R.id.detil_all);
//        detil_guize= (TextView) findViewById(R.id.detil_guize);
    }

    private void setonclick() {
        detil_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        //查看更多
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop();
//               //隐藏
//                if (isopen) {
//                    AnimatorUtil.animHeightToView(DetilsMapActivity.this, detilll, false, 200);
//                    mMapView.setClickable(true);
//                    isopen = !isopen;
//                    detil_imgopen.setImageResource(R.drawable.detil_close);
//                }
//                //显示
//                else {
//                    AnimatorUtil.animHeightToView(DetilsMapActivity.this, detilll, true, 200);
//                    mMapView.setClickable(false);
//                    isopen = !isopen;
//                    detil_imgopen.setImageResource(R.drawable.detil_open);
//                }
            }
        });


    }


    /**
     * 初始化OnTrackListener监听
     */
    private void initOnTrackListener() {

        trackListener = new OnTrackListener() {

            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
//                trackApp.getmHandler().obtainMessage(0, "track请求失败回调接口消息 : " + arg0).sendToTarget();
                Log.i("lkymsggg", "track请求失败回调接口消息 : " + arg0);
            }

            // 查询历史轨迹回调接口
            @Override
            public void onQueryHistoryTrackCallback(final String arg0) {
                // TODO Auto-generated method stub
                super.onQueryHistoryTrackCallback(arg0);
                Log.i("lkymsggg", "onQueryHistoryTrackCallback : " + arg0);

                //绘制轨迹
                showHistoryTrack(arg0);

            }

            //查询历史里程
            @Override
            public void onQueryDistanceCallback(String arg0) {
                // TODO Auto-generated method stub
                Log.i("lkymsggg", "onQueryDistanceCallback : " + arg0);
                try {
                    JSONObject dataJson = new JSONObject(arg0);
                    if (null != dataJson && dataJson.has("status") && dataJson.getInt("status") == 0) {
                        double distance = dataJson.getDouble("distance");
                        DecimalFormat df = new DecimalFormat("#.0");
//                        trackApp.getmHandler().obtainMessage(0, "里程 : " + df.format(distance) + "米").sendToTarget();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
//                    trackApp.getmHandler().obtainMessage(0, "queryDistance回调消息 : " + arg0).sendToTarget();
                    Log.i("lkymsggg", "queryDistance回调消息 : " + arg0);
                }
            }

            @Override
            public Map<String, String> onTrackAttrCallback() {
                // TODO Auto-generated method stub
                System.out.println("onTrackAttrCallback");
                Map<String, String> trackAttrs = new HashMap<String, String>();
                trackAttrs.put("ceshi1", "111");
                trackAttrs.put("ceshi", "222");
                trackAttrs.put("text", "333");
                return trackAttrs;
//                return null;
            }

        };
    }

    /**
     * 查询历史轨迹
     */
    private void queryHistoryTrack(int processed, String processOption) {

        // entity标识
        String entityName = trackApp.getEntityName();
        Log.i("lkymsgzz", "entityName" + entityName);
        // 是否返回精简的结果（0 : 否，1 : 是）
        int simpleReturn = 0;
        // 是否返回纠偏后轨迹（0 : 否，1 : 是）
        int isProcessed = processed;
        // 开始时间
        if (startTime == 0) {
            startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        }
        if (endTime == 0) {
            endTime = (int) (System.currentTimeMillis() / 1000);
        }
        // 分页大小
        int pageSize = 1000;
        // 分页索引
        int pageIndex = 1;

        Log.i("lkymsgzz", "startTime" + startTime);
        Log.i("lkymsgzz", "endTime" + endTime);

        trackApp.getClient().queryHistoryTrack(trackApp.getServiceId(), entityName, simpleReturn,
                isProcessed, processOption,
                startTime, endTime,
                pageSize,
                pageIndex,
                trackListener);
    }

    // 查询里程
    private void queryDistance(int processed, String processOption) {

        // entity标识
        String entityName = trackApp.getEntityName();

        Log.i("lkymsgzz", "entityName" + entityName);


        // 是否返回纠偏后轨迹（0 : 否，1 : 是）
        int isProcessed = processed;

        // 里程补充
        String supplementMode = "driving";

        // 开始时间
        if (startTime == 0) {
            startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        }
        // 结束时间
        if (endTime == 0) {
            endTime = (int) (System.currentTimeMillis() / 1000);
        }


        trackApp.getClient().queryDistance(trackApp.getServiceId(), entityName, isProcessed, processOption,
                supplementMode, startTime, endTime, trackListener);
    }


    /**
     * 显示历史轨迹
     *
     * @param historyTrack
     */
    private void showHistoryTrack(String historyTrack) {

        Log.i("lkymsgzz", "showHistoryTrack");
        HistoryTrackData historyTrackData = GsonService.parseJson(historyTrack,
                HistoryTrackData.class);

        List<LatLng> latLngList = new ArrayList<LatLng>();
        if (historyTrackData != null && historyTrackData.getStatus() == 0) {

            Log.i("lkymsgzz", "historyTrackData != null && historyTrackData.getStatus() == 0");
            if (historyTrackData.getListPoints() != null) {
                Log.i("lkymsgzz", "historyTrackData.getListPoints() != null");
                latLngList.addAll(historyTrackData.getListPoints());
            }
            // 绘制历史轨迹
            polylines = latLngList;
            drawHistoryTrack(latLngList, historyTrackData.distance);

        }

    }


    /**
     * 绘制历史轨迹
     *
     * @param points
     */
    private void drawHistoryTrack(final List<LatLng> points, final double distance) {
        // 绘制新覆盖物前，清空之前的覆盖物
        Log.i("lkymsgzz", "drawHistoryTrack");

        if (points.size() == 1) {
            points.add(points.get(0));
            Log.i("lkymsgzz", "points.size() == 1");
        }

        if (points == null || points.size() == 0) {
//            trackApp.getmHandler().obtainMessage(0, "当前查询无轨迹点").sendToTarget();
            Log.i("lkymsgzz", "points == null || points.size() == 0");
            resetMarker();
            Log.i("lkymsgzz", "当前查询无轨迹点");
            Toast.makeText(DetilsMapActivity.this, "当前订单无轨迹", Toast.LENGTH_SHORT).show();
            this.finish();
            Log.i("lkymsgzz", "finish");
        } else if (points.size() > 1) {
            Log.i("lkymsgzz", "points.size() > 1");
            LatLng llC = points.get(0);
            LatLng llD = points.get(points.size() - 1);
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(llC).include(llD).build();
            msUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);
            bmStart = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
            bmEnd = BitmapDescriptorFactory.fromResource(R.drawable.icon_end);

            // 添加起点图标
            startMarker = new MarkerOptions()
                    .position(points.get(points.size() - 1)).icon(bmStart)
                    .zIndex(9).draggable(true);

            // 添加终点图标
            endMarker = new MarkerOptions().position(points.get(0))
                    .icon(bmEnd).zIndex(9).draggable(true);

            // 添加路线（轨迹）
            polyline = new PolylineOptions().width(10)
                    .color(Color.RED).points(points);

            markerOptions = new MarkerOptions();
            markerOptions.flat(true);
            markerOptions.anchor(0.5f, 0.5f);
            markerOptions.icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_gcoding));
            markerOptions.position(points.get(points.size() - 1));

            addMarker();


//            mHandler = new Handler(Looper.getMainLooper());
//            drawPolyLine();
//            moveLooper();


        }

    }

    /**
     * 添加覆盖物
     */
    protected void addMarker() {
//        if(mBaiduMap==null){
//            mBaiduMap = mMapView.getMap();
//        }

        Log.i("lkymsgzz", "addMarker");
        if (null != msUpdate) {
//            mBaiduMap.animateMapStatus(msUpdate,2000);
            mBaiduMap.animateMapStatus(msUpdate);

//            mBaiduMap.setMapStatus(msUpdate);
            Log.i("lkymsgzz", "null != msUpdate");
            Log.i("lkymsgzz", "msUpdate =" + msUpdate.toString());
        }

        if (null != startMarker) {
            mBaiduMap.addOverlay(startMarker);
            Log.i("lkymsgzz", "null != startMarker");
        }

        if (null != endMarker) {
            mBaiduMap.addOverlay(endMarker);
            Log.i("lkymsgzz", "null != endMarker");
        }

        if (null != polyline) {
            mBaiduMap.addOverlay(polyline);
            Log.i("lkymsgzz", "null != polyline");
        }


    }

    /**
     * 重置覆盖物
     */
    private void resetMarker() {
        startMarker = null;
        endMarker = null;
        polyline = null;
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mBaiduMap.clear();
        mBaiduMap = null;
//        msUpdate=null;
        mMapView.onDestroy();
        trackApp.getClient().onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }


    private void drawPolyLine() {

//        List<LatLng> polylines = new ArrayList<>();
//        for (int index = 0; index < latlngs.length; index++) {
//            polylines.add(latlngs[index]);
//        }

//        polylines.add(latlngs[0]);
        PolylineOptions polylineOptions = new PolylineOptions().points(polylines).width(10).color(Color.RED);

        mPolyline = (Polyline) mBaiduMap.addOverlay(polylineOptions);
        OverlayOptions markerOptions;
        markerOptions = new MarkerOptions().flat(true).anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)).position(polylines.get(0))
                .rotate((float) getAngle(0));
        mMoveMarker = (Marker) mBaiduMap.addOverlay(markerOptions);

    }

    /**
     * 根据点获取图标转的角度
     */
    private double getAngle(int startIndex) {
        if ((startIndex + 1) >= mPolyline.getPoints().size()) {
            throw new RuntimeException("index out of bonds");
        }
        LatLng startPoint = mPolyline.getPoints().get(startIndex);
        LatLng endPoint = mPolyline.getPoints().get(startIndex + 1);
        return getAngle(startPoint, endPoint);
    }

    /**
     * 根据两点算取图标转的角度
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }

    /**
     * 根据点和斜率算取截距
     */
    private double getInterception(double slope, LatLng point) {

        double interception = point.latitude - slope * point.longitude;
        return interception;
    }

    /**
     * 算斜率
     */
    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
        return slope;

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);

    }


    /**
     * 计算x方向每次移动的距离
     */
    private double getXMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

    /**
     * 循环进行移动逻辑
     */
    public void moveLooper() {
        new Thread() {

            public void run() {

                while (true) {

                    for (int i = 1; i < polylines.size(); i++) {


                        final LatLng startPoint = polylines.get(polylines.size() - i);
                        final LatLng endPoint = polylines.get(polylines.size() - i - 1);
                        mMoveMarker
                                .setPosition(startPoint);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // refresh marker's rotate
                                if (mMapView == null) {
                                    return;
                                }
                                mMoveMarker.setRotate((float) getAngle(startPoint,
                                        endPoint));
                            }
                        });
                        double slope = getSlope(startPoint, endPoint);
                        // 是不是正向的标示
                        boolean isReverse = (startPoint.latitude > endPoint.latitude);

                        double intercept = getInterception(slope, startPoint);

                        double xMoveDistance = isReverse ? getXMoveDistance(slope) :
                                -1 * getXMoveDistance(slope);


                        for (double j = startPoint.latitude; !((j > endPoint.latitude) ^ isReverse);
                             j = j - xMoveDistance) {
                            LatLng latLng = null;
                            if (slope == Double.MAX_VALUE) {
                                latLng = new LatLng(j, startPoint.longitude);
                            } else {
                                latLng = new LatLng(j, (j - intercept) / slope);
                            }

                            final LatLng finalLatLng = latLng;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mMapView == null) {
                                        return;
                                    }
                                    mMoveMarker.setPosition(finalLatLng);
                                }
                            });
                            try {
                                Thread.sleep(TIME_INTERVAL);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            }

        }.start();
    }


    private void showPop() {
        View popupView = DetilsMapActivity.this.getLayoutInflater().inflate(R.layout.pop_detilmap, null);
        mTopLine = (RelativeLayout) findViewById(R.id.detil_top);
        TextView detil_money1 = (TextView) popupView.findViewById(R.id.detil_money);
        TextView detil_tvdistance1 = (TextView) popupView.findViewById(R.id.detil_tvdistance);
        TextView detil_tvtime1 = (TextView) popupView.findViewById(R.id.detil_tvtime);
        TextView detil_startaddress1 = (TextView) popupView.findViewById(R.id.detil_startaddress);
        TextView detil_endaddress1 = (TextView) popupView.findViewById(R.id.detil_endaddress);
        detil_ordertype = (TextView) popupView.findViewById(R.id.detil_ordertype);
        detil_platenumber = (TextView) popupView.findViewById(R.id.detil_platenumber);
        detil_beginwait_time = (TextView) popupView.findViewById(R.id.detil_beginwait_time);
        detil_overwait_time = (TextView) popupView.findViewById(R.id.detil_overwait_time);
        detil_endtime = (TextView) popupView.findViewById(R.id.detil_endtime);
        detil_createtime = (TextView) popupView.findViewById(R.id.detil_createtime);
        detil_waittimeduan = (TextView) popupView.findViewById(R.id.detil_waittimeduan);
        detil_waitmoney = (TextView) popupView.findViewById(R.id.detil_waitmoney);
        detil_distance = (TextView) popupView.findViewById(R.id.detil_distance);
        detil_distancemoney = (TextView) popupView.findViewById(R.id.detil_distancemoney);
        detil_fanli = (TextView) popupView.findViewById(R.id.detil_fanli);
        detil_all = (LinearLayout) popupView.findViewById(R.id.detil_all);
        detil_guize = (TextView) popupView.findViewById(R.id.detil_guize);
        //跳转到收费规则
        detil_guize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetilsMapActivity.this, PriceListActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout open1 = (LinearLayout) popupView.findViewById(R.id.detil_openll2);

//        open1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPop();
//            }
//        });
        if (open1 != null) {
            open1.setOnClickListener(onClickListener);
        }


        detil_money1.setText(pop_detil_money);
        detil_tvdistance1.setText(pop_detil_tvdistance);
        detil_startaddress1.setText(pop_detil_startaddress);
        detil_endaddress1.setText(pop_detil_endaddress);
        detil_tvtime1.setText(alltime + "分钟");
        detil_createtime.setText(pop_detil_createtime);
        detil_beginwait_time.setText(pop_detil_beginwait_time);
        detil_overwait_time.setText(pop_detil_overwait_time);
        detil_endtime.setText(pop_detil_endtime);
        int time02=Integer.parseInt(waittime)/60;

        detil_waittimeduan.setText(time02 + "(分钟)");
        detil_waitmoney.setText(pop_detil_waitmoney);
        detil_distance.setText(pop_detil_distance);
        detil_platenumber.setText(pop_detil_platenumber);
        detil_distancemoney.setText(pop_detil_distancemoney);
        if (waittime.equals("") || TextUtils.isEmpty(waittime)) {
            waittime = "0";
        }

        if (detil_fanli02.equals("") || TextUtils.isEmpty(detil_fanli02)) {
            detil_fanli02 = "0";
        }


        if (alltime.equals("") || TextUtils.isEmpty(alltime)) {
            alltime = "0";
        }

        switch (pop_detil_ordertype) {
            case "0":
                detil_ordertype.setText("普通叫车");

                break;
            case "1":
                detil_ordertype.setText("商户叫车");

                break;
            case "2":
                detil_ordertype.setText("司机自建订单");

                break;
        }

//        detil_fanli.setText(detil_fanli02+"元");

//        pop_detil_money=bundle.getString("original_totalmoney");
//        pop_detil_tvdistance=bundle.getString("distance")+"km";
//        alltime=bundle.getString("alltime");
//        pop_detil_startaddress=bundle.getString("saddress");
//        pop_detil_endaddress=bundle.getString("oaddress");
//        pop_detil_ordertype=bundle.getString("ordertype");
//        pop_detil_platenumber=bundle.getString("platenumber");
//        pop_detil_createtime=bundle.getString("createtime");
//        pop_detil_beginwait_time=DateUtil.getStrTime(bundle.getString("beginwait_time"));
//        pop_detil_overwait_time=DateUtil.getStrTime(bundle.getString("overwait_time"));
//        pop_detil_endtime=DateUtil.getStrTime(bundle.getString("endtime"));
//        pop_detil_waitmoney=bundle.getString("waitmoney")+"元";
//        pop_detil_distance=bundle.getString("distance")+"(公里)";
//        pop_detil_distancemoney=bundle.getString("distancemoney")+"元";
//        waittime=bundle.getString("waittime");
//        ordertype=bundle.getString("ordertype");


        window = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        window.setBackgroundDrawable(new BitmapDrawable());
        //将这两个属性设置为false，使点击popupwindow外面其他地方会消失
        window.setOutsideTouchable(true);
        window.setFocusable(true);
        //获取popupwindow高度确定动画开始位置
//        int contentHeight = ViewUtils.getViewMeasuredHeight(popupView);
        window.showAsDropDown(mTopLine, 0, 0);

//        fromYDelta = contentHeight+50;
//        window.getContentView().startAnimation(AnimationUtil.createInAnimation(this, fromYDelta));

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isPopWindowShowing = false;
            }
        });
        isPopWindowShowing = true;
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isPopWindowShowing) {
                showPop();
            } else {
                window.dismiss();
            }
        }
    };

}
