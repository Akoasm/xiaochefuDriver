package com.qf.rwxchina.xiaochefudriver.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/5/15 0015.
 */
public class GongxZTUtils {
    public static final String getusersurename(Context context){
        SharedPreferences sp=context.getSharedPreferences("userInfo",context.MODE_PRIVATE);
        return  sp.getString("n","");
    }
    public static final String getnamescard(Context context){
        SharedPreferences sp=context.getSharedPreferences("userInfo",context.MODE_PRIVATE);
        return  sp.getString("n","");
    }
}
