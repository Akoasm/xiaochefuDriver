package com.qf.rwxchina.xiaochefudriver.Utils;

import android.util.Base64;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.qf.rwxchina.xiaochefudriver.Bean.GpsLocation;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class BDLocation2GpsUtil {

   public static BDLocation tempBDLocation = new BDLocation();     // 临时变量，百度位置
   public static GpsLocation tempGPSLocation = new GpsLocation();  // 临时变量，gps位置

    public static enum Method{
        origin, correct;
    }

    private static final Method method = Method.correct;

    /**
     * 位置转换
     *
     * @param lBdLocation 百度位置
     * @return GPS位置
     */
    public static GpsLocation convertWithBaiduAPI(BDLocation lBdLocation) {
        switch (method) {
        case origin:    //原点
            GpsLocation location = new GpsLocation();
            location.lat = lBdLocation.getLatitude();
            location.lng = lBdLocation.getLongitude();
            Log.e("kunlun","原点x="+location.lng+"  原点y="+location.lat);
            return location;

        case correct:   //纠偏
                      //同一个地址不多次转换
            String url = "http://api.map.baidu.com/ag/coord/convert?from=0&to=4&"
                    + "x=" + lBdLocation.getLongitude() + "&y="
                    + lBdLocation.getLatitude();
            String result = executeHttpGet(url);
            Log.e("kunlun","result="+result);
            if (result != null) {
                GpsLocation gpsLocation = new GpsLocation();
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    String lngString = jsonObj.getString("x");
                    String latString = jsonObj.getString("y");
                    // 解码
                    double lng = Double.parseDouble(new String(Base64.decode(lngString,Base64.DEFAULT)));
                    double lat = Double.parseDouble(new String(Base64.decode(latString,Base64.DEFAULT)));
                    // 换算
                    gpsLocation.lng = 2 * lBdLocation.getLongitude() - lng;
                    gpsLocation.lat = 2 * lBdLocation.getLatitude() - lat;
                    Log.e("kunlun","gpsLocation.lng="+gpsLocation.lng+"  gpsLocation.lat="+gpsLocation.lat);
                    tempGPSLocation = gpsLocation;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("kunlun","e="+e.toString());
                    return null;
                }
                tempBDLocation = lBdLocation;
                Log.e("kunlun","tempBDLocation="+tempBDLocation.getLatitude()+"  "+tempBDLocation.getLongitude());
                return gpsLocation;
            }else{
                return null;
            }

        default:
            return null;
        }
    }

    private static String executeHttpGet(String requestUrl) {
        String result = null;
        URL url = null;
        HttpURLConnection connection = null;
        InputStreamReader in = null;
        try {
            url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            //设置连接超时和读超时
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            in = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuffer strBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
