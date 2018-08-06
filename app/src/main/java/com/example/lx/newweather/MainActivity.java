package com.example.lx.newweather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.lx.newweather.ui.WeatherActivity;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.view.HeConfig;

public class MainActivity extends AppCompatActivity {

    private LocationClient locationClient;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        HeConfig.init("HE1802280747411348", "cd1feda259d149d597016661aa3dacff");
        HeConfig.switchToFreeServerNode();
        LitePal.getDatabase();

        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new LocationListener());
        List<String> permissionList = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);   //GPS权限
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE); //文件写入权限
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }

    }

    private void requestLocation() {
        initLocation();
        locationClient.start();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(2000);
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "需要开启权限才能正常运行", Toast.LENGTH_SHORT).show();
                        }
                    }
                    requestLocation();

                } else {
                    Toast.makeText(MainActivity.this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    public class LocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            Log.e("区：", bdLocation.getDistrict());
            Log.e("城市：", bdLocation.getCity());
            Log.e("经纬度：", bdLocation.getLongitude() + "," + bdLocation.getLatitude());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final String location = bdLocation.getLongitude() + "," + bdLocation.getLatitude();
                    AlphaAnimation animation = new AlphaAnimation(0.1f, 0.1f);
                    animation.setDuration(3000);
                    ConstraintLayout constraintLayout = findViewById(R.id.main_constrain);
                    constraintLayout.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            if (prefs.getString("newLocation", null) != null) {
                                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                                intent.putExtra("newLocation", prefs.getString("newLocation", ""));
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                                intent.putExtra("location", location);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop();
    }
}
