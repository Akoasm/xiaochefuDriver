package com.qf.rwxchina.xiaochefudriver.Personal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hh.timeselector.timeutil.datedialog.DateListener;
import com.hh.timeselector.timeutil.datedialog.TimeConfig;
import com.hh.timeselector.timeutil.datedialog.TimeSelectorDialog;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.qf.rwxchina.xiaochefudriver.Adapter.MyBalanceAdapter;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.State.StateActivity;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.DateUtil;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by Administrator on 2016/9/12.
 * 我的账户余额
 */
public class My_account_balance extends Fragment implements View.OnClickListener{
    View view,contview;
    ListView list_balance;
    private SharedPreferences sp;
    private ImageView mMini,mAdd;
    private TextView mDate;
    private String dateText;

    private LinearLayout ti_ll;
    private TextView ti_money,ti_date;
    String  uid;
    TextView yu,all,expend;
    StateActivity stateActivity = new  StateActivity();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_account_balance, container, false);
        Log.i("hrr","My_account_balance");
        init();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("hrr","My_account_balance2");
        sp = getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        //用户id
        uid = sp.getString("uid", "");
        Balance();
    }
    public  void init()
    {
        // 拦截头部布局文件
//        contview = LayoutInflater.from(getActivity()).inflate(
//                R.layout.balance_head, null);
//
//
//        list_balance .addHeaderView(contview);
        list_balance= (ListView) view.findViewById(R.id.list_balance);
        yu= (TextView) view.findViewById(R.id.yu);
        all= (TextView) view.findViewById(R.id.all_money);
        expend= (TextView) view.findViewById(R.id.expend_money);
        mDate= (TextView) view.findViewById(R.id.date_text);

        ti_ll= (LinearLayout) view.findViewById(R.id.ti_ll);
        ti_money= (TextView) view.findViewById(R.id.ti_money);
        ti_date= (TextView) view.findViewById(R.id.ti_date);
        mAdd= (ImageView) view.findViewById(R.id.date_magnify);
        mMini= (ImageView) view.findViewById(R.id.date_minish);
        dateText=DateUtil.GetDate();
        mDate.setText(dateText);
        mAdd.setOnClickListener(this);
        mMini.setOnClickListener(this);
        mDate.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        Balance();
    }

    public void Balance() {

        if (Network.HttpTest(getActivity())) {
            stateActivity.showDialog(getActivity(),"余额加载中..");
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.MINGXI)
                                    .addParam("driverID",uid)
                                    .addParam("date",dateText)
                                    .build(),
                            new CallbackOk() {
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();
                                        Log.e("wh", "result="+result);
                                        if (result != null) {

                                            //将得到的json数据返回给HashMap
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> data = AnalyticalJSON.getHashMap(map.get("data"));
                                            HashMap<String, String> data_driver_account = AnalyticalJSON.getHashMap(data.get("driver_account"));
                                            ArrayList<HashMap<String, String>> billsinfo = AnalyticalJSON.getList_zj(data.get("driver_bills"));
                                            HashMap<String, String> state = AnalyticalJSON.getHashMap(map.get("state"));

                                            if(state.get("code").equals("0"))
                                            {
                                                yu.setText(data_driver_account.get("balance"));//余额
                                                MyApplication.yue=data_driver_account.get("balance");
                                                // 写入
                                                SharedPreferences.Editor editor = sp.edit();
                                                //用户余额
                                                editor.putString("yue",data_driver_account.get("balance"));
                                                Log.i("limming01","MyApplication.yue"+MyApplication.yue+"sss=="+sp.getString("yue",""));
                                                //提交
                                                editor.commit();
                                                all.setText(data_driver_account.get("income"));//总收入
                                                expend.setText(data_driver_account.get("spending"));//总支出

                                                MyBalanceAdapter my_account_balanceAdaPter=new MyBalanceAdapter(billsinfo,getActivity());
                                               list_balance.setAdapter(my_account_balanceAdaPter);

                                            }else
                                            {
                                                Toast.makeText(getActivity(), state.get("msg"), Toast.LENGTH_SHORT).show();
                                                stateActivity.dismissDialog();
                                            }

                                        } else {
                                            Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                            stateActivity.dismissDialog();

                                        }

                                    }else {
                                        Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
        }
        stateActivity.dismissDialog();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.date_minish://日期增加一天
                dateText= DateUtil.GetDateMini(DateUtil.StringForDate(dateText));
                mDate.setText(dateText);
                Balance();
                break;
            case R.id.date_magnify://日期减少一天
                dateText= DateUtil.GetDateAdd(DateUtil.StringForDate(dateText));
                mDate.setText(dateText);
                Balance();
                break;
            //选择时间
            case R.id.date_text:
                showDatePickDialog(TimeConfig.YEAR_MONTH_DAY);
                break;

        }
    }



      //时间弹框
    private void showDatePickDialog(int type) {
        TimeSelectorDialog dialog = new TimeSelectorDialog(getActivity());
        //设置标题
        dialog.setTimeTitle("选择时间:");
        //显示类型
        dialog.setIsShowtype(type);
        //默认时间
        dialog.setCurrentDate(DateUtil.GetDate());
        //隐藏清除按钮
        dialog.setEmptyIsShow(false);
        dialog.setDateListener(new DateListener() {
            @Override
            public void onReturnDate(String time,int year, int month, int day, int hour, int minute, int isShowType) {

                dateText=time;
                mDate.setText(time);
                Balance();
            }
            @Override
            public void onReturnDate(String empty) {
                //Toast.makeText(getActivity(),empty,Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();
    }

}
