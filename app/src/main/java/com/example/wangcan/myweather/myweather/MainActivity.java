package com.example.wangcan.myweather.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wangcan.myweather.R;
import com.example.wangcan.myweather.bean.TodayWeather;
import com.example.wangcan.myweather.util.NetUtil;
import com.example.wangcan.myweather.util.PinYin;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private ImageView mUpdatebtn, mCitySelect;
    private LinearLayout mlayout;
    private ProgressBar mUpdateProBar;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windTv, titleNameTv;
    private ImageView weatherImg, pmImg;
    private String cityCode;
    private static final int UPDATE_TODAY_WEATHER = 1;
    private static final String PM_FILE_NAME1 = "@drawable/biz_plugin_weather_0_50.png";
    private static final String tag = "MYAPP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        Log.v(tag, "MainActivity-->onCreate()");

        //add listener
        mUpdatebtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdatebtn.setOnClickListener(this);
        //为更新按钮加入progressbar
        mUpdateProBar = (ProgressBar) findViewById(R.id.title_update_progress);

        //add select-city listener
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        mUpdateProBar = (ProgressBar) findViewById(R.id.title_update_progress);
        mUpdateProBar.setOnClickListener(this);

        //添加layout,更新该星期其他时间的天气
        mlayout = (LinearLayout) findViewById(R.id.week_weather_info1);

        initView();

    }

    //定义主线程handler，更新天气信息
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }

        }
    };

    //初始化控件
    public void initView() {
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.wet);
        weekTv = (TextView) findViewById(R.id.date);
        pmDataTv = (TextView) findViewById(R.id.PMValue);
        pmQualityTv = (TextView) findViewById(R.id.result);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.weather);
        windTv = (TextView) findViewById(R.id.wind);
        titleNameTv = (TextView) findViewById(R.id.title_city_name);
        //获得ImageView
        pmImg = (ImageView) findViewById(R.id.head);
        weatherImg = (ImageView) findViewById(R.id.weatherpic);
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        weekTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
        titleNameTv.setText("N/A");

    }

    //更新天气给控件赋值
    public void updateTodayWeather(TodayWeather todayWeather) {
        String pmImgStr = null;
        String typeImg = null;
        cityTv.setText(todayWeather.getCity());
        timeTv.setText("今天" + todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度：" + todayWeather.getShidu());
        weekTv.setText(todayWeather.getDate());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        temperatureTv.setText(todayWeather.getHigh() + "～" + todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText(todayWeather.getFengli());
        titleNameTv.setText(todayWeather.getCity() + "天气");

        //更新PM值头像
        if (todayWeather.getPm25() != null) {
            int pmValue = Integer.parseInt(todayWeather.getPm25().trim());
            pmImgStr = "0_50";
            if (pmValue > 50 && pmValue < 200) {
                int startV = (pmValue - 1) / 50 * 50 + 1;
                int endV = ((pmValue - 1) / 50 + 1) * 50;
                pmImgStr = Integer.toString(startV) + "_" + Integer.toString(endV);
            } else if (pmValue >= 200) {
                pmImgStr = "201_300";
            } else if (pmValue > 300) {
                pmImgStr = "greater_300";
            }
        }

        if (todayWeather.getType() != null) {
            typeImg = "biz_plugin_weather_" + PinYin.converterToSpell(todayWeather.getType());//将汉字转化为拼音
        }
        Class aClass = R.drawable.class;
        int typeId = -1;
        int pmImgId = -1;
        try {
            Field field = aClass.getField(typeImg);
            Object value = field.get(new Integer(0));
            typeId = (int) value;

            Field pmfield = aClass.getField("biz_plugin_weather_" + pmImgStr);
            Object pmImg0 = pmfield.get(new Integer(0));
            pmImgId = (int) pmImg0;

        } catch (Exception e) {
            if (-1 == typeId) {
                typeId = R.drawable.biz_plugin_weather_qing;
            }
            if (-1 == pmImgId) {
                pmImgId = R.drawable.biz_plugin_weather_0_50;
            }

        } finally {
            Drawable drawable = getResources().getDrawable(typeId);
            weatherImg.setImageDrawable(drawable);
            drawable = getResources().getDrawable(pmImgId);
            pmImg.setImageDrawable(drawable);
            mUpdateProBar.setVisibility(View.INVISIBLE);
            mUpdatebtn.setVisibility(View.VISIBLE);
            //更新天气图片
            Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.title_update_btn) {
            //check the network state if it's ok query weather or give warn
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.v("myWeather", "the network state is ok");
                mUpdateProBar.setVisibility(View.VISIBLE);
                mUpdatebtn.setVisibility(View.INVISIBLE);
                queryWeatherCode(cityCode);
            } else {
                Log.v("myWeather", "No Network");
                Toast.makeText(MainActivity.this, "No Network,Please Check!", Toast.LENGTH_LONG).show();
            }


        }
        //add select city listener,通过intent在Activity之间通信
        else if (view.getId() == R.id.title_city_manager) {
            Intent i = new Intent(MainActivity.this, SelectCity.class);
            startActivityForResult(i, 1);
        }
    }

    /**
     * 通过intent两个Activity之间交换数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cityCode = data.getStringExtra("number");
            queryWeatherCode(cityCode);
        }
    }

    /**
     * search the weather information through the network
     *
     * @param cityCode
     */

    private void queryWeatherCode(String cityCode) {
        //get the url address,拼接字符串获得URL地址
        if (cityCode == null || cityCode == " ") {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            cityCode = sharedPreferences.getString("main_city_code", "101010100");
        }
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.v("myWeather", address);
        //放入子线程中进行以免阻塞主线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取网络数据
                    HttpClient httpClient = new DefaultHttpClient();//创建DefaultHttpClient实例
                    HttpGet httpget = new HttpGet(address);//发送get请求，创建HttpGet对象，传入URL地址获取网页资源
                    HttpResponse httpResponse = httpClient.execute(httpget);//发送请求返回一个HttpResponse对象

                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();

                        InputStream responseStream = entity.getContent();
                        responseStream = new GZIPInputStream(responseStream);//将文件解压缩

                        BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));//从字符输入流中读取文本缓冲各个字符
                        StringBuilder response = new StringBuilder();
                        String str = null;
                        while ((str = reader.readLine()) != null) {
                            response.append(str);//将网页获得的内容转化为StringBuilder
                        }

                        Log.v("myWeather", response.toString());
                        TodayWeather todayWeather = parseXml(response.toString());
                        if (todayWeather != null) {
                            //发送消息通知主线程更新UI
                            Message msg = new Message();
                            msg.what = UPDATE_TODAY_WEATHER;
                            msg.obj = todayWeather;
                            mhandler.sendMessage(msg);
                        }
                        Log.v("myapp", todayWeather.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * parserXMl:use Pull method to parse the XML 解析XML文件
     *
     * @param xmldata
     * @return
     */
    private TodayWeather parseXml(String xmldata) {
        //新建TodayWeather对象，将解析出来的数据放入该对象中
        TodayWeather todayWeather = new TodayWeather();

        try {
            int fengxiangCount = 0;
            int fengliCount = 0;
            int dateCount = 0;
            int highCount = 0;
            int lowCount = 0;
            int typeCount = 0;
            //通过工厂方法返回Pull的实例
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    //判断当前事件是否为XML声明
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:

                        if (xmlPullParser.getName().equals("city")) {
                            //利用 xmlPullParser.next()进入下一个元素
                            eventType = xmlPullParser.next();
                            todayWeather.setCity(xmlPullParser.getText());//通过getText()获得xml文件中的文字信息
                        } else if (xmlPullParser.getName().equals("updatetime")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setUpdatetime(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("shidu")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setShidu(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("wendu")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setWendu(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("pm25")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setPm25(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("quality")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setQuality(xmlPullParser.getText());

                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setFengxiang(xmlPullParser.getText());
                            fengxiangCount++;
                        } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setFengli(xmlPullParser.getText());
                            fengliCount++;
                        } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setDate(xmlPullParser.getText());
                            dateCount++;
                        } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                            highCount++;
                        } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                            lowCount++;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setType(xmlPullParser.getText());
                            typeCount++;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }

                eventType = xmlPullParser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return todayWeather;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
