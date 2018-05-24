package com.qf.rwxchina.xiaochefudriver.State;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.TraceLocation;
import com.bumptech.glide.Glide;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.orhanobut.logger.Logger;
import com.qf.rwxchina.xiaochefudriver.Bean.DistanceInfo;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.Bean.UserInfo;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.Order.SettlementOrderActivity;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.CallBackhelp;
import com.qf.rwxchina.xiaochefudriver.Utils.LocationXY;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;
import com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkCallBack;
import com.qf.rwxchina.xiaochefudriver.Utils.PayOKInterface;
import com.qf.rwxchina.xiaochefudriver.Utils.Utils;
import com.qf.rwxchina.xiaochefudriver.Utils.logutils.LogUtil;
import com.qf.rwxchina.xiaochefudriver.db.DistanceInfoDao;
import com.qf.rwxchina.xiaochefudriver.service.LocationService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/8.
 * 状态
 */
public class StateActivity extends AppCompatActivity implements View.OnClickListener, PayOKInterface {
    int num = 1;
    TextView fragment_state_time;//计时器
    TextView fragment_state_cost;//当前费用
    TextView fragment_state_waiting;//等待中
    TextView fragment_state_driving;//代驾中
    TextView fragment_state_over;//行程结束
    ImageView layout_state_point1, layout_state_point2;
    TextView fragment_state_daohang;//导航
    TextView fragment_state_track;
    String tvdistance;
    Button Begingo;
    String maxnumber, money, addmoney;
    int n = 0;
    private SharedPreferences sp;
    String driverid,phones;
    int time = 0;

    Double olng, olat, slat, slng;

    private DistanceInfoDao mDistanceInfoDao;
    private boolean isStartDirver = false;
    private volatile boolean isRefreshUI = true;
    private Intent intent;
    private String orderson;
    String userurl, name, balance, startmoney, distancemoney;
    Button call_phone;

    Float i = 0f;
    private ProgressDialog dia;

    private View contentView;
    private Button baidu, gaode, quxiao;
    private PopupWindow popupWindow;
    private RelativeLayout mTitle;

    public static double x_pi = 3.14159265358979324 * 3000 / 180;
    String lucheng = "0.0";
    Thread thread_time;
    Float chao = 0f;
    Float start_lucheng = 0f;//行驶距离的初始值
    MediaPlayer mediaPlayer;
    private Intent intent2;
    private String result;
    private HashMap<String, String> map;
    private HashMap<String, String> list;
//    private MyBroadCastRecevier myBroadCastRecevier;

    private MyApplication trackApp = null;

    int id = -1;
    String uid, status;
    /**
     * 开启轨迹服务监听器
     */
    protected static OnStartTraceListener startTraceListener = null;

    /**
     * 停止轨迹服务监听器
     */
    protected static OnStopTraceListener stopTraceListener = null;

    /**
     * Entity监听器
     */
    private static OnEntityListener entityListener = null;


    /**
     * Track监听器
     */
    protected static OnTrackListener trackListener = null;


    /**
     * 采集周期（单位 : 秒）
     */
    private int gatherInterval = 5;

    /**
     * 打包周期（单位 : 秒）
     */
    private int packInterval = 10;

    /**
     * 图标
     */
    private static BitmapDescriptor realtimeBitmap;

    private static Overlay overlay = null;

    // 覆盖物
    protected static OverlayOptions overlayOptions;

    // 路线覆盖物
    private static PolylineOptions polyline = null;

    private static List<LatLng> pointList = new ArrayList<LatLng>();

    private Intent serviceIntent = null;

    /**
     * 刷新地图线程(获取实时点)
     */
    protected RefreshThread refreshThread = null;

    protected static MapStatusUpdate msUpdate = null;

    private View view = null;

    private LayoutInflater mInflater = null;
    protected static boolean isInUploadFragment = true;

    private static boolean isRegister = false;

    protected static PowerManager pm = null;

    protected static PowerManager.WakeLock wakeLock = null;
    private TrackReceiver trackReceiver = new TrackReceiver();

    private TrackUploadHandler mHandler = null;

    private boolean isTraceStarted = false;
    private int startTime = 0;
    private int endTime = 0;
 private    Thread thread1;
    private IntentFilter filter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.i("lkymsggg", "onCreate");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.stateactivity);
        MyApplication.getInstance().addActivity(this);
//        trackApp = (MyApplication) getApplicationContext();
        //开启鹰眼服务
        trackApp = MyApplication.getInstance();
        trackApp.createTrace(this);
        n = MyApplication.n;

        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
//        Log.i("lkymsggg","查看n在Hi"+sp.getString("nnn",""));
//        if (sp.getString("nnn","").equals("2")){
//
//        }
        //司机ID
        driverid = sp.getString("uid", "");
        //用户电话号码
        phones = sp.getString("phones", "");
//        getDriverNotfinishedIndent();

        orderson = MyApplication.orderson;//订单编号
        trackApp.setEntityName(driverid);
//        Log.i("lkymsg", "MyApplication.overwait_time" + MyApplication.overwait_time);
        //鹰眼轨迹查询里程开始时间
        if (MyApplication.overwait_time != 0) {
            startTime = MyApplication.overwait_time;
        }

        mDistanceInfoDao = new DistanceInfoDao(getApplicationContext());
        DistanceInfo mDistanceInfo = new DistanceInfo();
        List<DistanceInfo> list = new ArrayList<>();
        list = mDistanceInfoDao.quereall();
        if (list.size() != 0) {
            Log.i("limmingshuju", "数据库数据距离：" + list.get(0).getDistance()
                    + "精度" + list.get(0).getLongitude() + "维度：" + list.get(0).getLatitude() + "长度" + list.size());
            mDistanceInfo.setDistance(list.get(list.size() - 1).getDistance());
            mDistanceInfo.setLatitude(list.get(list.size() - 1).getLatitude());
            mDistanceInfo.setLongitude(list.get(list.size() - 1).getLongitude());
            id = mDistanceInfoDao.getMaxId();
//            if (id != -1) {
//                MyApplication.orderDealInfoId = id;
//                Log.e("wh", "开始计算距离...");
//          thread1=      new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        myHandler.sendEmptyMessage(100);
//                        Log.i("limmingxc","seng00");
//                        try {
//                            thread1.sleep(3000);
//                            Log.i("limmingxc","sleep00");
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                thread1.start();
//            }
        }

        init();

        // 初始化监听器
        initListener();

        // 设置采集周期
        setInterval();

        // 设置http请求协议类型
        setRequestType();

        mHandler = new TrackUploadHandler(this);
        CallBackhelp.setinterface(this);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("changemoney");
