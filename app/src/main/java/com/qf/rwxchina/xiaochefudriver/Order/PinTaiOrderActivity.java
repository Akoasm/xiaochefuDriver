package com.qf.rwxchina.xiaochefudriver.Order;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/14.
 * 平台派单
 */
public class PinTaiOrderActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack;
    private TextView mDistance;
    private TextView mName;
    private TextView mCustomerType;
    private TextView mOrderType;
    private TextView mTime;
    private TextView mEnd;
    private TextView mStart;
    TextView p_ok, p_no;
    private LinearLayout mIsType;
    private String orderson, pingtai, driverid;
    private OrderInfo orderInfo = new OrderInfo();
    private UserInfo userInfo = new UserInfo();
    StateActivity stateActivity = new StateActivity();
    private SharedPreferences sp;
    private double lat;
    private double lng;
    MediaPlayer mediaPlayer;
    private String name, nametype = "";
    private ProgressDialog pd, progressDialog;
    //振动器实例化
    private Vibrator mVibrator1;

private Timer timer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pingtai_order);
        MyApplication.getInstance().addActivity(this);
        MyApplication.isNo = "2";
        MyApplication.n = -1;
//        mVibrator1 = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
////震动指定时间
//        mVibrator1.vibrate(10000);
////等待100ms后，按数组所给数值间隔震动；其后为重复次数，-1为不重复，0一直震动
//        mVibrator1.vibrate(new long[]{100, 10, 100, 1000}, 0);
        init();
        EventBus.getDefault().register(this);
