package com.qf.rwxchina.xiaochefudriver.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class Network {
    public static boolean HttpTest(Context context) {
        boolean http = true;
        if (context!=null) {
            ConnectivityManager con = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = con.getActiveNetworkInfo();
            if (networkinfo == null || !networkinfo.isAvailable()) {
                try {
                    // 无网络
                    // toast可能在子线程中弹出，需要捕获异常
                    Toast.makeText(context.getApplicationContext(), "没网络了哦，请检查网络",
                            Toast.LENGTH_SHORT).show();
                }catch (RuntimeException e){
                    Log.e("NetWork","异常="+e.getMessage());
                }

                http = false;
            }
            boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting();
            if (!wifi) {
                // WIFI 不可用
            }
        }
        return http;
    }

    public static boolean is3g2g(Context context) {
        boolean http = false;
        ConnectivityManager con = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = con.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            // 无网络
        } else {
            boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting();
            if (!wifi) {
                // WIFI 不可用
                http = true;
            }
        }
        return http;
    }
}