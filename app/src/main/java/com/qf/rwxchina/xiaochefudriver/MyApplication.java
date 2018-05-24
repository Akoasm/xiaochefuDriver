package com.qf.rwxchina.xiaochefudriver;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.Trace;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkHttpUtil;
import com.qf.rwxchina.xiaochefudriver.Utils.logutils.LogUtil;
import com.qf.rwxchina.xiaochefudriver.service.NetAddressService;
import com.tencent.bugly.crashreport.CrashReport;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

/**
 * 软件启动初始
 */
public class MyApplication extends Application {
    //    private static Set<Activity> allActivities;
    private List<Activity> allActivities ;
    private static MyApplication sInstance;
    public static int orderState = 0;//抢单的状态，0表示未在订单状态，1表示处于抢单状态,2表示用户已经支付订单
    public static int orderDealInfoId = -1;
    public static String yue;//余额
    public static double lng = 0d;
    public static double lat = 0d;
    public static String orderson = "";//订单号
    public static String type;//是否是平台下单
    public static String type_pingtai;//是否是商户
    public static int n = -1;//判断刚打开APP状态 0：司机接单、1：司机等待中、2：司机代驾中、3：司机代驾结束、4：用户已支付；
    public static int time;//等待时间
    public static String waitmoney;//等待费
    public static String isNo = "1";//判断程序是否退出过
    public static String distance;//订单行驶公里
    //头像
    public static String userurl;
    //姓名
    public static String name;
    //总价格
    public static String balance;
    //起步价
    public static String startmoney;
    //此订单公里费
    public static String distancemoney;
    //起步公里
    public static String maxnumber;
    //超出公里数——起步价
    public static String money;
    //超出的公里数
    public static String addmoney;
    //等待时间段
    public static String waittimeduan;

    public static String bianji = "0";

    //结伴同行被发起的司机ID;
    public static String driverid = "";
    //结伴同行被发起的司机电话号码;
    public static String phone = "";

    public static Intent service;

    public static String locationAddress = "";//当前具体位置

    public static String is_kongxian = ""; //判断是否是空闲状态

    public static String maxnumber_exc = ""; //超出初始里程公里数

    public static String waittimeduan_exc = ""; //超出等待时间段时间
    public static String use_couponmoney = ""; //优惠券

    public static boolean isFirst = true;

    private static String TAG = "xcf";

    //    private static String mCity = "成都";
    public static int cityId;
    //代价开始时间
    public static int overwait_time;


    private static Context mContext = null;

    /**
     * 轨迹服务
     */
    private Trace trace = null;

    /**
     * 轨迹服务客户端
     */
    private LBSTraceClient client = null;

    /**
     * 鹰眼服务ID，开发者创建的鹰眼服务对应的服务ID
     */
    private int serviceId = 137666;

    /**
     * entity标识
     */
    private String entityName;

    /**
     * 轨迹服务类型（0 : 不建立socket长连接， 1 : 建立socket长连接但不上传位置数据，2 : 建立socket长连接并上传位置数据）
     */
    private int traceType = 2;

    SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        sInstance = this;
        SDKInitializer.initialize(getApplicationContext());
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
//        LogUtil.initNone();//打印初始化,不打印
        LogUtil.initFull();//打印初始化，打印

        service = new Intent(getApplicationContext(), NetAddressService.class);
        // 设置定位模式
        OkHttpUtil.init();//网络加载框架OkHttpUtils初始化
        //bugly初始化
//        第三个参数为SDK调试模式开关，调试模式的行为特性如下：
//        输出详细的Bugly SDK的Log；
//        每一条Crash都会被立即上报；
//        自定义日志将会在Logcat中输出。
//        建议在测试阶段建议设置成true，发布时设置为false。
        CrashReport.initCrashReport(getApplicationContext(), "750e1dd16c", false);
    }


    //初始化鹰眼服务
    public void createTrace(Context context) {
        sp = getSharedPreferences("userInfo", context.MODE_PRIVATE);
        //司机ID
        entityName = sp.getString("uid", "");

        // 初始化轨迹服务客户端
        client = new LBSTraceClient(mContext);

        // 初始化轨迹服务
        trace = new Trace(mContext, serviceId, entityName, traceType);

        client.setLocationMode(LocationMode.High_Accuracy);

    }

    public Context getmContext() {
        return mContext;
    }

    public Trace getTrace() {
        return trace;
    }

    public LBSTraceClient getClient() {
        return client;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


    public static MyApplication getInstance() {
        if (sInstance == null) {
            sInstance = new MyApplication();
        }
        return sInstance;
    }

    /**
     * 添加activity
     */
//    public void addActivity(Activity act) {
//        if (allActivities == null) {
//            allActivities = new HashSet<>();
//        }
//        allActivities.add(act);
//    }
    public void addActivity(Activity activity) {
        if(allActivities == null){
            allActivities = new LinkedList<Activity>();
        }
        allActivities.add(activity);
    }

    /**
     * 退出app
     */
    public void exitApp() {
        if (allActivities != null) {
            for (Activity activity : allActivities) {
                if(activity != null){
                    activity.finish();
                }

            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static Context getContext(){
        return mContext;
    }

//    public void exit() {
//
//        for (Activity activity : allActivities) {
//            activity.finish();
//        }
//
//        System.exit(0);
//
//    }
}
