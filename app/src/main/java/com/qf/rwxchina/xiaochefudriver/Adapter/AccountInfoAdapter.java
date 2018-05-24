package com.qf.rwxchina.xiaochefudriver.Adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.qf.rwxchina.xiaochefudriver.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/7/7.
 */

public class AccountInfoAdapter extends BaseAdapter{
    Context context;
    ArrayList<HashMap<String, String>> list;

    public AccountInfoAdapter(Context context,ArrayList<HashMap<String, String>>  list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHoder;
        if(convertView==null)
        {
            viewHoder=new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.accountinfo_listview_item,null);
            viewHoder.phone= (TextView) convertView.findViewById(R.id.accountinfo_phone);
            viewHoder.platform= (TextView) convertView.findViewById(R.id.accountinfo_platform);


            convertView.setTag(viewHoder);

        }else
        {
            viewHoder= (ViewHolder) convertView.getTag();
        }
        String phonenumber =String.format(list.get(position).get("phone")) ;
        String name =String.format(list.get(position).get("name")) ;
        viewHoder.phone.setText(Html.fromHtml("电话："+"<font color='#333333'>"+phonenumber+"</font>"));
        viewHoder.platform.setText(Html.fromHtml("平台："+"<font color='#ffc928'>"+name+"</font>"));
        return convertView;
    }
   private class ViewHolder{
       TextView phone;
       TextView platform;
    }
}
