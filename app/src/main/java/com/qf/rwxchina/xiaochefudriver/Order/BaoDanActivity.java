package com.qf.rwxchina.xiaochefudriver.Order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by hrr
 * on 2017/2/20.
 * 报单页面
 */
public class BaoDanActivity extends Activity {
    private TextView sAddress,oAddress,carId;
    private String sAdd,oAdd,carid;
    private Button ok;
    private SharedPreferences sp ;
    private String driverID;
    private Toast toast=null;
    private EditText et_name,et_phone;
    private PopupWindow popupWindow;
    private RelativeLayout pop_cancle,pop_confim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baodan);
        MyApplication.getInstance().addActivity(this);
        init();
        initView();
        setData();
    }

    private void init(){

        Intent intent=getIntent();
        if (intent!=null){
            sAdd=intent.getStringExtra("sAddress");
            oAdd=intent.getStringExtra("oAddress");
        }
    }

    private void initView(){
        sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        driverID = sp.getString("uid","");
        sAddress= (TextView) findViewById(R.id.baodan_saddress);
        oAddress= (TextView) findViewById(R.id.baodan_oaddress);
        carId= (TextView) findViewById(R.id.baodan_carid);
        ok= (Button) findViewById(R.id.baodan_ok);
        et_name= (EditText) findViewById(R.id.baodan_name);
        et_phone= (EditText) findViewById(R.id.baodan_phone);
        View view = LayoutInflater.from(this).inflate(R.layout.popup_finish_declaration,null);
        pop_cancle = (RelativeLayout) view.findViewById(R.id.popup_finish_cancle);
        pop_confim = (RelativeLayout) view.findViewById(R.id.popup_finish_confim);
        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                    getlayoutColor();
                }else{
                    setlayoutColor();
                    popupWindow.showAtLocation(ok, Gravity.CENTER, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            }
        });
        pop_confim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok();
            }
        });
        pop_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                getlayoutColor();
            }
        });
        getOrder();
    }

    private void setData(){
        sAddress.setText(sAdd);
        oAddress.setText(oAdd);
        carId.setText(carid);
    }

    /**
     * 标记用户位置
     */
    public void getOrder(){
        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(this)
                .doPostAsync(
                        HttpInfo.Builder()
                                .setUrl(HttpPath.orderdata)
                                .addParam("orderson", MyApplication.orderson)
                                .build(),
                        new CallbackOk() {
                            @Override
                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()) {
                                    //获取到数据
                                    String result = info.getRetDetail();
                                    Log.e("hrr","result="+result);
                                    if (result != null) {
                                        try {
                                            JSONObject object = new JSONObject(result);
                                            JSONObject obj = new JSONObject(object.optString("data"));
                                            sAdd=obj.getString("saddress");
                                            oAdd=obj.getString("oaddress");
                                            carid=obj.getString("platenumber");
                                            setData();
//                                            userLat = obj.optDouble("slat");
//                                            userLng = obj.optDouble("slng");
//
//                                            //在地图上标记位置
//                                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_red_sign);
//                                            LatLng latLng = new LatLng(userLat,userLng);
//                                            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(latLng,14);
//                                            MarkerOptions options = new MarkerOptions().position(latLng).icon(bitmap);
//                                            mBaiduMap.addOverlay(options);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
    }

    private void ok(){
        String name=et_name.getText().toString();
        String phone=et_phone.getText().toString();
        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(BaoDanActivity.this)
                .doPostAsync(
                        HttpInfo.Builder().setUrl(HttpPath.NEEDDRIVER_OVERGO)
                                .addParam("driverID", driverID)
                                .addParam("orderson", MyApplication.orderson)
                                .addParam("phones", phone)
                                .addParam("username", name)
                                .build(), new CallbackOk() {
                            @Override
                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()){
                                    Log.e("hrr","info="+info.getRetDetail().toString());
                                    //获取到数据
                                    String result = info.getRetDetail();
                                    if (result != null) {
                                        try {
                                            JSONObject jsonObject=new JSONObject(result);
                                            JSONObject object=jsonObject.getJSONObject("state");
                                            String code=object.getString("code");
                                            if (!TextUtils.isEmpty(code)&&code.equals("0")){
                                                // 写入
                                                SharedPreferences.Editor editor = sp.edit();
                                                //更改上班状态
                                                editor.putInt("work_status",1);
                                                //提交
                                                editor.commit();
                                                Toast.makeText(getApplicationContext(),"结束成功",Toast.LENGTH_SHORT).show();
                                                finish();
                                            }else {
                                                Toast.makeText(getApplicationContext(),object.getString("msg"),Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (toast==null){
            toast=Toast.makeText(getApplicationContext(),"请完成当前订单",Toast.LENGTH_SHORT);
        }
        toast.show();
        return false;
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
    /*
* 改变背景颜色亮度,当点击window让屏幕变暗
*/
    protected void setlayoutColor() {
        // TODO Auto-generated method stub
        Float A = (float) .10f;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
//        daohang_layout.setAlpha(A);
    }
}
