package com.qf.rwxchina.xiaochefudriver.Utils.toastutils;

import android.content.Context;
import android.widget.Toast;

import com.qf.rwxchina.xiaochefudriver.MyApplication;

/**
 * Created by Administrator on 2017/7/17.
 * toast工具类
 */

public class ToastUtil {
    public static Toast toast;

    /**
     * 短时间显示toast
     * @param msg
     */
    public static void showShort(CharSequence msg){
        if (toast==null){
            toast=Toast.makeText(MyApplication.getContext(),msg,Toast.LENGTH_SHORT);
        }else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 长时间显示toast
     * @param msg
     */
    public static void showLong(CharSequence msg){
        if (toast==null){
            toast=Toast.makeText(MyApplication.getContext(),msg,Toast.LENGTH_LONG);
        }else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 隐藏toast
     */
    public static void hideToast(){
        if (toast!=null){
            toast.cancel();
        }
    }

}
