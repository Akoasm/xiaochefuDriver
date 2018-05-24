package com.qf.rwxchina.xiaochefudriver.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qf.rwxchina.xiaochefudriver.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/27.
 */
public class GongGaoAdapter extends BaseAdapter {

    Context context;
    ArrayList<HashMap<String, String>> list_data;

    private SharedPreferences sp;
    int msgid;

    public GongGaoAdapter(Context context, ArrayList<HashMap<String, String>> list_data) {
        this.context = context;
        this.list_data = list_data;
    }


    @Override
    public int getCount() {
        return list_data.size();
    }

    @Override
    public Object getItem(int i) {
        return list_data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHoder viewHoder;
        if (view == null) {
            viewHoder = new ViewHoder();
            view = LayoutInflater.from(context).inflate(R.layout.gonggao_context, null);
            viewHoder.gonggao_title = (TextView) view.findViewById(R.id.gonggao_title);
            viewHoder.gonggao_createtime = (TextView) view.findViewById(R.id.gonggao_createtime);


            view.setTag(viewHoder);

        } else {
            viewHoder = (ViewHoder) view.getTag();
        }

        viewHoder.gonggao_title.setText(list_data.get(i).get("title"));
        viewHoder.gonggao_createtime.setText(list_data.get(i).get("createtime"));
        //判断是否是第一次进入
//        sp = context.getSharedPreferences("userInfo", context.MODE_PRIVATE);
//        msgid = sp.getInt("msgid", 0);
//        if (msgid != 0) {
//            for (int n = 0; n < msgid; n++) {
//
//                msgid = sp.getInt("msgid", 0);
//                viewHoder.gonggao_title.setTextColor(Color.GRAY);
//                viewHoder.gonggao_createtime.setTextColor(Color.GRAY);
//            }
//        }

        return view;
    }


    public class ViewHoder {
        TextView gonggao_title, gonggao_createtime;


    }
}
