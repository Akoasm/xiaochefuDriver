package com.qf.rwxchina.xiaochefudriver.Order;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.orhanobut.logger.Logger;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.Bean.OrderInfo;
import com.qf.rwxchina.xiaochefudriver.Bean.UserInfo;
import com.qf.rwxchina.xiaochefudriver.Home.MainActivity;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.State.StateActivity;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.LocationXY;
import com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkStringCallBack;
import com.qf.rwxchina.xiaochefudriver.Utils.progressutils.LoadDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

/**
 * 抢单信息
 */
public class RobOrderActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack;
    private TextView mDistance;
    private TextView mName;
    private TextView mCustomerType;
    private TextView mOrderType;
    private TextView mTime;
    private TextView mStart;
    private TextView mEnd;
    private RelativeLayout mRobOrder;
    private LinearLayout mIsType;
    private ImageView mGround;
    private TextView mTimeing;

    private int time = 30;
    private String orderson;
    private int ordertype;
    private OrderInfo orderInfo = new OrderInfo();
    private UserInfo userInfo = new UserInfo();
    private String driverid, pingtai;

    StateActivity stateActivity = new StateActivity();
    private double lat;
    private double lng;
    MediaPlayer mediaPlayer;

    //振动器实例化
    private Vibrator mVibrator1;
    private ProgressDialog dialog;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rob_order);
        dialog = new ProgressDialog(this);
        MyApplication.getInstance().addActivity(this);
        dialog=new ProgressDialog(this);
        MyApplication.isNo = "2";
        MyApplication.n = -1;
        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        driverid = sp.getString("uid", "");
        Log.e("wh", "司机ID》》" + driverid);

