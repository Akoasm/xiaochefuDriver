package com.qf.rwxchina.xiaochefudriver.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qf.rwxchina.xiaochefudriver.Bean.My_account_calance_Info;
import com.qf.rwxchina.xiaochefudriver.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/10/8.
 * 我的账户-余额
 */
public class MyBalanceAdapter extends BaseAdapter {

    int TYPE_A = 0;
    int TYPE_B = 1;

    ArrayList<HashMap<String, String>> list;
    Context context;

    public MyBalanceAdapter(ArrayList<HashMap<String, String>> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        String type = list.get(position).get("type");
        switch (type) {
            case "1":
                return TYPE_A;
            case "2":
                return TYPE_B;
            default:
                return TYPE_B;
        }

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHoder viewHoder = null;
        ViewHoder_Mon viewHoder_mon = null;
        //获取布局类型
        int type = getItemViewType(i);
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            switch (type) {
                case 0:
                    view = inflater.inflate(R.layout.my_account_balance_context, viewGroup, false);
                    viewHoder = new ViewHoder();
                    viewHoder.money = (TextView) view.findViewById(R.id.order_income);
                    viewHoder.yong = (TextView) view.findViewById(R.id.order_commission);
                    viewHoder.ti = (TextView) view.findViewById(R.id.order_ticheng);
                    view.setTag(viewHoder);
                    break;
                case 1:
                    viewHoder_mon = new ViewHoder_Mon();
                    view = inflater.inflate(R.layout.tixianitem, viewGroup, false);
                    viewHoder_mon.t = (TextView) view.findViewById(R.id.t);
                    viewHoder_mon.ti_money = (TextView) view.findViewById(R.id.ti_money);
                    viewHoder_mon.ti_date = (TextView) view.findViewById(R.id.ti_date);
                    view.setTag(viewHoder_mon);
                    break;
            }


        } else {
            switch (type) {
                case 0:
                    viewHoder = (ViewHoder) view.getTag();
                    break;
                case 1:
                    viewHoder_mon = (ViewHoder_Mon) view.getTag();
                    break;
            }


        }


        //赋值
        if (type == 0) {
            //平台佣金
            if (TextUtils.isEmpty(list.get(i).get("ext1"))) {
                viewHoder.yong.setText("0元");
            } else {
                viewHoder.yong.setText(list.get(i).get("ext1") + "元");
            }
            //订单收入
            if (TextUtils.isEmpty(list.get(i).get("income"))) {

                viewHoder.money.setText("0元");
            } else {
                viewHoder.money.setText(list.get(i).get("income") + "元");
            }

            //商户提成
            if (TextUtils.isEmpty(list.get(i).get("ext2"))) {
                viewHoder.ti.setText("0元");
            } else {
                viewHoder.ti.setText(list.get(i).get("ext2") + "元");
            }


        } else if (type == 1) {


            //提现金额
            if (list.get(i).get("type").equals("2")) {
                viewHoder_mon.t.setText("充值");
                viewHoder_mon.ti_money.setText(list.get(i).get("income") + "元");
            } else if (list.get(i).get("type").equals("3")) {
                viewHoder_mon.t.setText("提现");
                viewHoder_mon.ti_money.setText(list.get(i).get("spending") + "元");
            }


            //时间
            viewHoder_mon.ti_date.setText("("+list.get(i).get("add_date")+")");


        }


        return view;
    }

    public class ViewHoder {
        TextView money, yong, ti;


    }

    public class ViewHoder_Mon {
        TextView ti_money, ti_date, t;
    }
}
