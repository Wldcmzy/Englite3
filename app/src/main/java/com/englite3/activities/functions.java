package com.englite3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.englite3.R;

public class functions extends AppCompatActivity implements View.OnClickListener{
    private TextView import_local;
    private Intent intent;

    private void initview(){
        import_local = findViewById(R.id.func_import_local);
        import_local.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions);
        initview();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.func_import_local:
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, 1);
                break;
            default:
                break;
        }
    }


}