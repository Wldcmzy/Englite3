package com.englite3.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.englite3.R;

import java.util.List;

public class DatabaseNameAdapter extends ArrayAdapter<String> {

    public DatabaseNameAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            // 将布局实例化
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_member, parent, false);

            holder = new Holder();
            holder.name = convertView.findViewById(R.id.dbname);
            holder.statement = convertView.findViewById(R.id.statement);
            //将holder数据缓存起来
            convertView.setTag(holder);
        } else {
            //提取holder缓存数据
            holder = (Holder) convertView.getTag();
        }
        String db_list_item = getItem(position);
        String[] item_data = db_list_item.split("_");
        holder.name.setText(item_data[0]);
        holder.statement.setText(item_data[1]);
        return convertView;
    }
}

class Holder{
    TextView name;
    TextView statement;
}