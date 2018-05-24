package com.qf.rwxchina.xiaochefudriver.Order;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;


/**
 * 价格表
 * 分为普通代驾CommonFragment和商务下单BusinessFragment
 */
public class PriceListActivity extends AppCompatActivity {
    private ImageView mBack;
    private TextView mCity;
    private TabLayout mTab;
    private ViewPager mViewPager;
    private String uid;
    private String tabTitles[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list);
        MyApplication.getInstance().addActivity(this);
        init();
        setViewPager();
        setOnClick();
    }

    private void setOnClick() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        mBack = (ImageView) findViewById(R.id.activity_price_list_back);
        mCity = (TextView) findViewById(R.id.activity_price_list_city);
        mTab = (TabLayout) findViewById(R.id.activity_price_list_tab);
        mViewPager = (ViewPager) findViewById(R.id.activity_price_list_viewPager);
        SharedPreferences sp = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        uid = sp.getString("uid","");
        if (TextUtils.isEmpty(uid)){
            tabTitles= new String[]{"普通代驾"};
        }else {
            tabTitles= new String[]{"普通代驾","商务下单"};
        }
    }

    private void setViewPager() {
        PaidPagerAdapter mAdapter = new PaidPagerAdapter(getSupportFragmentManager(),getApplicationContext());
        mViewPager.setAdapter(mAdapter);
        mTab.setupWithViewPager(mViewPager);
        mTab.setTabMode(TabLayout.MODE_FIXED);
    }

    /**
     *价格表中，普通代驾和商务下单的viewPager的适配器
     * @author zhangkunlun
     * 2016/09/19
     */
    public class PaidPagerAdapter extends FragmentPagerAdapter {
        private static final int PAY_COUNT = 2;
        private static final int PAY_COUNT_ONE=1;
        private Context context;

        public PaidPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }


        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return new CommonFragment();
            }
            if (position == 1){
                return new BusinessFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            if (TextUtils.isEmpty(uid)){
                return PAY_COUNT_ONE;
            }else {
                return PAY_COUNT;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
