package com.qf.rwxchina.xiaochefudriver.Order;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单
 */
public class OrderFragment extends Fragment implements OnClickListener{

    private TabLayout mTab;       //代金券使用情况选项卡
    private ViewPager mContent;   //具体内容
    private List<String> tablist = new ArrayList<>();//选项卡内容
    private List<View> views = new ArrayList<>();
    private PagerAdapter pagerAdapter;//适配器
    TextView bianji;//编辑

    //判断编辑或删除
     boolean fig=true;
    Historical_order_OK frgment = new Historical_order_OK();
    Historical_order_cancel frgment_cancel= new Historical_order_cancel();
    Historical_order_NO historical_order_no=new Historical_order_NO();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.historical_order, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        initViews();
        initTab();
        findView();
        setTab();

    }

    private void initViews() {
        views.clear();
    }


    /**
     * 初始化tab数据
     */
    private void initTab() {
        tablist.add("已完成");
        tablist.add("未完成");
        tablist.add("已取消");
    }

    /**
     * 设置tab
     */
    private void setTab() {
        pagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager(), tablist);
        mContent.setAdapter(pagerAdapter);
        //将tablelayout和ViewPager关联起来
        mTab.setupWithViewPager(mContent);
        mTab.setTabsFromPagerAdapter(pagerAdapter);

//         //滑动监听事件
//        mTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                switch (tab.getText().toString()) {
//                    case "已完成":
//                        bianji.setVisibility(View.VISIBLE);
//
//                        break;
//                    case "未完成":
//                        bianji.setVisibility(View.GONE);
//
//                        break;
//                    case "已取消":
//                        bianji.setVisibility(View.VISIBLE);
//
//                        break;
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }

    /**
     * 设置tab
     */
    private void findView() {
        mContent = (ViewPager) getView().findViewById(R.id.activity_voucher_content);
        mContent.setOffscreenPageLimit(0);
        mTab = (TabLayout) getView().findViewById(R.id.activity_voucher_state);
        bianji = (TextView) getView().findViewById(R.id.bianji);
        bianji.setOnClickListener(this);
        mTab.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < 3; i++) {
            mTab.addTab(mTab.newTab().setText(tablist.get(i)));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.bianji:
                if (fig) {

                    if(MyApplication.bianji.equals("0"))
                    {
                        frgment.fr_true();
                    }else
                    {  frgment.fr_true();
                        frgment_cancel.fr_true();
                    }


                    bianji.setText("删除");
                    fig=false;
                }else
                {
                    if(MyApplication.bianji.equals("0"))
                    {
                        frgment.fr_fal();
                    }else
                    {  frgment.fr_fal();
                        frgment_cancel.fr_fal();
                    }
                    bianji.setText("编辑");
                    fig=true;
                }

                break;
        }

    }


    /**
     * 历史订单PagerAdapter适配器
     */
    public class PagerAdapter extends FragmentPagerAdapter {
        private static final int PAGE_COUNT = 3;
        private Context context;
        private List<String> tablist;

        public PagerAdapter(FragmentManager fm, List<String> tablist) {
            super(fm);
            this.context = context;
            this.tablist = tablist;
        }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {//已完成
            return frgment;
        } else if (position == 1) {//未完成
            return historical_order_no;
        } else if (position == 2) {//已取消

            return  frgment_cancel;
        }
        return null;

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tablist.get(position);
    }
}



}
