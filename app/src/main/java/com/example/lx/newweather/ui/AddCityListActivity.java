package com.example.lx.newweather.ui;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.example.lx.newweather.R;
import com.example.lx.newweather.adapter.AddCityListAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class AddCityListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;
    private FloatingActionButton floating;
    private AddCityListAdapter adapter;
    private String location,cond,tmp;
    private List<Now> list = new ArrayList<Now>();

    public static final int TAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_city_list);
        initView();
        getWeatherNow();
    }

    private void initView() {
        toolbar = findViewById(R.id.add_city_toolbar);
        toolbar.setTitle("城市管理");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        floating = findViewById(R.id.add_city_float);
        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddCityListActivity.this, SearchCityActivity.class);
                startActivityForResult(intent, TAG);
            }
        });
        listView = findViewById(R.id.add_city_listView);
        adapter = new AddCityListAdapter(list,AddCityListActivity.this);
        listView.setAdapter(adapter);

    }
    private void getWeatherNow(){
        HeWeather.getWeatherNow(AddCityListActivity.this, location, Lang.CHINESE_SIMPLIFIED, Unit.METRIC
                , new HeWeather.OnResultWeatherNowBeanListener() {
                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(List<Now> list) {
                        Gson gson = new Gson();
                        String weatherNow = gson.toJson(list);
                        List<Now> nowList = gson.fromJson(weatherNow, new TypeToken<List<Now>>() {
                        }.getType());
                        for(Now now : nowList){
                            list.add(now);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case TAG:
                if (resultCode == RESULT_OK) {
                    location = data.getStringExtra("location");
                    cond = data.getStringExtra("cond");
                    tmp = data.getStringExtra("tmp");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWeatherNow();
    }
}
