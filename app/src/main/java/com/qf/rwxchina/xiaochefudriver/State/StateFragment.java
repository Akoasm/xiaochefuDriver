package com.qf.rwxchina.xiaochefudriver.State;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.Order.CurrentOrderActivity;
import com.qf.rwxchina.xiaochefudriver.R;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * 状态fragment
 */
public class StateFragment extends Fragment implements View.OnClickListener {


    int time = 0;
    private SharedPreferences sp;
    String driverid;
    String img, na, agentsum, avglevel;
    Button beginwait;
    TextView fragment_state_order;
    Intent intent;
    int workstate = 0;//1:空闲  2：服务中
    Boolean isLogin;
    ImageView fragment_state_img;
    TextView fragment_state_name;
    TextView fragment_state_num;
    RatingBar fragment_state_ratingBar;
    TextView fragment_state_working;//服务中
    TextView fragment_state_freeing;//空闲中
    View view;
    private ProgressDialog pd;
    private String isonline="0";//是否在线，0：不在线，1：在线
    private String driverId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_state, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init(view);
//        driverinfo();
    }

    private void init(View view) {
        sp = getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        driverid = sp.getString("uid", "");
        workstate = sp.getInt("work_status", 0);
        isLogin = sp.getBoolean("isLogin", false);
        //头像
        img = sp.getString("head_image", "");
        //名称
        na = sp.getString("name", "");
        //代驾次数
        agentsum = sp.getString("agentsum", "");
        //评星等级
        avglevel = sp.getString("avglevel", "");
        beginwait = (Button) view.findViewById(R.id.beginwait);
        fragment_state_order = (TextView) view.findViewById(R.id.fragment_state_order);
        fragment_state_img = (ImageView) view.findViewById(R.id.fragment_state_img);
        fragment_state_name = (TextView) view.findViewById(R.id.fragment_state_name);
        fragment_state_num = (TextView) view.findViewById(R.id.fragment_state_num);
        fragment_state_ratingBar = (RatingBar) view.findViewById(R.id.fragment_state_ratingBar);
        fragment_state_working = (TextView) view.findViewById(R.id.fragment_state_working);
        fragment_state_freeing = (TextView) view.findViewById(R.id.fragment_state_freeing);
        beginwait.setOnClickListener(this);
        fragment_state_order.setOnClickListener(this);


        //头像
        if (img.equals("")) {
            fragment_state_img.setImageResource(R.drawable.icon_account);
        } else {
            Picasso.with(getActivity()).load(img).error(R.drawable.icon_account).into(fragment_state_img);
        }

        //名称
        if (na.equals("")) {
            fragment_state_name.setText("小车夫");
        } else {
            fragment_state_name.setText(na);
        }

        //代驾次数
        if (agentsum.equals("")) {
            fragment_state_num.setText("未使用代驾");
        } else {
            fragment_state_num.setText("代驾次数:" + agentsum + "次");
        }

        //评星等级
        if (avglevel.equals("")) {
            fragment_state_ratingBar.setRating(0.0f);
        } else {

            fragment_state_ratingBar.setRating(Float.parseFloat(avglevel));
        }



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //当前订单
            case R.id.fragment_state_order:
                driverid = sp.getString("uid", "");
//                workstate = sp.getInt("work_status", 0);
                isLogin = sp.getBoolean("isLogin", false);
                if (isLogin) {
                    if (isonline.equals("0")) {
                        Toast.makeText(getActivity(), "你还没有上班", Toast.LENGTH_SHORT).show();
                    } else{
                        if (workstate == 1) {
                            Toast.makeText(getActivity(), "你还没有订单", Toast.LENGTH_SHORT).show();
                        } else {
                            intent = new Intent(getContext(), CurrentOrderActivity.class);
                            startActivity(intent);
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.beginwait:
                driverid = sp.getString("uid", "");
//                workstate = sp.getInt("work_status", 0);
                isLogin = sp.getBoolean("isLogin", false);
                if (isLogin) {
                    if (isonline.equals("0")) {
                        Toast.makeText(getActivity(), "你还没有上班", Toast.LENGTH_SHORT).show();
                    } else {
                        if (workstate == 1) {
                            Toast.makeText(getActivity(), "你还没有订单", Toast.LENGTH_SHORT).show();
                        } else {
                            Issure();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void Issure() {
        final Dialog dialog = new android.app.AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("是否开始等待？")
                .setPositiveButton("是", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        intent = new Intent(getContext(), StateActivity.class);

                        startActivity(intent);
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



    public  void driverinfo() {
        driverId = sp.getString("uid","");
        if ((driverId!=null)&&(!"".equals(driverId))) {
            showDialog(getActivity() , "加载中...000");
            //        Log.e("kunlun","driverid"+driverId);
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(60).build()
                    .doPostAsync(
                            HttpInfo.Builder()
                                    .setUrl(HttpPath.WORK_STATE)
                                    .addParam("driverid", driverId)
                                    .build(this),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()){
                                        String result=info.getRetDetail();
                                        if (result!=null){
//                                        Log.e("kunlun","driverinfo result="+result);
                                            try {
                                                JSONObject object=new JSONObject(result);
                                                JSONObject state=object.getJSONObject("state");
                                                String code=state.getString("code");
                                                if (code!=null&&code.equals("0")){
                                                    JSONObject data= object.getJSONObject("data");
                                                    isonline=data.getString("isonline");
                                                    workstate= Integer.parseInt(data.getString("work_status"));
//                                                Logger.t("xcf_state").e("workstate="+workstate+" isonline="+isonline);
                                                    if (workstate==2){//服务状态
                                                        fragment_state_freeing.setTextColor(Color.parseColor("#000000"));
                                                        fragment_state_working.setTextColor(Color.parseColor("#FFFFFF"));
                                                        fragment_state_freeing.setBackgroundResource(R.drawable.icon_white_garden);
                                                        fragment_state_working.setBackgroundResource(R.drawable.icon_yeellow_garden);
                                                    }else {//空闲状态
                                                        fragment_state_freeing.setTextColor(Color.parseColor("#FFFFFF"));
                                                        fragment_state_working.setTextColor(Color.parseColor("#000000"));
                                                        fragment_state_freeing.setBackgroundResource(R.drawable.icon_yeellow_garden);
                                                        fragment_state_working.setBackgroundResource(R.drawable.icon_white_garden);
                                                    }
//                                                if (isLogin){
//                                                if (!TextUtils.isEmpty(isonline)){
//                                                    if (isonline.equals("0")){
//                                                        fragment_state_freeing.setTextColor(Color.parseColor("#FFFFFF"));
//                                                        fragment_state_working.setTextColor(Color.parseColor("#000000"));
//                                                        fragment_state_freeing.setBackgroundResource(R.drawable.icon_yeellow_garden);
//                                                        fragment_state_working.setBackgroundResource(R.drawable.icon_white_garden);
//                                                    }else {
//                                                        fragment_state_freeing.setTextColor(Color.parseColor("#000000"));
//                                                        fragment_state_working.setTextColor(Color.parseColor("#FFFFFF"));
//                                                        fragment_state_freeing.setBackgroundResource(R.drawable.icon_white_garden);
//                                                        fragment_state_working.setBackgroundResource(R.drawable.icon_yeellow_garden);
//                                                    }
//                                                }
                                                }else {
                                                    Toast.makeText(getActivity(), state.getString("msg"), Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }else {
                                        Toast.makeText(getActivity(), "服务器请求失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );
//        Logger.e("空闲状态="+MyApplication.is_kongxian);
//        if (MyApplication.is_kongxian.equals("0")) {
//            fragment_state_freeing.setTextColor(Color.parseColor("#FFFFFF"));
//            fragment_state_working.setTextColor(Color.parseColor("#000000"));
//            fragment_state_freeing.setBackgroundResource(R.drawable.icon_yeellow_garden);
//            fragment_state_working.setBackgroundResource(R.drawable.icon_white_garden);
//        } else {
//            fragment_state_freeing.setTextColor(Color.parseColor("#000000"));
//            fragment_state_working.setTextColor(Color.parseColor("#FFFFFF"));
//            fragment_state_freeing.setBackgroundResource(R.drawable.icon_white_garden);
//            fragment_state_working.setBackgroundResource(R.drawable.icon_yeellow_garden);
//        }

            dismissDialog();
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        driverid = sp.getString("uid", "");
        workstate = sp.getInt("work_status", 0);
        isLogin = sp.getBoolean("isLogin", false);
        //头像
        img = sp.getString("head_image", "");
        //名称
        na = sp.getString("name", "");
        //代驾次数
        agentsum = sp.getString("agentsum", "");
        //评星等级
        avglevel = sp.getString("avglevel", "");

        //头像
        if (img.equals("")) {
            fragment_state_img.setImageResource(R.drawable.icon_account);
        } else {
            Picasso.with(getActivity()).load(img).error(R.drawable.icon_account).into(fragment_state_img);
        }

        //名称
        if (na.equals("")) {
            fragment_state_name.setText("小车夫");
        } else {
            fragment_state_name.setText(na);
        }

        //代驾次数
        if (agentsum.equals("")) {
            fragment_state_num.setText("未使用代驾");
        } else {
            fragment_state_num.setText("代驾次数:" + agentsum + "次");
        }

        //评星等级
        if (avglevel.equals("")) {
            fragment_state_ratingBar.setRating(0.0f);
        } else {

            fragment_state_ratingBar.setRating(Float.parseFloat(avglevel));
        }

        driverinfo();

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

}