//        intentFilter.addAction("changetime");
//        myBroadCastRecevier = new MyBroadCastRecevier();
//        registerReceiver(myBroadCastRecevier, intentFilter);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

    static class TrackUploadHandler extends Handler {
        WeakReference<StateActivity> state;

        TrackUploadHandler(StateActivity trackUploadFragment) {
            state = new WeakReference<StateActivity>(trackUploadFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            StateActivity sa = state.get();
            Toast.makeText(sa.trackApp, (String) msg.obj, Toast.LENGTH_LONG).show();

            switch (msg.what) {
                case 0:
                case 10006:
                case 10008:
                case 10009:
                    sa.isTraceStarted = true;
                    Log.i("lkymsgyy", " sa.isTraceStarted = true;");
//                    tu.btnStartTrace.setBackgroundColor(Color.rgb(0x99, 0xcc, 0xff));
//                    tu.btnStartTrace.setTextColor(Color.rgb(0x00, 0x00, 0xd8));
                    break;

                case 1:
                case 10004:
                    sa.isTraceStarted = false;
                    Log.i("lkymsgyy", "   sa.isTraceStarted = false;");
//                    tu.btnStartTrace.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
//                    tu.btnStartTrace.setTextColor(Color.rgb(0x00, 0x00, 0x00));
                    break;

                default:
                    break;
            }
        }
    }


    //鹰眼实时上传记录
    protected class RefreshThread extends Thread {

        protected boolean refresh = true;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Looper.prepare();
            while (refresh) {
                // 轨迹服务开启成功后，调用queryEntityList()查询最新轨迹；
                // 未开启轨迹服务时，调用queryRealtimeLoc()进行实时定位。
                Log.i("lkymsgyy", "refresh" + refresh);
                if (isTraceStarted) {
                    queryEntityList();
                } else {
                    queryRealtimeLoc();
                }

                try {
                    Thread.sleep(gatherInterval * 1000);//5秒上传一次距离
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    System.out.println("线程休眠失败");
                }
            }
            Looper.loop();
        }
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        // 初始化开启轨迹服务监听器
        if (null == startTraceListener) {
            initOnStartTraceListener();
        }

        // 初始化停止轨迹服务监听器
        if (null == stopTraceListener) {
            initOnStopTraceListener();
        }

        // 初始化entity监听器
        if (null == entityListener) {
            initOnEntityListener();
        }
        // 初始化Track监听器
        if (null == trackListener) {
            initOnTrackListener();
        }
    }

    public void startMonitorService() {
        startRefreshThread(true);
        serviceIntent = new Intent(trackApp,
                MonitorService.class);
        trackApp.startService(serviceIntent);
    }


    /**
     * 开启轨迹服务
     */
    private void startTrace() {
//         通过轨迹服务客户端client开启轨迹服务

        Log.i("lkymsgzz", "startTrace entityName" + trackApp.getEntityName());
        //添加一条实时监控
        trackApp.getClient().addEntity(trackApp.getServiceId(), trackApp.getEntityName(), "driverID=111,orderNumber=222", entityListener);
        //开启鹰眼服务
        trackApp.getClient().startTrace(trackApp.getTrace(), startTraceListener);
        //启动鹰眼
        if (!MonitorService.isRunning) {
//             开启监听service
            MonitorService.isCheck = true;
            MonitorService.isRunning = true;
            startMonitorService();
        }
    }

    /**
     * 停止轨迹服务
     */
    private void stopTrace() {

        // 停止监听service
        MonitorService.isCheck = false;
        MonitorService.isRunning = false;

        // 通过轨迹服务客户端client停止轨迹服务
        trackApp.getClient().stopTrace(trackApp.getTrace(), stopTraceListener);

        if (null != serviceIntent) {
            trackApp.stopService(serviceIntent);
        }
    }

    /**
     * 设置采集周期和打包周期
     */
    private void setInterval() {
        trackApp.getClient().setInterval(gatherInterval, packInterval);
    }

    /**
     * 设置请求协议
     */
    protected void setRequestType() {
        int type = 0;
        trackApp.getClient().setProtocolType(type);
    }

    /**
     * 查询实时轨迹
     */
    private void queryRealtimeLoc() {

        Log.i("lkymsgyy", "queryRealtimeLoc");

        trackApp.getClient().queryRealtimeLoc(trackApp.getServiceId(), entityListener);
    }

    /**
     * 查询entityList
     */
    private void queryEntityList() {

        Log.i("lkymsgyy", "queryEntityList");
        // entity标识列表（多个entityName，以英文逗号"," 分割）
        String entityNames = trackApp.getEntityName();
//        Log.i("lkymsgzz","entityName"+entityNames);
        // 属性名称（格式为 : "key1=value1,key2=value2,....."）
        String columnKey = "";
        // 返回结果的类型（0 : 返回全部结果，1 : 只返回entityName的列表）
        int returnType = 0;
        // 活跃时间（指定该字段时，返回从该时间点之后仍有位置变动的entity的实时点集合）
        int activeTime = (int) (System.currentTimeMillis() / 1000 - packInterval);
        // 分页大小
        int pageSize = 10;
        // 分页索引
        int pageIndex = 1;

        trackApp.getClient().queryEntityList(trackApp.getServiceId(), entityNames, columnKey, returnType, activeTime, pageSize, pageIndex, entityListener);
    }

    // 查询里程
    private void queryDistance(int processed, String processOption) {
        Log.i("lkymsgyy", "queryDistance");
        // entity标识
        String entityName = trackApp.getEntityName();
//        Log.i("lkymsgzz","entityName"+entityName);
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

        trackApp.getClient().queryDistance(trackApp.getServiceId(), entityName, isProcessed, processOption, supplementMode, startTime, endTime, trackListener);
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

        trackApp.getClient().queryHistoryTrack(trackApp.getServiceId(), entityName, simpleReturn,
                isProcessed, processOption,
                startTime, endTime,
                pageSize,
                pageIndex,
                trackListener);
    }

    /**
     * 初始化OnStartTraceListener
     */
    private void initOnStartTraceListener() {
        // 初始化startTraceListener
        startTraceListener = new OnStartTraceListener() {

            // 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
            public void onTraceCallback(int arg0, String arg1) {
                // TODO Auto-generated method stub
//                mHandler.obtainMessage(arg0, "开启轨迹服务回调接口消息 [消息编码 : " + arg0 + "，消息内容 : " + arg1 + "]").sendToTarget();
                Log.i("lkymsgyy", "开启轨迹服务回调接口消息 [消息编码 : " + arg0 + "，消息内容 : " + arg1 + "]");
//                if(arg0==0){
//                    Begingo_update();
//                }
            }

            // 轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
            public void onTracePushCallback(byte arg0, String arg1) {
                // TODO Auto-generated method stub
                if (0x03 == arg0 || 0x04 == arg0) {
                    try {
                        JSONObject dataJson = new JSONObject(arg1);
                        if (null != dataJson) {
                            String mPerson = dataJson.getString("monitored_person");
                            String action = dataJson.getInt("action") == 1 ? "进入" : "离开";
                            String date = DateUtils.getDate(dataJson.getInt("time"));
                            long fenceId = dataJson.getLong("fence_id");
                            mHandler.obtainMessage(-1,
                                    "监控对象[" + mPerson + "]于" + date + " [" + action + "][" + fenceId + "号]围栏")
                                    .sendToTarget();
                            Log.i("lkymsgyy", "监控对象[" + mPerson + "]于" + date + " [" + action + "][" + fenceId + "号]围栏");
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        mHandler.obtainMessage(-1, "轨迹服务推送接口消息 [消息类型 : " + arg0 + "，消息内容 : " + arg1 + "]")
                                .sendToTarget();

                        Log.i("lkymsgyy", "轨迹服务推送接口消息 [消息类型 : " + arg0 + "，消息内容 : " + arg1 + "]");
                    }
                } else {
                    mHandler.obtainMessage(-1, "轨迹服务推送接口消息 [消息类型 : " + arg0 + "，消息内容 : " + arg1 + "]").sendToTarget();

                    Log.i("lkymsgyy", "轨迹服务推送接口消息 [消息类型 : " + arg0 + "，消息内容 : " + arg1 + "]");
                }
            }

        };
    }

    /**
     * 初始化OnStopTraceListener
     */
    private void initOnStopTraceListener() {
        // 初始化stopTraceListener
        stopTraceListener = new OnStopTraceListener() {

            // 轨迹服务停止成功
            public void onStopTraceSuccess() {
                // TODO Auto-generated method stub
//                mHandler.obtainMessage(1, "停止轨迹服务成功").sendToTarget();
                Log.i("lkymsgyy", "停止轨迹服务成功");

                startRefreshThread(false);
                trackApp.getClient().onDestroy();
            }

            //             轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
            public void onStopTraceFailed(int arg0, String arg1) {
                // TODO Auto-generated method stub
//                mHandler.obtainMessage(-1, "停止轨迹服务接口消息 [错误编码 : " + arg0 + "，消息内容 : " + arg1 + "]").sendToTarget();
                Log.i("lkymsgyy", "停止轨迹服务接口消息 [错误编码 : " + arg0 + "，消息内容 : " + arg1 + "]");
                startRefreshThread(false);
            }
        };
    }

    /**
     * 初始化OnEntityListener
     */
    private void initOnEntityListener() {
        entityListener = new OnEntityListener() {
            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
//                trackApp.getmHandler().obtainMessage(0, "entity请求失败回调接口消息 : " + arg0).sendToTarget();
                Log.i("lkymsgyy", "entity请求失败回调接口消息 : " + arg0);
            }

            // 添加entity回调接口
            public void onAddEntityCallback(String arg0) {
                // TODO Auto-generated method stub
                Log.e("lkymsgyy", "onAddEntityCallback");

                Log.i("lkymsgyy", "添加entity回调接口消息 : " + arg0);
            }

            // 查询entity列表回调接口
            @Override
            public void onQueryEntityListCallback(String message) {
                // TODO Auto-generated method stub
                Log.i("lkymsgyy", "message " + message);
                TraceLocation entityLocation = new TraceLocation();
                try {
                    JSONObject dataJson = new JSONObject(message);
                    if (null != dataJson && dataJson.has("status") && dataJson.getInt("status") == 0
                            && dataJson.has("size") && dataJson.getInt("size") > 0) {
                        JSONArray entities = dataJson.getJSONArray("entities");
                        JSONObject entity = entities.getJSONObject(0);
                        JSONObject point = entity.getJSONObject("realtime_point");
                        JSONArray location = point.getJSONArray("location");
                        entityLocation.setLongitude(location.getDouble(0));
                        entityLocation.setLatitude(location.getDouble(1));
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
//                    trackApp.getmHandler().obtainMessage(0, "解析entityList回调消息失败").sendToTarget();
                    Log.i("lkymsgyy", "解析entityList回调消息失败");
                    return;
                }
//                showRealtimeTrack(entityLocation);
            }

            @Override
            public void onReceiveLocation(TraceLocation location) {
                // TODO Auto-generated method stub
//                showRealtimeTrack(location);
            }

        };
    }

    /**
     * 初始化OnTrackListener
     */
    private void initOnTrackListener() {

        trackListener = new OnTrackListener() {

            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
//                trackApp.getmHandler().obtainMessage(0, "track请求失败回调接口消息 : " + arg0).sendToTarget();

//                Log.i("lkymsggg", "track请求失败回调接口消息 : " + arg0);

            }

            // 查询历史轨迹回调接口
            @Override
            public void onQueryHistoryTrackCallback(String arg0) {
                // TODO Auto-generated method stub
                super.onQueryHistoryTrackCallback(arg0);
//                showHistoryTrack(arg0);
//                Log.i("lkymsggg", "onQueryHistoryTrackCallback : " +arg0);
            }


            //获取鹰眼距离
            @Override
            public void onQueryDistanceCallback(String arg0) {
                // TODO Auto-generated method stub
//                Log.i("lkymsggg", "onQueryDistanceCallback : " +arg0);
                try {
                    JSONObject dataJson = new JSONObject(arg0);
                    if (null != dataJson && dataJson.has("status") && dataJson.getInt("status") == 0) {

                        double distance = dataJson.getDouble("distance") / 1000;
                        DecimalFormat df = new DecimalFormat("#0.0");
//                        Log.i("lkymsggg", "里程 : " + df.format(distance) + "米");
                        tvdistance = df.format(distance);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block

                    Log.i("lkymsggg", "queryDistance回调消息 : " + arg0);
                }
            }


            //添加每个点自定义字段
            @Override
            public Map<String, String> onTrackAttrCallback() {
                // TODO Auto-generated method stub
                System.out.println("onTrackAttrCallback");
                Map<String, String> trackAttrs = new HashMap<String, String>();
                trackAttrs.put("ceshi1", "111");
                trackAttrs.put("ceshi", "222");
                trackAttrs.put("text", "333");
                return trackAttrs;

            }

        };
    }

    protected void startRefreshThread(boolean isStart) {

        if (null == refreshThread) {
            refreshThread = new RefreshThread();
            Log.i("lkymsgyy", " refreshThread = new RefreshThread();");
        }
        refreshThread.refresh = isStart;
        if (isStart) {
            Log.i("lkymsgyy", "startRefreshThread  isStart");
            if (!refreshThread.isAlive()) {
                refreshThread.start();
            }
        } else {
            refreshThread = null;
            Log.i("lkymsgyy", "startRefreshThread  = null");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }








    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (thread1 != null) {
            thread1.interrupt();
            thread1 = null;
        }
        if (n == 3) {
            Log.i("lkymsg", "终止更新距离");
            myHandler.removeCallbacks(runnable_juli);

//            myHandler.removeCallbacksAndMessages(null);
//            myHandler = null;
            trackApp.getClient().onDestroy();
        }

    }

    /**
     * 初始化控件
     */
    public void init() {
        mediaPlayer = MediaPlayer.create(StateActivity.this, R.raw.shake_match);
        mDistanceInfoDao = new DistanceInfoDao(getApplicationContext());
        mTitle = (RelativeLayout) findViewById(R.id.stateactivity_title);
        fragment_state_time = (TextView) findViewById(R.id.fragment_state_time);
        fragment_state_cost = (TextView) findViewById(R.id.fragment_state_cost);
        fragment_state_waiting = (TextView) findViewById(R.id.fragment_state_waiting);
        fragment_state_driving = (TextView) findViewById(R.id.fragment_state_driving);
        fragment_state_over = (TextView) findViewById(R.id.fragment_state_over);
        layout_state_point1 = (ImageView) findViewById(R.id.layout_state_point1);
        layout_state_point2 = (ImageView) findViewById(R.id.layout_state_point2);
        fragment_state_daohang = (TextView) findViewById(R.id.fragment_state_daohang);
        fragment_state_daohang.setVisibility(View.GONE);
        fragment_state_daohang.setOnClickListener(this);
        fragment_state_track = (TextView) findViewById(R.id.fragment_state_track);
        fragment_state_track.setVisibility(View.GONE);
        Glide.with(this).load(R.drawable.gif_point).asBitmap().into(layout_state_point1);
        Glide.with(this).load(R.drawable.gif_point).asBitmap().into(layout_state_point2);
        Begingo = (Button) findViewById(R.id.Begingo);
        call_phone= (Button) findViewById(R.id.call_phone);
        Begingo.setOnClickListener(this);
        call_phone.setOnClickListener(this);//拨打电话
        call_phone.setVisibility(View.VISIBLE);
//        fragment_state_cost.setVisibility(View.GONE);
        Begingo.setText("开始代驾");
        changeBackGround(0);
        thread_time = new Thread(runnable_time);
        if (MyApplication.n == 2) {
//            restart();
        } else if (MyApplication.n == -1) {
            Beginwait();
            Log.e("wh", "第一次进入程序执行");
        }
        orderdata();
        if (MyApplication.n == 0) {
            Beginwait();
            Log.e("wh", "第二次进入程序执行");
        }

        if (MyApplication.n == 1) {  //开始计时器
            call_phone.setVisibility(View.VISIBLE);//把拨号键显示出来
            time = MyApplication.time;
            Thread thread = new Thread(runnable);
            thread.start();
//            Intent intents = new Intent(StateActivity.this, TimeServier.class);
//            startService(intents);
            //开始10秒种上传时间
            thread_time.start();

        }

        //开始驾驶
        if (MyApplication.n == 2) {
            call_phone.setVisibility(View.GONE);//把拨号键隐藏出来
//            handler.removeCallbacks(runnable);
//            Toast.makeText(this, "正在开启轨迹服务，请稍候", Toast.LENGTH_LONG).show();
            //启动鹰眼
            startTrace();
            if (!isRegister) {
                if (null == pm) {
                    pm = (PowerManager) trackApp.getSystemService(Context.POWER_SERVICE);
                }
                if (null == wakeLock) {
                    wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "track upload");
                }
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_SCREEN_OFF);
                filter.addAction(Intent.ACTION_SCREEN_ON);
                filter.addAction("com.baidu.trace.action.GPS_STATUS");
                trackApp.registerReceiver(trackReceiver, filter);
                isRegister = true;
            }

            isStartDirver = true;
            if ("".equals(MyApplication.distance)||MyApplication.distance==null){
                MyApplication.distance="0";
            }
            start_lucheng = Float.parseFloat(MyApplication.distance);
            Log.e("wh", "程序第二次进来的形式距离》》》" + start_lucheng);
            i = Float.valueOf(MyApplication.waitmoney);
            startDriver();
//            DistanceInfo mDistanceInfo = new DistanceInfo();
            fragment_state_time.setText(MyApplication.distance + "公里");
//            fragment_state_time.setText(mDistanceInfo.getDistance()+ "公里");
            changeBackGround(1);
            Begingo.setText("结束代驾");
            fragment_state_daohang.setVisibility(View.VISIBLE);
        }

        if (MyApplication.n == 3) {
            Intent intent = new Intent(StateActivity.this, SettlementOrderActivity.class);
            intent.putExtra("userurl", MyApplication.userurl);
            intent.putExtra("name", MyApplication.name);
            intent.putExtra("balance", MyApplication.balance);
            intent.putExtra("startmoney", MyApplication.startmoney);
            intent.putExtra("distancemoney", MyApplication.startmoney);
            intent.putExtra("waitmoney", MyApplication.waitmoney);
            intent.putExtra("maxnumber", MyApplication.maxnumber);
            intent.putExtra("addmoney", MyApplication.addmoney);
            intent.putExtra("time", MyApplication.time);
            Log.i("hrr", "time=" + MyApplication.time);
            intent.putExtra("maxnumber_exc", MyApplication.maxnumber_exc);
            Log.i("limming-stateactivity", "balance=" + MyApplication.balance);
            startActivity(intent);
            finish();
        }

        //下拉框
        contentView = LayoutInflater.from(StateActivity.this).inflate(
                R.layout.pop_xiangce, null);
        baidu = (Button) contentView.findViewById(R.id.baidu);
        baidu.setOnClickListener(this);
        gaode = (Button) contentView.findViewById(R.id.gaode);
        gaode.setOnClickListener(this);
        quxiao = (Button) contentView.findViewById(R.id.quxiao);
        quxiao.setOnClickListener(this);
        //创建一个弹框
        popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        // 点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);

        //点击空白处让父窗口变亮
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                getlayoutColor();
            }
        });
    }

    @Override
    public void onClick(View view) {
        Log.i("lkymsggg", "n" + n);
        switch (view.getId()) {
            case R.id.Begingo:
                if (n == 1) {
                    call_phone.setVisibility(View.GONE);//开始代驾时把拨号键隐藏了
                    //  handler.removeCallbacks(runnable);
//                    Toast.makeText(this, "正在开启轨迹服务，请稍候", Toast.LENGTH_LONG).show();
                    startTrace();
//                    startRefreshThread(false);
                    if (!isRegister) {
                        if (null == pm) {
                            pm = (PowerManager) trackApp.getSystemService(Context.POWER_SERVICE);
                        }
                        if (null == wakeLock) {
                            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "track upload");
                        }
                        IntentFilter filter = new IntentFilter();
                        filter.addAction(Intent.ACTION_SCREEN_OFF);
                        filter.addAction(Intent.ACTION_SCREEN_ON);
                        filter.addAction("com.baidu.trace.action.GPS_STATUS");
                        trackApp.registerReceiver(trackReceiver, filter);
                        isRegister = true;
                    }
//~~

                    Begingo_update();
                } else if (n == 2) {
                    Issure();
//                    Overgo_uodate();

                } else if (n == 3) {
//                    Intent intent = new Intent(StateActivity.this, SettlementOrderActivity.class);
//                    intent.putExtra("userurl", MyApplication.userurl);
//                    intent.putExtra("name", MyApplication.name);
//                    intent.putExtra("balance", MyApplication.balance);
//                    intent.putExtra("startmoney", MyApplication.startmoney);
//                    intent.putExtra("distancemoney", MyApplication.distancemoney);
//                    intent.putExtra("waitmoney", MyApplication.waitmoney);
//                    startActivity(intent);
                }
                break;
            case R.id.fragment_state_daohang:

                daohang();

                break;
            case R.id.baidu:
                openBaiduMap();
                break;
            case R.id.gaode:
                openGaodeMap();
                break;
            case R.id.quxiao:
                popupWindow.dismiss();
                break;

            //拨打电话
            case R.id.call_phone:
                if(phones.equals("")) {

                }else
                {
                    cellCustomer();
                }

                break;
        }
    }


    private void changeBackGround(int i) {
        fragment_state_waiting.setTextColor(Color.parseColor("#000000"));
        fragment_state_driving.setTextColor(Color.parseColor("#000000"));
        fragment_state_over.setTextColor(Color.parseColor("#000000"));
        fragment_state_waiting.setBackgroundResource(R.drawable.icon_white_garden);
        fragment_state_driving.setBackgroundResource(R.drawable.icon_white_garden);
        fragment_state_over.setBackgroundResource(R.drawable.icon_white_garden);
        switch (i) {
            case 0:
                fragment_state_waiting.setTextColor(Color.parseColor("#FFFFFF"));
                fragment_state_waiting.setBackgroundResource(R.drawable.icon_yeellow_garden);
                break;
            case 1:
                fragment_state_driving.setTextColor(Color.parseColor("#FFFFFF"));
                fragment_state_driving.setBackgroundResource(R.drawable.icon_yeellow_garden);
                break;
            case 2:
                fragment_state_over.setTextColor(Color.parseColor("#FFFFFF"));
                fragment_state_over.setBackgroundResource(R.drawable.icon_yeellow_garden);
                break;
        }
    }


    /**
     * 开始等待
     */
    private void Beginwait() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        if ("".equals(orderson)||orderson==null){
                Logger.t("hrr").e("orderson="+orderson);
            orderson=sp.getString("orderson","");
        }
        hashMap.put("order", orderson);
        hashMap.put("driverid", driverid);
        if (Network.HttpTest(StateActivity.this)) {
            showDialog(StateActivity.this, "加载中...");
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25)
                    .setTag(this)
                    .build()
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.Beginwait)
                                    .addParams(hashMap)
                                    .build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();
                                        Logger.t("hrr").e("开始等待请求示例=" + HttpPath.Beginwait + "?order=" + orderson + "&driverid=" + driverid + "&from=1");
                                        Logger.t("hrr").e(result + ">>>");
                                        if (result != null) {
                                            //将得到的json数据返回给HashMap
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> list = AnalyticalJSON.getHashMap(map.get("state"));
                                            HashMap<String, String> list_data = AnalyticalJSON.getHashMap(map.get("data"));

                                            if (list.get("code").equals("0")) {
                                                //起步公里
                                                MyApplication.maxnumber = list_data.get("maxnumber");
                                                //超出公里数——起步价
                                                MyApplication.money = list_data.get("money");
                                                //超出的公里数
                                                MyApplication.addmoney = list_data.get("addmoney");
                                                //等待费
                                                MyApplication.waitmoney = list_data.get("waitmoney");
                                                //等待时间段
                                                MyApplication.waittimeduan = list_data.get("waittimeduan");
                                                Log.e("lkymsg", "waittimeduan=" + MyApplication.waittimeduan);
                                                //超出初始里程公里数
                                                MyApplication.maxnumber_exc = list_data.get("maxnumber_exc");
                                                // 超出等待时间段时间
                                                MyApplication.waittimeduan_exc = list_data.get("waittimeduan_exc");
                                                Log.i("lkymsg", "waittimeduan_exc" + MyApplication.waittimeduan_exc);
                                                //开始计时器
                                                Thread thread = new Thread(runnable);
                                                thread.start();
                                                //开始10秒种上传时间
                                                Thread thread_time = new Thread(runnable_time);
                                                thread_time.start();
//                                                Intent intents = new Intent(StateActivity.this, TimeServier.class);
//                                                startService(intents);
//                                                Log.i("limming", "走了吗02");
//                                                Log.e("wh", "起步公里》》" + MyApplication.maxnumber);//2
//                                                Log.e("wh", "超出公里数——起步价" +
//                                                        "》》" + MyApplication.money);//20
//                                                Log.e("wh", "超出的公里数》》" + MyApplication.addmoney);//7
//                                                Log.e("wh", "等待费》》" + MyApplication.waitmoney);//1
//                                                Log.e("wh", "等待时间段》》" + MyApplication.waittimeduan);//1
//                                                Log.e("wh", "超出初始里程公里数》》" + MyApplication.maxnumber_exc);//1
//                                                Log.e("wh", "超出等待时间段时间》》" + MyApplication.waittimeduan_exc);//1
                                                n = 1;
                                            } else {
                                                Log.i("limming000","0000"+ list.get("msg"));
                                                Toast.makeText(StateActivity.this, list.get("msg"), Toast.LENGTH_SHORT).show();
                                            }

                                        } else {
                                            Toast.makeText(StateActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(StateActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
        }
        dismissDialog();

    }

    /**
     * 开始代驾
     */
    public void Begingo_update() {
//        time=10854;//测试
//        //暂停计时器
        handler.removeCallbacks(runnable);
        //暂停上传时间
        handler_time.removeCallbacks(runnable_time);
        thread_time.interrupt();
//        stopService(new Intent(StateActivity.this, TimeServier.class));
        fragment_state_daohang.setVisibility(View.VISIBLE);
        fragment_state_cost.setVisibility(View.VISIBLE);

        //把等待时间段转化为秒数
        Float miao = Float.parseFloat(MyApplication.waittimeduan) * 60;

        //判断当前时间是否大于等待时间段
        if (time <= miao) {

            i = 0f;//不收取任何等待费
        } else {
            Float cao_time = time - miao;//得到等待超出的时间

            Float das = cao_time / (Float.parseFloat(MyApplication.waittimeduan_exc) * 60);// 计算步骤(超过等待时间段不足这个时间段按一个时间段计算)

            int t = (int) Math.ceil(das);//向上取整

            i = t * Float.parseFloat(MyApplication.waitmoney);//等待费价格
        }
        Logger.t("hrr").e(i+"");
        Begingo();
        Begingo.setText("结束行程");
        changeBackGround(1);
    }

    /**
     * 开始代驾
     */
    private void Begingo() {
        isStartDirver = true;
        if (mDistanceInfoDao==null){
            mDistanceInfoDao = new DistanceInfoDao(getApplicationContext());
        }
        mDistanceInfoDao.clear();//清空数据表
        startDriver();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("order", orderson);
        hashMap.put("wait_time", time + "");
        hashMap.put("wait_money", i + "");
        Log.i("limming","wait_time="+time+"wait_money="+i+"order="+orderson);
        if (Network.HttpTest(StateActivity.this)) {
            showDialog(StateActivity.this, "加载中...");
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.Begingo)
                                    .addParams(hashMap)
                                    .build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {

                                        //获取到数据
                                        String result = info.getRetDetail();

                                        if (result != null) {//将得到的json数据返回给HashMap
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> list = AnalyticalJSON.getHashMap(map.get("state"));
                                            HashMap<String, String> list_date = AnalyticalJSON.getHashMap(map.get("data"));
                                            if (list.get("code").equals("0")) {

                                                //起步公里
                                                MyApplication.maxnumber = list_date.get("maxnumber");
                                                //超出公里数——起步价
                                                MyApplication.money = list_date.get("money");
                                                //超出的公里数
                                                MyApplication.addmoney = list_date.get("addmoney");
                                                //等待费
                                                MyApplication.waitmoney = list_date.get("waitmoney");
                                                //等待时间段
                                                MyApplication.waittimeduan = list_date.get("waittimeduan");
                                                //超出初始里程公里数
                                                MyApplication.maxnumber_exc = list_date.get("maxnumber_exc");
                                                // 超出等待时间段时间
                                                MyApplication.waittimeduan_exc = list_date.get("waittimeduan_exc");

                                                MyApplication.overwait_time = Integer.parseInt(list_date.get("overwait_time"));
                                                startTime = Integer.parseInt(list_date.get("overwait_time"));

//                                                Log.e("wh", "起步公里》》" + MyApplication.maxnumber);//2
//                                                Log.e("wh", "超出公里数——起步价》》" + MyApplication.money);//20
//                                                Log.e("wh", "超出的公里数》》" + MyApplication.addmoney);//7
//                                                Log.e("wh", "等待费》》" + MyApplication.waitmoney);//1
//                                                Log.e("wh", "等待时间段》》" + MyApplication.waittimeduan);//30
//                                                Log.e("wh", "超出初始里程公里数》》" + MyApplication.maxnumber_exc);//1
//                                                Log.e("wh", "超出等待时间段时间》》" + MyApplication.waittimeduan_exc);//1
//                                                Log.e("lkymsg", "开始代驾时间戳》》" + MyApplication.overwait_time);//

                                                n = 2;

                                            } else {
                                                Log.i("limming0000","bbb="+ list.get("msg"));
                                                Toast.makeText(StateActivity.this, list.get("msg"), Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(StateActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(StateActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
        }
        dismissDialog();

    }



    /**
     * 请求成功，开始进行代驾
     * 打开距离计算服务
     */
    private void startDriver() {

        intent2 = new Intent(getApplicationContext(), LocationService.class);
        startService(intent2);
        if (isStartDirver) {
            isStartDirver = false;
            DistanceInfo mDistanceInfo = new DistanceInfo();
            List<DistanceInfo> list = new ArrayList<DistanceInfo>();
            list = mDistanceInfoDao.quereall();
            Logger.t("StateActivity").e("list="+list.toString());

            if (list.size() != 0) {
                mDistanceInfo.setDistance(list.get(list.size() - 1).getDistance());
                mDistanceInfo.setLatitude(list.get(list.size() - 1).getLatitude());
                mDistanceInfo.setLongitude(list.get(list.size() - 1).getLongitude());
                id = mDistanceInfoDao.getMaxId();
            }else {
                Location location = new LocationXY().init(getApplicationContext());
                if (location.getLatitude() != 0d && location.getLongitude() != 0d) {
                    mDistanceInfo.setLongitude(location.getLongitude());
                    mDistanceInfo.setLatitude(location.getLatitude());
                    id = mDistanceInfoDao.insertAndGet(mDistanceInfo);
                }
            }
//            Log.i("limmingxc",MyApplication.orderDealInfoId+"");
//            if (id != -1) {
//                MyApplication.orderDealInfoId = id;
//                Log.i("limmingxc",MyApplication.orderDealInfoId+"");
//                thread1 = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            thread1.sleep(3000);
//                            myHandler.sendEmptyMessage(100);
//                            Log.i("limmingxc","sleep");
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                thread1.start();
//
//            }
            if (id != -1) {
                MyApplication.orderDealInfoId = id;
               //更新距离
                Thread thread_juli=new Thread(runnable_juli);
                thread_juli.start();

            }
        }
    }


    Runnable runnable_juli = new Runnable() {
        @Override
        public void run() {
            Message msg = myHandler.obtainMessage();
            msg.what = 100;
            myHandler.sendMessage(msg);

        }

    };
    /**
     * 更新距离
     */
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    updateDistance();
                    queryDistance(1, "need_denoise=1,need_vacuate=1,need_mapmatch=1");
                    //鹰眼距离赋值
                    fragment_state_track.setText(tvdistance + "米");
                    myHandler.postDelayed(runnable_juli, 3000);
                    break;
            }
        }
    };
    void updateDistance() {
        if (isRefreshUI) {
            mDistanceInfoDao = new DistanceInfoDao(getBaseContext());
            DistanceInfo distanceInfo = mDistanceInfoDao.getById(MyApplication.orderDealInfoId);
            if (mDistanceInfoDao != null) {
                num++;
                BigDecimal b = new BigDecimal(distanceInfo.getDistance());
                fragment_state_time.setText(b.setScale(1, BigDecimal.ROUND_DOWN) + "公里");//保留小数后1位
                lucheng = Utils.getValueWith2Suffix(distanceInfo.getDistance());

                Log.e("juli", "当前距离..." + distanceInfo.getDistance()+"  222  "+lucheng+"  333=  "+MyApplication.maxnumber+Thread.currentThread().getName());
                //判断行驶公里是否大于起步公里
                if (Float.parseFloat(MyApplication.maxnumber) >= Float.parseFloat(lucheng)) {
                    chao = Float.parseFloat("0");//公里費
                    Float zong = Float.parseFloat(MyApplication.money) + i;//得到行驶的总的价格
                    fragment_state_cost.setText(zong + "元");
                } else {
                    Float you = Float.parseFloat(lucheng) - Float.parseFloat(MyApplication.maxnumber);  //行驶公里-起步公里=超出公里
                    chao = ((int) Math.ceil(you / Float.parseFloat(MyApplication.maxnumber_exc)) * Float.parseFloat(MyApplication.addmoney)); //超出的公里/超出初始里程公里数*超出公里价格=公里费

                    Float zong = Float.parseFloat(MyApplication.money) + chao + i; //得到行驶的总的价格
                    fragment_state_cost.setText(zong + "元");
                }
                upData(Utils.getValueWith2Suffix(distanceInfo.getDistance()));
                fragment_state_time.invalidate();
            }
        }
    }

    /**
     * 结束代驾
     */
    public void Overgo_uodate() {
        time = 0;
        MyApplication.time = 0;
        fragment_state_daohang.setVisibility(View.GONE);
//        fragment_state_cost.setVisibility(View.GONE);
        Begingo.setText("完成结算");
        changeBackGround(2);
//        Overgo();

        time = 0;
        MyApplication.time = 0;
    }

    private void Issure() {
        final Dialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否结束行程？")
                .setPositiveButton("是", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Overgo_uodate();
                        Overgo();
//                        Toast.makeText(StateActivity.this, "正在停止轨迹服务，请稍候", Toast.LENGTH_SHORT).show();
                        stopTrace();
                        if (isRegister) {
                            try {
                                trackApp.unregisterReceiver(trackReceiver);
                                isRegister = false;
                            } catch (Exception e) {
                                // TODO: handle
                            }

                        }
                    }
                })
                .setNegativeButton("否", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        // 显示对话框
        dialog.show();
    }

    /**
     * 结束代驾
     */
    private void Overgo() {
//        // 写入
//        SharedPreferences.Editor editor = sp.edit();
//        //判断是否登录，登录过后改为true
//        editor.putString("nnn", "-1");
//        editor.commit();
//        Log.e("hrr", "结束代驾请求示例=" + HttpPath.Overgo + "?orderson=" + orderson + "&driverID=" + driverid + "&distance=" + lucheng + "&distancemoney=" + chao + "&from=1");
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("orderson", orderson);
        hashMap.put("driverID", driverid);
        hashMap.put("distance", lucheng);
        hashMap.put("distancemoney", chao + "");
        if (Network.HttpTest(StateActivity.this)) {
            showDialog(StateActivity.this, "加载中...");
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.Overgo)
                                    .addParams(hashMap)
                                    .build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {

                                        //获取到数据
                                        String result = info.getRetDetail();
//                                        Log.e("wh", "结束代驾=" + result);
                                        if (result != null) {
                                            //将得到的json数据返回给HashMap
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> list = AnalyticalJSON.getHashMap(map.get("state"));
                                            HashMap<String, String> list_date = AnalyticalJSON.getHashMap(map.get("data"));
                                            HashMap<String, String> list_userInfo = AnalyticalJSON.getHashMap(list_date.get("userInfo"));
                                            if (list.get("code").equals("0")) {
                                                MyApplication.userurl = list_userInfo.get("userurl");
                                                MyApplication.name = list_userInfo.get("name");
                                                MyApplication.balance = list_date.get("original_totalmoney");
                                                MyApplication.startmoney = list_date.get("startmoney");
                                                MyApplication.distancemoney = list_date.get("distancemoney");
                                                MyApplication.waitmoney = list_date.get("waitmoney");
                                                MyApplication.maxnumber = list_date.get("_maxnumber");
                                                MyApplication.maxnumber_exc = list_date.get("_maxnumber_exc");
                                                MyApplication.time = Integer.valueOf(list_date.get("waittime"));
                                                MyApplication.use_couponmoney = list_date.get("use_couponmoney");
//                                                Log.i("hrr", "waitmoney=" + MyApplication.waitmoney);
                                                dismissDialog();
                                                n = 3;
                                                isRefreshUI = false;
                                                mDistanceInfoDao.delete(MyApplication.orderDealInfoId);//删除id对应记录
                                                mDistanceInfoDao.clear();//清空数据表
                                                MyApplication.orderDealInfoId = -1;//停止计算定位
                                                stopService(intent2);
                                                Intent intent = new Intent(StateActivity.this, SettlementOrderActivity.class);
                                                intent.putExtra("userurl", MyApplication.userurl);
                                                intent.putExtra("name", MyApplication.name);
                                                intent.putExtra("balance", MyApplication.balance);
                                                intent.putExtra("startmoney", MyApplication.startmoney);
                                                intent.putExtra("distancemoney", MyApplication.distancemoney);
                                                intent.putExtra("waitmoney", MyApplication.waitmoney);
                                                intent.putExtra("time", MyApplication.time);
                                                intent.putExtra("maxnumber", MyApplication.maxnumber);
                                                intent.putExtra("distance", MyApplication.distance);
                                                intent.putExtra("use_couponmoney", MyApplication.use_couponmoney);
                                                Log.i("limming-stateactivity02", "balance=" + MyApplication.balance);
                                                startActivity(intent);
                                                finish();


                                            } else {
                                                Log.i("limming0000","ccc="+ list.get("msg"));
                                                Toast.makeText(StateActivity.this, list.get("msg"), Toast.LENGTH_SHORT).show();
                                                dismissDialog();
                                            }

                                        } else {
                                            Toast.makeText(StateActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                                            dismissDialog();
                                        }
                                    }

                                }


                            });
        }

    }


    //计时器
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time++;
            Message msg = handler.obtainMessage();
            msg.obj = time;
            handler.sendMessage(msg);
        }
    };


    // 更新界面
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            getTime((Integer) msg.obj);
            handler.postDelayed(runnable, 1000);
        }
    };


    //十秒钟上传一次时间
    Runnable runnable_time = new Runnable() {
        @Override
        public void run() {
            Message msg = handler_time.obtainMessage();
            msg.obj = time;
            waitreLoad(time + "");
            handler_time.sendMessage(msg);

        }
    };


    // 10秒上传
    Handler handler_time = new Handler() {
        public void handleMessage(Message msg) {
//            getTime((Integer) msg.obj);
            handler_time.postDelayed(runnable_time, 4000);
        }
    };

    /**
     * 司机实时记录等待时间
     */
    public void waitreLoad(final String ti) {
        //把等待时间段转化为秒数
        Float miao = Float.parseFloat(MyApplication.waittimeduan) * 60;
        //判断当前时间是否大于等待时间段
        time = Integer.parseInt(ti);
        MyApplication.time = Integer.parseInt(ti);
        if (Integer.parseInt(ti) <= miao) {
//            Log.e("wh", "你的等待时间不足" + MyApplication.waittimeduan + "分钟");
            i = 0f;//不收取任何等待费
//            Log.i("lkymsg", "time=" + ti);
//            Log.i("lkymsg", "miao=" + miao);

        } else {
            Float cao_time = Integer.parseInt(ti) - miao;//得到等待超出的时间
//            Log.e("wh", "得到等待超出的时间》》》" + cao_time);
            Float das = cao_time / (Float.parseFloat(MyApplication.waittimeduan_exc) * 60);// 计算步骤(超过等待时间段不足这个时间段按一个时间段计算)
//            Log.e("wh", "超算过等待时间段不足这个时间段按一个时间段计算》》》" + das);
            int t = (int) Math.ceil(das);//向上取整
//            Log.e("wh", "向上取整》》》" + t);
            i = t * Float.parseFloat(MyApplication.waitmoney);//等待费价格

//            Log.i("lkymsg", "ti=" + ti);
//            Log.i("lkymsg", "miao=" + miao);
//            Log.i("lkymsg", "cao_time=" + cao_time);
//            Log.i("lkymsg", "das=" + das);
//            Log.i("lkymsg", "t=" + t);
//            Log.i("lkymsg", "MyApplication.waitmoney=" + MyApplication.waitmoney);
//            Log.i("lkymsg", "i=" + i);
        }

        if (i > 0) {//显示等待费
            fragment_state_cost.setText(i + "元");
        }
        if (Network.HttpTest(StateActivity.this)) {
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.waitreLoad)
                                    .addParam("orderson", orderson)
                                    .addParam("time", ti)
                                    .addParam("waitmoney", String.valueOf(i))
                                    .build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    LogUtil.e("等待时间提交示例=" + HttpPath.waitreLoad + "/orderson/" + orderson + "/time/" + ti + "/waitmoney/" + i);
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();
                                        if (result != null) {
                                            //将得到的json数据返回给HashMap
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> list = AnalyticalJSON.getHashMap(map.get("state"));
                                            HashMap<String, String> list_data = AnalyticalJSON.getHashMap(map.get("data"));

                                            if (list.get("code").equals("0")) {
                                            } else {
                                                Toast.makeText(StateActivity.this, list.get("msg"), Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(StateActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }
                            });
        }

    }
