package com.englite3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.englite3.R;

import java.util.List;

public class DatabaseNameAdapter extends BaseAdapter {
    public Context context;
    public List<String> lst;

    public DatabaseNameAdapter(Context context, List<String> lst){
        this.context = context;
        this.lst = lst;
    }

    @Override
    public int getCount() {
        return 0;
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            // 将布局实例化
            convertView = LayoutInflater.from(context).inflate(R.layout.list_member, null);

            holder = new Holder();
            holder.name = convertView.findViewById(R.id.dbname);
            //将holder数据缓存起来
            convertView.setTag(holder);
        } else {
            //提取holder缓存数据
            holder = (Holder) convertView.getTag();
        }
        holder.name.setText("153");
        return convertView;
    }
}

class Holder{
    TextView name;
}