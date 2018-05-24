package com.qf.rwxchina.xiaochefudriver.Utils;

/**
 * Created by Administrator on 2016/10/13.
 * 支付成功接口帮助类
 */
public class CallBackhelp {
   static PayOKInterface mPayOKInterface;

    public static void setinterface(PayOKInterface payOKInterface) {

        mPayOKInterface = payOKInterface;
    }

    public static void paytype(int type) {
        if (mPayOKInterface!=null){
            mPayOKInterface.paytype(type);
        }
    }
}
