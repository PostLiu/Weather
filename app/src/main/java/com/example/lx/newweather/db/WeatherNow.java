package com.example.lx.newweather.db;

import org.litepal.crud.LitePalSupport;

public class WeatherNow extends LitePalSupport {

    //城市
    private String location;
    //温度
    private String tmp;
    //天气状况
    private String cond;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }
}
