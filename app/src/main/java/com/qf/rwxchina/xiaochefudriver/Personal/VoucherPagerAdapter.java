package com.qf.rwxchina.xiaochefudriver.Personal;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import java.util.List;

/**
 * 我都账户ViewPager适配器
 *
 *
 */
public class VoucherPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 3;
    private Context context;
    private List<String> tablist;

    public VoucherPagerAdapter(FragmentManager fm,List<String> tablist) {
        super(fm);
        this.context = context;
        this.tablist=tablist;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {//我的账户余额
            return new My_account_balance();
        } else if (position == 1) {//我都账户充值
            return new My_account_recharge();
        } else if (position == 2) {//我的账户提现
            return new My_account_withdrawals();
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
