package com.example.lx.newweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lx.newweather.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class CityListAdapter extends BaseAdapter {

    private List<String> cityLists;
    private Context mContext;

    public CityListAdapter(List<String> cityLists, Context mContext) {
        this.mContext = mContext;
        this.cityLists = cityLists;
    }


    @Override
    public int getCount() {
        return cityLists.size();
    }

    @Override
    public Object getItem(int i) {
        return cityLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_city_item, viewGroup, false);
            holder = new ViewHolder();
            holder.textCity = view.findViewById(R.id.item_city);
//            holder.textCond = view.findViewById(R.id.item_cond);
//            holder.textTmp = view.findViewById(R.id.item_tmp);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.textCity.setText(cityLists.get(i));
//        holder.textCond.setText(cityLists.get(i).getNow().getCond_txt());
//        holder.textTmp.setText(cityLists.get(i).getNow().getTmp());
        return view;
    }

    static class ViewHolder {
        TextView textCity;
        TextView textCond;
        TextView textTmp;
    }
}
