package com.qf.rwxchina.xiaochefudriver.Home;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.Login.LoginActivity;
import com.qf.rwxchina.xiaochefudriver.MapHome.MapFragment;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.Order.BaoDanActivity;
import com.qf.rwxchina.xiaochefudriver.Order.OrderFragment;
import com.qf.rwxchina.xiaochefudriver.Order.OrderdetailsActivity;
import com.qf.rwxchina.xiaochefudriver.Order.PinTaiOrderActivity;
import com.qf.rwxchina.xiaochefudriver.Order.SettlementOrderActivity;
import com.qf.rwxchina.xiaochefudriver.Personal.fragment.PersonalFragment;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.State.StateActivity;
import com.qf.rwxchina.xiaochefudriver.State.StateFragment;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.BaseActivity;
import com.qf.rwxchina.xiaochefudriver.Utils.ExampleUtil;
import com.qf.rwxchina.xiaochefudriver.Utils.HttpInvoker;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;
import com.qf.rwxchina.xiaochefudriver.service.LocationService;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


/**
 * 主页，承载主题布局
 *
 * @author zhangkunlun
 *         2016/9/7
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    static final String[] PERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE,  //读取权限
            Manifest.permission.READ_PHONE_STATE,        //读取设备信息
            Manifest.permission.ACCESS_COARSE_LOCATION, //百度定位
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,//电话
//            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,//取消低电量模式限制
    };

    private FrameLayout mContent;   //中间内容
    private LinearLayout mMap;      //接单
    private LinearLayout mState;    //状态
    private LinearLayout mOrder;    //订单
    private LinearLayout mPersonal; //个人中心

    private MapFragment mMapFragment;
    private StateFragment mStateFragment;
    private OrderFragment mOrderFragment;
    private PersonalFragment mPersonalFragment;

    private FragmentManager mFragmentManager;//fragment管理器
    private FragmentTransaction mTransaction; //fragment事物

    public static boolean isForeground = true;
    private MessageReceiver mMessageReceiver;
    Dialog dialog;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    private static final String FILE_NAME = "usetInfo";
    private Intent service;

//    //版本号
//    int VerCode;
//    //版本名称
//    String VerName;
//    String url = "";
//    String description;
//    ProgressDialog m_progressDlg;

    String uid, status;
    Boolean islog;
    SharedPreferences sharedPreferences;
    int workstate;
    String m_newVerName; // 最新版的版本名
    String m_appNameStr; // 下载到本地要给这个APP命的名字
    Handler m_mainHandler;
    ProgressDialog m_progressDlg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication.getInstance().addActivity(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (MyApplication.isFirst){
            ifUpdate();
            MyApplication.isFirst = false;
        }

        mFragmentManager = getSupportFragmentManager();
        if(savedInstanceState != null){
            mMapFragment = (MapFragment) mFragmentManager.findFragmentByTag("mMapFragment");
            mStateFragment = (StateFragment) mFragmentManager.findFragmentByTag("mStateFragment");
            mOrderFragment = (OrderFragment) mFragmentManager.findFragmentByTag("mOrderFragment");
            mPersonalFragment = (PersonalFragment) mFragmentManager.findFragmentByTag("mPersonalFragment");
        }
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "");
        islog = sharedPreferences.getBoolean("isLogin", false);
        initView();
        JPushInterface.init(getApplicationContext());
        registerMessageReceiver();
        setAlias();

        //刚进入程序判断是否有未完成的订单
        if (islog) {
            if (MyApplication.isNo.equals("1")) {
                getDriverNotfinishedIndent();
            }
        }

        service = new Intent(getApplicationContext(), LocationService.class);
//        startService(service);

        //获取版本号
//        VerCode = getVerCode(this);
//        Log.e("wh", "当前版本号》》" + VerCode);
        //  update_for_driver();
//        Intent intent=getIntent();
//        if (intent!=null){
//            String tpye="";
//             tpye=intent.getStringExtra("tpye");
//            if (tpye.equals("1")||!tpye.equals("")){
//                setFragment(1);
//                MyApplication.is_kongxian="1";
//            }
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.i("MSG","走没走");
        uid = sharedPreferences.getString("uid", "");
        islog = sharedPreferences.getBoolean("isLogin", false);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getExtras() != null) {
                String sss = intent.getExtras().getString("info");
                if ("gotoState".equals(sss)) {
                    mMapFragment.setUserLocation();
                    changeBackGround(1);
                    setFragment(1);
                    MyApplication.is_kongxian="1";
                }
            }
        }

    }

    private void initView() {
        m_mainHandler = new Handler();
        m_progressDlg = new ProgressDialog(this);
        m_progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确
        m_progressDlg.setIndeterminate(false);
        mContent = (FrameLayout) findViewById(R.id.activity_main_content);
        mMap = (LinearLayout) findViewById(R.id.activity_main_map);
        mState = (LinearLayout) findViewById(R.id.activity_main_state);
        mOrder = (LinearLayout) findViewById(R.id.activity_main_order);
        mPersonal = (LinearLayout) findViewById(R.id.activity_main_personal);

        mMap.setOnClickListener(this);
        mState.setOnClickListener(this);
        mOrder.setOnClickListener(this);
        mPersonal.setOnClickListener(this);

        changeBackGround(0);
        setFragment(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_map:
                changeBackGround(0);
                setFragment(0);
                break;
            case R.id.activity_main_state:
                workstate = sharedPreferences.getInt("work_status", 1);
                //判断是否是空闲状态1：空闲  2：服务中
                if(workstate == 1) {
                    MyApplication.is_kongxian="0";
//                    Log.e("wh","执行了空闲");
                }else {
                    MyApplication.is_kongxian="1";
//                    Log.e("wh","执行了服务中");
                }
                changeBackGround(1);
                setFragment(1);
                break;
            case R.id.activity_main_order:
                if (islog) {
                    changeBackGround(2);
                    setFragment(2);
                } else {
                    Intent it = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.activity_main_personal:
                changeBackGround(3);
                setFragment(3);
                break;
        }
    }

    //点击修改相应的背景颜色
    private void changeBackGround(int i) {
        mMap.setBackgroundResource(R.color.colorBlack);
        mState.setBackgroundResource(R.color.colorBlack);
        mOrder.setBackgroundResource(R.color.colorBlack);
        mPersonal.setBackgroundResource(R.color.colorBlack);
        switch (i) {
            case 0:
                mMap.setBackgroundResource(R.color.colorOrange);
                break;
            case 1:
                mState.setBackgroundResource(R.color.colorOrange);
                break;
            case 2:
                mOrder.setBackgroundResource(R.color.colorOrange);
                break;
            case 3:
                mPersonal.setBackgroundResource(R.color.colorOrange);
                break;
        }
    }

    /**
     * 设置fragment
     *
     * @param i 2016/09/7
     * @auther zhangkunlun
     */
    public void setFragment(int i) {
//        Log.i("lkymsggg","123");
        mTransaction = mFragmentManager.beginTransaction();
        hideFragment(mTransaction);
        switch (i) {
            case 0:
                if (mMapFragment == null) {
                    mMapFragment = new MapFragment();
                    mTransaction.add(R.id.activity_main_content, mMapFragment,"mMapFragment");
                } else {
                    mTransaction.show(mMapFragment);
                }
                break;
            case 1:
                if (mStateFragment == null) {
                    mStateFragment = new StateFragment();
                    mTransaction.add(R.id.activity_main_content, mStateFragment,"mStateFragment");

                } else {
                    mTransaction.show(mStateFragment);
                    mStateFragment.driverinfo();

                }
                break;
            case 2:
                if (mOrderFragment == null) {
                    mOrderFragment = new OrderFragment();
                    mTransaction.add(R.id.activity_main_content, mOrderFragment,"mOrderFragment");
                } else {
                    mTransaction.show(mOrderFragment);
                }
                break;
            case 3:
                if (mPersonalFragment == null) {
                    mPersonalFragment = new PersonalFragment();
                    mTransaction.add(R.id.activity_main_content, mPersonalFragment,"mPersonalFragment");
                } else {
                    mTransaction.show(mPersonalFragment);
                }
                break;
        }
        if (mFragmentManager.getFragments()!=null){
//            Log.i("hrr","mTransaction="+mFragmentManager.getFragments().toString());
        }
        mTransaction.commit();
    }

    /**
     * 隐藏所有Fragment
     *
     * @param mTransaction 2016/09/7
     * @auther zhangkunlun
     */
    private void hideFragment(FragmentTransaction mTransaction) {
        if (mMapFragment != null) {
            mTransaction.hide(mMapFragment);
        }
        if (mStateFragment != null) {
            mTransaction.hide(mStateFragment);
        }
        if (mOrderFragment != null) {
            mTransaction.hide(mOrderFragment);
        }
        if (mPersonalFragment != null) {
            mTransaction.hide(mPersonalFragment);
        }
//        mTransaction.commit();
    }


    /**
     * 权限
     */
    @Override
    public void getAllGrantedPermission() {
        //当获取到所需权限后，进行相关业务操作
        super.getAllGrantedPermission();
    }

    @Override
    public String[] getPermissions() {
        return PERMISSION;
    }

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        try{
            registerReceiver(mMessageReceiver, filter);
        }catch (Exception e){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
           MyApplication.isNo="1";
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                //TODO:操作处理
                setCostomMsg(showMsg.toString());
            }
        }
    }

    private void setCostomMsg(String msg) {
//        if (null != msgText) {
//            msgText.setText(msg);
//            msgText.setVisibility(android.view.View.VISIBLE);
//        }
    }

    //设置用户的别名
    private void setAlias() {

        String alias = uid;
        if (TextUtils.isEmpty(alias)) {
            return;
        }
        if (!ExampleUtil.isValidTagAndAlias(alias)) {
            return;
        }
        handler.sendMessage(handler.obtainMessage(100, alias));
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
//                    Log.e("kunlun", "别名设置成功,别名=" + alias);
                    break;
                case 6002:
//                    Log.e("kunlun", "别名设置失败");
                    handler.sendMessageDelayed(handler.obtainMessage(100, alias), 1000 * 60);
                    break;
            }
        }
    };

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;
                default:
