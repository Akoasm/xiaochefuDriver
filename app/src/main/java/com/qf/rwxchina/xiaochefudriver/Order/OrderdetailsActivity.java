package com.qf.rwxchina.xiaochefudriver.Order;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.CircleImageView;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;
import com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkStringCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 订单详情
 */
public class OrderdetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView mBack;
    private CircleImageView mImg;
    private TextView mName;
    private TextView mClientName;
    private TextView mNum;
    private TextView mPhone;
    private LinearLayout mCall;
    private TextView mStart;
    private TextView mDestination;
    private TextView mTime;
    private TextView mSource;
    private Button mAppointment;
    private Button mCancel;

    private Intent intent;
    private String orderson;
    private String phone,phones;
    private OrderInfo orderInfo = new OrderInfo();
    private UserInfo userInfo = new UserInfo();
    private String driverid;
    LinearLayout Lin_daohang;
    private PopupWindow popupWindow;
    private Button baidu, gaode, quxiao;
    private View contentView;
    private String name;
    double olng, olat, slat, slng;
    public static double x_pi = 3.14159265358979324*3000/180;
    private ProgressDialog mProgressDialog;
    private boolean gaodeFirst=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetails);
        MyApplication.getInstance().addActivity(this);
        MyApplication.isNo="2";
        init();
        getIntentInfo();
        orderdata();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetRequest();
    }

    private void getIntentInfo() {
        orderson = MyApplication.orderson;
        SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        driverid = sp.getString("uid","");
        Log.e("wh",driverid);
    }

    //请求订单数据
    private void NetRequest() {
        Map<String,String> map=new HashMap<>();
        map.put("orderson",orderson);
        com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkHttpUtil.post(HttpPath.ORDER_DETAILS_PATH, map, this, new OkStringCallBack(this) {
            @Override
            public void myError(Call call, Exception e, int id) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void myResponse(String response, int id) {
                //获取到数据
                String result = response;
                if (result != null) {
                    Logger.t("OrderdetailsActivity").e("result=" + result);
                    try {
                        JSONObject object = new JSONObject(result);
                        JSONObject obj = new JSONObject(object.optString("data"));
                        orderInfo.setOut_trade_no(obj.optString("out_trade_no"));
                        orderInfo.setOrderson(obj.optString("orderson"));
                        orderInfo.setDrivercount(obj.optInt("drivercount"));
                        orderInfo.setSaddress(obj.optString("saddress"));
                        orderInfo.setBespeaktime(obj.optString("bespeaktime"));
                        orderInfo.setOaddress(obj.optString("oaddress"));
                        orderInfo.setIndentdistance(obj.optString("indentdistance"));
                        orderInfo.setUname(obj.optString("uname"));
                        name=obj.optString("uname");
                        phones=obj.optString("phones");
                        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                        // 写入
                        SharedPreferences.Editor editor = sp.edit();
                        //更改上班状态
                        editor.putString("phones", phones);
                        //提交
                        editor.commit();
                        JSONObject obj2 = new JSONObject(obj.optString("userInfo"));
                        userInfo.setName(obj2.optString("name"));
                        userInfo.setType_id(obj2.optInt("type_id"));
                        userInfo.setPhone(obj2.optString("phone"));
                        userInfo.setUserurl(obj2.optString("userurl"));
                        changeInfo();
                    } catch (JSONException e) {
                        e.printStackTrace();
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
//                        HttpInfo.Builder().setUrl(HttpPath.ORDER_DETAILS_PATH)
//                                .addParam("orderson",orderson)
//                                .build(),
//                        new CallbackOk() {
//                            @Override
//                            public void onResponse(HttpInfo info) throws IOException {
//                                if (info.isSuccessful()) {
//                                    //获取到数据
//                                    String result = info.getRetDetail();
//                                    if (result != null) {
//                                        Logger.t("OrderdetailsActivity").e("result=" + result);
//                                        try {
//                                            JSONObject object = new JSONObject(result);
//                                            JSONObject obj = new JSONObject(object.optString("data"));
//                                            orderInfo.setOut_trade_no(obj.optString("out_trade_no"));
//                                            orderInfo.setOrderson(obj.optString("orderson"));
//                                            orderInfo.setDrivercount(obj.optInt("drivercount"));
//                                            orderInfo.setSaddress(obj.optString("saddress"));
//                                            orderInfo.setBespeaktime(obj.optString("bespeaktime"));
//                                            orderInfo.setOaddress(obj.optString("oaddress"));
//                                            orderInfo.setIndentdistance(obj.optString("indentdistance"));
//                                            orderInfo.setUname(obj.optString("uname"));
//                                            name=obj.optString("uname");
//                                            phones=obj.optString("phones");
//                                            SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
//                                            // 写入
//                                            SharedPreferences.Editor editor = sp.edit();
//                                            //更改上班状态
//                                            editor.putString("phones", phones);
//                                            //提交
//                                            editor.commit();
//                                            JSONObject obj2 = new JSONObject(obj.optString("userInfo"));
//                                            userInfo.setName(obj2.optString("name"));
//                                            userInfo.setType_id(obj2.optInt("type_id"));
//                                            userInfo.setPhone(obj2.optString("phone"));
//                                            userInfo.setUserurl(obj2.optString("userurl"));
//                                            changeInfo();
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        });
    }

    //修改信息
    private void changeInfo() {
        mName.setText(userInfo.getName());
//        mName.setText(name);
        mNum.setText(orderInfo.getDrivercount()+"名");
        mClientName.setText(userInfo.getName());
//        mClientName.setText(name);
        phone = userInfo.getPhone();
        mPhone.setText(phones);
     //  mTime.setText(orderInfo.getBespeaktime());
        mStart.setText(orderInfo.getSaddress());
        mDestination.setText(orderInfo.getOaddress());
        mSource.setText("人工下单");
        Glide.with(this).load(userInfo.getUserurl()).error(R.drawable.icon_account).into(mImg);
    }

    private void init() {
        mProgressDialog = new ProgressDialog(this);
        mBack = (ImageView) findViewById(R.id.acitivity_orderdetails_back);
        mBack.setOnClickListener(this);
        mImg = (CircleImageView) findViewById(R.id.acitivity_orderdetails_img);
        mName = (TextView) findViewById(R.id.acitivity_orderdetails_name);
        mClientName = (TextView) findViewById(R.id.acitivity_orderdetails_clientName);
        mNum = (TextView) findViewById(R.id.acitivity_orderdetails_num);
        mPhone = (TextView) findViewById(R.id.acitivity_orderdetails_phone);
        mCall = (LinearLayout) findViewById(R.id.acitivity_orderdetails_call);
        mCall.setOnClickListener(this);
        mStart = (TextView) findViewById(R.id.acitivity_orderdetails_start);
        mDestination = (TextView) findViewById(R.id.acitivity_orderdetails_destination);
       // mTime = (TextView) findViewById(R.id.acitivity_orderdetails_time);
        mSource = (TextView) findViewById(R.id.acitivity_orderdetails_source);
        mAppointment = (Button) findViewById(R.id.acitivity_orderdetails_appointment);
        mAppointment.setOnClickListener(this);
        mCancel = (Button) findViewById(R.id.acitivity_orderdetails_cancel);
        mCancel.setOnClickListener(this);
        Lin_daohang= (LinearLayout) findViewById(R.id.Lin_daohang);
        Lin_daohang.setOnClickListener(this);

        //下拉框
        contentView = LayoutInflater.from(OrderdetailsActivity.this).inflate(
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.acitivity_orderdetails_back:
                Intent inten = new Intent(getApplicationContext(),CancelOrderActivity.class);
                startActivity(inten);
                finish();
                break;
          //拨打电话
            case R.id.acitivity_orderdetails_call:
                cellCustomer();
                break;
            case R.id.acitivity_orderdetails_appointment:
                appointment();
                break;
           //取消订单
            case R.id.acitivity_orderdetails_cancel:
//                cancel();
                Intent intent = new Intent(getApplicationContext(),CancelOrderActivity.class);
                startActivity(intent);

                break;
            //导航
            case R.id.Lin_daohang:
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
        }
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
            popupWindow.showAtLocation(Lin_daohang, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    //调用高德地图
    private void openGaodeMap() {
        if (isInstallByread("com.autonavi.minimap")){
            if (gaodeFirst){
                bd09_To_Gcj02();
                gaodeFirst=false;
            }
            try {
                Log.e("wh", "导航>>" + slat + "," + slng);
                intent = Intent.getIntent("androidamap://navi?sourceApplication=小车夫&poiname=我的目的地&lat="+slat+"&lon="+slng+"&dev=0&style=2");
//                Intent intent = new Intent("android.intent.action.VIEW",android.net.Uri.parse("androidamap://viewMap?sourceApplication=爱车点点&poiname=+"+merchant.getName()+"&lat="+gg_lat+ "&lon="+ gg_lon + "&dev=0"));

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        }else {
            Toast.makeText(getApplicationContext(),"未安装高德地图客户端",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 * * 将 BD-09 坐标转换成GCJ-02 坐标 * * @param
     * bd_lat * @param bd_lon * @return
     */
    public void bd09_To_Gcj02() {
        double x = slng - 0.0065, y = slat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        slng = z * Math.cos(theta);
        slat = z * Math.sin(theta);
    }

    //调用百度地图
    private void openBaiduMap() {
        Intent intent = null;
        if (isInstallByread("com.baidu.BaiduMap")){
            try {
                intent = Intent.getIntent("intent://map/navi?location="+slat+","+slng+"&type=BLK&src=thirdapp.navi.yourCompanyName.yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        }else {
            Toast.makeText(getApplicationContext(),"未安装百度地图客户端",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断是否安装目标应用
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    //接受订单
    private void appointment() {
        mProgressDialog.show();
        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(this)
                .doPostAsync(
                        HttpInfo.Builder().setUrl(HttpPath.AGREEORDER_PAHT)
                                .addParam("orderson",orderson)
                                .addParam("driverID",driverid)
                                .build(),
                        new CallbackOk() {
                            @Override
                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()) {
                                    //获取到数据
                                    String result = info.getRetDetail();
                                    if (result != null) {
                                        mProgressDialog.dismiss();
                                        Log.e("kunlun","result="+result);
                                        MyApplication.orderState = 0;
                                        Toast.makeText(getApplicationContext(), "成功接单", Toast.LENGTH_SHORT).show();
                                        gotoState();

                                    } else {
                                        Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

    }

    //接到订单后跳转
    private void gotoState() {
        Log.i("limming","走了吗");
        SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        // 写入
        SharedPreferences.Editor editor = sp.edit();
        //更改上班状态
        editor.putInt("work_status",2);
        //提交
        editor.commit();

//        intent = new Intent(getApplicationContext(), StateActivity.class);
        intent =new Intent(getApplicationContext(),MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("info","gotoState");
        intent.putExtras(bundle);
        startActivity(intent);
        finish();

    }

    /**
     * 根据当前订单得到经纬度
     */
    private void orderdata() {
        Map<String,String> params=new HashMap<>();
        params.put("orderson", orderson);
        com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkHttpUtil.post(HttpPath.orderdata,params, this, new OkStringCallBack(this) {
            @Override
            public void myError(Call call, Exception e, int id) {
                Toast.makeText(OrderdetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void myResponse(String response, int id) {
                //获取到数据
                String result = response;
                // Log.e("wh", result + ">>>");
                if (result != null) {
                    mProgressDialog.dismiss();
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

                        Log.e("wh", "目的地经纬度>>" + olng + "," + olat);
                        Log.e("wh", "出发地经纬度>>" + slng + "," + slat);
                    } else {
                        Toast.makeText(OrderdetailsActivity.this, list.get("msg"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OrderdetailsActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
//        mProgressDialog.show();
//        if (Network.HttpTest(OrderdetailsActivity.this)) {
//            OkHttpUtil.Builder()
//                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
//                    .setConnectTimeout(25).build(this)
//                    .doPostAsync(
//                            HttpInfo.Builder().setUrl(HttpPath.orderdata).addParam("orderson", orderson).build(),
//                            new CallbackOk() {
//                                @Override
//                                public void onResponse(HttpInfo info) throws IOException {
//                                    if (info.isSuccessful()) {
//                                        //获取到数据
//                                        String result = info.getRetDetail();
//                                        // Log.e("wh", result + ">>>");
//                                        if (result != null) {
//                                            mProgressDialog.dismiss();
//                                            //将得到的json数据返回给HashMap
//                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
//                                            HashMap<String, String> list = AnalyticalJSON.getHashMap(map.get("state"));
//                                            if (list.get("code").equals("0")) {
//                                                HashMap<String, String> list_data = AnalyticalJSON.getHashMap(map.get("data"));
//                                                //经度-目的地
//                                                olng = Double.parseDouble(list_data.get("olng"));
//                                                //纬度-目的地
//                                                olat = Double.parseDouble(list_data.get("olat"));
//
//                                                //经度-出发地
//                                                slng = Double.parseDouble(list_data.get("slng"));
//                                                //纬度-出发地
//                                                slat = Double.parseDouble(list_data.get("slat"));
//
//                                                Log.e("wh", "目的地经纬度>>" + olng + "," + olat);
//                                                Log.e("wh", "出发地经纬度>>" + slng + "," + slat);
//
//                                            } else {
//
//                                                Toast.makeText(OrderdetailsActivity.this, list.get("msg"), Toast.LENGTH_SHORT).show();
//                                            }
//
//                                        } else {
//                                            Toast.makeText(OrderdetailsActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
//
//                                        }
//
//                                    }
//                                }
//                            });
//        }

    }


    final public static int REQUEST_CODE_ASK_CALL_PHONE = 123;
    //拨打电话
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


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            Intent intent = new Intent(getApplicationContext(),CancelOrderActivity.class);
            startActivity(intent);
            finish();
            return false;
        }else {
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


    private void cancel() {
        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(this)
                .doPostAsync(
                        HttpInfo.Builder().setUrl(HttpPath.order_cancle)
                                .addParam("order", MyApplication.orderson)
                                .addParam("reason"," ")
                                .build(),
                        new CallbackOk() {
                            @Override
                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()) {
                                    //获取到数据
                                    String result = info.getRetDetail();
                                    Log.e("wh",result+"<<<");
                                    if (result != null) {
                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                        HashMap<String, String> list_msg= AnalyticalJSON.getHashMap(map.get("state"));
                                        if(list_msg.get("code").equals("0")) {
                                            Intent intent = new Intent(getApplicationContext(),CancelOrderActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else if (list_msg.get("code").equals("1")) {
                                            Toast.makeText(getApplicationContext(),list_msg.get("msg"), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
    }

}
