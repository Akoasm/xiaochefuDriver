package com.qf.rwxchina.xiaochefudriver.Personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/12.
 * 我的账户
 */
public class My_account extends AppCompatActivity implements View.OnClickListener{
    private TabLayout mTab;       //代金券使用情况选项卡
    private ViewPager mContent;   //具体内容
    private List<String> tablist = new ArrayList<>();//选项卡内容
    private List<View> views = new ArrayList<>();
    private VoucherPagerAdapter pagerAdapter;//适配器
    private ImageView ivBack;//返回
    private ImageView requstion;
    private String type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);
        MyApplication.getInstance().addActivity(this);
        Intent intent=getIntent();
        type=intent.getStringExtra("type");
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
        tablist.add("余额");
        tablist.add("充值");
        tablist.add("提现");
    }

    /**
     * 设置tab

     */
    private void setTab() {
        pagerAdapter = new VoucherPagerAdapter(getSupportFragmentManager(),tablist);
        mContent.setAdapter(pagerAdapter);
        mContent.setOffscreenPageLimit(3);
        if ("mapFragment".equals(type)){
            mContent.setCurrentItem(1);//加载第几页，从0开始
        }else {
            mContent.setCurrentItem(0);
        }
//        mContent.setOffscreenPageLimit(0);
        //将tablelayout和ViewPager关联起来

        mTab.setupWithViewPager(mContent);
        mTab.setTabsFromPagerAdapter(pagerAdapter);
    }

    /**
     * 设置tab
     *

     */
    private void findView() {
        mContent = (ViewPager) findViewById(R.id.activity_voucher_content);
        mTab = (TabLayout) findViewById(R.id.activity_voucher_state);
        mTab.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < 3; i++) {
            mTab.addTab(mTab.newTab().setText(tablist.get(i)));
        }
        ivBack= (ImageView) findViewById(R.id.zhanghu_back);
        ivBack.setOnClickListener(this);
        requstion= (ImageView) findViewById(R.id.zhanghu_question);
        requstion.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.zhanghu_back:
                finish();
                break;
            case R.id.zhanghu_question:
                Intent intent=new Intent(this, MinuteActivity.class);
                startActivity(intent);
                break;
        }
    }
}