//    public void waitreLoad(String ti){
//        fragment_state_cost.setText(ti+"元");
//    }


    /**
     * 代驾中上报行驶数据
     */
    public void upData(final String lu) {
        Logger.t("StateActivity").e("网络=" +Network.HttpTest(StateActivity.this) );
        if (Network.HttpTest(StateActivity.this)) {
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.upData)
                                    .addParam("orderson", orderson)
                                    .addParam("driverID", driverid)
                                    .addParam("distance", lu)
                                    .addParam("distancemoney", chao + "")
                                    .build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    Logger.t("StateActivity").e("上传行驶中的数据示例=" + HttpPath.upData + "/orderson/" + orderson + "/driverID/" + driverid + "/distance/" + lu + "/distancemoney/" + chao);
                                    if (info.isSuccessful()) {

                                        //获取到数据
                                        result = info.getRetDetail();

                                        if (result != null) {
                                            //将得到的json数据返回给HashMap
                                            map = AnalyticalJSON.getHashMap(result);
                                            list = AnalyticalJSON.getHashMap(map.get("state"));
                                            HashMap<String, String> list_data = AnalyticalJSON.getHashMap(map.get("data"));

                                            if (list.get("code").equals("0")) {

                                            } else {
                                                Toast.makeText(StateActivity.this, list.get("msg"), Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(StateActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    thread1.start();
                                }

                            });
        }

    }


    /**
     * 根据当前订单得到经纬度
     */
    private void orderdata() {
        if (Network.HttpTest(StateActivity.this)) {
//            Log.e("wh", "根据订单得到经纬度=" + HttpPath.orderdata + "?orderson=" + orderson);
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.orderdata).addParam("orderson", orderson)
                                    .build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();
//                                        Log.e("wh", result + ">>>");
                                        if (result != null) {
                                            //将得到的json数据返回给HashMap
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> list = AnalyticalJSON.getHashMap(map.get("state"));
                                            if (list.get("code").equals("0")) {
                                                HashMap<String, String> list_data = AnalyticalJSON.getHashMap(map.get("data"));
                                                //经度-目的地
                                                olng = Double.parseDouble(list_data.get("olng"));
                                                //纬度-目的地
                                                olat = Double.parseDouble(list_data.get("olat"));
                                                //经度-出发地
                                                slng = Double.parseDouble(list_data.get("slng"));
                                                //纬度-出发地
                                                slat = Double.parseDouble(list_data.get("slat"));
//                                                Log.e("wh", "目的地经纬度>>" + olng + "," + olat);
//                                                Log.e("wh", "出发地经纬度>>" + slng + "," + slat);
                                            } else {
                                                Toast.makeText(StateActivity.this, list.get("msg"), Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(StateActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                }
                            });
        }

    }


    // 时间判断
    public void getTime(int num) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = num % 3600;
        if (num > 3600) {
            h = num / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = num / 60;
            if (num % 60 != 0) {
                s = num % 60;
            }
        }
        fragment_state_time.setText(h + "时" + d + "分" + s + "秒");

    }
