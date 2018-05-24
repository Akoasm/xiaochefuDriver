package com.qf.rwxchina.xiaochefudriver.Order;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.Home.MainActivity;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.CallBackhelp;
import com.qf.rwxchina.xiaochefudriver.Utils.CircleImageView;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;
import com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkHttpUtil;
import com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkStringCallBack;
import com.qf.rwxchina.xiaochefudriver.Utils.PayOKInterface;
import com.qf.rwxchina.xiaochefudriver.Utils.logutils.LogUtil;
import com.qf.rwxchina.xiaochefudriver.Utils.toastutils.ToastUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


/**
 * 订单结算
 * 2017.08.07 hrr  修改网络请求框架，修改订单详情为网络请求（之前为本地application存储）
 */
public class SettlementOrderActivity extends AppCompatActivity implements View.OnClickListener, PayOKInterface {
    private CircleImageView mImg;
    private TextView mName;
    private TextView mPrice;
    private TextView mStart;
    private TextView mKmprice;
    private TextView mWait;
    private TextView mpop_money;
    private TextView mstartmoney, mdistancemoney, mwaitmoney;
    private Button button;
    private ImageView close;
    private TextView zhifu_type;
    private Button mSure, activity_settlement_order_sure02;
    private SharedPreferences sp;
    private String driverid;
    private LinearLayout lin_s, lin_h;
    private MediaPlayer mediaPlayer;
    private View view;
    private PopupWindow popupWindow;
    private int from;
    private EditText chexing, chepaihao;
    private String orderson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(getParent());
        setContentView(R.layout.activity_settlement_order);
        MyApplication.getInstance().addActivity(this);
        MyApplication.isNo = "2";
        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        //司机ID
        driverid = sp.getString("uid", "");
        from = sp.getInt("from", 0);
        orderson=MyApplication.orderson;
        LogUtil.e("SettlementOrderActivity","orderson="+orderson);
        init();

