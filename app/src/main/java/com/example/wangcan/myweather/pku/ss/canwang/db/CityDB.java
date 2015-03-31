package com.example.wangcan.myweather.pku.ss.canwang.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.wangcan.myweather.bean.City;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangcan on 2015/3/30.
 */
public class CityDB {
    public static final String CITY_DB_NAME="city.db";
    public static final String CITY_TABLE_NAME="city";
    private SQLiteDatabase db;

    public CityDB(Context context,String path){
        db=context.openOrCreateDatabase(CITY_DB_NAME,Context.MODE_PRIVATE,null);
    }

    public List<City> getAllCity(){
        List<City> list = new ArrayList<>();
        Cursor c = db.rawQuery("select * from " + CITY_TABLE_NAME, null);//返回多行数据
        while (c.moveToNext()) {
            String province = c.getString(c.getColumnIndex("province"));
            String city = c.getString(c.getColumnIndex("city"));
            String number = c.getString(c.getColumnIndex("number"));
            String allPY = c.getString(c.getColumnIndex("allpy"));
            String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY = c.getString(c.getColumnIndex("firstpy"));

            City item = new City(province, city, number, firstPY, allPY, allFirstPY);
            list.add(item);
        }
        return list;
    }


}
