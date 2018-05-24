package com.qf.rwxchina.xiaochefudriver.Utils;

import android.content.Context;
import android.location.Location;

import com.qf.rwxchina.xiaochefudriver.MyApplication;

/**
 * 获取当前位置的x,y轴坐标
 * @author zhangkunlun
 * 2016/09/19
 */
public class LocationXY {
    private Location location = new Location("");

    public Location init(Context context) {
        location.setLatitude(MyApplication.lat);
        location.setLongitude(MyApplication.lng);

        return location;
    }
}
