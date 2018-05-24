package com.qf.rwxchina.xiaochefudriver.Personal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qf.rwxchina.xiaochefudriver.Personal.activity.NoticeActivity;
import com.qf.rwxchina.xiaochefudriver.Personal.entity.Notice;
import com.qf.rwxchina.xiaochefudriver.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/12.
 */

public class NoticeAdapter extends BaseAdapter {
    private Context mC;
    private List<Notice> notices;
    private int mRightWidth = 0;
    /**
     * 侧滑时间监听
     */
    private IOnItemRightClickListener mListener = null;

    public interface IOnItemRightClickListener {
        void onRightClick(View v, int position);
    }


    //    public NoticeAdapter(Context mC) {
//        this.mC = mC;
//        this.notices=new ArrayList<Notice>();
//    }


    public NoticeAdapter(Context mC, int mRightWidth, IOnItemRightClickListener mListener) {
        this.mC = mC;
        this.notices = new ArrayList<>();
        this.mRightWidth = mRightWidth;
        this.mListener = mListener;
    }

    public void setNotices(List<Notice> notices) {
        this.notices = notices;
    }

    @Override
    public int getCount() {
        return notices.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder vh=null;
        if (view==null){
            vh=new ViewHolder();
            view= LayoutInflater.from(mC).inflate(R.layout.item_notice,null);
            vh.title= (TextView) view.findViewById(R.id.item_notice_title);
            vh.time= (TextView) view.findViewById(R.id.item_notice_time);
            vh.selet= (ImageView) view.findViewById(R.id.item_notice_selet);
            vh.item_right = view.findViewById(R.id.item_right);
            view.setTag(vh);
        }else {
            vh= (ViewHolder) view.getTag();
        }

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(mRightWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        vh.item_right.setLayoutParams(lp2);
        vh.item_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRightClick(v, i);
                }
            }
        });

        vh.title.setText(notices.get(i).getTitle());
        vh.time.setText(notices.get(i).getCreatetime());
        String isread=notices.get(i).getIsread();//消息是否已读 0-没有 1-已读
        if ("0".equals(isread)){
            vh.selet.setVisibility(View.VISIBLE);
        }else {
            vh.selet.setVisibility(View.GONE);
        }
        return view;
    }

    private class ViewHolder{
        private TextView title,time;
        private ImageView selet;
        private View item_right;
    }
}
