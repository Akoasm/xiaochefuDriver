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
 * 用户评价
 */
public class DriverDetailsAdapter extends BaseAdapter{

    ArrayList<HashMap<String, String>> list;
    Context context;
    public DriverDetailsAdapter(ArrayList<HashMap<String, String>> list, Context context)
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
            view = LayoutInflater.from(context).inflate(R.layout.driver_details_context,null);
            viewHoder.co_context= (TextView) view.findViewById(R.id.co_context);
            viewHoder.co_time= (TextView) view.findViewById(R.id.co_time);
            viewHoder.co_bar= (RatingBar) view.findViewById(R.id.co_bar);

            view.setTag(viewHoder);

        }else
        {
            viewHoder= (ViewHoder) view.getTag();
        }

        viewHoder.co_context.setText(list.get(i).get("content"));
        viewHoder.co_time.setText(list.get(i).get("add_date"));
        viewHoder.co_bar.setRating(Float.parseFloat(list.get(i).get("level")));




        return view;
    }

    public class  ViewHoder
    {
        TextView co_context,co_time;
        RatingBar co_bar;

    }
}
