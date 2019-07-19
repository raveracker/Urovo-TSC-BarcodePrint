package com.example.tscsample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {
    private ArrayList<BlutoothList> listData;
    private LayoutInflater layoutInflater;
    public CustomListAdapter(Context aContext, ArrayList<BlutoothList> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }
    @Override
    public int getCount() {
        return listData.size();
    }
    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View v, ViewGroup vg) {
        ViewHolder holder;
        if (v == null) {
            v = layoutInflater.inflate(R.layout.list_row, null);
            holder = new ViewHolder();
            holder.txt_name = (TextView) v.findViewById(R.id.txt_name);
            holder.txt_id = (TextView) v.findViewById(R.id.txt_id);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.txt_name.setText(listData.get(position).getName());
        holder.txt_id.setText(listData.get(position).getId());
        return v;
    }
    static class ViewHolder {
        TextView txt_name;
        TextView txt_id;
    }
}