//    public void getTime(int h, int d, int s) {
//        fragment_state_time.setText(h + "时" + d + "分" + s + "秒");
//    }


    //自定义加载框
    public void showDialog(Context context, String msg) {
        if (dia == null) {
            dia = new ProgressDialog(context, R.style.Theme_CustomDialog);
            dia.setCanceledOnTouchOutside(false);
        }
        dia.setMessage(msg);
        if (!dia.isShowing()) {
            dia.show();
        }
    }

    public void dismissDialog() {
        if (dia != null && dia.isShowing()) {
            dia.dismiss();
        }
    }

    @Override
    public void paytype(int type) {
        if (type == 52) {
            LogUtil.e("StateActivity","type=52");
            MyApplication.orderState = 2;
            Intent intent = new Intent(StateActivity.this, SettlementOrderActivity.class);
            intent.putExtra("userurl", MyApplication.userurl);
            intent.putExtra("name", MyApplication.name);
            intent.putExtra("balance", MyApplication.balance);
            intent.putExtra("startmoney", MyApplication.startmoney);
            intent.putExtra("distancemoney", MyApplication.distancemoney);
            intent.putExtra("waitmoney", MyApplication.waitmoney);
            startActivity(intent);
            mediaPlayer.start();
            finish();
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Toast.makeText(StateActivity.this, "请完成行程", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    /*
* 改变背景颜色亮度,当点击window让屏幕变暗
*/
    protected void setlayoutColor() {
        // TODO Auto-generated method stub
        Float A = (float) .10f;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);

    }

    /*
     * 恢复背景颜色亮度
     */
    protected void getlayoutColor() {
        // TODO Auto-generated method stub
        Float A = (float) 1f;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1f;
        getWindow().setAttributes(lp);

    }

    /**
     * 选择使用哪种导航
     */
    private void daohang() {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
            getlayoutColor();
        } else {
            setlayoutColor();
            // 这里是位置显示方式,在屏幕的底部
            popupWindow.showAtLocation(mTitle, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    //调用高德地图
    private void openGaodeMap() {
        if (isInstallByread("com.autonavi.minimap")) {
            bd09_To_Gcj02();
            try {
                intent = Intent.getIntent("androidamap://navi?sourceApplication=小车夫&poiname=我的目的地&lat=" + olat + "&lon=" + olng + "&dev=0&style=2");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "未安装高德地图客户端", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 * * 将 BD-09 坐标转换成GCJ-02 坐标 * * @param
     * bd_lat * @param bd_lon * @return
     */
    public void bd09_To_Gcj02() {
        double x = olng - 0.0065, y = olat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        olng = z * Math.cos(theta);
        olat = z * Math.sin(theta);
    }

    //调用百度地图
    private void openBaiduMap() {
        Intent intent = null;
        if (isInstallByread("com.baidu.BaiduMap")) {
            try {
                intent = Intent.getIntent("intent://map/navi?location=" + olat + "," + olng + "&type=BLK&src=thirdapp.navi.yourCompanyName.yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "未安装百度地图客户端", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

//    class MyBroadCastRecevier extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Intent intent1 = intent;
//            int h = 0, d = 0, s = 0;
//            String money = "";
//            if (intent1 != null) {
//                String type = intent.getAction();
//                switch (type) {
//                    case "changetime":
//                        h = intent.getIntExtra("timeh", 0);
//                        d = intent.getIntExtra("timed", 0);
//                        s = intent.getIntExtra("times", 0);
//                        getTime(h, d, s);
//                        break;
//                    case "changemoney":
//                        money = intent.getStringExtra("timemoney");
//                        waitreLoad(money);
//                        break;
//                }
//            }
//        }
//    }
//    /**
//     * 显示历史轨迹
//     *
//     * @param historyTrack
//     */
//    private void showHistoryTrack(String historyTrack) {
//
//        HistoryTrackData historyTrackData = GsonService.parseJson(historyTrack,
//                HistoryTrackData.class);
//
//        List<LatLng> latLngList = new ArrayList<LatLng>();
//        if (historyTrackData != null && historyTrackData.getStatus() == 0) {
//            if (historyTrackData.getListPoints() != null) {
//                latLngList.addAll(historyTrackData.getListPoints());
//            }
//
//            // 绘制历史轨迹
//            drawHistoryTrack(latLngList, historyTrackData.distance);
//
//        }
//
//    }
//
//
//
//
//    /**
//     * 绘制历史轨迹
//     *
//     * @param points
//     */
//    private void drawHistoryTrack(final List<LatLng> points, final double distance) {
//        // 绘制新覆盖物前，清空之前的覆盖物
//        trackApp.getmBaiduMap().clear();
//
//        if (points.size() == 1) {
//            points.add(points.get(0));
//        }
//
//        if (points == null || points.size() == 0) {
////            trackApp.getmHandler().obtainMessage(0, "当前查询无轨迹点").sendToTarget();
//            resetMarker();
//        } else if (points.size() > 1) {
//
//            LatLng llC = points.get(0);
//            LatLng llD = points.get(points.size() - 1);
//            LatLngBounds bounds = new LatLngBounds.Builder()
//                    .include(llC).include(llD).build();
//
//            msUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);
//
//            bmStart = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
//            bmEnd = BitmapDescriptorFactory.fromResource(R.drawable.icon_end);
//
//            // 添加起点图标
//            startMarker = new MarkerOptions()
//                    .position(points.get(points.size() - 1)).icon(bmStart)
//                    .zIndex(9).draggable(true);
//
//            // 添加终点图标
//            endMarker = new MarkerOptions().position(points.get(0))
//                    .icon(bmEnd).zIndex(9).draggable(true);
//
//            // 添加路线（轨迹）
//            polyline = new PolylineOptions().width(10)
//                    .color(Color.RED).points(points);
//
//            markerOptions = new MarkerOptions();
//            markerOptions.flat(true);
//            markerOptions.anchor(0.5f, 0.5f);
//            markerOptions.icon(BitmapDescriptorFactory
//                    .fromResource(R.drawable.icon_gcoding));
//            markerOptions.position(points.get(points.size() - 1));
//
//            addMarker();
//
//            trackApp.getmHandler().obtainMessage(0, "当前轨迹里程为 : " + (int) distance + "米").sendToTarget();
//
//        }
//
//    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void  QuXiaoEvent(String msg){
        if (msg!=null&&msg.equals("12")){
            Log.i("limming", "tuisong_12");
                Toast.makeText(getApplicationContext(),"该订单已由平台取消！",Toast.LENGTH_LONG).show();
            this.finish();
        }
    }
    //拨打电话
    final public static int REQUEST_CODE_ASK_CALL_PHONE = 123;
    private void cellCustomer() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getParent(),new String[]{Manifest.permission.CALL_PHONE},REQUEST_CODE_ASK_CALL_PHONE);
                return;
            }else{
                callDirectly(phones);
            }
        } else {
            callDirectly(phones);
        }
    }

    private void callDirectly(String mobile){
        intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:" + mobile));
        startActivity(intent);
    }

}
