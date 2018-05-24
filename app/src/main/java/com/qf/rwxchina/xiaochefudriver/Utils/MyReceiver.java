package com.qf.rwxchina.xiaochefudriver.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.MapHome.MapFragment;
import com.qf.rwxchina.xiaochefudriver.Order.OrderQuxiao;
import com.qf.rwxchina.xiaochefudriver.Order.PinTaiOrderActivity;
import com.qf.rwxchina.xiaochefudriver.Order.RobOrderActivity;
import com.qf.rwxchina.xiaochefudriver.Return.AgreeDialogActivity;
import com.qf.rwxchina.xiaochefudriver.Return.RefuseDialogActivity;
import com.qf.rwxchina.xiaochefudriver.Return.ReturnDialogActivity2;
import com.qf.rwxchina.xiaochefudriver.Utils.logutils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 广播接收器
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private MapFragment mapFragment = new MapFragment();
    private SharedPreferences sp;
    private boolean isLogin;//司机是否登录
    private int workstate;//司机状态

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        isLogin = sp.getBoolean("isLogin", false);
        workstate = sp.getInt("work_status", 0);
        if (isLogin) {
            String info = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            Logger.t("tuisong").e(info);
            if (!TextUtils.isEmpty(info)) {
                Log.e("wh", bundle.getString(JPushInterface.EXTRA_MESSAGE));
                try {
                    JSONObject obj = new JSONObject(info);
                    int type = obj.optInt("type");
                    Log.e("wh", "推送类型》》" + type);
                    String content = obj.optString("content");
                    JSONObject obj2 = new JSONObject(content);
                    switch (type) {
                        case 11://用户订单
                            if (MyApplication.orderState == 0 && workstate == 1) {
                                MyApplication.orderson = obj2.optString("orderson");
                                addWindow(context);
                                MyApplication.orderState = 1;
                            }
                            break;
                        case 12://用户取消订单
                            MyApplication.orderson = obj2.optString("orderson");
                            addWindow_clear(context);
                            Log.e("wh", "用户取消状态》》" + MyApplication.orderState);
                            EventBus.getDefault().post("12");
                            break;
                        case 51://平台派单
                            if (MyApplication.orderState == 0 && workstate == 1) {
                                MyApplication.orderson = obj2.optString("orderson");
                                addWindow_pingtai(context);
                                MyApplication.orderState = 1;
                            }
                            break;
                        case 52://用户已付款
                            MyApplication.orderson = obj2.optString("orderson");
                            MyApplication.orderState = 2;
                            LogUtil.e("MyReceiver","paytype=52,用户已付款");
                            CallBackhelp.paytype(52);
                            break;
                        case 14://是否结伴同行
                            MyApplication.driverid  = obj2.optString("driverid");
                            Intent it = new Intent(context, ReturnDialogActivity2.class);
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(it);
                         break;

                        case 16://拒绝结伴同行
                            Intent it_no = new Intent(context, RefuseDialogActivity.class);
                            it_no.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(it_no);
                            break;

                        case 15://同意结伴同行
                            MyApplication.phone  = obj2.optString("phone");
                            Intent it_ok = new Intent(context, AgreeDialogActivity.class);
                            it_ok.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(it_ok);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");


//

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    //当有新的订单时，
    public void addWindow(Context context) {
        Intent intent = new Intent(context, RobOrderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("pingtai", "0");
        context.startActivity(intent);
    }

    //平台派单
    public void addWindow_pingtai(Context context) {
        Intent intent = new Intent(context, PinTaiOrderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("pingtai", "1");
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("type","1");
        editor.commit();
        context.startActivity(intent);
    }

    //用户取消订单
    public void addWindow_clear(Context context) {
        Intent intent = new Intent(context, OrderQuxiao.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        SharedPreferences sp = context.getSharedPreferences("userInfo", context.MODE_PRIVATE);
        // 写入
        SharedPreferences.Editor editor = sp.edit();
        //更改上班状态
        editor.putInt("work_status", 1);
        //提交
        editor.commit();
    }


    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    //TODO:发送消息到主页面
    private void processCustomMessage(Context context, Bundle bundle) {
//        if (MainActivity.isForeground) {
//            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//            if (!ExampleUtil.isEmpty(extras)) {
//                try {
//                    JSONObject extraJson = new JSONObject(extras);
//                    if (null != extraJson && extraJson.length() > 0) {
//                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//                    }
//                } catch (JSONException e) {
//
//                }
//
//            }
//            context.sendBroadcast(msgIntent);
//        }
    }
}
