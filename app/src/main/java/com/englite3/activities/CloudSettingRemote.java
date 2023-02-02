package com.englite3.activities;

import static com.englite3.logic.Functions.getCloudAddr;
import static com.englite3.logic.Functions.saveCloudAddr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.englite3.R;

import com.englite3.utils.AddrInfo;

public class CloudSettingRemote extends AppCompatActivity implements View.OnClickListener{
    private EditText edit_host, edit_port, edit_pubkey;
    private Button show_addr, clear_addr, save_addr;
    private CheckBox use_aes;

    private void initView(){
        edit_host = findViewById(R.id.edit_host);
        edit_port = findViewById(R.id.edit_port);
        edit_pubkey = findViewById(R.id.edit_pubkey);
        use_aes = findViewById(R.id.use_aes);

        show_addr = findViewById(R.id.show_addr);
        show_addr.setOnClickListener(this);
        clear_addr = findViewById(R.id.clear_addr);
        clear_addr.setOnClickListener(this);
        save_addr = findViewById(R.id.save_addr);
        save_addr.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_setting_remote);
        initView();
    }

    @Override
    public void onClick(View v) {
        confirm(v.getId());
    }

    private void confirm(int id){
        AlertDialog.Builder builder = new AlertDialog.Builder(CloudSettingRemote.this);
        String query = "";
        switch (id){
            case R.id.clear_addr:
                query = "你确定要清空你输入的信息吗?";
                break;
            case R.id.show_addr:
                query="进行这个操作, 你输入框的内容将被你的默认配置覆盖, 你确定这么做吗?";
                break;
            case R.id.save_addr:
                query = "你确定要保存当前输入框内容为默认配置吗?";
                break;
            default:
                break;
        }
        builder.setMessage(query)
                .setTitle("二次询问")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(id){
                                    case R.id.clear_addr:
                                        edit_host.setText("");
                                        edit_port.setText("");
                                        edit_pubkey.setText("");;
                                        break;
                                    case R.id.show_addr:
                                        show();
                                        break;
                                    case R.id.save_addr:
                                        save();
                                        break;
                                    default:
                                        break;
                                }
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("不,刚才我手滑了",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    private void save(){
        boolean aes = use_aes.isChecked();
        String host = edit_host.getText().toString();
        String port = edit_port.getText().toString();
        String pubkey = edit_pubkey.getText().toString();
        saveCloudAddr(this,host,port,aes,pubkey);
    }


    private void show() {

        try {
            AddrInfo ai = getCloudAddr(this);

            edit_host.setText(ai.getHost());
            edit_port.setText(ai.getPort());
            use_aes.setChecked(ai.isIfaes());
            edit_pubkey.setText(ai.getPubkey());

        }catch (Exception e){
            Log.e("sdf", e.getMessage());
        }
    }
}