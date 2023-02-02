package com.englite3.activities;

import static com.englite3.logic.Functions.getUserInfo;
import static com.englite3.logic.Functions.saveUserInfo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.englite3.R;
import com.englite3.utils.UserInfo;

public class CloudSettingUserInfo extends AppCompatActivity implements View.OnClickListener{

    private EditText edit_username, edit_password;
    private Button show_info, clear_info, save_info;

    private void initView(){
        edit_username = findViewById(R.id.edit_username);
        edit_password = findViewById(R.id.edit_password);

        show_info = findViewById(R.id.show_userinfo);
        show_info.setOnClickListener(this);
        clear_info = findViewById(R.id.clear_userinfo);
        clear_info.setOnClickListener(this);
        save_info = findViewById(R.id.save_userinfo);
        save_info.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_setting_user_info);
        initView();
    }

    @Override
    public void onClick(View v) {
        confirm(v.getId());
    }

    private void confirm(int id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String query = "";
        switch (id){
            case R.id.clear_userinfo:
                query = "你确定要清空你输入的信息吗?";
                break;
            case R.id.show_userinfo:
                query="进行这个操作, 你输入框的内容将被你的默认配置覆盖, 你确定这么做吗?";
                break;
            case R.id.save_userinfo:
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
                                    case R.id.clear_userinfo:
                                        edit_username.setText("");
                                        edit_password.setText("");
                                        break;
                                    case R.id.show_userinfo:
                                        show();
                                        break;
                                    case R.id.save_userinfo:
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
        String username = edit_username.getText().toString();
        String password = edit_password.getText().toString();
        saveUserInfo(this,username, password);
    }


    private void show() {

        try {
            UserInfo ui = getUserInfo(this);
            edit_username.setText(ui.getUsername());
            edit_password.setText(ui.getPassword());

        }catch (Exception e){
            Log.e("at CloudUserinfosetting", e.getMessage());
        }
    }
}