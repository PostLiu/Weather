package com.example.lx.newweather.ui;

import android.content.Intent;
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

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class CityManagerActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView listView;
    private FloatingActionButton floatBtn;
    private CityManagerAdapter adapter;
    private List<WeatherNow> lists = new ArrayList<WeatherNow>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_city_manager);
        Intent intent = getIntent();
        WeatherNow now = new WeatherNow();
        now.setCond(intent.getStringExtra("cond"));
        now.setTmp(intent.getStringExtra("tmp"));
        initView();
        onClick();
        getWeatherNow();
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
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //点击返回数据到WeatherActivity中
                Intent intent = new Intent();
                intent.putExtra("newLocation",lists.get(i).getLocation());
                setResult(RESULT_OK,intent);
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

    private void getWeatherNow() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lists.clear();
                List<WeatherNow> weather = LitePal.findAll(WeatherNow.class);
                for (WeatherNow now : weather) {
                    lists.add(now);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWeatherNow();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWeatherNow();
    }
}
