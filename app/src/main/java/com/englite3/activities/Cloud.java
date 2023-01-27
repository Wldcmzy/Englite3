package com.englite3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.englite3.R;
import com.englite3.logic.Tcp;

public class Cloud extends AppCompatActivity implements View.OnClickListener{
    private Button query_db_list;
    private EditText editHost, editPort;
    private Tcp tcp;

    private void init(){
        query_db_list = findViewById(R.id.query_db_list);
        query_db_list.setOnClickListener(this);

        editHost = findViewById(R.id.edit_host);
        editPort = findViewById(R.id.edit_port);

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
                String host = editHost.getText().toString();
                String port = editPort.getText().toString();
                tcp.query_db_list(host, port);
                break;
            default:
                break;
        }
    }
}