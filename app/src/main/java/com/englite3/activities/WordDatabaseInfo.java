package com.englite3.activities;

import static com.englite3.logic.Functions.randomAddFlagWords;
import static com.englite3.logic.Functions.uploadDatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.englite3.Config;
import com.englite3.R;
import com.englite3.database.DbOperator;

public class WordDatabaseInfo extends AppCompatActivity implements View.OnClickListener {

    private String dbname;
    private DbOperator dop;
    private TextView db__name, sta100, sta80, sta60, sta40, sta20, sta0, flagwords, totalwords;
    private Button db_upload, db_export, db_delete, recite, addflagwords;
    private EditText level_min, level_max, recitenumber, addflagwordsnumber;

    private void initView(){
        db__name = findViewById(R.id.db__name);
        sta100= findViewById(R.id.db_status_100);
        sta80= findViewById(R.id.db_status_80);
        sta60= findViewById(R.id.db_status_60);
        sta40= findViewById(R.id.db_status_40);
        sta20= findViewById(R.id.db_status_20);
        sta0 = findViewById(R.id.db_status_0);
        level_min = findViewById(R.id.level_min);
        level_max = findViewById(R.id.level_max);
        flagwords = findViewById(R.id.flagwords);
        totalwords = findViewById(R.id.totalwords);
        recitenumber = findViewById(R.id.recite_number);
        addflagwordsnumber = findViewById(R.id.add_flagwords_number);

        db_export= findViewById(R.id.db_export);
        db_export.setOnClickListener(this);
        db_upload= findViewById(R.id.db_upload);
        db_upload.setOnClickListener(this);
        db_delete= findViewById(R.id.db_delete);
        db_delete.setOnClickListener(this);
        recite= findViewById(R.id.start_recite);
        recite.setOnClickListener(this);
        addflagwords = findViewById(R.id.add_flagwords);
        addflagwords.setOnClickListener(this);
    }

    private void resetText(){
        db__name.setText(dbname);

        int total = dop.getWordsNumber(false);
        int flagtotal = dop.getWordsNumber(true);
        totalwords.setText("词库单词总量为:" + total);
        flagwords.setText("已加入规划的单词总量为:" + flagtotal);

        String s = "%d%%的已规划单词达到了等级%d";
        int[] stas = dop.getWordsLevel();
        sta100.setText(String.format(s, 100, stas[0]));
        sta80.setText(String.format(s, 80, stas[1]));
        sta60.setText(String.format(s, 60, stas[2]));
        sta40.setText(String.format(s, 40, stas[3]));
        sta20.setText(String.format(s, 20, stas[4]));
        sta0.setText("在所有的单词中,等级最高达到了" + stas[5]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_database_info);

        dbname = getIntent().getStringExtra("dbname");
        dop = new DbOperator(this, Config.Dbprefix + dbname);

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        resetText();
    }

    @Override
    public void onClick(View v) {
        int num, mn, mx;
        switch(v.getId()){
            case R.id.db_export:
                break;
            case R.id.db_upload:
                confirm(R.id.db_upload, 0);
                break;
            case R.id.start_recite:
                mn = Integer.parseInt(level_min.getText().toString());
                mx = Integer.parseInt(level_max.getText().toString());
                num = Integer.parseInt(recitenumber.getText().toString());
                Intent intent = new Intent(WordDatabaseInfo.this, WordRecite.class);
                intent.putExtra("dbname", dbname);
                intent.putExtra("mn", mn);
                intent.putExtra("mx", mx);
                intent.putExtra("num", num);
                startActivity(intent);
                break;
            case R.id.add_flagwords:
                num = Integer.parseInt(addflagwordsnumber.getText().toString());
                confirm(R.id.add_flagwords, num);
                break;
            default:
                break;
        }

    }

    private void confirm(int id, int arg){
        AlertDialog.Builder builder = new AlertDialog.Builder(WordDatabaseInfo.this);
        String query = "";
        switch (id){
            case R.id.add_flagwords:
                query = "你希望再把" + arg + "个加入规划, 是否继续?";
                break;
            case R.id.db_upload:
                query = "你确定要把词库" + dbname + "同步到云端吗?";
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
                                    case R.id.add_flagwords:
                                        randomAddFlagWords(WordDatabaseInfo.this, dop, arg);
                                        resetText();
                                        break;
                                    case R.id.db_upload:
                                        uploadDatabase(WordDatabaseInfo.this, dbname);
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

}