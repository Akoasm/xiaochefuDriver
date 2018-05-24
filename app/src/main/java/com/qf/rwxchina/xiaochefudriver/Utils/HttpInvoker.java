package com.qf.rwxchina.xiaochefudriver.Utils;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Administrator on 2016/11/15.
 */
public class HttpInvoker {
    public static String HttpGetMethod(String get_url) {
        Log.d("zh1", "get_url1==" + get_url);
        String result = "";

        HttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, 15 * 1000);
        HttpConnectionParams.setSoTimeout(basicHttpParams, 15 * 1000);

        HttpClient htc = new DefaultHttpClient(basicHttpParams);// 创建HttpClien对象

        HttpGet request = new HttpGet(get_url);// 创建HttpGet连接对象

        request.setHeader("contentType","UTF-8");
        request.setHeader("Accept-Charset", "UTF-8");
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        HttpResponse response;
        try {
            response = htc.execute(request);// 执行HttpClien请求
            Log.i("TAG", "请求码" + response.getStatusLine().getStatusCode());
            // HttpStatus.SC_OK==200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
                if(result==null||result.equals("")){
                    return null;
                }
                Log.d("zh1", "result==" + result);
                Log.i("zh1", "获取返回的字符串");
            } else {
                result = null;
                Log.i("zh1", "请求失败");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            result = null;
            Log.i("zh1", "请求错误");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("zh1", "请求错误" + e.getMessage());
            result = null;
        }
        return result;
    }
}
