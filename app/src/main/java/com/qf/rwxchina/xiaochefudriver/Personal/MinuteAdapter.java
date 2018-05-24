package com.qf.rwxchina.xiaochefudriver.Personal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qf.rwxchina.xiaochefudriver.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/20.
 */
public class MinuteAdapter extends BaseAdapter{
    Context mC;
    List<MinuteInfo> minuteInfos;

    public MinuteAdapter(Context mC) {
        this.mC = mC;
        this.minuteInfos=new ArrayList<MinuteInfo>();
    }

    public List<MinuteInfo> getMinuteInfos() {
        return minuteInfos;
    }

    public void setMinuteInfos(List<MinuteInfo> minuteInfos) {
        this.minuteInfos = minuteInfos;
    }

    @Override
    public int getCount() {
        return minuteInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return minuteInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(mC).inflate(R.layout.item_activity_minute,null);
            viewHolder.title= (TextView) convertView.findViewById(R.id.item_activity_minute_title);
            viewHolder.content= (TextView) convertView.findViewById(R.id.item_activity_minute_content);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(minuteInfos.get(position).getTitle());
        viewHolder.content.setText(minuteInfos.get(position).getContent());
        return convertView;
    }

    class ViewHolder{
        TextView title,content;
    }
}
