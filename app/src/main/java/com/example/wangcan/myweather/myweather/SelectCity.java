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
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
            Log.d("myapp","beforeTextChanged"+s );
            Log.d("myApp","the first cityList is"+cityList.hashCode());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
           Log.d("myapp","onTextChanged："+ s);
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = mEditTxt.getSelectionStart();
            editEnd = mEditTxt.getSelectionEnd();
            if (temp.length() > 0 || temp.length() == 0) {
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
