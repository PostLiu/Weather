package com.example.lx.newweather.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lx.newweather.adapter.CityListAdapter;
import com.example.lx.newweather.R;
import com.example.lx.newweather.db.WeatherNow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.crud.LitePalSupport;
import org.litepal.crud.callback.FindMultiCallback;
import org.litepal.crud.callback.SaveCallback;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.basic.Basic;
import interfaces.heweather.com.interfacesmodule.bean.search.Search;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class SearchCityActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editCity;
    private ListView listView;
    private List<Basic> basicList = new ArrayList<Basic>();
    private String cityList;
    private List<String> city = new ArrayList<String>();
    private CityListAdapter adapter;
    private String location, cond, tmp, cityName;
    private WeatherNow now = new WeatherNow();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_city_first);
        initView();
        clickList();
    }

    private void initView() {
        toolbar = findViewById(R.id.add_city_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editCity = findViewById(R.id.edit_city);
        listView = findViewById(R.id.add_city_listView);
        adapter = new CityListAdapter(city, SearchCityActivity.this);
        listView.setAdapter(adapter);
        editCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getCityList();
                getWeather(location);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    /**
     * 控件点击事件
     */
    private void clickList() {
        city.clear();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                int index = city.get(i).indexOf(" ");
                cityName = city.get(i).substring(0, index);
                LitePal.findAllAsync(WeatherNow.class).listen(new FindMultiCallback() {
                    @Override
                    public <T> void onFinish(List<T> t) {
                        List<WeatherNow> nows = (List<WeatherNow>) t;
                        String name = nows.get(i).getLocation();
                        if (cityName.equals(name)) {
                            Toast.makeText(SearchCityActivity.this, "城市已存在", Toast.LENGTH_SHORT).show();
                        } else {
                            saveWeather();
                            Intent intent = new Intent();
                            intent.putExtra("newLocation", cityName);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });


            }
        });

    }

    /**
     * 将需要的数据保存到数据库
     */
    private void saveWeather() {
        now.setLocation(cityName);
        now.setCond(cond);
        now.setTmp(tmp);
        now.save();

    }

    /**
     * 获取特定城市的天气数据
     *
     * @param location
     */
    private void getWeather(final String location) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HeWeather.getWeatherNow(SearchCityActivity.this, location, new HeWeather.OnResultWeatherNowBeanListener() {
                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(List<Now> list) {
                        Gson gson = new Gson();
                        String weather = gson.toJson(list);
                        List<Now> nowList = gson.fromJson(weather, new TypeToken<List<Now>>() {
                        }.getType());
                        for (Now now : nowList) {
                            cond = now.getNow().getCond_txt();
                            tmp = now.getNow().getTmp() + "℃";
                        }
                    }
                });

            }
        });
    }

    /**
     * 搜索城市获取列表
     */
    private void getCityList() {
        location = editCity.getText().toString();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!location.equals("")) {
                    HeWeather.getSearch(SearchCityActivity.this, location, "CN", 10, Lang.CHINESE_SIMPLIFIED
                            , new HeWeather.OnResultSearchBeansListener() {
                                @Override
                                public void onError(Throwable throwable) {
                                    Toast.makeText(SearchCityActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess(Search search) {
                                    Gson gson = new Gson();
                                    basicList = search.getBasic();
                                    cityList = gson.toJson(basicList);
                                    List<Basic> basics = gson.fromJson(cityList, new TypeToken<List<Basic>>() {
                                    }.getType());
                                    city.clear();
                                    for (Basic basic : basics) {
                                        city.add(basic.getLocation() + " " + basic.getParent_city() + " " + basic.getAdmin_area());
                                        Log.e("aaaa", city + "");
                                    }
                                    adapter.notifyDataSetChanged();
                                }

                            });
                }
            }
        });
    }

}