//                    Log.e("kunlun", "Unhandled msg - " + msg.what);
            }
        }
    };

    //下班
    public void offwork() {
        mMapFragment.HttpGetOffWork();
    }

    /**
     * 司机是否有未完成的订单
     */
    public  void getDriverNotfinishedIndent() {
//        Log.e("wh","是否有未完成的订单="+HttpPath.getDriverNotfinishedIndent+"/driverID/"+uid);
        if (Network.HttpTest(MainActivity.this)) {
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.getDriverNotfinishedIndent)
                                    .addParam("driverID", uid)
                                    .build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();
                                        Log.e("wh", "是否有未完成的订单》》" + result);
                                        if (result != null) {
                                            //将得到的json数据返回给HashMap
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
//                                            Log.e("wh","map="+map.toString());
                                            HashMap<String, String> list_msg = AnalyticalJSON.getHashMap(map.get("state"));
                                            HashMap<String, String> list_data = AnalyticalJSON.getHashMap(map.get("data"));
                                            HashMap<String, String> list_userInfo = AnalyticalJSON.getHashMap(list_data.get("userInfo"));
                                            Log.e("wh","code="+list_msg.get("code"));
                                            if (list_msg.get("code").equals("0")) {
                                                   Intent intent;
                                                   //当前订单号
                                                   MyApplication.orderson = list_data.get("orderson");
                                                   //等待的时间
                                                   MyApplication.time = Integer.parseInt(list_data.get("waittime"));
                                                Log.e("wh","退出后在进来的时间是YYYYYYY==="+ MyApplication.time);
                                                   //等待费
                                                   MyApplication.waitmoney = list_data.get("waitmoney");
                                                   //起步公里
                                                   MyApplication.maxnumber = list_data.get("_maxnumber");
                                                   //超出公里数——起步价
                                                   MyApplication.money = list_data.get("_money");
                                                   //超出的公里数
                                                   MyApplication.addmoney = list_data.get("_addmoney");
                                                   //等待时间段
                                                  MyApplication.waittimeduan = list_data.get("_waittimeduan");
                                                   //行驶公里
                                                   MyApplication.distance = list_data.get("distance");
                                                //总价格
                                                MyApplication.balance = list_data.get("original_totalmoney");
                                                //起步价
                                                MyApplication.startmoney = list_data.get("startmoney");
                                                //此订单公里费
                                                MyApplication.distancemoney = list_data.get("distancemoney");
                                                //超出初始里程公里数
                                                MyApplication.maxnumber_exc = list_data.get("_maxnumber_exc");
                                                // 超出等待时间段时间
                                                MyApplication.waittimeduan_exc = list_data.get("_waittimeduan_exc");
                                                      String time02=list_data.get("overwait_time");
                                                if (!time02.equals("")||time02.equals("0")){
                                                    MyApplication.overwait_time = Integer.parseInt(list_data.get("overwait_time"));
                                                }else {
                                                    MyApplication.overwait_time=0;
                                                }


                                                if (list_userInfo!=null){
                                                    //姓名
                                                    MyApplication.name = list_userInfo.get("name");

                                                    //判断是否是商户
                                                    MyApplication.type_pingtai = list_userInfo.get("type_id");
                                                    //头像
                                                    MyApplication.userurl = list_userInfo.get("userurl");
                                                }else {
                                                    //姓名
                                                    MyApplication.name = "游客";

                                                    //头像
                                                    MyApplication.userurl = "";
                                                }



                                                   status = list_data.get("status");
                                                Log.e("wh","status="+status);
                                                if (status.equals("0")){
                                                    intent = new Intent(MainActivity.this, PinTaiOrderActivity.class);
                                                    startActivity(intent);
                                                }
                                                else   if (status.equals("2")) {
//                                                       Log.e("wh", "司机接单》》》");
                                                       intent = new Intent(MainActivity.this, OrderdetailsActivity.class);
                                                       startActivity(intent);
                                                       MyApplication.n = 0;
                                                   } else if (status.equals("3")) {
//                                                       Log.e("wh", "司机等待中》》》");
                                                       intent = new Intent(MainActivity.this, StateActivity.class);
                                                       startActivity(intent);
                                                       MyApplication.n = 1;
                                                   } else if (status.equals("4")) {
//                                                       Log.e("wh", "司机代驾中》》》");
                                                       intent = new Intent(MainActivity.this, StateActivity.class);
                                                       startActivity(intent);
                                                       MyApplication.n = 2;
                                                       // 写入
                                                       SharedPreferences.Editor editor = sharedPreferences.edit();
                                                       //判断是否登录，登录过后改为true
                                                       editor.putString("nnn", "2");
                                                       editor.commit();

                                                   } else if (status.equals("5")) {
//                                                       Log.e("wh", "司机代驾结束》》》");
                                                       intent = new Intent(MainActivity.this, StateActivity.class);
                                                       MyApplication.n = 3;
                                                       startActivity(intent);
                                                   } else if (status.equals("6")) {
//                                                       Log.e("limming", "用户已支付》》》");
                                                       intent = new Intent(MainActivity.this, SettlementOrderActivity.class);
                                                       startActivity(intent);
                                                       MyApplication.n = 4;
                                                       MyApplication.orderState = 2;
                                                       Log.e("limming", "用户已支付》》》"+MyApplication.orderState);
                                                   }else if (status.equals("10")) {
//                                                       Log.e("wh", "司机报单");
                                                       intent = new Intent(MainActivity.this, BaoDanActivity.class);
                                                       startActivity(intent);

                                                   }
                                            }
                                        } else {

                                            Toast.makeText(MainActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                }
                            });
        }


    }






    /**
     * 获取版本名称
     *
     * @param context
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    "com.qf.rwxchina.xiaochefudriver", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
//            Log.e("wh", e.getMessage());
        }
        return verName;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            dialog = new Dialog(MainActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);

            View view_exi = LayoutInflater.from(MainActivity.this).inflate(
                    R.layout.dialog_judge, null);
            TextView title_exit = (TextView) view_exi.findViewById(R.id.title);
            title_exit.setText("是否退出小车夫");
            view_exi.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
//                    AppManager.getAppManager().finishAllActivity();
//                    ActivityManager manager = (ActivityManager)MainActivity.this.getSystemService(ACTIVITY_SERVICE); //获取应用程序管理器
//                    manager.killBackgroundProcesses(getPackageName());//杀死进程
                    MyApplication.getInstance().exitApp();
//                    System.exit(0);

//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    intent.addCategory(Intent.CATEGORY_HOME);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                    android.os.Process.killProcess(android.os.Process.myPid());
//                    finish();
//                    MyApplication.getInstance().exitApp();
                }
            });
            view_exi.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setContentView(view_exi);
            dialog.show();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }
    Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String name= (String) msg.obj;
            String vername=null;
            String down=null;
            if (name!=null){
                try {
                    JSONObject jsonObject=new JSONObject(name);
                    m_newVerName=jsonObject.getString("etition");
                    down=jsonObject.getString("downloadurl");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (m_newVerName!=null&&down!=null){
//                Log.i("hrr","vername="+m_newVerName+" getvername="+getVerName(MainActivity.this)+"  down="+down);
                if (!m_newVerName.equals(getVerName(MainActivity.this))){//判断服务器apk版本号是否和本地相同
                    m_appNameStr = "XCFDriver.apk";
                    doNewVersionUpdate(down);
                }
            }

        }
    };
    /**
     * 检查是否需要更新
     */
    private void ifUpdate(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String version= HttpInvoker.HttpGetMethod(HttpPath.UPDATEURL);
                Message msg=new Message();
                msg.obj=version;
                mhandler.sendMessage(msg);
            }
        }).start();
    }
    /**
     * 告诉HANDER已经下载完成了，可以安装了
     */
    private void down() {
        m_mainHandler.post(new Runnable() {
            public void run() {
//                Log.i("lkymsg","321321");
                m_progressDlg.cancel();
                update();
            }
        });
    }

    /**
     * 安装程序
     */
    void update() {
        File file = new File(Environment.getExternalStorageDirectory()
                , m_appNameStr);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 以新压入栈
        intent.addCategory("android.intent.category.DEFAULT");
        Uri abc = Uri.fromFile(file);
        intent.setDataAndType(abc, "application/vnd.android.package-archive");
        startActivity(intent);
    }
    /**
     * 提示更新新版本
     *
     * @param
     */
    private void doNewVersionUpdate(final String loadurl) {

        String verName = getVerName(this);
        String str = "当前版本：" + verName + " ,发现新版本：" + m_newVerName + " ,是否更新？";
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle("软件更新")
                .setMessage(str)
                // 设置内容
                .setPositiveButton("更新",// 设置确定按钮
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                m_progressDlg.setTitle("正在下载");
                                m_progressDlg.setMessage("请稍候...");
                                downFile(loadurl); // 开始下载
                            }
                        })
                .setNegativeButton("暂不更新",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                //关闭对话框
                                dialog.dismiss();
                                // 点击"取消"按钮之后退出程序
                                System.exit(0);
                            }
                        }).create();// 创建
        // 显示对话框
        dialog.show();
    }

    /**
     * 下载安装包
     * @param url
     */
    private void downFile(final String url) {
        m_progressDlg.show();
        new Thread() {
            public void run() {

                HttpResponse response;
                try {
                    URL url1 = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                    long length = connection.getContentLength();
//                    Log.i("hrr","length="+length);
                    m_progressDlg.setMax((int) length);// 设置进度条的最大值

                    InputStream is = (InputStream) connection.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(
                                Environment.getExternalStorageDirectory()
                                , m_appNameStr);
                        Log.e("hrr","路径="+file.getPath());
//                        File file = new File(
//                                getContext().getPackageResourcePath()
//                                );
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[4154];
                        int ch = -1;
                        int count = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            count += ch;
                            if (length > 0) {
                                m_progressDlg.setProgress(count);
                            }
                        }
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
//                    Log.i("lkymsg","123123");

                    down(); // 告诉HANDER已经下载完成了，可以安装了
                } catch (IOException e) {
//                    Log.i("hrr","IOException="+e.toString());
                    e.printStackTrace();
                } catch (Exception e) {
//                    Log.i("hrr","Exception="+e.toString());
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