        CallBackhelp.setinterface(this);
        //判断用户是否支付2：已支付，
        if (MyApplication.orderState == 2) {
            zhifu_type.setText("用户支付状态:已支付");
            mSure.setText("确认");
            mSure.setBackgroundResource(R.drawable.background_orange);
            mSure.setEnabled(true);
        } else {
            zhifu_type.setText("用户支付状态:未支付");
            mSure.setText("客户暂未支付");
            mSure.setBackgroundResource(R.drawable.background_unclick);
            mSure.setEnabled(false);
        }
        //获取订单信息
        getOrderson();

    }

    private void init() {
        view = LayoutInflater.from(this).inflate(R.layout.activity_settlement_order_pop, null);
        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));

        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
        mediaPlayer = MediaPlayer.create(SettlementOrderActivity.this, R.raw.shake_match);
        mImg = (CircleImageView) findViewById(R.id.activity_settlement_order_img);
        mName = (TextView) findViewById(R.id.activity_settlement_order_name);
        mPrice = (TextView) findViewById(R.id.activity_settlement_order_price);
        mstartmoney = (TextView) view.findViewById(R.id.activity_settlement_order_startmoney);
        mdistancemoney = (TextView) view.findViewById(R.id.activity_settlement_order_distancemoney);
        mwaitmoney = (TextView) view.findViewById(R.id.activity_settlement_order_waitmoney);
        mStart = (TextView) view.findViewById(R.id.activity_settlement_order_start);
        mKmprice = (TextView) view.findViewById(R.id.activity_settlement_order_kmprice);
        mWait = (TextView) view.findViewById(R.id.activity_settlement_order_wait);
        mpop_money = (TextView) view.findViewById(R.id.activity_settlement_order_pop_money);
        close = (ImageView) view.findViewById(R.id.close);
        button = (Button) findViewById(R.id.activity_settlement_order_examine);
        mSure = (Button) findViewById(R.id.activity_settlement_order_sure);
        activity_settlement_order_sure02 = (Button) findViewById(R.id.activity_settlement_order_sure02);
        zhifu_type = (TextView) findViewById(R.id.zhifu_type);
        lin_s = (LinearLayout) findViewById(R.id.lin_s);
        lin_h = (LinearLayout) findViewById(R.id.lin_h);

        chexing = (EditText) findViewById(R.id.chexing);
        chepaihao = (EditText) findViewById(R.id.chepaihao);

        close.setOnClickListener(this);
        mSure.setOnClickListener(this);
        button.setOnClickListener(this);
        activity_settlement_order_sure02.setOnClickListener(this);

    }

    //确认支付
    private void pay_ok() {
        if (from == 1) {
            Intent intent = new Intent(SettlementOrderActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (Network.HttpTest(SettlementOrderActivity.this)) {
                Map<String,String>params=new HashMap<>();
                params.put("order", MyApplication.orderson);
                params.put("driverid", driverid);
                params.put("cartype", chexing.getText().toString());
                params.put("carnum", chepaihao.getText().toString());
                OkHttpUtil
                        .post(HttpPath.PkayOK, params, this, new OkStringCallBack(SettlementOrderActivity.this) {
                            @Override
                            public void myError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void myResponse(String response, int id) {
                                //获取到数据
                                String result = response;
                                Log.e("wh", result);
                                if (result != null) {
                                    //将得到的json数据返回给HashMap
                                    HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                    HashMap<String, String> list = AnalyticalJSON.getHashMap(map.get("state"));
                                    HashMap<String, String> list02 = AnalyticalJSON.getHashMap(map.get("data"));
                                    if (list.get("code").equals("0")) {
                                        String agentsum = list02.get("agentsum");
                                        Log.i("wh", "agentsum=" + agentsum);
                                        MyApplication.orderState = 0;
                                        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                                        // 写入
                                        SharedPreferences.Editor editor = sp.edit();
                                        //更改上班状态
                                        editor.putInt("work_status", 1);
                                        //代驾次数
                                        editor.putString("agentsum", list02.get("agentsum"));
                                        //提交
                                        editor.commit();
                                        Intent intent = new Intent(SettlementOrderActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SettlementOrderActivity.this, list.get("msg"), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(SettlementOrderActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_settlement_order_img:
                finish();
                break;
            case R.id.activity_settlement_order_sure:

                if (MyApplication.orderState == 2) {
                    pay_ok();
                } else {
                    Toast.makeText(getApplicationContext(), "等待用户支付", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.activity_settlement_order_examine:
                WindowManager.LayoutParams lp = this.getWindow().getAttributes();
                lp.alpha = 0.5f;
                this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                this.getWindow().setAttributes(lp);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.close:
                popupWindow.dismiss();
                break;
            case R.id.activity_settlement_order_sure02:
                isok02();
                break;
        }
    }
    //我已收款弹出框
    private void isok02() {
        final Dialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("提示!")
                .setMessage("是否确认已经收款？")
                .setPositiveButton("是", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getmoney();
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
     * 我已收款接口
     */
    private void getmoney(){
        if (Network.HttpTest(SettlementOrderActivity.this)) {
            Map<String,String> params=new HashMap<String,String>();
            params.put("orderson",MyApplication.orderson);
            params.put("driverID", driverid);

            OkHttpUtil
                    .post(HttpPath.GETMONEY, params, this, new OkStringCallBack(SettlementOrderActivity.this) {
                        @Override
                        public void myError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void myResponse(String response, int id) {
                            //获取到数据
                            String result = response;
                            Log.i("liming","woyishoukuan"+result.toString());
                            if (result != null) {
                                //将得到的json数据返回给HashMap
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                HashMap<String, String> list = AnalyticalJSON.getHashMap(map.get("state"));
                                HashMap<String, String> list02 = AnalyticalJSON.getHashMap(map.get("data"));
                                if (list.get("code").equals("0")) {
                                    MyApplication.orderState = 0;
                                    SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                                    // 写入
                                    SharedPreferences.Editor editor = sp.edit();
                                    //更改上班状态
                                    editor.putInt("work_status", 1);
                                    //代驾次数
                                    editor.putString("agentsum", list02.get("agentsum"));
                                    //提交
                                    editor.commit();
                                    Intent intent = new Intent(SettlementOrderActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if(list.get("msg").equals("您已付款确认,请勿重复操作")){
                                    Intent intent = new Intent(SettlementOrderActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SettlementOrderActivity.this, list.get("msg"), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SettlementOrderActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        }
    }
    //用户是否支付成功接口回调
    @Override
    public void paytype(int type) {
        if (type == 52) {
            zhifu_type.setText("用户支付状态:已支付");
            mSure.setText("确认");
            mSure.setBackgroundResource(R.drawable.background_linear);
            mSure.setEnabled(true);
            mediaPlayer.start();
        } else {
            zhifu_type.setText("用户支付状态:未支付");
            mSure.setText("客户暂未支付");
            mSure.setBackgroundResource(R.drawable.background_unclick);
            mSure.setEnabled(false);
        }

    }

    /**
     * 获取订单信息
     */
    private void getOrderson(){
        Map<String,String> maps=new HashMap<String,String>();
        maps.put("orderson",orderson);
        OkHttpUtil
                .post(HttpPath.ORDER_DETAILS_PATH,maps,this,stringCallBack);
    }
    //请求返回
    OkStringCallBack stringCallBack=new OkStringCallBack(this) {
        @Override
        public void myError(Call call, Exception e, int id) {

        }

        @Override
        public void myResponse(String response, int id) {
            LogUtil.e("SettlementOrderActivity","response="+response);
            JSONObject object=null;
            try {
                object=new JSONObject(response);
                JSONObject state=object.getJSONObject("state");
                String code=state.optString("code");
                String msg=state.optString("msg");
                if ("0".equals(code)){
                    LogUtil.e("SettlementOrderActivity","code="+code);
                    JSONObject data=object.getJSONObject("data");
                    JSONObject userInfo=data.getJSONObject("userInfo");
                    String userurl=userInfo.optString("userurl");//头像
                    String name=userInfo.optString("name");//头像
                    String totalmoney=data.optString("totalmoney");//订单总价格
                    String startmoney=data.optString("startmoney");//起步价
                    String maxnumber=data.optString("_maxnumber");//起步公里数
                    String distance=data.optString("distance");//行驶公里
                    String distancemoney=data.optString("distancemoney");//公里费
                    double waittime= Integer.parseInt(data.optString("waittime"));//等待时间，单位秒
                    String waitmoney=data.optString("waitmoney");//等待费
                    //头像
                    if (userurl == null || userurl.equals("")) {
                        mImg.setImageResource(R.drawable.icon_account);
                    } else {
                        Glide.with(SettlementOrderActivity.this).load(userurl).error(R.drawable.icon_account).into(mImg);
                    }
                    //姓名
                    if (TextUtils.isEmpty(name)) {
                        mName.setText("小车夫");
                    } else {
                        mName.setText(name);
                    }
                    //价格
                    mPrice.setText(totalmoney + "元");
                    mpop_money.setText(totalmoney + "元");
                    if (TextUtils.isEmpty(maxnumber)) {
                        maxnumber = "0";
                    }
                    if (TextUtils.isEmpty(distance)) {
                        distance = "0";
                    }
                    //起步价
                    mStart.setText("起步价（含" + maxnumber + "公里）");
                    //公里费
                    mKmprice.setText("公里费（" + distance + "公里）");
                    //等待费
                    waittime = waittime / 60;
                    waittime = Math.ceil(waittime);

                    mWait.setText("等待费（" + waittime + "分钟）");
                    //优惠券
                    mstartmoney.setText(startmoney + "元");
                    mdistancemoney.setText(distancemoney + "元");
                    mwaitmoney.setText(waitmoney + "元");
                }else {
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            if (MyApplication.orderState == 2) {
                pay_ok();
            } else {
                Toast.makeText(getApplicationContext(), "等待用户支付", Toast.LENGTH_SHORT).show();
            }

            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

}
