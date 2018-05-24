package com.qf.rwxchina.xiaochefudriver.Utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AnalyticalJSON {
    /** 解析得到HashMap。{"":"","":""}*/
    public static HashMap<String, String> getHashMap(String json) {
        HashMap<String, String> item = new HashMap<String, String>();
        try {
            JSONObject json_data = new JSONObject(json);
            Iterator<String> keysIterator = json_data.keys();
            while (keysIterator.hasNext()) {
                String key = (String) keysIterator.next();
                item.put(key, json_data.getString(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("zhou", "e==" + e.getMessage());
            return null;
        }
        return item;
    }

    /** 解析得到List。{[{"":""},{"":""}]}*/
    public static ArrayList<HashMap<String, String>> getList(String json) {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray result = jsonObj.getJSONArray("result");
            if (result == null) {
                return null;
            }
            for (int i = 0; i < result.length(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject json_data = result.getJSONObject(i);
                Iterator<String> keysIterator = json_data.keys();
                while (keysIterator.hasNext()) {
                    String key = (String) keysIterator.next();
                    map.put(key, json_data.getString(key));
                }
                data.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("zhou", "e==" + e.getMessage());
            return null;
        }
        return data;
    }

    /** 解析得到List。[{"":""},{"":""}]*/
    public static ArrayList<HashMap<String, String>> getList_zj(String json) {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        try {
            JSONArray result = new JSONArray(json);
            if (result == null) {
                return null;
            }
            for (int i = 0; i < result.length(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject json_data = result.getJSONObject(i);
                Iterator<String> keysIterator = json_data.keys();
                while (keysIterator.hasNext()) {
                    String key = (String) keysIterator.next();
                    map.put(key, json_data.getString(key));
                }
                data.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("zhou", "e==" + e.getMessage());
            return null;
        }
        return data;
    }

    /** 解析得到List。["",""]*/
    public static ArrayList<String> getList_string(String json) {
        ArrayList<String> data = new ArrayList<String>();
        try {
            JSONArray result = new JSONArray(json);
            if (result == null) {
                return null;
            }
            for (int i = 0; i < result.length(); i++) {
                data.add(result.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("zhou", "e==" + e.getMessage());
            return null;
        }
        return data;
    }

    /** 解析得到List。["",""]*/
    public static ArrayList<Integer> getList_int(String json) {
        ArrayList<Integer> data = new ArrayList<Integer>();
        try {
            JSONArray result = new JSONArray(json);
            if (result == null) {
                return null;
            }
            for (int i = 0; i < result.length(); i++) {
                data.add(result.getInt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("zhou", "e==" + e.getMessage());
            return null;
        }
        return data;
    }
}
