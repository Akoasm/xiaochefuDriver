package com.qf.rwxchina.xiaochefudriver.Personal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;

/**
 * Created by Administrator on 2016/9/12.
 * 我的账户提现
 */
public class My_account_withdrawals extends Fragment implements View.OnClickListener {
    View view;
    LinearLayout lin_zhifu, lin_weixin, lin_yinlian;
    ImageView zhifubao_img, weixin_img, yinlian_img;
    TextView y;
    Button bt_tixian;

    private SharedPreferences sp;
    String driverid,phone,mon;
    EditText ed_tixian;
    String money;

    String n="1";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_account_withdrawals, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("hrr","My_account_withdrawals2");

        init();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            y = (TextView) view.findViewById(R.id.y);
            y.setText(MyApplication.yue);
        }
    }

    public void init() {
        sp = getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        //司机ID
        driverid = sp.getString("uid", "");
        phone=sp.getString("phone","");

        lin_zhifu = (LinearLayout) view.findViewById(R.id.lin_zhifu);
        lin_weixin = (LinearLayout) view.findViewById(R.id.lin_weixin);
        lin_yinlian = (LinearLayout) view.findViewById(R.id.lin_yinlian);
        zhifubao_img = (ImageView) view.findViewById(R.id.zhifubao_img);
        weixin_img = (ImageView) view.findViewById(R.id.weixin_img);
        yinlian_img = (ImageView) view.findViewById(R.id.yinlian_img);

        bt_tixian = (Button) view.findViewById(R.id.bt_tixian);
        ed_tixian = (EditText) view.findViewById(R.id.ed_tixian);
        lin_zhifu.setOnClickListener(this);
        lin_weixin.setOnClickListener(this);
        lin_yinlian.setOnClickListener(this);
        bt_tixian.setOnClickListener(this);
        sp = getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //支付宝
            case R.id.lin_zhifu:
                zhifubao_img.setImageResource(R.drawable.icon_select_ture);
                weixin_img.setImageResource(R.drawable.icon_select_false);
                yinlian_img.setImageResource(R.drawable.icon_select_false);
                n="1";
                break;
            //微信
            case R.id.lin_weixin:
                zhifubao_img.setImageResource(R.drawable.icon_select_false);
                weixin_img.setImageResource(R.drawable.icon_select_ture);
                yinlian_img.setImageResource(R.drawable.icon_select_false);
                n="2";
                break;
            //银联
            case R.id.lin_yinlian:
                zhifubao_img.setImageResource(R.drawable.icon_select_false);
                weixin_img.setImageResource(R.drawable.icon_select_false);
                yinlian_img.setImageResource(R.drawable.icon_select_ture);
                n="3";
                break;
            case R.id.bt_tixian:
               mon= ed_tixian.getText().toString();
                Log.e("wh","输入的金额》》》"+mon);
                if (mon.equals("")) {
                    Toast.makeText(getActivity(), "请输入金额", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent=new Intent(getActivity(),TiXingXiangQingActivity.class);
                    intent.putExtra("mon",mon);
                    intent.putExtra("type",n);
                    startActivity(intent);
                }


                break;
        }
    }







}
