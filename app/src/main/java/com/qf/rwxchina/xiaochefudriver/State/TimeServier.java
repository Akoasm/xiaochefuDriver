package com.qf.rwxchina.xiaochefudriver.State;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/12/30.
 */
public class TimeServier extends Service {
    SharedPreferences.Editor spfe;
    SharedPreferences spf;
    int num;
    int time = 0;
    boolean login;
    Thread thread;
    Float i = 0f;
    private String orderson;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i("hrr","服务接收到");
        orderson = MyApplication.orderson;
        spfe=getApplicationContext().getSharedPreferences("waittime", Context.MODE_PRIVATE).edit();
        spf=getApplicationContext().getSharedPreferences("waittime",Context.MODE_PRIVATE);
        time=spf.getInt("time",0);
        Log.e("hrr","spf.time="+time);
        if (thread==null){
            thread=new Thread(runnable);
            thread.start();
        }
    }
    //计时器
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time++;
            spfe.putInt("time",time);
            spfe.commit();
            Log.e("hrr","time="+time);
            Message msg = handler.obtainMessage();
            msg.obj = time;
//            waitreLoad(time + "");
            Intent intent=new Intent();
            intent.putExtra("timemoney",time+"");
            intent.setAction("changemoney");
            sendBroadcast(intent);
            handler.sendMessage(msg);
        }
    };


    // 更新界面
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            getTime((Integer) msg.obj);
            handler.postDelayed(runnable, 1000);
        }
    };
    /**
     * 司机实时记录等待时间
     */
    public void waitreLoad(final String ti) {
        //把等待时间段转化为秒数
        Float miao=Float.parseFloat(MyApplication.waittimeduan)*60;

        //判断当前时间是否大于等待时间段
        if(time<=miao) {
            Log.e("wh","你的等待时间不足"+MyApplication.waittimeduan+"分钟");
            i=0f;//不收取任何等待费
        }else {
            Float cao_time=time-miao;//得到等待超出的时间
            Log.e("wh","得到等待超出的时间》》》"+cao_time);
            Float das=cao_time/(Float.parseFloat(MyApplication.waittimeduan_exc)*60);// 计算步骤(超过等待时间段不足这个时间段按一个时间段计算)
            Log.e("wh","超算过等待时间段不足这个时间段按一个时间段计算》》》"+das);
            int t= (int) Math.ceil(das);//向上取整
            Log.e("wh","向上取整》》》"+t);
            i= t*Float.parseFloat(MyApplication.waitmoney);//等待费价格
        }
        if (i>0){
//            fragment_state_cost.setText(i+"元");
            Intent intent=new Intent();
            intent.putExtra("timemoney",i+"");
            intent.setAction("changemoney");
            sendBroadcast(intent);
        }
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
                                Log.i("hrr","等待时间提交示例="+HttpPath.waitreLoad+"/orderson/"+orderson+"/time/"+ti+"/waitmoney/"+i);
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
                                            Toast.makeText(getApplicationContext(), list.get("msg"), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                        });

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
        Intent intent=new Intent();
        intent.putExtra("timeh",h);
        intent.putExtra("timed",d);
        intent.putExtra("times",s);
        intent.setAction("changetime");
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        //暂停计时器
        handler.removeCallbacks(runnable);
        spfe.putInt("time",0);
        spfe.commit();
    }
}
