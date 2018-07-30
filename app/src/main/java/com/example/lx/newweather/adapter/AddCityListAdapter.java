package com.example.lx.newweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lx.newweather.R;


import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;

public class AddCityListAdapter extends BaseAdapter {

    private List<Now> lists;
    private Context mContext;

    public AddCityListAdapter(List<Now> lists, Context mContext) {
        this.lists = lists;
        this.mContext = mContext;
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
        Now nowBase = new Now();
        if(view ==null){
            view = LayoutInflater.from(mContext).inflate(R.layout.add_city_list_item,viewGroup,false);
            holder = new ViewHolder();
            holder.textLocation = view.findViewById(R.id.add_city_item_location);
            holder.textCond = view.findViewById(R.id.add_city_item_cond);
            holder.textTmp = view.findViewById(R.id.add_city_item_tmp);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        holder.textLocation.setText(nowBase.getBasic().getLocation());
        holder.textCond.setText(nowBase.getNow().getCond_txt());
        holder.textTmp.setText(nowBase.getNow().getTmp());
        return view;
    }

    static class ViewHolder{
        TextView textLocation;
        TextView textCond;
        TextView textTmp;
    }
}
