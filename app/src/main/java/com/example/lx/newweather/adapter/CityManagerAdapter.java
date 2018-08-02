package com.example.lx.newweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lx.newweather.R;
import com.example.lx.newweather.db.WeatherNow;

import java.util.List;

public class CityManagerAdapter extends BaseAdapter {
    private Context context;
    private List<WeatherNow> lists;

    public CityManagerAdapter(Context context, List<WeatherNow> lists) {
        this.context = context;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        WeatherNow weatherNow = lists.get(i);   //这里需要获取相应的实例
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.activity_city_manager_item, null, false);
            holder = new ViewHolder();
            holder.itemLocation = view.findViewById(R.id.manager_item_location);
            holder.itemCond = view.findViewById(R.id.manager_item_cond);
            holder.itemTmp = view.findViewById(R.id.manager_item_tmp);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.itemLocation.setText(weatherNow.getLocation());
        holder.itemCond.setText(weatherNow.getCond());
        holder.itemTmp.setText(weatherNow.getTmp());
        return view;
    }

    static class ViewHolder {
        TextView itemLocation;
        TextView itemCond;
        TextView itemTmp;

    }
}
