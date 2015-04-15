package com.example.wangcan.myweather.myweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wangcan.myweather.R;
import com.example.wangcan.myweather.app.MyApplication;
import com.example.wangcan.myweather.bean.City;
import com.example.wangcan.myweather.bean.TodayWeather;
import com.example.wangcan.myweather.pku.ss.canwang.db.CityDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;


/**
 * Created by wangcan on 2015/3/30.
 */
public class SelectCity extends Activity implements View.OnClickListener {
    private ImageView mBackBtn;
    private List<String> cityList;
    private ListView mlistView;
    private EditText mEditTxt;
    private static final int UPDATE_DISPLAY_LIST=1;
    private ArrayAdapter<String> adapter;
    private CityDB mCityDB=new CityDB(MyApplication.getInstance(),"");
    private Map<String,String> cityNumMap=new HashMap<>();//定义map存入cityCode与cityNumber的映射
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        //获取EditText
        mEditTxt=(EditText)findViewById(R.id.search_edit);
        //为EditText添加监视器
        mEditTxt.addTextChangedListener(mTextWatcher);


        //显示城市列表
        mlistView=(ListView)findViewById(R.id.my_listView);
        cityList=getData();//获取城市名称列表
        adapter=new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,cityList);
        mlistView.setAdapter(adapter);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SelectCity.this,"你单击了："+cityList.get(i),Toast.LENGTH_LONG).show();
                String cityNumber=cityNumMap.get(cityList.get(i));
                //通过intent传递数据传递citynumber数据
                Intent cityIntent=new Intent(SelectCity.this,MainActivity.class);
                cityIntent.putExtra("number",cityNumber);
                setResult(RESULT_OK,cityIntent);
                finish();
            }
        });

    }


    @Override
    public void onClick(View view){
           switch(view.getId()){
               case R.id.title_back:
                   finish();
                   break;
               default:
                   break;
           }
    }

    public List<String> getData(){
        List<City> clist=new ArrayList<>();
        List<String> mcity=new ArrayList<>();
         clist=mCityDB.getAllCity();
         for(City city:clist){
             String cityName=city.getCity();
             String cityNumber=city.getNumber();
             cityNumMap.put(cityName,cityNumber);
             mcity.add(cityName);
         }

        return mcity;

    }

    /**
     * 创建mTextWatcher对象
     */

    TextWatcher mTextWatcher=new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;
        @Override
        /**This method is called to notify you that, within s, the count characters
         * beginning at start are about to be replaced by new text with length after.
         * 在s中，从start处开始的count个字符将要被长度为after的文本替代
         * s 为变化前的内容；
         * start 为开始变化位置的索引，从0开始计数；
         * count 为将要发生变化的字符数
         * after 为用来替换旧文本的长度，比如s由1变为12，after为1，由12变为1，after为0；
         */
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
            Log.d("myapp","beforeTextChanged"+s );
            Log.d("myapp","before:s,"+s+",start,"+start+",count,"+count+",after,"+after);
        }

        @Override
        /**
         * This method is called to notify you that, within s, the count characters
         *  beginning at start have just replaced old text that had length before
         *  在s中，从start处开始的count个字符刚刚替换了原来长度为before的文本
         *  s 为变化后的内容；
         *  start 为开始变化位置的索引，从0开始计数；
         *  before 为被取代的老文本的长度，比如s由1变为12，before为0，由12变为1，before为1；
         *  count 为将要发生变化的字符数
         */
        public void onTextChanged(CharSequence s, int start, int before, int count) {
           Log.d("myapp","ing:s,"+s+",start,"+start+",before,"+before+",count,"+count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = mEditTxt.getSelectionStart();
            editEnd = mEditTxt.getSelectionEnd();
            Log.d("myapp",s.toString());
            if (s.length() > 0 || s.length() == 0) {
                String data = mEditTxt.getText().toString();
                getDataSub(data);//为cityList重新绑定数据
                //发送消息通知主线程更新UI
                Message msg = new Message();
                msg.what = UPDATE_DISPLAY_LIST;
                mhandler.sendMessage(msg);
            }
        }
    };


    //定义主线程handler，更新listView信息
    private android.os.Handler mhandler = new android.os.Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_DISPLAY_LIST:
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }

        }
    };

    //为listView重新绑定数据
    private void getDataSub(String data) {
        List<String> temList=new ArrayList<>();
        if(!cityList.isEmpty()){
            cityList.clear();
        }
        if (data.equals(null) || data.equals("")) {
            cityNumMap.clear();//重新给Map赋值
            temList = getData();
            if(!temList.isEmpty()){
                for(int i=0;i<temList.size();i++) {
                    cityList.add(temList.get(i));
                }
            }

        } else {
            data.toUpperCase();
            temList = mCityDB.searchCity(data);//通过模糊查询返回搜索结果
            if(!temList.isEmpty()) {
                for (int i = 0; i < temList.size(); i++) {
                    cityList.add(temList.get(i));
                }
            }
        }
    }

    //更新天气信息


}
