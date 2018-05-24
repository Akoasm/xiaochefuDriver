package com.qf.rwxchina.xiaochefudriver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.qf.rwxchina.xiaochefudriver.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/8.
 * 司机保单
 */
public class DriverBaoDanAdapter extends BaseAdapter{

    ArrayList<HashMap<String, String>> list;
    Context context;
    public DriverBaoDanAdapter(ArrayList<HashMap<String, String>> list, Context context)
    {
        this.list=list;
        this.context=context;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHoder viewHoder;
        if(view==null)
        {
            viewHoder=new ViewHoder();
            view = LayoutInflater.from(context).inflate(R.layout.baodan_gonggao,null);
            viewHoder.bao_ping= (TextView) view.findViewById(R.id.bao_ping);
            viewHoder.bao_time= (TextView) view.findViewById(R.id.bao_time);


            view.setTag(viewHoder);

        }else
        {
            viewHoder= (ViewHoder) view.getTag();
        }

        viewHoder.bao_time.setText(list.get(i).get("reporttime"));
        viewHoder.bao_ping.setText("订单保单结算成功,平台扣取服务费"+list.get(i).get("platformmoney")+"元,当前账户余额"+list.get(i).get("report_balance")+"元！");




        return view;
    }

    public class  ViewHoder
    {
        TextView bao_time,bao_ping;


    }
}
