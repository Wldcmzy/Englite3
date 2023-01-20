package com.englite3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.englite3.R;
import com.englite3.adapters.DatabaseNameAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class main extends AppCompatActivity {
    private long mExitTime;
    private DatabaseNameAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDbName();
    }

    private void initview(){

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差

            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "检测到返回操作,再操作一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public List<String> getDbName(String path) {
        File file=new File(path);
        File[] files=file.listFiles();

        if (files == null){
            return null;
        }

        List<String> s = new ArrayList<>();
        for(int i =0;i<files.length;i++){
            String x =files[i].getName();
            if(x.endsWith(".db")) {
                s.add(x);
            }
        }
        return s;
    }

    public void showDbName(){
        List<String> lst = getDbName("./database/");
        if(lst == null) {
            Log.w("nodb", "未检测到db文件");
        }else {
            adapter = new DatabaseNameAdapter(this, lst);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        }
    }

}