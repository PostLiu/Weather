package com.example.lx.newweather.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lx.newweather.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.ForecastBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class WeatherActivity extends AppCompatActivity {

    private String location;
    private Toolbar toolbar;
    private SwipeRefreshLayout refresh;
    private ScrollView scrollView;
    private TextView weatherInfoText, weatherTmp, weatherWind, weatherHum, weatherVis, weatherFl, weatherTextForecast;
    private LinearLayout forecastLayout;
    private List<ForecastBase> forecastBaseList = new ArrayList<ForecastBase>();
    private String forecastBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather);
        Intent intent = getIntent();
        location = intent.getStringExtra("location");
        initView();
        getWeatherData();
    }

    private void initView() {
        toolbar = findViewById(R.id.weather_toolbar);
        refresh = findViewById(R.id.swipe_refresh);
        scrollView = findViewById(R.id.weather_layout_scr);
        weatherInfoText = findViewById(R.id.weatherInfo_text);
        weatherFl = findViewById(R.id.weather_fl);
        weatherTmp = findViewById(R.id.weather_tmp_number);
        weatherWind = findViewById(R.id.weather_wind);
        weatherHum = findViewById(R.id.weather_hum);
        weatherVis = findViewById(R.id.weather_vis);
        forecastLayout = findViewById(R.id.forecast_layout);
        weatherTextForecast = findViewById(R.id.weather_text_forecast);
        toolbar.setTitle(location);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));

        setSupportActionBar(toolbar);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeatherData();
            }
        });
    }

    private void getWeatherData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HeWeather.getWeatherNow(WeatherActivity.this, location, Lang.CHINESE_SIMPLIFIED
                        , Unit.METRIC, new HeWeather.OnResultWeatherNowBeanListener() {
                            @Override
                            public void onError(Throwable throwable) {
                                Toast.makeText(WeatherActivity.this, throwable.getMessage().toString() + "222222", Toast.LENGTH_SHORT).show();
                                refresh.setRefreshing(false);
                            }

                            @Override
                            public void onSuccess(List<Now> list) {
                                Gson gson = new Gson();
                                String weatherNow = gson.toJson(list);
                                List<Now> nowList = gson.fromJson(weatherNow, new TypeToken<List<Now>>() {
                                }.getType());
                                for (Now now : nowList) {
                                    weatherInfoText.setText(now.getNow().getCond_txt());
                                    weatherTmp.setText(now.getNow().getTmp() + "℃");
                                    weatherFl.setText("体感温度 " + now.getNow().getFl());
                                    weatherWind.setText(now.getNow().getWind_dir() + "-" + now.getNow().getWind_sc() + "级");
                                    weatherHum.setText(now.getNow().getHum());
                                    weatherVis.setText(now.getNow().getVis() + "公里");
                                    toolbar.setTitle(location);
                                    location = now.getBasic().getLocation();
                                }
                            }
                        });
                HeWeather.getWeatherForecast(WeatherActivity.this, location, Lang.CHINESE_SIMPLIFIED
                        , Unit.METRIC, new HeWeather.OnResultWeatherForecastBeanListener() {
                            @Override
                            public void onError(Throwable throwable) {
                                Toast.makeText(WeatherActivity.this, throwable.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                refresh.setRefreshing(false);
                            }

                            @Override
                            public void onSuccess(List<Forecast> list) {
                                Gson gson = new Gson();
                                String forecasts = gson.toJson(list);
                                List<Forecast> forecastList = gson.fromJson(forecasts, new TypeToken<List<Forecast>>() {
                                }.getType());
                                for (Forecast forecast : forecastList) {
                                    forecastBaseList = forecast.getDaily_forecast();
                                }
                                forecastBase = gson.toJson(forecastBaseList);
                                List<ForecastBase> forecastBaseList = gson.fromJson(forecastBase, new TypeToken<List<ForecastBase>>() {
                                }.getType());
                                forecastLayout.removeAllViews();
                                weatherTextForecast.setText("未来" + forecastBaseList.size() + "天预报");
                                for (ForecastBase forecastBase : forecastBaseList) {
                                    View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item, forecastLayout, false);
                                    TextView dateText = view.findViewById(R.id.weather_date_text);
                                    TextView infoText = view.findViewById(R.id.weather_info_text);
                                    TextView tmpMinMax = view.findViewById(R.id.weather_tmp_min_max);
                                    dateText.setText(forecastBase.getDate());
                                    infoText.setText(forecastBase.getCond_txt_d());
                                    tmpMinMax.setText(forecastBase.getTmp_min() + "℃" + "/" + forecastBase.getTmp_max() + "℃");
                                    forecastLayout.addView(view);
                                }

                            }
                        });
                refresh.setRefreshing(false);
            }
        });
        scrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.city, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.city_group:
                Intent intent = new Intent(WeatherActivity.this, SearchCityActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.city_set:
                Intent inSet = new Intent(WeatherActivity.this, SetActivity.class);
                startActivity(inSet);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    location = data.getStringExtra("location");
                }
                break;
            default:
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWeatherData();
    }
}