//        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        getIntentInfo();
        NetRequest();
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
            mediaPlayer=null;
        }catch(Exception e){
            Logger.t("PinTaiOrderActivity").e("异常="+e.getMessage());
            e.printStackTrace();
        }
        EventBus.getDefault().unregister(this);
    }

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
    protected void onStop() {
        super.onStop();
//        if (mVibrator1 != null) {
//            //取消震动
//            mVibrator1.cancel();
//        }
    }

    public void init() {
        progressDialog = new ProgressDialog(this);
        mediaPlayer = MediaPlayer.create(PinTaiOrderActivity.this, R.raw.message);
        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        driverid = sp.getString("uid", "");
        mBack = (ImageView) findViewById(R.id.activity_rob_order_back);
        mDistance = (TextView) findViewById(R.id.activity_rob_order_distance);
        mIsType = (LinearLayout) findViewById(R.id.activity_rob_order_isType);
        mName = (TextView) findViewById(R.id.activity_rob_order_name);
        mCustomerType = (TextView) findViewById(R.id.activity_rob_order_customertype);
        mOrderType = (TextView) findViewById(R.id.activity_rob_order_ordertype);
        mTime = (TextView) findViewById(R.id.activity_rob_order_time02);
        mStart = (TextView) findViewById(R.id.activity_rob_order_start);
        mEnd = (TextView) findViewById(R.id.activity_rob_order_end);
        p_ok = (TextView) findViewById(R.id.p_ok);
//        p_no = (TextView) findViewById(R.id.p_no);
        mBack.setOnClickListener(this);
        p_ok.setOnClickListener(this);
//        p_no.setOnClickListener(this);

    }

    //获取订单子编号、派单类型1：平台派单
    private void getIntentInfo() {
        //获取当前位置的经纬度
        Location location = new LocationXY().init(getApplicationContext());
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        }
        Intent intent = getIntent();
        orderson = MyApplication.orderson;
        MyApplication.type = intent.getStringExtra("pingtai");
        MyApplication.type = sp.getString("type", "");
        Log.e("limming", "派单类型》》" + MyApplication.type);
    }


    //请求订单数据
    private void NetRequest() {
        Map<String,String> params=new HashMap<>();
        params.put("driverID", driverid);
        params.put("orderson", orderson);
        params.put("lng", lng + "");
        params.put("lat", lat + "");
        com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkHttpUtil.post(HttpPath.ORDER_DETAILS_PATH, params, this, new OkStringCallBack(this) {
            @Override
            public void myError(Call call, Exception e, int id) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void myResponse(String response, int id) {
                //获取到数据
                String result = response;
                if (result != null) {
                    progressDialog.dismiss();
                    Log.e("kunlun", result);
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
                        name = obj.optString("uname");
                        JSONObject obj2 = new JSONObject(obj.optString("userInfo"));
                        userInfo.setName(obj2.optString("name"));
                        nametype = userInfo.getName();
                        userInfo.setType_id(obj2.optInt("type_id"));
                        Log.e("lmming", "距离》" + orderInfo.getIndentdistance() + "米");
                        changeInfo();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        progressDialog.show();
//        Log.i("limming", "lng=" + lng + "  " + "lat" + lat);
//        stateActivity.showDialog(PinTaiOrderActivity.this, "正在获取订单");
//        OkHttpUtil.Builder()
//                .setCacheLevel(CacheLevel.FIRST_LEVEL)
//                .setConnectTimeout(25).build(this)
//                .doPostAsync(
//                        HttpInfo.Builder().setUrl(HttpPath.ORDER_DETAILS_PATH)
//                                .addParam("driverID", driverid)
//                                .addParam("orderson", orderson)
//                                .addParam("lng", lng + "")
//                                .addParam("lat", lat + "")
//                                .build(),
//                        new CallbackOk() {
//                            @Override
//                            public void onResponse(HttpInfo info) throws IOException {
//                                if (info.isSuccessful()) {
//                                    //获取到数据
//                                    String result = info.getRetDetail();
//                                    if (result != null) {
//                                        progressDialog.dismiss();
//                                        Log.e("kunlun", result);
//                                        try {
//                                            JSONObject object = new JSONObject(result);
//                                            JSONObject obj = new JSONObject(object.optString("data"));
//                                            orderInfo.setOut_trade_no(obj.optString("out_trade_no"));
//                                            orderInfo.setOrderson(obj.optString("orderson"));
//                                            orderInfo.setSaddress(obj.optString("saddress"));
//                                            orderInfo.setBespeaktime(obj.optString("bespeaktime"));
//                                            orderInfo.setOaddress(obj.optString("oaddress"));
//                                            orderInfo.setIndentdistance(obj.optString("indentdistance"));
//                                            orderInfo.setOrdertype(Integer.parseInt(obj.optString("ordertype")));
//                                            orderInfo.setBespeaktime(obj.optString("createtime"));
//                                            name = obj.optString("uname");
//                                            JSONObject obj2 = new JSONObject(obj.optString("userInfo"));
//                                            userInfo.setName(obj2.optString("name"));
//                                            nametype = userInfo.getName();
//                                            userInfo.setType_id(obj2.optInt("type_id"));
//                                            Log.e("lmming", "距离》" + orderInfo.getIndentdistance() + "米");
//                                            changeInfo();
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
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

    //修改信息
    private void changeInfo() {
        mDistance.setText("平台派单\n距离你:" + orderInfo.getIndentdistance());
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

        if (userInfo.getType_id() == 1) {
            mDistance.setBackgroundResource(R.drawable.background_up);
//            p_ok.setBackgroundResource(R.drawable.icon_yeellow_garden);
//            p_no.setBackgroundResource(R.drawable.icon_yeellow_garden);
            mCustomerType.setText("普通用户");
            MyApplication.type_pingtai = "1";
        } else if (userInfo.getType_id() == 2) {
            mDistance.setBackgroundResource(R.drawable.background_up_blue);
//            p_ok.setBackgroundResource(R.drawable.icon_blue_garden);
//            p_no.setBackgroundResource(R.drawable.icon_blue_garden);
//            mIsType.setVisibility(View.GONE);
            mCustomerType.setText("商户");
            MyApplication.type_pingtai = "0";
        } else if (userInfo.getType_id() == 3) {
            mDistance.setBackgroundResource(R.drawable.background_up_blue);
//            p_ok.setBackgroundResource(R.drawable.icon_blue_garden);
//            p_no.setBackgroundResource(R.drawable.icon_blue_garden);
//            mIsType.setVisibility(View.GONE);
            mName.setText(name);
            mCustomerType.setText(nametype);
            MyApplication.type_pingtai = "3";
        }
    }

    /*
      *  * 抢单
      */
    private void agreeIndent() {
//        showDialog(PinTaiOrderActivity.this, "抢单中..");
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
                Log.e("wh", result + "<<<");
                if (result != null) {
                    HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                    HashMap<String, String> list_msg = AnalyticalJSON.getHashMap(map.get("state"));
                    if (list_msg.get("code").equals("0")) {
                        MyApplication.orderState = 1;
                        Log.e("wh", "抢单成功》》" + MyApplication.orderState);
                        mediaPlayer.stop();
                        timer.cancel();
                        mediaPlayer.release();
//                      写入
//                      SharedPreferences.Editor editor = sp.edit();
//                      更改订单状态
//                      editor.putInt("from", 0);
//                      提交
//                      editor.commit();
                        Intent intent = new Intent(getApplicationContext(), OrderdetailsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        MyApplication.orderState = 0;
                        Log.e("wh", "抢单失败》》" + MyApplication.orderState);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(PinTaiOrderActivity.this, list_msg.get("msg"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
//                                dismissDialog();
//                                if (info.isSuccessful()) {
//                                    //获取到数据
//                                    String result = info.getRetDetail();
//                                    Log.e("wh", result + "<<<");
//                                    if (result != null) {
//                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
//                                        HashMap<String, String> list_msg = AnalyticalJSON.getHashMap(map.get("state"));
//                                        if (list_msg.get("code").equals("0")) {
//                                            MyApplication.orderState = 1;
//                                            Log.e("wh", "抢单成功》》" + MyApplication.orderState);
//                                            mediaPlayer.stop();
//                                            timer.cancel();
//                                            mediaPlayer.release();
////                                            // 写入
////                                            SharedPreferences.Editor editor = sp.edit();
////                                            //更改订单状态
////                                            editor.putInt("from", 0);
////                                            //提交
////                                            editor.commit();
//                                            Intent intent = new Intent(getApplicationContext(), OrderdetailsActivity.class);
//                                            startActivity(intent);
//                                            finish();
//                                        } else {
//                                            MyApplication.orderState = 0;
//                                            Log.e("wh", "抢单失败》》" + MyApplication.orderState);
//                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                            startActivity(intent);
//                                            Toast.makeText(PinTaiOrderActivity.this, list_msg.get("msg"), Toast.LENGTH_SHORT).show();
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

    }


    /*
    *拒绝订单
     */
    private void cancel() {
        showDialog(PinTaiOrderActivity.this, "拒绝中....");
        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(this)
                .doPostAsync(
                        HttpInfo.Builder().setUrl(HttpPath.rejectIndent)
                                .addParam("orderson", orderson)
                                .addParam("driverID", driverid)
                                .build(),
                        new CallbackOk() {
                            @Override
                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()) {
                                    //获取到数据
                                    String result = info.getRetDetail();
                                    Log.e("wh", result + "<<<");
                                    if (result != null) {
                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                        HashMap<String, String> list_msg = AnalyticalJSON.getHashMap(map.get("state"));
                                        if (list_msg.get("code").equals("0")) {
                                            MyApplication.orderState = 0;
                                            SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                                            // 写入
                                            SharedPreferences.Editor editor = sp.edit();
                                            //更改上班状态
                                            editor.putInt("work_status", 1);
                                            //提交
                                            editor.commit();

//                                            Intent intent = new Intent(PinTaiOrderActivity.this,MainActivity.class);
//                                            startActivity(intent);
                                            finish();

                                        } else {

                                            Toast.makeText(PinTaiOrderActivity.this, list_msg.get("msg"), Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
        dismissDialog();

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.activity_rob_order_back:
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

            //接受
            case R.id.p_ok:
                agreeIndent();
                break;

//            //拒绝
//            case R.id.p_no:
//                cancel();
//                break;
        }

    }

    //自定义加载框
    public void showDialog(Context context, String msg) {
        if (pd == null) {
            pd = new ProgressDialog(context, R.style.Theme_CustomDialog);
            pd.setCanceledOnTouchOutside(false);
        }
        pd.setMessage(msg);
        if (!pd.isShowing()) {
            pd.show();
        }
    }

    public void dismissDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEvent(String s){
        if ("12".equals(s)){
            Logger.t("PinTaiOrderActivity").e("接收到推送");
            finish();
        }
    }

}
