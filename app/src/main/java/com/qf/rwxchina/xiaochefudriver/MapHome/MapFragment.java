package com.qf.rwxchina.xiaochefudriver.MapHome;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.okhttplib.HttpInfo;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.orhanobut.logger.Logger;
import com.qf.rwxchina.xiaochefudriver.Bean.DriverInfo;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.Bean.OrderInfo;
import com.qf.rwxchina.xiaochefudriver.MyApplication;

import com.qf.rwxchina.xiaochefudriver.Login.LoginActivity;
import com.qf.rwxchina.xiaochefudriver.Order.BaoDanActivity;
import com.qf.rwxchina.xiaochefudriver.Personal.My_account;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Return.BaoDanDestinationActivity;
import com.qf.rwxchina.xiaochefudriver.Utils.CircleImageView;
import com.qf.rwxchina.xiaochefudriver.Utils.LocationXY;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;
import com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkHttpUtil;
import com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkStringCallBack;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 首页
 */

public class MapFragment extends Fragment implements View.OnClickListener {
    private TextView mAddress;  //位置
    private BaiduMap mBaiduMap = null;
    private MapView mMapView = null;
    private LocationClient mLocationClient = null;
    private BDLocationListener mLocationListener = new MyLocationListener();
    boolean isFirstLoc = true;// 是否首次定位
    private String locationAddress;

    private ImageView mWork;
    private LinearLayout mAdd;
    private RelativeLayout mWorkState;
    private TextView mWorkText;
    private Intent intent;
    //出发地经纬度
    private double sLng;
    private double sLat;
    private Double lat;
    private Double lng;
    private List<OrderInfo> list;
    private Bundle bundle;

    private SharedPreferences sp;
    public static boolean isLogin = false;
    private static int workstate = -1;//司机状态 0下班,1空闲，2服务中
    private String driverId;

    private Double userLat;
    private Double userLng;
    ImageView activity_main_navigation;

    private BDLocation mLocation;
    private TextView tvSaddress;
    private BitmapDescriptor bd;
    private LatLng pt;
    private OverlayOptions options;
    private TextView mStart;
    private LinearLayout lDestination, start_ll;
    private EditText mPhone;
    private TextView mDestination;
    private PopupWindow popupWindow;
    private PopupWindow pop_money;
    private TextView mBaoDan;
    private Button mBaoDanOk, mBaoDanCancel;
    private String mCity;
    private int cityId;
    //目的地经纬度
    private double dLng;
    private double dLat;
    private static final int DESTINATION = 111;
    private static final int START = 112;
    private ProgressDialog pd;
    private String isonline = "0";//是否在线，0：不在线，1：在线
    private Thread getDriver;
    private String driverInfo;
    private ArrayList<DriverInfo> driver = new ArrayList<>();
    private String driverid = "";
    private View myView;
    private ImageView close;
    private Button recharge;//跳转到充值
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        mLocationClient = new LocationClient(getContext());
        mLocationClient.registerLocationListener(mLocationListener);
        sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        if (isLogin) {
            getDriverState();

        }

        init(view);
        getWorkState(view);
        initLocation(view);
        if (Network.HttpTest(getActivity())){
            NetRequest();
        }

