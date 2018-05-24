package com.qf.rwxchina.xiaochefudriver.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.qf.rwxchina.xiaochefudriver.Bean.DistanceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 司机行驶距离数据处理类
 */
public class DistanceInfoDao {
    private DBOpenHelper helper;
    private SQLiteDatabase db;

    public DistanceInfoDao(Context context) {
        helper = new DBOpenHelper(context);
    }

    /**
     * 添加数据
     *
     * @param distanceInfo
     */
    public void insert(DistanceInfo distanceInfo) {
        if (distanceInfo == null) {
            return;
        }
        db = helper.getWritableDatabase();
        String sql = "INSERT INTO milestone(distance,longitude,latitude) VALUES('" + distanceInfo.getDistance() + "','" + distanceInfo.getLongitude() + "','" + distanceInfo.getLatitude() + "')";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 得到最大版本
     */
    public int getMaxId() {
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(id) as id from milestone", null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndex("id"));
        }
        return -1;
    }

    /**
     * 添加数据，得到版本
     *
     * @param distanceInfo
     * @return
     */
    public synchronized int insertAndGet(DistanceInfo distanceInfo) {
        int result = -1;
        insert(distanceInfo);
        result = getMaxId();
        return result;
    }

    /**
     * 根据id获取距离数据
     *
     * @param id
     * @return
     */
    public DistanceInfo getById(int id) {
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from milestone WHERE id = ?", new String[]{String.valueOf(id)});
        DistanceInfo distanceInfo = null;
        if (cursor.moveToFirst()) {
            distanceInfo = new DistanceInfo();
            distanceInfo.setId(cursor.getColumnIndex("id"));
            distanceInfo.setDistance(cursor.getFloat(cursor.getColumnIndex("distance")));
            distanceInfo.setLongitude(cursor.getFloat(cursor.getColumnIndex("longitude")));
            distanceInfo.setLatitude(cursor.getFloat(cursor.getColumnIndex("latitude")));
        }
        cursor.close();
        db.close();
        return distanceInfo;
    }

    public List<DistanceInfo> quereall() {
        db = helper.getReadableDatabase();
        List<DistanceInfo> distanceInfos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * from milestone", null);//rawQuery
        if (cursor != null) {
            while (cursor.moveToNext()) {
                DistanceInfo distanceInfo = new DistanceInfo();
                distanceInfo.setId(cursor.getColumnIndex("id"));
                distanceInfo.setDistance(cursor.getFloat(cursor.getColumnIndex("distance")));
                distanceInfo.setLongitude(cursor.getFloat(cursor.getColumnIndex("longitude")));
                distanceInfo.setLatitude(cursor.getFloat(cursor.getColumnIndex("latitude")));
                distanceInfos.add(distanceInfo);
            }
            cursor.close();
            db.close();
        }
        return distanceInfos;
    }

    /**
     * 更新数据库中的距离数据
     *
     * @param distanceInfo
     */
    public void updateDistance(DistanceInfo distanceInfo) {
        if (distanceInfo == null) {
            return;
        }
        db = helper.getWritableDatabase();
        String sql = "update milestone set distance=" + distanceInfo.getDistance() + ",longitude=" + distanceInfo.getLongitude() + ",latitude=" + distanceInfo.getLatitude() + " where id = " + distanceInfo.getId();
        db.execSQL(sql);
        db.close();
    }

    public void delete(int id) {
        if (id == 0 || id < 0) {
            return;
        }
        db = helper.getWritableDatabase();
        String sql = "delete from milestone where id = " + id;
        db.execSQL(sql);
        db.close();
    }

    /**
     * 清空表数据
     */
    public void clear(){
        db=helper.getWritableDatabase();
        String sql="delete from milestone";
        db.execSQL(sql);
        db.close();
    }
}
