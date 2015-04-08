package com.example.wangcan.myweather.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.example.wangcan.myweather.bean.City;
import com.example.wangcan.myweather.pku.ss.canwang.db.CityDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangcan on 2015/3/30.
 */
public class MyApplication extends Application {
    private static final String tag="MYAPP";
    private static Application mApplication;
    private static CityDB mCityDB;//返回数据库对象
    private static List<City> mCityList;//用于存储数据库返回数据
    @Override
    public void onCreate(){
        super.onCreate();
        Log.v(tag,"MyApplication-->onCreate()");
        mApplication=this;
        mCityDB=openCityDB();
        //initCityList();
    }

    public static Application getInstance(){
        return mApplication;
    }

    private CityDB openCityDB(){
        String path="/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases"
                + File.separator
                + CityDB.CITY_DB_NAME;
        //打开db文件
        File db=new File(path);
        Log.v(tag,path);
        //如果db文件不存在则从asset文件夹中读取city文件并创建db文件
        if(!db.exists()){
            Log.v(tag,"my db doesn't exist");
            try {
                InputStream input = getAssets().open("city.db");
                db.getParentFile().mkdirs();
                FileOutputStream fos=new FileOutputStream(db);
                int length=-1;
                byte buffer[]=new byte[1024];
                while((length=input.read(buffer))!=-1){
                    fos.write(buffer,0,length);
                    fos.flush();
                }
                input.close();
                fos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return new CityDB(this,path);
    }

    /*private void initCityList(){
        mCityList=new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    public List<String> prepareCityList(){
        List<String> mcity=new ArrayList<>();
        mCityList=mCityDB.getAllCity();//获取数据库返回记录
        for(City city:mCityList){
            String cityname=city.getCity();
            String cityNumber=city.getNumber();

            mcity.add(cityname);
            Log.v(tag,cityname);
        }
        return mcity;
    }
*/
}
