package com.englite3.activities;

import static com.englite3.logic.Os.getCloudAddr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.englite3.R;
import com.englite3.logic.Tcp;
import com.englite3.utils.AddrInfo;

public class Cloud extends AppCompatActivity implements View.OnClickListener{
    private Button query_db_list;
    private Tcp tcp;

    private void init(){
        query_db_list = findViewById(R.id.query_db_list);
        query_db_list.setOnClickListener(this);

        tcp = new Tcp(Cloud.this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);

        init();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.query_db_list:
                AddrInfo ai = getCloudAddr(this);
                if(ai == null){
                    Toast.makeText(this, "加载服务器信息失败", Toast.LENGTH_SHORT).show();
                }
                else{
                    tcp.query_db_list(ai.getHost(), ai.getPort());
                }
                break;
            default:
                break;
        }
    }
}