        return view;
    }


    //获取司机状态
    private void getWorkState(View view) {
        workstate = sp.getInt("work_status", 0);
        isLogin = sp.getBoolean("isLogin", false);
        driverId = sp.getString("uid", "");
        if (isLogin) {
            getDriverState();
        }

    }

    //从网络获取附近的订单
    private void HttpGetOrder() {
        list = new ArrayList<>();
        if (lng == null) {
            Location ml = new LocationXY().init(getContext());
            lng = ml.getLongitude();
            lat = ml.getLatitude();
        }
//        Log.e("kunlun",HttpPath.GETORDER_PATH+"?lng="+lng+"&lat="+lat);
        Map<String,String> params=new HashMap<>();
        params.put("lng", lng + "");
        params.put("lat", lat + "");
        OkHttpUtil.post(HttpPath.GETORDER_PATH, params, this, new OkStringCallBack(getActivity()) {
            @Override
            public void myError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void myResponse(String response, int id) {
                //获取到数据
                String result = response;
                if (result != null) {
//                                        Log.e("kunlun","result="+result);
                    try {
                        JSONObject object = new JSONObject(result);
                        JSONArray arr = new JSONArray(object.optString("data"));
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            OrderInfo order = new OrderInfo();
                            order.setId(obj.optInt("id"));
                            order.setOut_trade_no(obj.optString("out_trade_no"));
                            order.setOrderson(obj.optString("orderson"));
                            order.setOrdertype(obj.optInt("ordertype"));
                            order.setUid(obj.optInt("uid"));
                            order.setDrivercount(obj.optInt("drivercount"));
                            order.setSaddress(obj.optString("saddress"));
                            order.setSlat(obj.optDouble("slat"));
                            order.setSlng(obj.optDouble("slng"));
                            order.setBespeaktime(obj.optString("bespeaktime"));
                            order.setCreatetime(obj.optString("createtime"));
                            order.setPhones(obj.optString("phones"));
                            order.setOaddress(obj.optString("oaddress"));
                            order.setOlng(obj.optDouble("olng"));
                            order.setOlat(obj.optDouble("olat"));
                            order.setIndentdistance(obj.optString("indentdistance"));

                            list.add(order);
                        }
                        if (list.size() == 0) {
                            Toast.makeText(getContext(), "没有订单", Toast.LENGTH_SHORT).show();
                        } else {
//                                                for (int i = 0;i<list.size();i++){
//                                                    test(i);
//                                                }
//                                                mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
//                                                    @Override
//                                                    public boolean onMarkerClick(Marker marker) {
//
//                                                        intent = new Intent(getContext(),RobOrderActivity.class);
//                                                        bundle = new Bundle();
//                                                        bundle.putString("orderson",marker.getTitle());
//                                                        intent.putExtras(bundle);
//                                                        startActivity(intent);
//                                                        return false;
//                                                    }
//                                                });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        OkHttpUtil.Builder()
//                .setCacheLevel(CacheLevel.FIRST_LEVEL)
//                .setConnectTimeout(25).build(this)
//                .doPostAsync(
//                        HttpInfo.Builder().setUrl(HttpPath.GETORDER_PATH)
//                                .addParam("lng", lng + "")
//                                .addParam("lat", lat + "")
//                                .build(),
//                        new CallbackOk() {
//                            @Override
//                            public void onResponse(final HttpInfo info) throws IOException {
//                                if (info.isSuccessful()) {
//                                    //获取到数据
//                                    String result = info.getRetDetail();
//                                    if (result != null) {
////                                        Log.e("kunlun","result="+result);
//                                        try {
//                                            JSONObject object = new JSONObject(result);
//                                            JSONArray arr = new JSONArray(object.optString("data"));
//                                            for (int i = 0; i < arr.length(); i++) {
//                                                JSONObject obj = arr.getJSONObject(i);
//                                                OrderInfo order = new OrderInfo();
//                                                order.setId(obj.optInt("id"));
//                                                order.setOut_trade_no(obj.optString("out_trade_no"));
//                                                order.setOrderson(obj.optString("orderson"));
//                                                order.setOrdertype(obj.optInt("ordertype"));
//                                                order.setUid(obj.optInt("uid"));
//                                                order.setDrivercount(obj.optInt("drivercount"));
//                                                order.setSaddress(obj.optString("saddress"));
//                                                order.setSlat(obj.optDouble("slat"));
//                                                order.setSlng(obj.optDouble("slng"));
//                                                order.setBespeaktime(obj.optString("bespeaktime"));
//                                                order.setCreatetime(obj.optString("createtime"));
//                                                order.setPhones(obj.optString("phones"));
//                                                order.setOaddress(obj.optString("oaddress"));
//                                                order.setOlng(obj.optDouble("olng"));
//                                                order.setOlat(obj.optDouble("olat"));
//                                                order.setIndentdistance(obj.optString("indentdistance"));
//
//                                                list.add(order);
//                                            }
//                                            if (list.size() == 0) {
//                                                Toast.makeText(getContext(), "没有订单", Toast.LENGTH_SHORT).show();
//                                            } else {
////                                                for (int i = 0;i<list.size();i++){
////                                                    test(i);
////                                                }
////                                                mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
////                                                    @Override
////                                                    public boolean onMarkerClick(Marker marker) {
////
////                                                        intent = new Intent(getContext(),RobOrderActivity.class);
////                                                        bundle = new Bundle();
////                                                        bundle.putString("orderson",marker.getTitle());
////                                                        intent.putExtras(bundle);
////                                                        startActivity(intent);
////                                                        return false;
////                                                    }
////                                                });
//                                            }
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else {
//                                        Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        });
    }

    //获取定位
    private void initLocation(View view) {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        int span = 2000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);

        mBaiduMap = mMapView.getMap();
        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient.start();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
    }

    //初始化
    private void init(View view) {
        //创建一个下单弹框
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_order, null);
        mStart = (TextView) contentView.findViewById(R.id.layout_order_start);
        lDestination = (LinearLayout) contentView.findViewById(R.id.layout_order_l_destination);
        lDestination.setOnClickListener(this);
        start_ll = (LinearLayout) contentView.findViewById(R.id.start_ll);
        start_ll.setOnClickListener(this);
        mPhone = (EditText) contentView.findViewById(R.id.layout_order_phone);
        mDestination = (TextView) contentView.findViewById(R.id.layout_order_destination);
        mDestination.setText("");
        popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // 点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //软键盘把popupWindow顶上去
        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View money=LayoutInflater.from(getContext()).inflate(R.layout.pop_money,null);
        pop_money=new PopupWindow(money,WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        // 点击外部消失
        pop_money.setOutsideTouchable(true);
        pop_money.setBackgroundDrawable(new BitmapDrawable());
        pop_money.setFocusable(true);
        //余额不足时弹出框取消按钮
        close= (ImageView) money.findViewById(R.id.pop_close);
        close.setOnClickListener(this);
        recharge= (Button) money.findViewById(R.id.recharge);
        recharge.setOnClickListener(this);
        mBaoDan = (TextView) view.findViewById(R.id.fragment_map_baodan);
        mBaoDan.setOnClickListener(this);

        mBaoDanOk = (Button) contentView.findViewById(R.id.layout_order_sure);
        mBaoDanCancel = (Button) contentView.findViewById(R.id.layout_order_cancel);
        mBaoDanOk.setOnClickListener(this);
        mBaoDanCancel.setOnClickListener(this);
        mMapView = (MapView) view.findViewById(R.id.fragment_map_baiduMap);
        mAddress = (TextView) view.findViewById(R.id.fragment_map_address);
        mWork = (ImageView) view.findViewById(R.id.fragment_map_work_img);
        mAdd = (LinearLayout) view.findViewById(R.id.fragment_map_add);
        mAdd.setOnClickListener(this);
        mWorkState = (RelativeLayout) view.findViewById(R.id.fragment_map_work);
        mWorkState.setOnClickListener(this);
        mWorkText = (TextView) view.findViewById(R.id.fragment_map_text);
        activity_main_navigation = (ImageView) view.findViewById(R.id.activity_main_navigation);
        activity_main_navigation.setOnClickListener(this);
        Picasso.with(getContext()).load(R.drawable.icon_startwork).into(mWork);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recharge:
                Intent intent=new Intent(getActivity(), My_account.class);
                intent.putExtra("type","mapFragment");
                startActivity(intent);
                break;
            case R.id.pop_close:
                if (pop_money.isShowing()) {
                    pop_money.dismiss();
                }
                break;
            case R.id.fragment_map_add:
                intent = new Intent(getContext(), ManualLocationActivity.class);
                startActivityForResult(intent, 110);
                break;
            case R.id.fragment_map_work:
                //1.先判断是否登录，没有登录则跳到登录页面
                //2.如果登录则判断是否是上线状态，如果是下线状态，请求服务器上线
                //3.如果是上线状态，则判断是空闲还是服务状态，如果是空闲，则请求服务器下线
                //4.如果是服务状态，则提醒还有订单未完成
                workstate = sp.getInt("work_status", 1);
//                Log.e("kunlun","登录状态="+isLogin+"  上下班状态="+isonline+"  服务状态="+workstate);
                if (isLogin) {
                    if (isonline.equals("1")) {//在线状态
                        if (workstate == 1) {//空闲状态
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage("确认下班？");
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    HttpGetOffWork();
                                    //判断是否是空闲状态0：空闲  1：服务中
                                    MyApplication.is_kongxian = "0";
                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        } else if (workstate == 2) {//服务状态
                            Toast.makeText(getActivity(), "您还有订单未完成", Toast.LENGTH_SHORT).show();
                        }
                    } else {//下线状态
//                        if (workstate == 0){
                        HttpStartWork();
                        //判断是否是空闲状态0：空闲  1：服务中
                        MyApplication.is_kongxian = "0";
//                        HttpGetOrder();
//                        }
                    }

                } else {
                    intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.activity_main_navigation:
                navigation();
                break;
            case R.id.fragment_map_baodan:
                needDriver(v);
                break;
            case R.id.layout_order_l_destination:
                intent = new Intent(getContext(), BaoDanDestinationActivity.class);
                bundle = new Bundle();
                bundle.putString("address", mAddress.getText().toString());
                bundle.putString("type", "destination");
                intent.putExtras(bundle);
                startActivityForResult(intent, DESTINATION);
                break;
            //确认保单
            case R.id.layout_order_sure:
                //获取司机状态
                workstate = sp.getInt("work_status", 0);
//                Log.e("hrr","在线状态="+isonline);
                //判断是否在线
                if (isonline.equals("1")) {
//                    Log.e("hrr","进来了");
                    if (workstate == 1) {
//                        Log.e("hrr","上班状态="+workstate);
                        String sadd = mStart.getText().toString();
                        String oadd = mDestination.getText().toString();
                        String carid = mPhone.getText().toString();
                        if (!TextUtils.isEmpty(sadd) && !TextUtils.isEmpty(carid)) {
//                            Log.e("hrr","上传数据");
                            setBaoDan();
                        } else {
//                            Log.e("hrr","填写信息");
                            Toast.makeText(getActivity(), "请填写完整信息", Toast.LENGTH_SHORT).show();
                        }
                    } else if (workstate == 2) {
                        Toast.makeText(getActivity(), "您还有未完成订单", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getActivity(), "您暂未上班", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "您暂未上班", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layout_order_cancel:
                popupWindow.dismiss();
                break;
            case R.id.start_ll:
                intent = new Intent(getContext(), BaoDanDestinationActivity.class);
                bundle = new Bundle();
                bundle.putString("address", mStart.getText().toString());
                bundle.putString("type", "start");
                intent.putExtras(bundle);
                startActivityForResult(intent, DESTINATION);
                break;

        }
    }

    /**
     * 定位到自己的位置
     */
    private void navigation() {
        mAddress.setText(locationAddress);
        LatLng ll = new LatLng(mLocation.getLatitude(),
                mLocation.getLongitude());
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 14);    //设置地图中心点以及缩放级别
//				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);
    }

    //在地图上添加订单信息
    private void test(int i) {
        View myView = LayoutInflater.from(getContext()).inflate(R.layout.layout_address_point, null);
        myView.setBackgroundResource(R.drawable.icon_driver_background);
        tvSaddress = (TextView) myView.findViewById(R.id.layout_address_saddress);
//        Log.e("kunlun","saddress="+list.get(i).getSaddress());
        tvSaddress.setText(list.get(i).getSaddress());
        TextView tvOaddress = (TextView) myView.findViewById(R.id.layout_address_oaddress);
//        Log.e("kunlun","oaddress="+list.get(i).getOaddress());
        tvOaddress.setText(list.get(i).getOaddress());

        bd = BitmapDescriptorFactory.fromView(myView);

        pt = new LatLng(list.get(i).getSlat(), list.get(i).getSlng());

        options = new MarkerOptions().position(pt).icon(bd).title(list.get(i).getOrderson());
        mBaiduMap.addOverlay(options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 111:
                Bundle bundle = data.getExtras();
                mAddress.setText(bundle.getString("detailedAddress"));
                lat = bundle.getDouble("lat");
                lng = bundle.getDouble("lng");
                break;
            case 201:
                bundle = data.getExtras();
                mDestination.setText(bundle.getString("position"));
                dLat = bundle.getDouble("lat");
                dLng = bundle.getDouble("lng");
                break;
            case 202:
                bundle = data.getExtras();
                mStart.setText(bundle.getString("position"));
                lat = bundle.getDouble("lat");
                lng = bundle.getDouble("lng");
                break;
        }
    }
    //TODO 上下班为同一个接口，可以融合
    //向服务器传递开始上班
    private void HttpStartWork() {
//        pd.show();
        String mobilemodel= Build.MANUFACTURER+"-"+Build.MODEL;//手机厂商+手机型号
        String systemmodel=android.os.Build.VERSION.RELEASE;//系统版本
        String appversion = "";//系统版本
        PackageManager packageManager = getActivity().getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo= packageManager.getPackageInfo("com.qf.rwxchina.xiaochefudriver", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo!=null){
            appversion = packageInfo.versionName;
        }
        Log.e("hrr",mobilemodel+"   "+systemmodel+"  "+appversion);
//        showDialog(getActivity(), "加载中01");
        driverId = sp.getString("uid", "");
        Map<String,String> params=new HashMap<>();
        params.put("driverid", driverId);
        params.put("state", "1");
        params.put("mobilemodel",mobilemodel);//手机型号
        params.put("systemmodel",systemmodel);//系统型号
        params.put("mobiletype","Android");//手机类别
        params.put("appversion",appversion);//系统版本
        com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkHttpUtil.post(HttpPath.WORK_PATH, params, this, new OkStringCallBack(getActivity()) {
            @Override
            public void myError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void myResponse(String response, int id) {
                //获取到数据
                String result = response;

                if (result != null) {
                    Logger.e("result=" + result);
                    try {
                        JSONObject obj1 = new JSONObject(result);
                        JSONObject obj2 = new JSONObject(obj1.optString("state"));
                        String code = obj2.optString("code");
                        if ("0".equals(code)) {
                            isLogin = true;
                            isonline = "1";
                            startWork();
                            getActivity().startService(MyApplication.service);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("work_status", 1);
                            editor.commit();
                        } else {
                            if(!pop_money.isShowing()){
                                pop_money.showAtLocation(view,Gravity.CENTER,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                            }
//                                                Toast.makeText(getActivity(), obj2.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        OkHttpUtil.Builder()
//                .setCacheLevel(CacheLevel.FIRST_LEVEL)
//                .setConnectTimeout(25).build(this)
//                .doPostAsync(
//                        HttpInfo.Builder().setUrl(HttpPath.WORK_PATH)
//                                .addParam("driverid", driverId)
//                                .addParam("state", "1")
//                                .addParam("mobilemodel",mobilemodel)//手机型号
//                                .addParam("systemmodel",systemmodel)//系统型号
//                                .addParam("mobiletype","Android")//手机类别
//                                .addParam("appversion",appversion)//系统版本
//                                .build(),
//                        new CallbackOk() {
//                            @Override
//                            public void onResponse(HttpInfo info) throws IOException {
//                                pd.dismiss();
//                                if (info.isSuccessful()) {
//                                    //获取到数据
//                                    String result = info.getRetDetail();
//
//                                    if (result != null) {
//                                        Logger.e("result=" + result);
//                                        try {
//                                            JSONObject obj1 = new JSONObject(result);
//                                            JSONObject obj2 = new JSONObject(obj1.optString("state"));
//                                            String code = obj2.optString("code");
//                                            if ("0".equals(code)) {
//                                                isLogin = true;
//                                                isonline = "1";
//                                                startWork();
//                                                getActivity().startService(MyApplication.service);
//                                                SharedPreferences.Editor editor = sp.edit();
//                                                editor.putInt("work_status", 1);
//                                                editor.commit();
//                                            } else {
//                                                if(!pop_money.isShowing()){
//                                                    pop_money.showAtLocation(view,Gravity.CENTER,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
//                                                }
////                                                Toast.makeText(getActivity(), obj2.getString("msg"), Toast.LENGTH_SHORT).show();
//                                            }
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else {
//                                        Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        });
    }

    //向服务器传递下班
    public void HttpGetOffWork() {
//        pd.show();
        String mobilemodel= Build.MANUFACTURER+"-"+Build.MODEL;//手机厂商+手机型号
        String systemmodel=android.os.Build.VERSION.RELEASE;//系统版本
        String appversion = "";//系统版本
        PackageManager packageManager = getActivity().getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo= packageManager.getPackageInfo("com.qf.rwxchina.xiaochefudriver", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo!=null){
            appversion = packageInfo.versionName;
        }
        Log.e("hrr",mobilemodel+"   "+systemmodel+"  "+appversion);
        Map<String,String> params=new HashMap<>();
        params.put("driverid", driverId);
        params.put("state", "2");
        params.put("mobilemodel",mobilemodel);//手机型号
        params.put("systemmodel",systemmodel);//系统型号
        params.put("mobiletype","Android");//手机类别
        params.put("appversion",appversion);//系统版本
        com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkHttpUtil.post(HttpPath.WORK_PATH, params, this, new OkStringCallBack(getActivity()) {
            @Override
            public void myError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void myResponse(String response, int id) {
                //获取到数据
                String result = response;
                if (result != null) {
                    Logger.t("limming").e("result=" + result);
                    try {
                        JSONObject obj1 = new JSONObject(result);
                        JSONObject obj2 = new JSONObject(obj1.optString("state"));
                        String code = obj2.optString("code");
                        if ("0".equals(code)) {
//                                                getActivity().stopService(MyApplication.service);
                            gooffwork();
                        } else {
                            Toast.makeText(getActivity(), obj2.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        showDialog(getActivity(), "加载中02");
//        OkHttpUtil.Builder()
//                .setCacheLevel(CacheLevel.FIRST_LEVEL)
//                .setConnectTimeout(25).build(this)
//                .doPostAsync(
//                        HttpInfo.Builder().setUrl(HttpPath.WORK_PATH)
//                                .addParam("driverid", driverId)
//                                .addParam("state", "2")
//                                .addParam("mobilemodel",mobilemodel)//手机型号
//                                .addParam("systemmodel",systemmodel)//系统型号
//                                .addParam("mobiletype","Android")//手机类别
//                                .addParam("appversion",appversion)//系统版本
//                                .build(),
//                        new CallbackOk() {
//                            @Override
//                            public void onResponse(HttpInfo info) throws IOException {
//                                pd.dismiss();
//                                if (info.isSuccessful()) {
//                                    //获取到数据
//                                    String result = info.getRetDetail();
//                                    if (result != null) {
//                                        Logger.t("limming").e("result=" + result);
//                                        try {
//                                            JSONObject obj1 = new JSONObject(result);
//                                            JSONObject obj2 = new JSONObject(obj1.optString("state"));
//                                            String code = obj2.optString("code");
//                                            if ("0".equals(code)) {
////                                                getActivity().stopService(MyApplication.service);
//                                                gooffwork();
//                                            } else {
//                                                Toast.makeText(getActivity(), obj2.getString("msg"), Toast.LENGTH_SHORT).show();
//                                            }
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else {
//                                        Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        });
    }

    /**
     * 上班
     */
    public void startWork() {
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putInt("work_status",1);
//        editor.commit();
        mWorkText.setText("上班中");
        mWorkText.setVisibility(View.VISIBLE);
        Glide.with(this).load(R.mipmap.gif_work).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mWork);
    }

    /**
     * 下班
     */
    public void gooffwork() {
        isonline = "0";
        int work_state = sp.getInt("work_status", 0);
        Logger.t("ssss").e("下班前work_state=" + work_state);
        if (work_state == 1 || work_state == 0) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("work_status", 0);
            editor.commit();
            mWorkText.setVisibility(View.GONE);
            Picasso.with(getContext()).load(R.drawable.icon_startwork).into(mWork);
        } else if (work_state == 2) {
            Toast.makeText(getContext(), "您还有订单未完成，请先完成订单", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 标记用户位置
     */
    public void setUserLocation() {
        Map<String,String> params=new HashMap<>();
        params.put("orderson", MyApplication.orderson);
        OkHttpUtil.post(HttpPath.orderdata,params,this, new OkStringCallBack(getActivity()) {
            @Override
            public void myError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void myResponse(String response, int id) {
                //获取到数据
                String result = response;

                if (result != null) {
                    try {
                        JSONObject object = new JSONObject(result);
                        JSONObject obj = new JSONObject(object.optString("data"));
                        userLat = obj.optDouble("slat");
                        userLng = obj.optDouble("slng");

                        //在地图上标记位置
                        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_red_sign);
                        LatLng latLng = new LatLng(userLat, userLng);
                        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(latLng, 14);
                        MarkerOptions options = new MarkerOptions().position(latLng).icon(bitmap);
                        mBaiduMap.addOverlay(options);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        Log.i("MSG2","走没走");
//        OkHttpUtil.Builder()
//                .setCacheLevel(CacheLevel.FIRST_LEVEL)
//                .setConnectTimeout(25).build(this)
//                .doPostAsync(
//                        HttpInfo.Builder()
//                                .setUrl(HttpPath.orderdata)
//                                .addParam("orderson", MyApplication.orderson)
//                                .build(),
//                        new CallbackOk() {
//                            @Override
//                            public void onResponse(HttpInfo info) throws IOException {
//                                if (info.isSuccessful()) {
//                                    //获取到数据
//                                    String result = info.getRetDetail();
//
//                                    if (result != null) {
//                                        try {
//                                            JSONObject object = new JSONObject(result);
//                                            JSONObject obj = new JSONObject(object.optString("data"));
//                                            userLat = obj.optDouble("slat");
//                                            userLng = obj.optDouble("slng");
//
//                                            //在地图上标记位置
//                                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_red_sign);
//                                            LatLng latLng = new LatLng(userLat, userLng);
//                                            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(latLng, 14);
//                                            MarkerOptions options = new MarkerOptions().position(latLng).icon(bitmap);
//                                            mBaiduMap.addOverlay(options);
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else {
//                                        Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mLocationClient.stop();
        if (getDriver != null) {
            Log.i("xianc","1");
            getDriver.interrupt();
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            mLocation = location;
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            lat = location.getLatitude();
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            lng = location.getLongitude();
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息

            locationAddress = location.getAddrStr();
            MyApplication.locationAddress = locationAddress;

            MyApplication.lat = location.getLatitude();
            MyApplication.lng = location.getLongitude();
//            Log.e("location","location=lat:"+MyApplication.lat+"\nlng:"+MyApplication.lng);

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360dhuiya
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);    //设置定位数据
            if (isFirstLoc) {
                mCity = location.getCity();
                checkCityId();
                mAddress.setText(location.getAddrStr());
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 13);    //设置地图中心点以及缩放级别
//				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }
    }

    //获取司机上下班状态
    private void getDriverState() {
//        showDialog(getActivity(), "加载中...");
//        Log.i("hrr","获取司机上下班状态"+HttpPath.URL+HttpPath.WORK_STATE+"/driverid/"+driverId);
        driverId = sp.getString("uid", "");
        Map<String,String> param=new HashMap<>();
        param.put("driverid", driverId);
        com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkHttpUtil.post(HttpPath.WORK_STATE, param, this, new OkStringCallBack(getActivity()) {
            @Override
            public void myError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void myResponse(String response, int id) {
                String result = response;
                if (result != null) {
                    Logger.json(result);
                    try {
                        JSONObject object = new JSONObject(result);
                        JSONObject state = object.getJSONObject("state");
                        String code = state.getString("code");
                        if (code != null && code.equals("0")) {
                            JSONObject data = object.getJSONObject("data");
                            isonline = data.getString("isonline");
                            Logger.t("limming").e("workstate=" + workstate + " isonline=" + isonline);
//                                                if (isLogin){
                            if (!TextUtils.isEmpty(isonline)) {
                                if (isonline.equals("0")) {
                                    workstate = 0;
                                    SharedPreferences.Editor spe = sp.edit();
                                    spe.putInt("work_status", 0);
                                    spe.commit();
                                    mWorkText.setText("点击开始\n上班");
                                    Glide.with(getActivity()).load(R.drawable.icon_startwork).into(mWork);
                                } else {
                                    workstate = Integer.parseInt(data.getString("work_status"));
                                    SharedPreferences.Editor spe = sp.edit();
                                    spe.putInt("work_status", workstate);
                                    spe.commit();
                                    isLogin = true;
                                    getActivity().startService(MyApplication.service);
                                    startWork();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), state.getString("msg")+"1", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
//        OkHttpUtil.Builder()
//                .setCacheLevel(CacheLevel.FIRST_LEVEL)
//                .setConnectTimeout(25).build()
//                .doPostAsync(
//                        HttpInfo.Builder()
//                                .setUrl(HttpPath.WORK_STATE)
//                                .addParam("driverid", driverId)
//                                .build(this),
//                        new CallbackOk() {
//                            @Override
//                            public void onResponse(HttpInfo info) throws IOException {
//                                dismissDialog();
//                                if (info.isSuccessful()) {
//                                    String result = info.getRetDetail();
//                                    if (result != null) {
//                                        Logger.json(result);
//                                        try {
//                                            JSONObject object = new JSONObject(result);
//                                            JSONObject state = object.getJSONObject("state");
//                                            String code = state.getString("code");
//                                            if (code != null && code.equals("0")) {
//                                                JSONObject data = object.getJSONObject("data");
//                                                isonline = data.getString("isonline");
//                                                Logger.t("limming").e("workstate=" + workstate + " isonline=" + isonline);
////                                                if (isLogin){
//                                                if (!TextUtils.isEmpty(isonline)) {
//                                                    if (isonline.equals("0")) {
//                                                        workstate = 0;
//                                                        SharedPreferences.Editor spe = sp.edit();
//                                                        spe.putInt("work_status", 0);
//                                                        spe.commit();
//                                                        mWorkText.setText("点击开始\n上班");
//                                                        Glide.with(getActivity()).load(R.drawable.icon_startwork).into(mWork);
//                                                    } else {
//                                                        workstate = Integer.parseInt(data.getString("work_status"));
//                                                        SharedPreferences.Editor spe = sp.edit();
//                                                        spe.putInt("work_status", workstate);
//                                                        spe.commit();
//                                                        isLogin = true;
//                                                        getActivity().startService(MyApplication.service);
//                                                        startWork();
//                                                    }
//                                                }
//                                            } else {
//                                                Toast.makeText(getActivity(), state.getString("msg")+"1", Toast.LENGTH_SHORT).show();
//                                            }
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                );
    }

    /**
     * 点击报单
     */
    public void needDriver(View view) {
        mStart.setText(mAddress.getText().toString());
        mPhone.setText("");
        mDestination.setText("");
        mDestination.setHint(R.string.hint);
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            // 这里是位置显示方式,在屏幕的底部
            popupWindow.showAtLocation(view, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    /**
     * 向后台请求生成一个报单订单
     */
    private void setBaoDan() {
        Map<String,String> params=new HashMap<>();
        params.put("cityid", cityId + "");
        params.put("driverID", driverId);
        params.put("sAddress", mStart.getText().toString());
        params.put("slng", lng + "");
        params.put("slat", lat + "");
        params.put("platenumber", mPhone.getText().toString());
        params.put("olng", dLng + "");
        params.put("olat", dLat + "");
        params.put("oAddress", mDestination.getText().toString());
        OkHttpUtil.post(HttpPath.NEEDDRIVER_PATH, params, this, new OkStringCallBack(getActivity()) {
            @Override
            public void myError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void myResponse(String response, int id) {
                //获取到数据
                String result = response;
                if (result != null) {
//                                        Log.e("kunlun","确认订单结果="+result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONObject object = jsonObject.getJSONObject("state");
                        String code = object.getString("code");
                        if (!TextUtils.isEmpty(code) && code.equals("0")) {//成功
                            popupWindow.dismiss();
                            String orderson= jsonObject.getString("data");
                            MyApplication.orderson = orderson;
                            // 写入
                            SharedPreferences.Editor editor = sp.edit();
                            //更改上班状态
                            editor.putInt("work_status", 2);
                            //保存orderson订单号到共享参数
                            editor.putString("orderson",orderson);
                            //提交
                            editor.commit();
                            Intent intent = new Intent(getActivity(), BaoDanActivity.class);
                            intent.putExtra("sAddress", mStart.getText().toString());
                            intent.putExtra("oAddress", mDestination.getText().toString());
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), object.getString("msg"+"3"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "数据获取失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
//        showDialog(getActivity(), "加载中03");

//        Log.e("hrr","http="+HttpPath.NEEDDRIVER_PATH+"?cityid="+cityId+"&driverID="+driverId+"&sAddress="
//                +mStart.getText().toString()+"&slng="+lng+"&slat="+lat+"&platenumber="
//                +mPhone.getText().toString()+"&olng="+dLng+"&olat="+dLat+"&oAddress="
//                +mDestination.getText().toString());
//        OkHttpUtil.Builder()
//                .setCacheLevel(CacheLevel.FIRST_LEVEL)
//                .setConnectTimeout(25).build(getContext())
//                .doPostAsync(
//                        HttpInfo.Builder().setUrl(HttpPath.NEEDDRIVER_PATH)
//                                .addParam("cityid", cityId + "")
//                                .addParam("driverID", driverId)
//                                .addParam("sAddress", mStart.getText().toString())//出发地
//                                .addParam("slng", lng + "")
//                                .addParam("slat", lat + "")
//                                .addParam("platenumber", mPhone.getText().toString())
//                                .addParam("olng", dLng + "")
//                                .addParam("olat", dLat + "")
//                                .addParam("oAddress", mDestination.getText().toString())
//                                .build(),
//                        new CallbackOk() {
//                            @Override
//                            public void onResponse(HttpInfo info) throws IOException {
//                                if (info.isSuccessful()) {
//                                    dismissDialog();
//                                    Log.e("hrr","info="+info.getRetDetail().toString());
//                                    //获取到数据
//                                    String result = info.getRetDetail();
//                                    if (result != null) {
////                                        Log.e("kunlun","确认订单结果="+result);
//                                        try {
//                                            JSONObject jsonObject = new JSONObject(result);
//                                            JSONObject object = jsonObject.getJSONObject("state");
//                                            String code = object.getString("code");
//                                            if (!TextUtils.isEmpty(code) && code.equals("0")) {//成功
//                                                popupWindow.dismiss();
//                                                String orderson= jsonObject.getString("data");
//                                                MyApplication.orderson = orderson;
//                                                // 写入
//                                                SharedPreferences.Editor editor = sp.edit();
//                                                //更改上班状态
//                                                editor.putInt("work_status", 2);
//                                                //保存orderson订单号到共享参数
//                                                editor.putString("orderson",orderson);
//                                                //提交
//                                                editor.commit();
//                                                Intent intent = new Intent(getActivity(), BaoDanActivity.class);
//                                                intent.putExtra("sAddress", mStart.getText().toString());
//                                                intent.putExtra("oAddress", mDestination.getText().toString());
//                                                startActivity(intent);
//                                            } else {
//                                                Toast.makeText(getActivity(), object.getString("msg"+"3"), Toast.LENGTH_SHORT).show();
//                                            }
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else {
//                                        dismissDialog();
//                                        Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
//
//                                    }
//                                }
//                            }
//                        });

    }

    /**
     * 获取城市id
     */
    public void checkCityId() {
        Map<String,String> params=new HashMap<>();
        params.put("cityName", mCity);
        OkHttpUtil.post(HttpPath.CHECKCITY, params, this, new OkStringCallBack() {
            @Override
            public void myError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void myResponse(String response, int id) {
                //获取到数据
                String result = response;
                if (result != null) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(result);
                        JSONArray arr = new JSONArray(object.optString("data"));
                        JSONObject obj = arr.getJSONObject(0);

                        cityId = obj.optInt("id");
                        MyApplication.cityId = cityId;
//                                            Log.e("kunlun","cityId="+cityId);

                    } catch (JSONException e) {
                        e.printStackTrace();
//                                            Log.e("kunlun","e="+e.toString());
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = object.getJSONObject("state");
                            String msg = jsonObject.getString("code");
                            if (msg.equals("1")) {
                                AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
                                ab.setMessage("该城市尚未开通服务,敬请期待")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        OkHttpUtil.Builder()
//                .setCacheLevel(CacheLevel.FIRST_LEVEL)
//                .setConnectTimeout(25).build(getContext())
//                .doPostAsync(
//                        HttpInfo.Builder().setUrl(HttpPath.CHECKCITY)
//                                .setTag(this)
//                                .addParam("cityName", mCity)
//                                .build(),
//                        new CallbackOk() {
//                            @Override
//                            public void onResponse(HttpInfo info) throws IOException {
//                                if (info.isSuccessful()) {
//                                    //获取到数据
//                                    String result = info.getRetDetail();
////                                    Log.e("kunlun","result="+result);
//                                    if (result != null) {
//                                        JSONObject object = null;
//                                        try {
//                                            object = new JSONObject(result);
//                                            JSONArray arr = new JSONArray(object.optString("data"));
//                                            JSONObject obj = arr.getJSONObject(0);
//
//                                            cityId = obj.optInt("id");
//                                            MyApplication.cityId = cityId;
////                                            Log.e("kunlun","cityId="+cityId);
//
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
////                                            Log.e("kunlun","e="+e.toString());
//                                            JSONObject jsonObject = null;
//                                            try {
//                                                jsonObject = object.getJSONObject("state");
//                                                String msg = jsonObject.getString("code");
//                                                if (msg.equals("1")) {
//                                                    AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
//                                                    ab.setMessage("该城市尚未开通服务,敬请期待")
//                                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                                                @Override
//                                                                public void onClick(DialogInterface dialog, int which) {
//                                                                    dialog.dismiss();
//                                                                }
//                                                            })
//                                                            .show();
//                                                }
//                                            } catch (JSONException e1) {
//                                                e1.printStackTrace();
//                                            }
//
//                                        }
//                                    } else {
//
//                                    }
//                                }
//                            }
//                        });
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getDriver != null) {
            getDriver.interrupt();
        }
    }


    /**
     * 网络请求司机数据
     */
    private void NetRequest() {
        driver = new ArrayList<>();
        getDriver =new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    getDr();
                    try {
                        Thread.sleep(10000);
                        Log.e("wh", "加载了司机位置");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        getDriver.start();


    }

    public  void getDr() {
        driver.clear();
        //获取当前经纬度
        Location myLocation = new LocationXY().init(getActivity());
        sLng = myLocation.getLongitude();
        sLat = myLocation.getLatitude();
        Map<String,String> params=new HashMap<>();
        params.put("lng", sLng + "");
        params.put("lat", sLat + "");
        OkHttpUtil.post(HttpPath.MAIN_PATH, params, this, new OkStringCallBack() {
            @Override
            public void myError(Call call, Exception e, int id) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void myResponse(String response, int id) {
                driverInfo = response;
                if (driverInfo != null) {
                    Log.e("lililiming", "结果=" + driverInfo);
                    try {
                        JSONObject obj1 = new JSONObject(driverInfo);
                        JSONArray arr = new JSONArray(obj1.optString("data"));
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj2 = arr.getJSONObject(i);
                            DriverInfo dinfo = new DriverInfo();
                            dinfo.setDriverid(obj2.optInt("driverid"));
                            driverid = String.valueOf(obj2.optInt("driverid"));
                            dinfo.setHead_image(obj2.optString("head_image"));
                            dinfo.setName(obj2.optString("name"));
                            dinfo.setAvglevel(obj2.optInt("avglevel"));
                            dinfo.setDriving_years(obj2.optInt("driving_years"));
                            dinfo.setWork_status(obj2.optInt("work_status"));
                            dinfo.setAgentsum(obj2.optInt("agentsum"));
                            dinfo.setLng(obj2.optDouble("lng"));
                            dinfo.setLat(obj2.optDouble("lat"));
                            dinfo.setDistance(obj2.optString("distance"));
                            dinfo.setWork_number(obj2.optString("work_number"));
                            driver.add(dinfo);
                        }
                        if (driver.size() != 0) {
                            handler.sendEmptyMessage(100);
                        } else {
                            handler.sendEmptyMessage(101);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        OkHttpUtil.Builder()
//                .setCacheLevel(CacheLevel.FIRST_LEVEL)
//                .setConnectTimeout(25)
//                .setTag(this)
//                .build()
//                .doPostAsync(
//                        HttpInfo.Builder().setUrl(HttpPath.MAIN_PATH)
//                                .addParam("lng", sLng + "")
//                                .addParam("lat", sLat + "")
//                                .build(),
//                        new CallbackOk() {//返回结果
//                            @Override
//                            public void onResponse(HttpInfo info) throws IOException {
//                                if (info.isSuccessful()) {
//                                    driverInfo = info.getRetDetail();
//                                    if (driverInfo != null) {
//                                        Log.e("lililiming", "结果=" + driverInfo);
//                                        try {
//                                            JSONObject obj1 = new JSONObject(driverInfo);
//                                            JSONArray arr = new JSONArray(obj1.optString("data"));
//                                            for (int i = 0; i < arr.length(); i++) {
//                                                JSONObject obj2 = arr.getJSONObject(i);
//                                                DriverInfo dinfo = new DriverInfo();
//                                                dinfo.setDriverid(obj2.optInt("driverid"));
//                                                driverid = String.valueOf(obj2.optInt("driverid"));
//                                                dinfo.setHead_image(obj2.optString("head_image"));
//                                                dinfo.setName(obj2.optString("name"));
//                                                dinfo.setAvglevel(obj2.optInt("avglevel"));
//                                                dinfo.setDriving_years(obj2.optInt("driving_years"));
//                                                dinfo.setWork_status(obj2.optInt("work_status"));
//                                                dinfo.setAgentsum(obj2.optInt("agentsum"));
//                                                dinfo.setLng(obj2.optDouble("lng"));
//                                                dinfo.setLat(obj2.optDouble("lat"));
//                                                dinfo.setDistance(obj2.optString("distance"));
//                                                dinfo.setWork_number(obj2.optString("work_number"));
//                                                driver.add(dinfo);
//                                            }
//                                            if (driver.size() != 0) {
//                                                handler.sendEmptyMessage(100);
//                                            } else {
//                                                handler.sendEmptyMessage(101);
//                                            }
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else {
//                                        Toast.makeText(getContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        }
//                );
    }




    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    mBaiduMap.clear();
                    for (int i = 0; i < driver.size(); i++) {
                        test(driver.get(i));
                    }
                    break;
                case 101:
//                    Toast.makeText(getContext(), "附近没有代驾司机", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void test(DriverInfo driverInfo) {
        if (getActivity() != null) {
            myView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_out_driver, null);
            DriverInfo drivers = driverInfo;
            if (drivers!=null) {
                CircleImageView mImg = (CircleImageView) myView.findViewById(R.id.layout_out_driver_img);
                if (!TextUtils.isEmpty(drivers.getHead_image())) {
                    Picasso.with(getContext()).load(drivers.getHead_image()).placeholder(R.drawable.logo).error(R.drawable.logo).into(mImg);
                }
                TextView mName = (TextView) myView.findViewById(R.id.layout_out_driver_name);
                TextView mWork_Number = (TextView) myView.findViewById(R.id.layout_out_driver_work_num);
                mName.setText(drivers.getName());
                mWork_Number.setText(drivers.getWork_number());
                RatingBar mRatingBar = (RatingBar) myView.findViewById(R.id.layout_out_driver_ratingbar);
                Logger.t("MapFragment").e("ratingBar="+drivers.getAvglevel());
                mRatingBar.setRating(drivers.getAvglevel());
                myView.setBackgroundResource(R.drawable.icon_driver_background);
                bd = BitmapDescriptorFactory.fromView(myView);
                pt = new LatLng(drivers.getLat(),drivers.getLng());
                options = new MarkerOptions().position(pt).icon(bd).title(String.valueOf(drivers.getDriverid()));
                mBaiduMap.addOverlay(options);
            }
        }
    }
}
