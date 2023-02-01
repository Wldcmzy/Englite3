package com.englite3.activities;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import com.englite3.R;


public class More extends AppCompatActivity implements View.OnClickListener {
    private TextView import_local, import_cloud, cloud_setting, cloud_userinfo_setting;
    private Intent intent;
    private final int CODE_IMPORT_BY_FILE = 123;
    private final int CODE_IMPORT_BY_CLOUD = 321;


    private void initview() {

        import_local = findViewById(R.id.func_import_local);
        import_local.setOnClickListener(this);

        import_cloud = findViewById(R.id.func_import_cloud);
        import_cloud.setOnClickListener(this);

        cloud_setting = findViewById(R.id.func_cloud_setting);
        cloud_setting.setOnClickListener(this);

        cloud_userinfo_setting = findViewById(R.id.func_cloud_userinfo_setting);
        cloud_userinfo_setting.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        initview();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CODE_IMPORT_BY_FILE:
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.func_import_local:
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, 123);
                break;
            case R.id.func_import_cloud:
                intent = new Intent(More.this, CloudQuery.class);
                startActivity(intent);
                break;
            case R.id.func_cloud_setting:
                intent = new Intent(More.this, CloudSettingRemote.class);
                startActivity(intent);
                break;
            case R.id.func_cloud_userinfo_setting:
                intent = new Intent(More.this, CloudSettingUserInfo.class);
                startActivity(intent);
                break;
            default:

                break;
        }
    }

}