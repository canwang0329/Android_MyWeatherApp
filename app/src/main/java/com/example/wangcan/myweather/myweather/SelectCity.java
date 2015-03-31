package com.example.wangcan.myweather.myweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wangcan.myweather.R;
import com.example.wangcan.myweather.app.MyApplication;

import java.util.List;



/**
 * Created by wangcan on 2015/3/30.
 */
public class SelectCity extends Activity implements View.OnClickListener {
    private ImageView mBackBtn;
    private List<String> cityList;
    private ListView mlistView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        cityList=MyApplication.prepareCityList();//获取城市列表

        mlistView=(ListView)findViewById(R.id.my_listView);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,cityList);
        mlistView.setAdapter(adapter);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SelectCity.this,"你单击了："+cityList.get(i),Toast.LENGTH_LONG).show();
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


}