//        mVibrator1 = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
////震动指定时间
//        mVibrator1.vibrate(10000);
//
////等待100ms后，按数组所给数值间隔震动；其后为重复次数，-1为不重复，0一直震动
//        mVibrator1.vibrate(new long[]{100, 10, 100, 1000}, 0);
        init();
        mediaPlayer.start();
        getIntentInfo();
        //订单信息
        NetRequest();
        //抢单倒计时
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(mVibrator1!=null){
//            //取消震动
//            mVibrator1.cancel();
//        }
    }

    //获取订单子编号、派单类型1：平台派单
    private void getIntentInfo() {
        //获取当前位置的经纬度
        Location location = new LocationXY().init(getApplicationContext());
        if (location != null || location.getLatitude() != 0.01 || location.getLongitude() != 0.0 || location.getLatitude() != 0 || location.getLongitude() != 0) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        }
        Intent intent = getIntent();
        orderson = MyApplication.orderson;
        MyApplication.type = intent.getStringExtra("pingtai");
        Log.e("wh", "派单类型》》" + MyApplication.type);
    }

    //请求订单数据
    private void NetRequest() {
        dialog.show();
        Log.i("limming", "orderson=" + orderson + "lng=" + lng + "lat=" + lat);
        stateActivity.showDialog(RobOrderActivity.this, "正在获取订单");
        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(this)
                .doPostAsync(
                        HttpInfo.Builder().setUrl(HttpPath.ORDER_DETAILS_PATH)
                                .addParam("driverID", driverid)
                                .addParam("orderson", orderson)
                                .addParam("lng", lng + "")
                                .addParam("lat", lat + "")
                                .build(),
                        new CallbackOk() {
                            @Override
                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()) {
                                    //获取到数据
                                    String result = info.getRetDetail();
                                    Logger.t("dingdan").e(result);
                                    if (result != null) {

                                        try {
                                            JSONObject object = new JSONObject(result);
                                            JSONObject obj = new JSONObject(object.optString("data"));
                                            orderInfo.setOut_trade_no(obj.optString("out_trade_no"));
                                            orderInfo.setOrderson(obj.optString("orderson"));
                                            orderInfo.setSaddress(obj.optString("saddress"));
                                            orderInfo.setBespeaktime(obj.optString("bespeaktime"));
                                            orderInfo.setOaddress(obj.optString("oaddress"));
                                            orderInfo.setIndentdistance(obj.optString("indentdistance"));
                                            orderInfo.setOrdertype(Integer.parseInt(obj.optString("ordertype")));
                                            orderInfo.setBespeaktime(obj.optString("createtime"));
                                            JSONObject obj2 = new JSONObject(obj.optString("userInfo"));
                                            userInfo.setName(obj2.optString("name"));
                                            userInfo.setType_id(obj2.optInt("type_id"));
                                            Log.e("limming", "距离》" + orderInfo.getIndentdistance());
                                            changeInfo();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
        stateActivity.dismissDialog();

    }

    //修改信息
    private void changeInfo() {
        mDistance.setText("距离你:" + orderInfo.getIndentdistance());
        mName.setText(userInfo.getName());
        mTime.setText(orderInfo.getBespeaktime());
        mStart.setText(orderInfo.getSaddress());
        mEnd.setText(orderInfo.getOaddress());

        //订单类型
        if (orderInfo.getOrdertype() == 0) {
            mOrderType.setText("我要司机");

        } else {
            mOrderType.setText("我要代驾");
        }


        //客户类型
        if (userInfo.getType_id() == 1) {
            mDistance.setBackgroundResource(R.drawable.background_up);
            mGround.setImageResource(R.drawable.icon_yeellow_garden);
            mCustomerType.setText("普通用户");
            MyApplication.type_pingtai = "1";
        } else if (userInfo.getType_id() == 2) {
            mDistance.setBackgroundResource(R.drawable.background_up_blue);
            mGround.setImageResource(R.drawable.icon_blue_garden);
            mIsType.setVisibility(View.GONE);
            mCustomerType.setText("商户");
            MyApplication.type_pingtai = "0";
        } else if (userInfo.getType_id() == 3) {
            mDistance.setBackgroundResource(R.drawable.background_up_blue);
            mGround.setImageResource(R.drawable.icon_blue_garden);
            mIsType.setVisibility(View.GONE);
            mCustomerType.setText("平台直接下单");
            MyApplication.type_pingtai = "3";
        }

        dialog.dismiss();
    }


    //倒计时
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time--;
            Message msg = handler.obtainMessage();
            msg.arg1 = time;
            handler.sendMessage(msg);
        }
    };


    // 更新界面
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Log.e("wh", "秒数" + msg.arg1);
            if (msg.arg1 == 0) {
                stateActivity.dismissDialog();
                MyApplication.orderState = 0;
                finish();
                Log.e("wh", "时间满了状态返回》》" + MyApplication.orderState);
            } else {
                mTimeing.setText("抢单\n" + msg.arg1 + "s");
                handler.postDelayed(runnable, 1000);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer!=null){
                    try {
                        mediaPlayer.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0,5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
        try{
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            Logger.t("RobOrderActivity").e("结束了");
        }catch(Exception e){
            Logger.t("RobOrderActivity").e("异常="+e.getMessage());
            e.printStackTrace();
        }
    }

    private void init() {
        mediaPlayer = MediaPlayer.create(RobOrderActivity.this, R.raw.message);
        mBack = (ImageView) findViewById(R.id.activity_rob_order_back);
        mBack.setOnClickListener(this);
        mDistance = (TextView) findViewById(R.id.activity_rob_order_distance);
        mIsType = (LinearLayout) findViewById(R.id.activity_rob_order_isType);
        mName = (TextView) findViewById(R.id.activity_rob_order_name);
        mCustomerType = (TextView) findViewById(R.id.activity_rob_order_customertype);
        mOrderType = (TextView) findViewById(R.id.activity_rob_order_ordertype);
        mTime = (TextView) findViewById(R.id.activity_rob_order_time);
        mStart = (TextView) findViewById(R.id.activity_rob_order_start);
        mEnd = (TextView) findViewById(R.id.activity_rob_order_end);
        mRobOrder = (RelativeLayout) findViewById(R.id.activity_rob_order_makeorder);
        mRobOrder.setOnClickListener(this);
        mGround = (ImageView) findViewById(R.id.activity_rob_orer_background);
        mTimeing = (TextView) findViewById(R.id.activity_rob_orer_timeing);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(RobOrderActivity.this, "请打开GPS", Toast.LENGTH_SHORT).show();
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("请打开GPS连接");
            dialog.setMessage("为方便使你更好的抢单，请先打开GPS");
            dialog.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // 转到手机设置界面，用户设置GPS

                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    Toast.makeText(RobOrderActivity.this, "打开后直接点击返回键即可，若不打开返回下次将再次出现", Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                }
            });
            dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {


//            Intent intent = new Intent();
//            String packageName = this.getPackageName();
//            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//            if (pm.isIgnoringBatteryOptimizations(packageName)){
////                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
////                Log.i("lkymsg","111111111111111");
//
//            }
//            else {
//                Toast.makeText(RobOrderActivity.this, "请关闭电池优化", Toast.LENGTH_SHORT).show();
//                final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                dialog.setTitle("请关闭电池优化");
//                dialog.setMessage("为方便使你更好的抢单，请关闭电池优化");
//                dialog.setPositiveButton("设置", new android.content.DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//
//                        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//                        Toast.makeText(RobOrderActivity.this, "打开后直接点击返回键即可，若不打开返回下次将再次出现", Toast.LENGTH_SHORT).show();
//                        startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
//                    }
//                });
//                dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        arg0.dismiss();
//                    }
//                });
//                dialog.show();
//            }
////            startActivity(intent);

            agreeIndent();


        }
    }

    /**
     * 抢单
     */
    private void agreeIndent() {
//        LoadDialog loadDialog=new LoadDialog(this);
//        loadDialog.setContent("抢单中...");
        Map<String,String> map=new HashMap<>();
        map.put("orderson",orderson);
        map.put("driverID",driverid);
        map.put("isSend",MyApplication.type);
        com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkHttpUtil.post(HttpPath.agreeIndent, map, this, new OkStringCallBack(this) {
            @Override
            public void myError(Call call, Exception e, int id) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void myResponse(String response, int id) {
                //获取到数据
                String result = response;
                Logger.t("抢单").e(result);
                if (result != null) {
                    HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                    HashMap<String, String> list_msg = AnalyticalJSON.getHashMap(map.get("state"));
                    if (list_msg.get("code").equals("0")) {
                        mediaPlayer.stop();
                        timer.cancel();
                        mediaPlayer.release();
                        handler.removeCallbacks(runnable);
                        MyApplication.orderState = 1;
                        Log.e("wh", "抢单成功》》" + MyApplication.orderState);
                        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                        // 写入
                        SharedPreferences.Editor editor = sp.edit();
                        //更改上班状态
                        editor.putInt("from", 0);
                        //提交
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(), OrderdetailsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        handler.removeCallbacks(runnable);
                        MyApplication.orderState = 0;
                        Log.e("wh", "抢单失败》》" + MyApplication.orderState);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(RobOrderActivity.this, list_msg.get("msg"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        stateActivity.showDialog(RobOrderActivity.this, "抢单中..");
//        OkHttpUtil.Builder()
//                .setCacheLevel(CacheLevel.FIRST_LEVEL)
//                .setConnectTimeout(25).build(this)
//                .doPostAsync(
//                        HttpInfo.Builder().setUrl(HttpPath.agreeIndent)
//                                .addParam("orderson", orderson)
//                                .addParam("driverID", driverid)
//                                .addParam("isSend", MyApplication.type)
//                                .build(),
//                        new CallbackOk() {
//                            @Override
//                            public void onResponse(HttpInfo info) throws IOException {
//                                if (info.isSuccessful()) {
//                                    //获取到数据
//                                    String result = info.getRetDetail();
//                                    Logger.t("抢单").e(result);
//                                    if (result != null) {
//                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
//                                        HashMap<String, String> list_msg = AnalyticalJSON.getHashMap(map.get("state"));
//                                        if (list_msg.get("code").equals("0")) {
//                                            mediaPlayer.stop();
//                                            timer.cancel();
//                                            mediaPlayer.release();
//                                            handler.removeCallbacks(runnable);
//                                            MyApplication.orderState = 1;
//                                            Log.e("wh", "抢单成功》》" + MyApplication.orderState);
//                                            SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
//                                            // 写入
//                                            SharedPreferences.Editor editor = sp.edit();
//                                            //更改上班状态
//                                            editor.putInt("from", 0);
//                                            //提交
//                                            editor.commit();
//                                            Intent intent = new Intent(getApplicationContext(), OrderdetailsActivity.class);
//                                            startActivity(intent);
//                                            finish();
//                                        } else {
//                                            handler.removeCallbacks(runnable);
//                                            MyApplication.orderState = 0;
//                                            Log.e("wh", "抢单失败》》" + MyApplication.orderState);
//                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                            startActivity(intent);
//                                            finish();
//                                            Toast.makeText(RobOrderActivity.this, list_msg.get("msg"), Toast.LENGTH_SHORT).show();
//
//                                        }
//
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
//                                    }
//                                } else {
//                                    Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//        stateActivity.dismissDialog();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_rob_order_back:
                handler.removeCallbacks(runnable);
                MyApplication.orderState = 0;
                Log.e("wh", "返回状态》》" + MyApplication.orderState);
                SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                // 写入
                SharedPreferences.Editor editor = sp.edit();
                //更改上班状态
                editor.putInt("work_status", 1);
                //提交
                editor.commit();
                finish();
                break;
            case R.id.activity_rob_order_makeorder:
                initGPS();
//                agreeIndent();
                break;
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            handler.removeCallbacks(runnable);
            MyApplication.orderState = 0;
            Log.e("wh", "返回状态》》" + MyApplication.orderState);
            SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
            // 写入
            SharedPreferences.Editor editor = sp.edit();
            //更改上班状态
            editor.putInt("work_status", 1);
            //提交
            editor.commit();
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }
}

