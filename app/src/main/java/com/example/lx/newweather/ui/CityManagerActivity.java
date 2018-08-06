package com.example.lx.newweather.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lx.newweather.R;
import com.example.lx.newweather.adapter.CityManagerAdapter;
import com.example.lx.newweather.db.WeatherNow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;


public class CityManagerActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView listView;
    private FloatingActionButton floatBtn;
    private CityManagerAdapter adapter;
    private List<WeatherNow> lists = new ArrayList<WeatherNow>();
    private String location, cond, tmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_city_manager);
        initView();
        onClick();
        getWeather();

    }

    private void initView() {
        toolbar = findViewById(R.id.activity_city_manager_toolbar);
        toolbar.setTitle("城市管理");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.activity_city_manager_listView);
        adapter = new CityManagerAdapter(this, lists);
        listView.setAdapter(adapter);
        floatBtn = findViewById(R.id.activity_city_manager_floatBtn);
    }

    /**
     * 控件的点击/长按事件
     */
    private void onClick() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CityManagerActivity.this, SearchCityActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //点击返回数据到WeatherActivity中
                Intent intent = new Intent();
                intent.putExtra("location", lists.get(i).getLocation());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //长按删除item
                return false;
            }
        });
    }

    /**
     * 从数据库查找数据并显示在界面上
     */
    private void getWeather() {
        lists.clear();
        List<WeatherNow> weatherNowList = LitePal.findAll(WeatherNow.class);
        for (WeatherNow now : weatherNowList) {
            lists.add(now);
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getWeather();

    }

    @Override
    protected void onPause() {
        super.onPause();
        getWeather();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //从搜索城市返回数据
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    location = data.getStringExtra("newLocation");
                }
                break;
        }
    }
}
