package com.englite3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.englite3.R;
import com.englite3.adapters.DatabaseNameAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import com.englite3.logic.Functions;

public class Main extends AppCompatActivity implements View.OnClickListener {
    private long mExitTime;
    private DatabaseNameAdapter adapter;
    private ListView listView;
    private FloatingActionButton circleButton;
    private TextView textview;
    private Intent intent;


    private void initview(){
        listView = findViewById(R.id.main_listview);
        circleButton = findViewById(R.id.main_circlebutton);
        textview = findViewById(R.id.main_textview);

        circleButton.setOnClickListener(this);
    }

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "两秒内连续两次返回退出软件", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.main_circlebutton:
                intent = new Intent(Main.this, More.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }


    public void showDbName(){
        List<String> lst = Functions.getDbName(this);
        if(lst == null || lst.size() <= 0) {
            Log.w("at main activity", "未检测到db文件");
            textview.setText("无词库");
        }else {
            adapter = new DatabaseNameAdapter(this, R.layout.list_member,lst);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String db = lst.get(position);
                    intent = new Intent(Main.this, WordDatabaseInfo.class);
                    intent.putExtra("dbname", db);
                    startActivity(intent);
                }
            });
            textview.setText("请选择词库");
        }
    }


}