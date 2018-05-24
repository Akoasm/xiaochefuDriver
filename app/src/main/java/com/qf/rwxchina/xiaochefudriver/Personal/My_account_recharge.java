package com.qf.rwxchina.xiaochefudriver.Personal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkHttpUtil;
import com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil.OkStringCallBack;
import com.qf.rwxchina.xiaochefudriver.alipay.PayAppanger;
import com.qf.rwxchina.xiaochefudriver.alipay.PayFailActivity;
import com.qf.rwxchina.xiaochefudriver.alipay.PayOkActivity;
import com.qf.rwxchina.xiaochefudriver.alipay.PayResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/12.
 * 我的账户充值
 */
public class My_account_recharge extends Fragment implements OnClickListener {
    private View view;
    private Button bu_chongzhi;
    private LinearLayout lin_zhifu, lin_weixin, lin_yinlian;
    private ImageView zhifubao_img, weixin_img, yinlian_img;
    private EditText ed_chongzhi;
    private TextView y;
    private String pay = "12";
    private SharedPreferences sp;
    private String driverid,fBack;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_account_recharge, container, false);
//        IpaynowPlugin.getInstance().init(getActivity())./*取消检测微信、qq等安装情况*/unCkeckEnvironment();// 1.插件初始化
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("hrr","My_account_recharge2 "+MyApplication.yue);
        sp = getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        //司机ID
        driverid = sp.getString("uid", "");
        init();
        getYue();
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i("hrr","My_account_recharge "+MyApplication.yue);
    }

    public void init() {
        lin_zhifu = (LinearLayout) view.findViewById(R.id.lin_zhifu);
        lin_weixin = (LinearLayout) view.findViewById(R.id.lin_weixin);
        lin_yinlian = (LinearLayout) view.findViewById(R.id.lin_yinlian);
        zhifubao_img = (ImageView) view.findViewById(R.id.zhifubao_img);
        weixin_img = (ImageView) view.findViewById(R.id.weixin_img);
        yinlian_img = (ImageView) view.findViewById(R.id.yinlian_img);
        ed_chongzhi = (EditText) view.findViewById(R.id.ed_chongzhi);
        bu_chongzhi = (Button) view.findViewById(R.id.bu_chongzhi);
        y= (TextView) view.findViewById(R.id.yu);
        lin_zhifu.setOnClickListener(this);
        lin_weixin.setOnClickListener(this);
        lin_yinlian.setOnClickListener(this);
        bu_chongzhi.setOnClickListener(this);
        lin_weixin.setVisibility(View.GONE);
        lin_yinlian.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //支付宝支付
            case R.id.lin_zhifu:
                zhifubao_img.setImageResource(R.drawable.icon_select_ture);
                weixin_img.setImageResource(R.drawable.icon_select_false);
                yinlian_img.setImageResource(R.drawable.icon_select_false);
                pay = "12";
                break;
            //微信支付
            case R.id.lin_weixin:
                zhifubao_img.setImageResource(R.drawable.icon_select_false);
                weixin_img.setImageResource(R.drawable.icon_select_ture);
                yinlian_img.setImageResource(R.drawable.icon_select_false);
                pay = "13";
                break;
            //银联支付
            case R.id.lin_yinlian:
                zhifubao_img.setImageResource(R.drawable.icon_select_false);
                weixin_img.setImageResource(R.drawable.icon_select_false);
                yinlian_img.setImageResource(R.drawable.icon_select_ture);
                pay = "11";
                break;
            //充值
            case R.id.bu_chongzhi:
                if (ed_chongzhi.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "请输入充值金额", Toast.LENGTH_SHORT).show();
                } else {
                    z();
                }

                break;
        }
    }

    /**
     * 支付
     */
    public void z() {
        Map<String,String> params=new HashMap<String,String>();
        params.put("driverID",driverid);
        params.put("paytype",pay);
        params.put("money",ed_chongzhi.getText().toString());
        OkHttpUtil.post(HttpPath.recharge, params, this, new OkStringCallBack(getContext()) {
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
                    HashMap<String, String> list_data = AnalyticalJSON.getHashMap(map.get("data"));
                    if (list.get("code").equals("0")) {
                        //获取当前时间
                        SimpleDateFormat sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd   hh:mm:ss");
                        String date = sDateFormat.format(new Date());
                        PayAppanger.getInstance().setGoods_name("司机充值");
                        PayAppanger.getInstance().setMoney( ed_chongzhi.getText().toString());
                        PayAppanger.getInstance().setDate(date);
                        fBack=list_data.get("sign");
                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    } else {
                        Toast.makeText(getActivity(), list.get("msg"), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
//        showProgressDialog();
    }



//    @Override
//    public void onIpaynowTransResult(ResponseParams responseParams) {
//        Log.e("wh", "进入回调接口了》》");
//        String respCode = responseParams.respCode;
//        String errorCode = responseParams.errorCode;
//        String errorMsg = responseParams.respMsg;
//        StringBuilder temp = new StringBuilder();
//        if (respCode.equals("00")) {
//            temp.append("交易状态:成功");
//        } else if (respCode.equals("02")) {
//            temp.append("交易状态:取消");
//        } else if (respCode.equals("01")) {
//            temp.append("交易状态:失败").append("\n").append("错误码:").append(errorCode).append("原因:" + errorMsg);
//        } else if (respCode.equals("03")) {
//            temp.append("交易状态:未知").append("\n").append("原因:" + errorMsg);
//        } else {
//            temp.append("respCode=").append(respCode).append("\n").append("respMsg=").append(errorMsg);
//        }
//        Toast.makeText(getActivity(), "onIpaynowTransResult:" + temp.toString(), Toast.LENGTH_LONG).show();
//    }
//开启支付宝
private void postAlipay() {
    Runnable payRunnable = new Runnable() {
        @Override
        public void run() {
            PayTask alipay = new PayTask(getActivity());
            String result = alipay.pay(fBack, true);
            Message msg = new Message();
            msg.what = 2;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    };
    // 必须异步调用a
    Thread payThread = new Thread(payRunnable);
    payThread.start();
}

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: {
                    Log.i("hrr", "本服务器请求的数据=" + fBack);
                    postAlipay();
                    break;
                }
                case 2:
                    PayResult payResult=new PayResult((String)msg.obj);
//                    PayResult payResult = new PayResult((String) msg.obj);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    Log.i("hrr", "resultStatus=" + resultStatus);
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(getActivity(), "支付成功", Toast.LENGTH_SHORT).show();
                        Intent iok=new Intent(getActivity(),PayOkActivity.class);
                        Bundle b=new Bundle();
                        b.putString("name", PayAppanger.getInstance().getGoods_name());
                        b.putString("time",PayAppanger.getInstance().getDate());
                        b.putString("money",PayAppanger.getInstance().getMoney());
                        iok.putExtras(b);
                        startActivity(iok);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "支付失败", Toast.LENGTH_SHORT).show();
                        Intent ifail = new Intent(getActivity(), PayFailActivity.class);
                        startActivity(ifail);
                    }
                    break;
            }
        }
    };


    private void getYue(){
        Map<String,String> map=new HashMap<String,String>();
        map.put("driverID",driverid);
        OkHttpUtil.post(HttpPath.MINGXI, map, this, new OkStringCallBack(getContext()) {
            @Override
            public void myError(Call call, Exception e, int id) {

            }

            @Override
            public void myResponse(String response, int id) {
                Log.e("account_recharge",response);
                if (response != null) {
                    //将得到的json数据返回给HashMap
                    HashMap<String, String> map = AnalyticalJSON.getHashMap(response);
                    HashMap<String, String> data = AnalyticalJSON.getHashMap(map.get("data"));
                    HashMap<String, String> data_driver_account = AnalyticalJSON.getHashMap(data.get("driver_account"));
                    HashMap<String, String> state = AnalyticalJSON.getHashMap(map.get("state"));
                    if(state.get("code").equals("0")){
                        y.setText(data_driver_account.get("balance"));//余额
                    }else{
                        Toast.makeText(getActivity(), state.get("msg"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}
